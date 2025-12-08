const Order = require("../models/orderModel");
const User = require("../models/userModel");
const Chat = require("../models/chatModel");
const Traffic = require("../models/trafficModel");

// Hàm để tính doanh thu tổng theo khoảng thời gian
const totalRevenue = async (startDate, endDate) => {
    const orders = await Order.find({
        createdAt: { $gte: startDate, $lte: endDate }
    });
    // console.log("Tổng số đơn hàng trong khoảng:", orders.length);

    let total = 0;
    orders.forEach(order => {
        if (order.paymentStatus === "SUCCESS" || order.status === "Đã giao thành công") {
            // console.log("Mã đơn hàng " + order._id + " có trạng thái thanh toán:", order.paymentStatus, "và trạng thái đơn hàng:", order.status);
            total += order.total_price;
            // console.log("giá đơn hàng:", order.total_price) ;
            // console.log("Đơn hàng hợp lệ, cộng doanh thu:", order.total_price);
        }
    })

  return total;
};

// Hàm tính số lượng khách hàng mới trong khoảng thời gian 
const countNewCustomers = async (startDate, endDate) => {
    const newCustomers = await User.countDocuments({
        createdAt: { $gte: startDate, $lte: endDate }
    });
    return newCustomers;
}

// Hàm tính đơn đặt hàng trong khoảng thời gian
const countNewOrders = async (startDate, endDate) => {
    const ordersCount = await Order.countDocuments({
        createdAt: { $gte: startDate, $lte: endDate }
    });
    return ordersCount;
}

// Hàm tính các tin nhắn trong khoảng thời gian 
const countNewMessages = async (startDate, endDate) => {
    const messagesCount = await Chat.countDocuments({
        createdAt: { $gte: startDate, $lte: endDate },
        senderModel: "User"
    });
    
    return messagesCount;
}

// Hàm tính tăng trưởng theo phần trăm 
const calculateGrowth = (currentValue, previousValue) => {
    if (previousValue === 0) {
        return currentValue === 0 ? 0 : 100; // Nếu cả hai đều là 0, tăng trưởng là 0%, nếu chỉ previous là 0, tăng trưởng là 100%
    }
    const growth = ((currentValue - previousValue) / previousValue) * 100;
    return growth;
}

// Hàm tính doanh thu theo loại bánh 
const revenueByCategory = async (category, startDate, endDate) => {
  const result = await Order.aggregate([
    {
      $match: {
        createdAt: { $gte: startDate, $lte: endDate },
        $or: [
          { paymentStatus: "SUCCESS" },
          { status: "Đã giao thành công" }
        ]
      }
    },

    { $unwind: "$items" },

    {
      $lookup: {
        from: "SANPHAM",  
        localField: "items.productId",
        foreignField: "_id",
        as: "product"
      }
    },

    { $unwind: "$product" },

    {
      $match: { "product.category": category }
    },

    {
      $group: {
        _id: null,
        revenue: { $sum: { $multiply: ["$items.price", "$items.quantity"] } }
      }
    }
  ]);

  return result[0]?.revenue || 0;
};


// Hàm trả về lượt truy cập ứng dụng 
const appVisits = async (startDate, endDate) => {
  const countTraffic = await Traffic.countDocuments({
    timestamp: { $gte: startDate, $lte: endDate }
  });

  return countTraffic;
};


// Hàm trả về top khách nổi bật trong khoảng thời gian 

const topCustomers = async (startDate, endDate, limit = 5) => {
  const topList = await Order.aggregate([
    {
      $match: {
        createdAt: { $gte: startDate, $lte: endDate },
        $or: [
          { paymentStatus: "SUCCESS" },
          { status: "Đã giao thành công" }
        ]
      }
    },

    {
      $group: {
        _id: "$user",                 // userId
        totalSpent: { $sum: "$total_price" },
        orderCount: { $sum: 1 }
      }
    },

    { $sort: { totalSpent: -1 } },
    { $limit: limit },

    {
      $lookup: {
        from: "KHACHHANG",
        localField: "_id",
        foreignField: "_id",
        as: "userInfo"
      }
    },

    { $unwind: "$userInfo" },

    // Chỉ lấy thông tin cần thiết
    {
      $project: {
        _id: 0,
        customerId: "$_id",
        name: "$userInfo.HoTen",
        phone: "$userInfo.SoDienThoai",
        totalSpent: 1,
        orderCount: 1
      }
    }
  ]);

  return topList;
};



  


module.exports = { 
    totalRevenue,
    countNewCustomers,
    countNewOrders,
    countNewMessages,
    calculateGrowth,
    revenueByCategory,
    appVisits,
    topCustomers,
};