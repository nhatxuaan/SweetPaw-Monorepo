const {default: ApiError } = require("./ApiError");
const Discount = require("../models/discountModel");

const calculateDiscountedPrice = async (discount_code, subtotal) => {
    let discountAmount = 0;
    const discount = await Discount.findOne({ code: discount_code, isActive: true });
    if (!discount) {
        throw new ApiError(404, "Mã giảm giá không hợp lệ");
    }
    if (discount.type === "percent") {
        // Kiểm tra điều kiện giảm giá 
        if (subtotal < discount.minOrderValue) {
            throw new ApiError(400, `Đơn hàng tối thiểu để áp dụng mã giảm giá này là ${discount.minOrderValue}`);
        }
        discountAmount = (discount.value / 100) * subtotal;

        // Xử lý trường hợp giảm giá vượt quá giá trị tối đa
        if (discount.maxDiscount && discountAmount > discount.maxDiscount) {
            discountAmount = discount.maxDiscount;
        }
    } else if (discount.type === "fixed") {
        // Kiểm tra điều kiện giảm giá 
        if (subtotal < discount.minOrderValue) {
            throw new ApiError(400, `Đơn hàng tối thiểu để áp dụng mã giảm giá này là ${discount.minOrderValue}`);
        }
        discountAmount = discount.value;

        // Xử lý trường hợp giảm giá vượt quá tổng giá trị đơn hàng
        if (discountAmount > subtotal) {
            discountAmount = subtotal;
        }

        if (discount.maxDiscount && discountAmount > discount.maxDiscount) {
            discountAmount = discount.maxDiscount;
        }
    }

    return discountAmount;
}

module.exports = {
    calculateDiscountedPrice,
};