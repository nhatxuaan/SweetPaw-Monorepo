const Admin = require("../models/adminModel");
const Product = require("../models/productModel");
const Order = require("../models/orderModel");
const User = require("../models/userModel");
const Cart = require("../models/cartModel");
const Chat = require("../models/chatModel")
const { sendToTopic } = require("./notificationService");
const Discount = require("../models/discountModel");
const { OAuth2Client } = require('google-auth-library');
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const client = new OAuth2Client(process.env.GOOGLE_CLIENT_ID);
const { default: ApiError } = require("../utils/ApiError");

// API đăng ký của admin 
const registerAdminService = async (username, email, password, displayName) => {
    // Kiểm tra email đã tồn tại chưa
    const existingAdmin = await Admin.findOne({ email: email, role: 'admin' });
    if (existingAdmin) {
        throw new ApiError(409,"Email đã được sử dụng");
    }
    // Tạo admin mới
    const newAdmin = new Admin({ username, email, password, displayName, role: 'admin' }); 
    await newAdmin.save(); // Tự động hash nhờ middleware pre('save') trong model
    return {
        Boolean: true,
        id: newAdmin._id,
        username: newAdmin.username,
        email: newAdmin.email,
        displayName: newAdmin.displayName,
        role: newAdmin.role
    };
}

// API đăng nhập của admin 
const adminLoginService = async(email, password) => {
    // Lấy truy vấn email có trong csdl k 
    const admin = await Admin.findOne({email: email, role: 'admin'});

    // Kiểm tra email
    if (!admin) throw new ApiError(404, "Không tìm thấy người dùng");
    // kiểm tra mật khẩu 
    const isMatch = await bcrypt.compare(password, admin.password);
    const hashedInput = await bcrypt.hash(password, 10);

    if (!isMatch) throw new ApiError(401, "Sai mật khẩu");
    const token = jwt.sign({ id: admin._id, role: admin.role }, process.env.JWT_SECRET,{ expiresIn: process.env.JWT_EXPIRES_IN });

    return {
        Boolean: true,
        token,
        admin: {
            id: admin._id,
            username: admin.username,
            password: admin.password,
            email: admin.email,
            displayName: admin.displayName,
            role: admin.role
        }
    };
}

// API lấy danh sách sản phẩm 
const getProductsService = async () => {
    // Logic để lấy danh sách sản phẩm từ cơ sở dữ liệu
    const products = await Product.find(); 
    return products;
}

// API thêm sản phẩm mới 
const addProductService = async (productData) => {
    if (!productData.name || !productData.price) {
        throw new ApiError(400, "Tên sản phẩm và giá là bắt buộc");
    }
    const newProduct = new Product(productData);
    await newProduct.save();
    return newProduct;
}

// API cập nhật sản phẩm
const updateProductService = async (productId, updateData) => {
    const product = await Product.findById(productId);
    if (!product) {
        throw new ApiError(404, "Sản phẩm không tồn tại");
    }
    Object.assign(product, updateData);
    await product.save();
    return product;
}

// API xóa sản phẩm, chỉ xóa những sản phẩm không có trong đơn hàng và xóa cả sản phẩm trong giỏ hàng 
const deleteProductService = async (productId) => {
    const product = await Product.findById(productId);
    if (!product) {
        throw new ApiError(404, "Sản phẩm không tồn tại");
    }

    // Kiểm tra sản phẩm có trong đơn hàng không
    const existingOrder = await Order.findOne({ "items.productId": productId });
    if (existingOrder) {
        throw new ApiError(400, "Không thể xóa sản phẩm vì nó có trong đơn hàng");
    }

    // Xóa sản phẩm trong giỏ hàng của tất cả khách hàng
    await Cart.updateMany(
        { "items.productId": productId },
        { $pull: { items: { productId: productId } } }
    );

    // Xóa sản phẩm khỏi cơ sở dữ liệu
    await Product.findByIdAndDelete(productId);
    return { message: "Xóa sản phẩm thành công" };
}

// API lấy danh sách tất cả khách hàng
const getCustomersService = async () => {
    const customers = await User.find();
    return customers;
}

// API thêm khách hàng mới 
const addCustomerService = async (customerData) => {
    if (!customerData.Hoten || !customerData.Email) {
        throw new ApiError(400, "Tên đăng nhập, email và mật khẩu là bắt buộc");
    }
    const existingUser = await User.findOne({ Email: customerData.Email });
    if (existingUser) {
        throw new ApiError(409, "Email đã được sử dụng");
    }
    // Khởi tạo mật khẩu mặc định là 123456 
    customerData.MatKhau = 123456;

    const newCustomer = new User(customerData);
    await newCustomer.save();
    return newCustomer;
}

// API cập nhật thông tin khách hàng 
const updateCustomerService = async (id, updateData) => {
    const customer = await User.findOne({_id: id}) 
    if (!customer) throw new ApiError (404, "Không tồn tại người dùng")

    Object.assign(customer, updateData);
    await customer.save();
    return customer;
}

// API lấy tất cả các đoạn chat 
const getAllChatsService = async (adminId) => {
    const admin = await Admin.findById(adminId);
    if (!admin) throw new ApiError(404, "Admin không tồn tại");
    const chats = await Chat.find({admin: adminId})
    .populate("user", "HoTen Email")
    .populate("admin", "username displayName")
    .populate("messages.sender", "HoTen Email username displayName")
    .sort({updatedAt: -1});
    return chats;
};

