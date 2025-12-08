const ghnService = require("./ghnService");
const Discount = require("../models/discountModel");
const { retrainModelAI } = require("./AIService");
const User = require("../models/userModel");
const { getAddressCodesFromNames } = require("../utils/ghnHelper");
const { calculateDiscountedPrice } = require("../utils/orderHelper");
const { default: ApiError } = require("../utils/ApiError");
const { default: GHN_API } = require("../utils/ghnAPI");
const Order = require("../models/orderModel");
const Product = require("../models/productModel")
const Cart = require("../models/cartModel")
const { default: mapGhnStatus } = require("../utils/mapGhnStatusToApp");
const { sendToToken } = require("./notificationService");
const mongoose = require("mongoose");


const paymentTypeMap = {
    1: "Chuyển khoản",
    2: "Thanh toán khi nhận hàng",
  };

const mapGhnStatusToApp = (status) => {
const map = {
  ready_to_pick: "Đang xử lý",
  delivering: "Đang giao hàng",
  delivered: "Đã giao thành công",
};
return map[status] || "Đang xử lý";
};

// API tinh tien
const previewOrder = async (userId, body) => {
  const { items, discount_code, address } = body;

  // 1 Ưu tiên địa chỉ do người dùng chọn, nếu không thì lấy địa chỉ mặc định
  const user = await User.findById(userId);
  const defaultAddress = user.DiaChi.find(a => a.MacDinh);
  const shipAddress =  address|| defaultAddress;

  if (!shipAddress) throw new ApiError(400, "Không có địa chỉ giao hàng hợp lệ");

  const { to_province_id, to_district_id, to_ward_code } =
  await getAddressCodesFromNames(shipAddress);

  // Kiểm tra tồn kho từng sản phẩm trước khi tính toán
  for (const item of items) {
    const product = await Product.findById(item.productId);

    if (!product) {
      throw new ApiError(404, `Không tìm thấy sản phẩm với ID: ${item.productId}`);
    }

    if (product.stock <= 0) {
      throw new ApiError(400, `Sản phẩm "${product.name}" đã hết hàng!`);
    }

    if (item.quantity > product.stock) {
      throw new ApiError(
        400,
        `Sản phẩm "${product.name}" chỉ còn ${product.stock} sản phẩm, không thể đặt ${item.quantity}!`
      );
    }
  }

  // 2 Tính tổng giá trị sản phẩm
  const subtotal = items.reduce((sum, item) => sum + item.price * item.quantity, 0);

  // 3️ Gọi GHN tính phí giao hàng
  const feePayload = {
    to_district_id,
    to_ward_code,
    weight: items.reduce((s, i) => s + i.weight * i.quantity, 0),
    items, 
    shop_id: process.env.GHN_SHOP_ID, 
    payment_type_id: 2, 
    service_type_id: 2, 
    required_note: "KHONGCHOXEMHANG",
  };
  const feeRes = await ghnService.getFeeGHN(feePayload);
  const shipping_fee = feeRes?.data?.total || 0;

  // 4️ Áp dụng mã giảm giá nếu có
  let discount_amount = 0;
  if (discount_code) {
    discount_amount = await calculateDiscountedPrice (
      discount_code,
      subtotal
    );
  }

  // 5️ Tính tổng tiền cuối cùng
  const total = subtotal + shipping_fee - discount_amount;

  return {
    subtotal,
    shipping_fee,
    discount_amount,
    total,
    address_used: {
      label: shipAddress.label,
      address: shipAddress.address,
      district_name: shipAddress.district_name,
      province_name: shipAddress.province_name,
    },
  };
};

