const adminService = require("../services/adminService");
const User = require("../models/userModel");
const Admin = require("../models/adminModel");
const cloudinary = require("../config/cloudinary");
const Product = require("../models/productModel");
const { sendToToken } = require("../services/notificationService");

// controller đăng ký admin
const adminRegisterController = async(req, res, next) => {
    try {
        const { username, email, password, displayName } = req.body;
        const result = await adminService.registerAdminService(username, email, password, displayName);
        res.status(201).json(result);
    } catch(error) {
        next(error);
    }
};

// controller đăng nhập admin
const adminLoginController = async(req, res, next) => {
    try {
        const { email, password } = req.body;
        const result = await adminService.adminLoginService(email, password);
        res.status(200).json(result);
    } catch(error) {
        next(error);
    }   
};

// controller lấy danh sách sản phẩm
const getProductsController = async(req, res, next) => {
    try {
        const products = await adminService.getProductsService();   
        res.status(200).json(products);
    } catch(error) {
        next(error);
    }
};

// controller thêm sản phẩm mới
const addProductController = async(req, res, next) => {
    try {

        console.log("BODY:", req.body);
        console.log("FILE:", req.file);

        let imageUrl = "";

        // nếu có upload ảnh
        if (req.file) {
        const result = await cloudinary.uploader.upload(req.file.path, {
            folder: "Ảnh_SweetPaw",
            use_filename: true
        });

        imageUrl = result.secure_url; // => URL ảnh
        }

        const lastProduct = await Product.findOne().sort({ id: -1 });
        
        const nextId = lastProduct ? lastProduct.id + 1 : 1;

        const product = await Product.create({
        id: nextId,
        name: req.body.name,
        category: req.body.category,
        price: req.body.price,
        cost: req.body.cost,
        url: imageUrl,        // Lưu vào DB
        des: req.body.des,
        stock: req.body.stock,
        sold_count: 0,
        rating_avg: 0
        });

        console.log(cloudinary.uploader);


        res.status(201).json({
        message: "Tạo sản phẩm thành công",
        product
        });

    } catch (error) {
        console.log(error);
        res.status(500).json({ message: "Lỗi server" });
    }
};

// controller cập nhật sản phẩm
const updateProductController = async (req, res, next) => {
    try {
        const productId = req.params.productId;
        const updateData = req.body;
        const result = await adminService.updateProductService(productId, updateData);
        res.status(200).json(result);
    } catch (error) {
        next(error);
    }   
};

//controller xóa sản phẩm
const deleteProductController = async (req, res, next) => {
    try {
        const productId = req.params.productId;
        const result = await adminService.deleteProductService(productId);
        res.status(200).json({ message: "Xóa sản phẩm thành công", result });
    } catch (error) {
        next(error);
    }
};

// controller lấy danh sách khách hàng
const getCustomersController = async (req, res, next) => {
    try {
        const customers = await adminService.getCustomersService();
        res.status(200).json(customers);
    } catch (error) {
        next(error);
    }
};

// controller thêm khách hàng 
const addCustomerController = async(req, res, next) => {
    try {
        const customerData = req.body; 
        const result = await adminService.addCustomerService(customerData);

        res.status(200).json(result);
    } catch(error) {
        next(error)
    }
};

// Controller cập nhật khách hàng 
const updateCustomerController = async (req, res, next) => {
    try {
        const id = req.params.id;
        const updateData = req.body;

        const result = await adminService.updateCustomerService(id, updateData)

        res.status(200).json(result);
    } catch(error) {next(error)}
}

// Controller lấy tất cả các đoạn chat
const getAllChatsController = async (req, res, next) => {
    try {
        const adminId = req.user.id;
        const result = await adminService.getAllChatsService(adminId);
        res.status(200).json({
            status:200,
            message:"Lấy danh sách chat thành công",
            data: result
        });
    } catch(error) {next(error)}
};