//API lấy lịch sử chat
const getChatHistoryService = async (userId, adminId) => {
    // Kiểm tra userId và adminId có tồn tại không
    if (!userId || !adminId) throw new ApiError(400, "Thiếu userId hoặc adminId");
    const user = await User.findById(userId);
    if (!user) throw new ApiError(404, "User không tồn tại");
    const admin = await Admin.findById(adminId);
    if (!admin) throw new ApiError(404, "Admin không tồn tại");

    // lấy tất cả đoạn chat giữa user và admin
    const chat = await Chat.find({user: userId, admin: adminId})
    .populate("user", "HoTen Email")
    .populate("admin", "username displayName")
    .populate("messages.sender", "HoTen Email username displayName")
    .sort({updatedAt: -1});

    if (!chat) throw new ApiError(404, "Không tồn tại đoạn chat");

    return chat;
};

// API gửi tin nhắn 
const sendMessageService = async (userId, adminId, senderId, content, media) => {
    if (
        senderId.toString() !== userId.toString() &&
        senderId.toString() !== adminId.toString()
    ) {
        throw new ApiError(400, "Người gửi không hợp lệ");
    }

    const [user, admin] = await Promise.all([
        User.findById(userId),
        Admin.findById(adminId)
    ]);
    if (!user) throw new ApiError(404, "User không tồn tại");
    if (!admin) throw new ApiError(404, "Admin không tồn tại");

    let chat = await Chat.findOne({ user: userId, admin: adminId });
    if (!chat) {
        chat = await Chat.create({
            user: userId,
            admin: adminId,
            messages: [],
            lastMessage: ""
        });
    }

    const senderModel = senderId.toString() === userId.toString() ? "User" : "Admin";

    const message = {
        sender: senderId,
        senderModel,
        content: content || "",
        media: media || [],
        timestamp: new Date(),
        readBy: [senderId] // sender mặc định đã đọc
    };

    const updatedChat = await Chat.findByIdAndUpdate(
        chat._id,
        {
            $push: { messages: message },
            $set: {
                lastMessage: content || "[Media]",
                updatedAt: new Date()
            }
        },
        { new: true }
    )
    .populate("messages.sender", "HoTen displayName")
    .lean();

    const sentMessage = updatedChat.messages.at(-1);

    return {
        chatId: updatedChat._id,
        message: {
            _id: sentMessage._id,
            sender: {
                _id: sentMessage.sender._id,
                name: sentMessage.sender.HoTen || sentMessage.sender.displayName
            },
            content: sentMessage.content,
            media: sentMessage.media,
            timestamp: sentMessage.timestamp,
            read: sentMessage.readBy.includes(senderId)
        }
    };
}

//API lấy đơn đặt hàng 
const getOrdersService = async () => {
    const orders = await Order.find();
    return orders;
}

//API cập nhật trạng thái đơn hàng
const updateOrderService = async (orderId, paymentStatus) => {
    const order = await Order.findById(orderId);
    if (!order) throw new ApiError(404, "Đơn hàng không tồn tại");

    // Kiem tra phuong thuc thanh toan
    if (order.paymentMethod === "Chuyển khoản") throw new ApiError(400, "Không thể cập nhật trạng thái thanh toán đơn hàng khi phương thức thanh toán là chuyển khoản");
    order.paymentStatus = paymentStatus;
    await order.save();
    return order;
}

//API xem chi tiết đơn hàng
const getOrderDetailService = async (orderId) => {
    const order = await Order.findById(orderId);
    if (!order) throw new ApiError(404, "Đơn hàng không tồn tại");
    return order;
}   

//API lấy danh sách khuyến mãi
const getDiscountsService = async () => {
    const discounts = await Discount.find();
    return discounts;
}

// API thêm khuyến mãi mới
const addDiscountService = async (discountData) => {
    const discount = new Discount(discountData);
    await discount.save();
    await sendToTopic({
            topic: "allUsers",
            title: `Mã giảm giá mới: ${discount.code}`,
            body: `${discount.name} - ${discount.description}`,
            data: { discountId: discount._id.toString(), type: "DISCOUNT" }
        });
    return discount;
}

// API cập nhật khuyến mãi
const updateDiscountService = async (discountId, updateData) => {
    const discount = await Discount.findById(discountId);
    if (!discount) throw new ApiError(404, "Khuyến mãi không tồn tại");
    Object.assign(discount, updateData);
    await discount.save();
    return discount;
}

// API xóa khuyến mãi
const deleteDiscountService = async (discountId) => {
    const discount = await Discount.findById(discountId);
    if (!discount) throw new ApiError(404, "Khuyến mãi không tồn tại");
    await discount.deleteOne();
    return { message: "Xóa khuyến mãi thành công" };
}

module.exports = {
    adminLoginService, 
    registerAdminService,
    getProductsService,
    addProductService,
    updateProductService,
    deleteProductService,
    getCustomersService,
    addCustomerService,
    updateCustomerService,
    getChatHistoryService,
    getAllChatsService,
    sendMessageService,
    getOrdersService,
    updateOrderService,
    getOrderDetailService,
    getDiscountsService,
    addDiscountService,
    updateDiscountService,
    deleteDiscountService,
    
};