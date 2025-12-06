const express = require("express");
const router = express.Router();
const orderController = require("../controllers/orderController");
const authMiddleware = require("../middlewares/authMiddleware");

// Tính tiền preview đơn hàng
router.post("/preview", authMiddleware, orderController.previewOrder);

// Xác nhận đặt hàng thật
router.post("/", authMiddleware, orderController.createOrderController);

// Lay don dat hang
router.get("/my-orders", authMiddleware, orderController.getUserOrdersController);

// Lay chi tiet don dat hang
router.get("/detail/:orderId", authMiddleware, orderController.getOrderDetailController);


module.exports = router;
