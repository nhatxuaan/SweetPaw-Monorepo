const cron = require("node-cron");
const Order = require("../models/orderModel");
const ghnService = require("../services/ghnService");
const { mapGhnStatusToApp } = require("../utils/mapGhnStatusToApp");

// Hàm chính để cập nhật trạng thái
const updateOrderStatuses = async () => {
  console.log("Kiểm tra trạng thái đơn hàng...");

  const orders = await Order.find({
    status: { $ne: "Đã giao thành công" },
  });

  for (const order of orders) {
    try {
      const res = await ghnService.getOrderInfoGHN(order.ghn_order_code);
      const ghnData = res?.data || {};
      const newStatus = mapGhnStatusToApp(ghnData.status || "unknown");

      if (newStatus && newStatus !== order.status) {
        await Order.updateOne(
          { _id: order._id },
          {
            $set: {
              status: newStatus,
              status_raw: ghnData.status,
              updatedAt: new Date(),
            },
          }
        );
        console.log(
          `Cập nhật đơn ${order.ghn_order_code}: ${order.status} -> ${newStatus}`
        );
      } else {
        console.log(
          `Đơn ${order.ghn_order_code}: Không thay đổi (${order.status})`
        );
      }
    } catch (error) {
      console.error(
        `Lỗi khi kiểm tra đơn ${order.ghn_order_code}:`,
        error.response?.data || error.message
      );
    }
  }
};


//cron.schedule("*/1 * * * *", updateOrderStatuses);
cron.schedule("0 0 * * *", updateOrderStatuses);

