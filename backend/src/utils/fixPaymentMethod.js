// fixPaymentMethod.js
const mongoose = require("mongoose");
const Order = require("../models/orderModel");
const ghnService = require("../services/ghnService");
require("dotenv").config({path: ".env"});

(async () => {
  try {
    await mongoose.connect(process.env.DB_URI);
    console.log("Đã kết nối MongoDB");

    // lấy 129 đơn KHÔNG có payment_method
    const orders = await Order.find({ payment_method: { $exists: false } });

    console.log(`Có ${orders.length} đơn cần cập nhật.`);

    for (let order of orders) {
      if (!order.ghn_order_code) continue;

      const ghnRes = await ghnService.getOrderInfoGHN(order.ghn_order_code);
      const paymentTypeId = ghnRes?.data?.payment_type_id;

      if (!paymentTypeId) continue;

      const mapped = paymentTypeId == 1 ? "Chuyển khoản" : "Thanh toán khi nhận hàng";

      await Order.updateOne(
        { _id: order._id },
        { $set: { payment_method: mapped } }
      );

      console.log(`Cập nhật đơn ${order._id}: ${mapped}`);
    }

    console.log("Hoàn tất cập nhật!");
    process.exit();
  } catch (err) {
    console.error(err);
    process.exit(1);
  }
})();
