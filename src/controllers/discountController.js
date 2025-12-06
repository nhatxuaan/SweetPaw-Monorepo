const { createDiscountService } = require("../services/discountService");
const { getDiscountDetailService } = require("../services/discountService");
const { getDiscountListService } = require("../services/discountService");
const { deleteDiscountService } = require("../services/discountService");
const { updateDiscountService } = require("../services/discountService");   
const ApiError = require("../utils/ApiError");

const createDiscount = async (req, res, next) => {
    try {
        const adminId = req.body.adminId || req.user.id; // Lấy từ middleware auth admin

        const result = await createDiscountService({
            ...req.body,
            adminId,
        });

        return res.status(result.status).json(result);
    } catch (error) {
        next(error);
    }
};

const getDiscountDetail = async (req, res, next) => {
    try {
        const discountId = req.params.id;
        const callerId = req.body.callerId || req.user.id || req.admin.id; 

        if (!callerId) {
            throw new ApiError(400, "callerId không được để trống");
        }

        const discount = await getDiscountDetailService({ discountId, callerId });

        return res.status(200).json({
            status: "success",
            data: discount
        });
    } catch (error) {
        next(error);
    }
};

const getDiscountList = async (req, res, next) => {
    try {
        const callerId = req.body.callerId || req.user.id || req.admin.id;

        if (!callerId) {
            throw new ApiError(400, "callerId không được để trống");
        }

        const discounts = await getDiscountListService({ callerId });

        return res.status(200).json({
            status: "success",
            data: discounts
        });
    } catch (error) {
        next(error);
    }
};

const deleteDiscount = async (req, res, next) => {
    try {
        const discountId = req.params.id;
        const adminId = req.body.adminId || req.admin.id; // lấy từ middleware auth admin

        const result = await deleteDiscountService({ discountId, adminId });

        return res.status(result.status).json(result);
    } catch (error) {
        next(error);
    }
};

const updateDiscount = async (req, res, next) => {
    try {
        const discountId = req.params.id;
        const adminId = req.body.adminId || req.admin.id; 
        const updateData = req.body;

        const result = await updateDiscountService({ discountId, adminId, updateData });

        return res.status(result.status).json(result);
    } catch (error) {
        next(error);
    }
};


module.exports = { 
    createDiscount,
    getDiscountDetail,
    getDiscountList,
    deleteDiscount,
    updateDiscount
};
