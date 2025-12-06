const orderService = require("../services/orderService");

const previewOrder = async (req, res, next) => {
  try {
    // Lấy userId từ middleware xác thực
    const userId = req.user.id;  

    // Gọi service để tính toán đơn hàng
    const result = await orderService.previewOrder(userId, req.body);

    // Trả kết quả cho FE
    res.status(200).json({
      message: "Tính toán đơn hàng thành công",
      data: result,
    });
  } catch (error) {
    console.error("Lỗi previewOrder:", error);
    next(error);
  }
};

const createOrderController = async (req, res, next) => {
  try {
    const userId = req.user.id; // lấy từ middleware auth
    const result = await orderService.createOrder(userId, req.body);
    res.status(200).json({
      message: "Tạo đơn hàng thành công",
      data: result,
    });
  } catch (error) {
    console.error("Lỗi createOrder:", error);
    next(error);
  }
};

const getUserOrdersController = async (req, res, next) => {
  try {
    const userId = req.user.id; // lấy từ token (middleware auth)
    const orders = await orderService.getOrdersByUserService(userId);

    res.status(200).json({
      code: 200,
      message: "Lấy danh sách đơn hàng thành công",
      data: orders,
    });
  } catch (error) {
    next(error);
  }
};

const getOrderDetailController = async (req, res, next) => {
  try {
    const userId = req.user.id;
    const { orderId } = req.params;

    const result = await orderService.getOrderDetailService(userId, orderId);

    res.status(200).json({
      code: 200,
      message: "Lấy chi tiết đơn hàng thành công",
      data: result,
    });
  } catch (error) {
    next(error);
  }
};

module.exports = { 
  previewOrder, 
  createOrderController,
  getUserOrdersController, 
  getOrderDetailController,
};
