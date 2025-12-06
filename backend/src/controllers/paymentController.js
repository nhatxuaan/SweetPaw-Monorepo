const Order = require("../models/orderModel");
const orderService = require("../services/orderService");
const { default: ApiError } = require("../utils/ApiError");
const paymentService = require("../services/paymentService");
const paymentPendingModel = require("../models/paymentPendingModel");

// Tạo thanh toán cho đơn hàng
const createPaymentController = async (req, res, next) => {
  try {
    const { orderId } = req.params;
    const userId = req.user.id; 

    if (!orderId) {
    throw new ApiError(400, "Thiếu mã đơn hàng");
    }

    // Lấy thông tin đơn hàng trong DB
    const order = await Order.findOne({ _id: orderId, user: userId });
    const paymentStatus = order.paymentStatus;

    // Kiểm tra quyền truy cập đơn hàng

    if (!order) {
      throw new ApiError(403, "Bạn không có quyền truy cập đơn hàng này");
    }
    if (paymentStatus === "SUCCESS") {
      throw new ApiError(400, "Đơn hàng đã được thanh toán");
    }

    const amount = order.total_price;
    if (!amount) {
      throw new ApiError(400, "Đơn hàng không có tổng tiền");
    }

    // Gọi service tạo QR
    const paymentData = await paymentService.createPaymentService(orderId, amount);

    return res.status(200).json({
      message: "Tạo QR thanh toán thành công",
      data: paymentData,
    });
    
  } catch (error) {
    return next(error);
  }
}

const handleCassoWebhookController = async (req, res, next) => {
  try {
    const webhookData = req.body;
    await paymentService.handleCassoWebhookService(webhookData);
    return res.status(200).json({ message: "Xử lý webhook thành công" });
  } catch (error) {
    return next(error);
  }

}

const getPaymentStatusController = async (req, res, next) => {
  try {
    const { orderId } = req.params;
    const userId = req.user.id; 
    if (!orderId) {
      throw new ApiError(400, "Thiếu orderId để kiểm tra trạng thái thanh toán");
    }
    const order = await Order.findOne({ _id: orderId, user: userId });
    if (!order) {
      throw new ApiError(403, "Bạn không có quyền truy cập đơn hàng này");
    }
    const paymentStatusData = await paymentService.getPaymentStatusService(orderId);
    return res.status(200).json({
      message: "Lấy trạng thái thanh toán thành công",
      data: paymentStatusData,
    });
  } catch (error) {
    return next(error);
  } 
}

module.exports = {
  createPaymentController,
  handleCassoWebhookController,
  getPaymentStatusController, 
};