const express = require('express');
const {adminRegisterController, adminLoginController, getProductsController, addProductController, 
  updateProductController, deleteProductController, getCustomersController, addCustomerController,
  updateCustomerController, getChatHistoryController, getAllChatsController, sendMessageController,
  getOrdersController, updateOrderController, getOrderDetailController, getDiscountsController,
  addDiscountController, updateDiscountController, deleteDiscountController,

} = require('../controllers/adminController');
const {verifyToken, verifyAdmin} = require('../middlewares/adminMiddleware');
const router = express.Router();
const multer = require('multer');
const upload = require('../middlewares/upload');

const placeholder = (req, res) => {
  res.status(501).json({ message: "API chưa được triển khai" });
};

// endpoint đăng ký admin
router.post('/register', adminRegisterController);

// endpoint đăng nhập 
router.post('/login', adminLoginController);

// endpoint quên mật khẩu 
router.post('/forgot-password', placeholder);

// endpoint đặt lại mật khẩu 
router.post('/reset-password', placeholder);

// endpoint lấy danh sách sản phẩm 
router.get('/products', verifyToken, verifyAdmin, getProductsController);

// endpoint thêm sản phẩm mới 
router.post('/products', verifyToken, verifyAdmin, upload.single("imageFile"), addProductController);

// endpoint cập nhật sản phẩm 
router.put('/products/:productId', verifyToken, verifyAdmin, updateProductController);

// endpoint xóa sản phẩm 
router.delete('/products/:productId', verifyToken, verifyAdmin, deleteProductController);

// endpoint lấy danh sách khách hàng 
router.get('/customers', verifyToken, verifyAdmin, getCustomersController);

// endpoint thêm khách hàng mới
router.post('/customers', verifyToken, verifyAdmin, addCustomerController);

// endpoint cập nhật khách hàng
router.put('/customers/:id', verifyToken, verifyAdmin, updateCustomerController);

// endpoint lấy tin nhắn 
router.get('/messages/:userId', verifyToken, verifyAdmin, getChatHistoryController);

// endpoint hiển thị tất cả các đoạn chat 
router.get('/messages', verifyToken, verifyAdmin, getAllChatsController);

// endpoint gửi tin nhắn 
router.post('/messages', verifyToken, verifyAdmin, sendMessageController); // huhu cứu tôi nhớ check lại nha

// endpoint lấy đơn đặt hàng 
router.get('/orders', verifyToken, verifyAdmin, getOrdersController);

// endpoint cập nhật trạng thái thanh toán đơn hàng
router.put('/orders/:orderId', verifyToken, verifyAdmin, updateOrderController);

// endpoint xem chi tiết đơn hàng 
router.get('/orders/:orderId', verifyToken, verifyAdmin, getOrderDetailController);

// endpoint lấy danh sách khuyến mãi 
router.get('/promotions', verifyToken, verifyAdmin, getDiscountsController);

// endpoint thêm khuyến mãi mới 
router.post('/promotions', verifyToken, verifyAdmin, addDiscountController);

// endpoint cập nhật khuyến mãi 
router.put('/promotions/:discountId', verifyToken, verifyAdmin, updateDiscountController);

// endpoint xóa khuyến mãi
router.delete('/promotions/:discountId', verifyToken, verifyAdmin, deleteDiscountController);


module.exports = router;