// Controller lấy thông tin đoạn chat
const getChatHistoryController = async (req, res, next) => {
    try {
        const userId = req.params.userId || req.body.userId;
        const adminId = req.user.id;
        const currentUserId = req.user.id;
        if (!adminId || !currentUserId || !userId) return res.status(400).json({ status: 400, message: "Thiếu adminId hoặc currentUserId hoặc userId" });
        console.log("userId:", userId + ", adminId:", adminId + ", currentUserId:", currentUserId);
        const io = req.app.get("io"); // lấy Socket.io instance
        const result = await adminService.getChatHistoryService(userId, adminId, currentUserId, io);
        res.status(200).json({
            status: 200,
            message: "Lấy lịch sử chat thành công",
            data: result
        });
    } catch(error) {next(error)}
}

// Controller gửi tin nhắn
const sendMessageController = async (req, res, next) => {
    try {
        const userId = req.params.userId || req.body.userId;
        const adminId = req.user.id;
        const senderId = req.user.id;
        const content = req.body.content;
        const media = req.body.media;

        if (!adminId || !userId || !senderId) {
            return res.status(400).json({ status: 400, message: "Thiếu adminId hoặc userId hoặc senderId" });
        } 

        const result = await adminService.sendMessageService(userId, adminId, senderId, content, media);

        const io = req.app.get("io");
        io.to(userId).emit("adminMessage", result);

        const user = await User.findById(userId);
        const sender = await Admin.findById(senderId);
        const senderName =  sender?.displayName || "SweetPaw";

        if (user?.fcmToken && (!global.activeChatUsers[userId] || global.activeChatUsers[userId] !== result.chatId)) {
            await sendToToken({
                fcmToken: user.fcmToken,
                title: `Tin nhắn mới từ ${senderName}`,
                body: content || "Bạn có tin nhắn mới",
                data: { 
                    chatId: String(result.chatId),
                    type: "CHAT",
                    userId: String(userId) },
                userId
            });
        }

        return res.status(200).json({
            status: 200,
            message: "Gửi tin nhắn thành công",
            data: result
        });
    }
    catch(error) {next(error)}
}

// Controller lấy đơn đặt hàng
const getOrdersController = async (req, res, next) => {
    try {
        const result = await adminService.getOrdersService();
        res.status(200).json(result);
    } catch(error) {next(error)}
}

// Controller cập nhật trạng thái đơn hàng
const updateOrderController = async (req, res, next) => {
    try {
        const orderId = req.params.orderId;
        const paymentStatus = req.body.paymentStatus;
        const result = await adminService.updateOrderService(orderId, paymentStatus);
        res.status(200).json(result);
    } catch(error) {next(error)}
}

// Controller xem chi tiết đơn hàng
const getOrderDetailController = async (req, res, next) => {
    try {
        const orderId = req.params.orderId;
        const result = await adminService.getOrderDetailService(orderId);
        res.status(200).json(result);
    } catch(error) {next(error)}
}

// Controller lấy danh sách khuyến mãi
const getDiscountsController = async (req, res, next) => {
    try {
        const result = await adminService.getDiscountsService();
        res.status(200).json(result);
    } catch(error) {next(error)}
}

// Controller thêm khuyến mãi mới
const addDiscountController = async (req, res, next) => {
    try {
        const discountData = req.body;
        const result = await adminService.addDiscountService(discountData);
        res.status(200).json(result);
    } catch(error) {next(error)}
}

// Controller cập nhật khuyến mãi
const updateDiscountController = async (req, res, next) => {
    try {
        const discountId = req.params.discountId;
        const updateData = req.body;
        const result = await adminService.updateDiscountService(discountId, updateData);
        res.status(200).json(result);
    } catch(error) {next(error)}
}

// Controller xóa khuyến mãi
const deleteDiscountController = async (req, res, next) => {
    try {
        const discountId = req.params.discountId;
        const result = await adminService.deleteDiscountService(discountId);
        res.status(200).json(result);
    } catch(error) {next(error)}
}

module.exports = {
    adminLoginController, 
    adminRegisterController,
    getProductsController,
    addProductController,
    updateProductController,
    deleteProductController,
    getCustomersController,
    addCustomerController,
    updateCustomerController,
    getChatHistoryController,
    getAllChatsController,
    sendMessageController,
    getOrdersController,
    updateOrderController,
    getOrderDetailController,
    getDiscountsController,
    addDiscountController,
    updateDiscountController,
    deleteDiscountController,
};