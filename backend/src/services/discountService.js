const Discount = require("../models/discountModel");
const Admin = require("../models/adminModel"); 
const User = require("../models/userModel");
const ApiError = require("../utils/ApiError"); 
const { sendToTopic } = require("./notificationService");
const mongoose = require("mongoose");


// SERVICE: ADMIN TẠO KHUYẾN MÃI
const createDiscountService = async ({
    code,
    name,
    description,
    type,
    value,
    maxDiscount,
    minOrderValue,
    startDate,
    endDate,
    quantity,
    adminId,
}) => {
    // Validate ngày
    if (new Date(startDate) >= new Date(endDate)) {
        return {
            status: 400,
            message: "Ngày bắt đầu phải nhỏ hơn ngày kết thúc",
        };
    }

    const admin = await Admin.findById(adminId).lean();
    if (!admin) throw new ApiError(404,"không phải Admin không thể tạo khuyến mãi");

    // Kiểm tra trùng mã giảm giá
    const existed = await Discount.findOne({ code: code.toUpperCase() });
    if (existed) {
        return {
            status: 400,
            message: "Mã giảm giá đã tồn tại",
        };
    }

    if (!mongoose.Types.ObjectId.isValid(adminId)) {
    return { status: 400, message: "AdminId không hợp lệ" };
    }

    // Convert adminId sang ObjectId
    const createdByObjectId = new mongoose.Types.ObjectId(adminId);

    const discount = await Discount.create({
        code,
        name,
        description,
        type,
        value,
        maxDiscount,
        minOrderValue,
        startDate,
        endDate,
        quantity,
        createdBy: createdByObjectId, 
    });

    await sendToTopic({
        topic: "allUsers",
        title: `Mã giảm giá mới: ${discount.code}`,
        body: `${name} - ${description}`,
        data: { discountId: discount._id.toString(), type: "DISCOUNT" }
    });

    return {
        status: 201,
        message: "Tạo mã khuyến mãi thành công",
        data: discount,
    };
};


const getDiscountDetailService = async ({ discountId, callerId }) => {
    if (!mongoose.Types.ObjectId.isValid(discountId)) {
        throw new ApiError(400, "DiscountId không hợp lệ");
    }
    if (!mongoose.Types.ObjectId.isValid(callerId)) {
        throw new ApiError(400, "callerId không hợp lệ");
    }

    // Dò xem callerId là admin hay user
    let discount;
    if (await Admin.findById(callerId).lean()) {
        discount = await Discount.findById(discountId).lean();
    } 
    else if (await User.findById(callerId).lean()) {
       const now = new Date();
        discount = await Discount.findOne({
            _id: discountId,
            isActive: true,
            endDate: { $gte: now }
        }).lean();
    }

    if (!discount) {
        throw new ApiError(404, "Khuyến mãi không tồn tại hoặc đã hết hạn");
    }

    return discount;
};


const getDiscountListService = async ({ callerId }) => {
    // callerId bắt buộc
    if (!callerId || !mongoose.Types.ObjectId.isValid(callerId)) {
        throw new ApiError(400, "callerId không hợp lệ");
    }

    // Dò adminId trong collection Admin
    const admin = await Admin.findById(callerId).lean();
    const isAdmin = !!admin;

    let query = {};

    if (!isAdmin) {
        query.isActive = true;
        query.endDate = { $gte: new Date() };
    }

    const discounts = await Discount.find(query).sort({ startDate: -1 }).lean();

    return discounts;
};

const deleteDiscountService = async ({ discountId, adminId }) => {
    // Validate ObjectId
    if (!mongoose.Types.ObjectId.isValid(discountId)) {
        throw new ApiError(400, "discountId không hợp lệ");
    }
    if (!mongoose.Types.ObjectId.isValid(adminId)) {
        throw new ApiError(400, "adminId không hợp lệ");
    }

    // Kiểm tra admin
    const admin = await Admin.findById(adminId).lean();
    if (!admin) {
        throw new ApiError(403, "Chỉ admin mới được xóa khuyến mãi");
    }

    // Kiểm tra khuyến mãi tồn tại
    const discount = await Discount.findById(discountId);
    if (!discount) {
        throw new ApiError(404, "Khuyến mãi không tồn tại");
    }

    await Discount.deleteOne({ _id: discountId });

    await sendToTopic({
        topic: "allUsers",
        title: `Khuyến mãi đã kết thúc: ${discount.code}`,
        body: `${discount.name} đã kết thúc.`,
        data: { discountId: discount._id.toString(), type: "DISCOUNT_DELETE" }
    });

    return {
        status: 200,
        message: "Xóa khuyến mãi thành công",
    };
};

const updateDiscountService = async ({ discountId, adminId, updateData }) => {
    // Validate ObjectId
    if (!mongoose.Types.ObjectId.isValid(discountId)) {
        throw new ApiError(400, "discountId không hợp lệ");
    }
    if (!mongoose.Types.ObjectId.isValid(adminId)) {
        throw new ApiError(400, "adminId không hợp lệ");
    }

    // Kiểm tra admin
    const admin = await Admin.findById(adminId).lean();
    if (!admin) {
        throw new ApiError(403, "Chỉ admin mới được chỉnh sửa khuyến mãi");
    }

    // Kiểm tra khuyến mãi tồn tại
    const discount = await Discount.findById(discountId);
    if (!discount) {
        throw new ApiError(404, "Khuyến mãi không tồn tại");
    }

    // Chỉ cho phép cập nhật một số trường
    const allowedFields = [
        "code",
        "name",
        "description",
        "type",
        "value",
        "maxDiscount",
        "minOrderValue",
        "startDate",
        "endDate",
        "quantity",
        "isActive"
    ];

    allowedFields.forEach(field => {
        if (updateData[field] !== undefined) {
            discount[field] = updateData[field];
        }
    });

    await discount.save();

    await sendToTopic({
        topic: "allUsers",
        title: `Khuyến mãi đã được cập nhật: ${discount.code}`,
        body: `${discount.name} - ${discount.description}`,
        data: { discountId: discount._id.toString(), type: "DISCOUNT_UPDATE" }
    });

    return {
        status: 200,
        message: "Cập nhật khuyến mãi thành công",
        data: discount
    };
};

module.exports = { 
    createDiscountService,
    getDiscountDetailService,
    getDiscountListService,
    deleteDiscountService,
    updateDiscountService
};