// API tao don dat hang
const createOrder = async (userId, body) => {
  const {
    to_name,
    to_phone,
    to_address,
    to_ward_name,
    to_district_name,
    to_province_name,
    items,
    note,
    payment_type_id,
    discount_code
  } = body;

  // 1️ Kiểm tra thông tin người nhận
  if (!to_name || !to_phone || !to_address) throw new ApiError(400, "Thiếu thông tin người nhận!");
  
  // Kiểm tra tồn kho
  for (const item of items) {
  const product = await Product.findById(item.productId);

  if (!product) {
    throw new ApiError(404, `Không tìm thấy sản phẩm với ID: ${item.productId}`);
  }

  // nếu tồn kho sau khi trừ < 0 thì không cho đặt
  if (product.stock - item.quantity < 0) {
    throw new ApiError(
      402,
      `Sản phẩm "${product.name}" chỉ còn ${product.stock} sản phẩm, không đủ để đặt ${item.quantity}!`
    );
  }
}


  // Tính tổng tiền total_price
  let tongtien = items.reduce((sum, item) => sum + item.price * item.quantity, 0) + 0; // + shipping_fee (sẽ cập nhật sau)

  // Áp dụng mã giảm giá nếu có
  let discountAmount = 0;
  if (discount_code) {
    discountAmount = await calculateDiscountedPrice(
      discount_code,
      tongtien
    );
  }

  console.log(">>> Giảm giá áp dụng:", discountAmount);


  // Tính khối lượng 
  const weight = items.reduce(
    (sum, i) => sum + (i.weight || 0) * (i.quantity || 1),
    0
  );

  let payload = {};
  // Kiểm tra hình thức thanh toán
    if (payment_type_id == 1) {
      // Chuẩn bị payload chuyển khoản không có cod_amount
      payload = {
        shop_id: process.env.GHN_SHOP_ID,
        payment_type_id, //payment_type_id = 1 là chuyển khoản
        service_type_id: 2, // giao hàng TMĐT
        required_note: "KHONGCHOXEMHANG",
        to_name,
        to_phone,
        to_address,
        to_ward_name,
        to_district_name,
        to_province_name,
        items,
        note,
        weight,

        from_name: "SweetPaw Bakery",
        from_phone: "0909000000",
        from_address: "1 Võ Văn Ngân, Phường Linh Chiểu, Thủ Đức",
        from_ward_name: "Phường Linh Chiểu",
        from_district_name: "Thành phố Thủ Đức",
        from_province_name: "Hồ Chí Minh",
      };

      console.log(">>> Payload gửi GHN:", payload);
    }
    else if (payment_type_id == 2) {
      // Chuẩn bị payload cod_amount
      const cod_amount = tongtien - discountAmount ; // tiền thu hộ là tổng tiền đơn hàng
        payload = {
        shop_id: process.env.GHN_SHOP_ID,
        payment_type_id, // payment_type_id = 2 nghĩa là cod
        service_type_id: 2, // giao hàng TMĐT
        required_note: "KHONGCHOXEMHANG",
        to_name,
        to_phone,
        to_address,
        to_ward_name,
        to_district_name,
        to_province_name,
        items,
        note,
        cod_amount,
        weight,

        from_name: "SweetPaw Bakery",
        from_phone: "0909000000",
        from_address: "1 Võ Văn Ngân, Phường Linh Chiểu, Thủ Đức",
        from_ward_name: "Phường Linh Chiểu",
        from_district_name: "Thành phố Thủ Đức",
        from_province_name: "Hồ Chí Minh",
      };

      console.log(">>> Payload gửi GHN:", payload);
    }
  // 4️ Gọi GHN tạo đơn
  const ghnRes = await ghnService.createOrderGHN(payload);
  const ghnData = ghnRes?.data || {};

  const total_price = tongtien + Number(ghnData.total_fee || 0) - discountAmount;

  // 5️ Lưu vào DB
  const newOrder = await Order.create({
    user: userId,
    to_name,
    to_phone,
    to_address,
    to_ward_name,
    to_district_name,
    to_province_name,
    items,
    note,
    ghn_order_code: ghnData.order_code,
    shipping_fee: Number(ghnData.total_fee || 0),
    tongtien,
    discountAmount,
    total_price,
    status: "Đang xử lý",
    payment_method: paymentTypeMap[payment_type_id],
  });

  await sendToToken({
    fcmToken: (await User.findById(userId)).fcmToken,
    title: "Đặt hàng thành công!",
    body: `Đơn hàng ${ghnData.order_code} của bạn đang được xử lý.`,
    data: {
      type: "order_created",
      orderId: newOrder._id.toString(),
      orderCode: ghnData.order_code
    },
    userId
  });

  for (const item of items) {
    await Product.updateOne(
      { _id: item.productId },
      {
        $inc: {
          stock: -item.quantity,      // giảm tồn kho
          sold_count: item.quantity,  // tăng số lượng đã bán
        },
      }
    );
  }

  // Dat thanh cong xoa san pham ra khoi gio hang
  const deleteResult = await Cart.updateOne(
    { userId },
    {
      $pull: {
        items: {
          productId: { $in: items.map((i) => i.productId) },
        },
      },
    }
  );

  console.log("Đã xoá sản phẩm khỏi giỏ hàng:", deleteResult);

  //Retrain model 
  retrainModelAI()
  .then(res => console.log("Đã trigger retrain AI:", res.message))
  .catch(err => console.error("Lỗi retrain AI:", err.message));

  // 6️ Trả kết quả về FE
  return {
    code: 200,
    message: "Tạo đơn hàng thành công",
    orderId: newOrder._id,
    data: {
      order_code: ghnData.order_code,
      sort_code: ghnData.sort_code,
      trans_type: ghnData.trans_type,
      expected_delivery_time: ghnData.expected_delivery_time,
      fee: ghnData.fee,
      total_fee: Number(ghnData.total_fee || 0),
      discountAmount: discountAmount,
      total_price,
    },
    message_display: `Tạo đơn hàng thành công. Mã đơn hàng: ${ghnData.order_code || ""}`,
  };
};



