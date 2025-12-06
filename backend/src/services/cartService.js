const Cart = require("../models/cartModel");
const Product = require("../models/productModel")
const { default: ApiError } = require("../utils/ApiError");

// Lấy danh sách sản phẩm trong giỏ hàng 
// Trả về hình ảnh, tên, số lượng, giá, mô tả => giống như là 1 object product luôn rồi

const getCartByUserService = async(userId) => {
    const cart = await Cart.findOne({userId})
    .populate("items.productId", "name price url des category stock") // chỉ lấy field cần thiết
    .lean(); // chuyển sang object JS cho dễ xử lý

    if (!cart) {
        return { items: [], total: 0 };
    }

    const total = cart.items
    .filter((i) => i.checked)
    .reduce((sum, i) => sum + i.productId.price * i.quantity, 0);

    return {
        userId: cart.userId,
        items: cart.items.map((i) => ({
        _id: i._id,
        productId: i.productId._id,
        name: i.productId.name,
        url: i.productId.url,
        des: i.productId.des,
        category: i.productId.category,
        stock: i.productId.stock,
        price: i.productId.price,
        quantity: i.quantity,
        checked: i.checked,
        
        })),
        total,
    };

}

// API them san pham vao gio hang
const addToCartService = async (userId, productId, quantity) => {

  // Lấy thông tin sản phẩm để kiểm tra tồn kho
  const product = await Product.findById(productId);
  if (!product) {
    throw new ApiError(404, "Không tìm thấy sản phẩm");
  }

  // Tìm giỏ hàng của user
  let cart = await Cart.findOne({ userId });

  // Nếu chưa có giỏ -> tạo mới
  if (!cart) {
    cart = new Cart({
      userId,
      items: [{ productId, quantity }],
      updatedAt: new Date(),
    });
  } else {
    // Kiểm tra xem sản phẩm đã có trong giỏ chưa
    const existingItem = cart.items.find(
      (i) => i.productId.toString() === productId.toString()
    );


    if (existingItem) {

      const newQuantity = existingItem.quantity + quantity;
      // Nếu vượt quá tồn kho thì chặn
      if (newQuantity > product.stock) {
        throw new ApiError(
          400,
          `Sản phẩm "${product.name}" chỉ còn ${product.stock} sản phẩm, không thể thêm ${quantity} (hiện trong giỏ đã có ${existingItem.quantity})!`
        );
      }

      existingItem.quantity = newQuantity;
    } else {
      // Nếu chưa có -> thêm mới
      if (quantity > product.stock) {
        throw new ApiError(
          400,
          `Sản phẩm "${product.name}" chỉ còn ${product.stock} sản phẩm, không thể thêm ${quantity}!`
        );
      }

      cart.items.push({ productId, quantity });
    }

    cart.updatedAt = new Date();
  }

  await cart.save();

  // populate để trả về thông tin sản phẩm
  const populatedCart = await Cart.findById(cart._id)
    .populate("items.productId", "name price url des category")
    .lean();

  return populatedCart;
};

//API chinh sua so luong san pham
const updateCartQuantityService = async (userId, productId, change) => {
  const cart = await Cart.findOne({ userId });
  if (!cart) throw new ApiError(404, "Không tìm thấy giỏ hàng");

  const item = cart.items.find(
    (i) => i.productId.toString() === productId.toString()
  );
  if (!item) throw new ApiError(404, "Sản phẩm không có trong giỏ");

  // Lấy thông tin sản phẩm để kiểm tra stock
  const product = await Product.findById(productId);
  if (!product) throw new ApiError(404, "Không tìm thấy sản phẩm");

  const newQuantity = item.quantity + change;


  if (newQuantity < 1) {
    throw new ApiError(400, "Số lượng tối thiểu là 1");
  }

  if (newQuantity > product.stock) {
    throw new ApiError(400, `Chỉ còn ${product.stock} sản phẩm trong kho`);
  }

 
  item.quantity = newQuantity;
  cart.updatedAt = Date.now();
  await cart.save();

  // Populate để trả lại sản phẩm đầy đủ
  await cart.populate("items.productId", "name price url des category");

  return cart;
};

// API tinh tien dua tren checked UI
const updateCartCheckService = async (userId, productId, checked) => {
  const cart = await Cart.findOne({ userId });
  if (!cart) throw new ApiError(404, "Không tìm thấy giỏ hàng");

  const item = cart.items.find(
    (i) => i.productId.toString() === productId.toString()
  );
  if (!item) throw new ApiError(404, "Sản phẩm không có trong giỏ");

  item.checked = checked; // true hoặc false
  cart.updatedAt = Date.now();
  await cart.save();

  await cart.populate("items.productId", "name price url des category");

  return cart;
};

const updateCartCheckAllService = async (userId, checked) => {
  const cart = await Cart.findOne({ userId });
  if (!cart) throw new ApiError(404, "Không tìm thấy giỏ hàng");

  // Gán checked cho tất cả item
  cart.items = cart.items.map((item) => ({
    ...item.toObject(),
    checked,
  }));

  cart.updatedAt = Date.now();
  await cart.save();

  await cart.populate("items.productId", "name price url des category");

  return cart;
};

const removeSelectedItemsService = async (userId, selectedItems) => {
  const cart = await Cart.findOne({ userId });
  if (!cart) throw new ApiError(404, "Không tìm thấy giỏ hàng");

  if (!selectedItems || selectedItems.length === 0) {
    throw new ApiError(400, "Danh sách sản phẩm cần xóa trống");
  }

  // Giữ lại các item KHÔNG nằm trong selectedItems
  cart.items = cart.items.filter(
    (item) => !selectedItems.includes(item.productId.toString())
  );

  cart.updatedAt = Date.now();
  await cart.save();

  await cart.populate("items.productId", "name price url des category");

  return {
    success: true,
    message: "Đã xóa sản phẩm được chọn khỏi giỏ hàng",
    cart,
  };
};


const removeSingleItemService = async (userId, productId) => {
  const cart = await Cart.findOne({ userId });

  if (!cart) {
    throw new ApiError(404, "Không tìm thấy giỏ hàng");
  }

  console.log("CART HIỆN TẠI:");
  cart.items.forEach((item, idx) => {
    console.log(
      `item[${idx}] = productId: ${item.productId.toString()}, quantity: ${item.quantity}`
    );
  });

  console.log("productId cần xóa:", productId);

  // Tìm vị trí của sản phẩm trong giỏ
  const itemIndex = cart.items.findIndex(
    (item) => item.productId.toString() === productId
  );

  console.log("itemIndex tìm được:", itemIndex);

  if (itemIndex === -1) {
    throw new ApiError(404, "Sản phẩm không tồn tại trong giỏ hàng");
  }

  // Xóa sản phẩm đó ra khỏi mảng
  cart.items.splice(itemIndex, 1);
  cart.updatedAt = Date.now();
  await cart.save();

  await cart.populate("items.productId", "name price url des category");

  return cart;
};


module.exports = {
    getCartByUserService,
    addToCartService,
    updateCartQuantityService,
    updateCartCheckService,
    updateCartCheckAllService,
    removeSelectedItemsService,
    removeSingleItemService,


}