//API lay chi tiet don hang
const getOrderDetailService = async (userId, orderId) => {
  // 1️ Lấy đơn hàng trong DB
  const order = await Order.findOne({ _id: orderId, user: userId })
    .populate("items.productId", "name price url weight")
    .lean();

  if (!order) throw new ApiError(400, "Không tìm thấy đơn hàng!");

  // 2️ Gọi GHN API để lấy thông tin trạng thái
  const ghnResponse = await ghnService.getOrderInfoGHN(order.ghn_order_code);
  //console.log(ghnResponse)
  const ghnData = ghnResponse?.data || {};


  // 3️ Trích xuất log trạng thái giao hàng
  const tracking = ghnData?.log?.map((log) => ({
    status: log.status,
    updated_date: log.updated_date,
  })) || [];

  
  // 4️ Map trạng thái GHN sang trạng thái hiển thị
  const mappedStatus = mapGhnStatusToApp(ghnData?.status);

  // 5️ Nếu khác với DB thì cập nhật lại
  if (mappedStatus && mappedStatus !== order.status) {
    console.log(`Cập nhật trạng thái đơn ${order.ghn_order_code}: ${order.status} -> ${mappedStatus}`);
    await Order.updateOne(
      { _id: order._id },
      { $set: { status: mappedStatus } }
    );
    order.status = mappedStatus; // đồng bộ trong biến trả về luôn

    await sendToToken({
      fcmToken: (await User.findById(userId)).fcmToken,
      title: "Cập nhật đơn hàng",
      body: `Đơn hàng ${order.ghn_order_code} hiện tại: ${mappedStatus}`,
      data: {
        type: "order_status",
        orderId: order._id.toString(),
        status: mappedStatus
      },
      userId
    });
  }


  // 6 Gộp dữ liệu lại
  return {
    orderId: order._id,
    discountAmount: order.discountAmount,
    order_code: order.ghn_order_code,
    created_at: order.createdAt,
    to_name: order.to_name,
    to_phone: order.to_phone,
    to_address: `${order.to_address}, ${order.to_ward_name}, ${order.to_district_name}, ${order.to_province_name}`,
    note: order.note || "",
    payment_method: paymentTypeMap[ghnData.payment_type_id],
    display_status: order.status,
    expected_delivery: ghnData?.leadtime,
    payment_status: order.paymentStatus,
    items: order.items.map((i) => ({
      productId: i.productId?._id,
      name: i.name || i.productId?.name,
      quantity: i.quantity,
      price: i.price,
      image: i.productId?.url,
      total: i.price * i.quantity,
    })),
    subtotal: order.tongtien,
    shipping_fee: order.shipping_fee,
    total_price: order.total_price,
    ghn_tracking: tracking, // log từ GHN
  };
};

const getOrdersByUserService = async (userId) => {
  const orders = await Order.aggregate([
    // lọc đúng user đang đăng nhập
    { $match: { user: new mongoose.Types.ObjectId(userId) } },

    // sắp xếp đơn mới nhất lên đầu
    { $sort: { createdAt: -1 } },

    // lấy id sản phẩm đầu tiên trong items
    {
      $addFields: {
        firstProductId: { $arrayElemAt: ["$items.productId", 0] },
      },
    },

    // nối dữ liệu ảnh từ bảng SANPHAM
    {
      $lookup: {
        from: "SANPHAM",
        localField: "firstProductId",
        foreignField: "_id",
        as: "firstProduct",
      },
    },
    { $unwind: { path: "$firstProduct", preserveNullAndEmptyArrays: true } },

    // thêm url ảnh ra ngoài cùng cấp
    {
      $addFields: {
        thumbnail_url: {
          $ifNull: ["$firstProduct.url", null],
        },
      },
    },

    // chỉ giữ lại field FE cần
    {
      $project: {
        _id: 1,
        items: 1,
        ghn_order_code: 1,
        total_price: 1,
        status: 1,
        createdAt: 1,
        thumbnail_url: 1,
        paymentStatus: 1,
      },
    },
  ]);

  return orders;
};
module.exports = { 
  previewOrder, 
  createOrder, 
  getOrdersByUserService, 
  getOrderDetailService,
};
