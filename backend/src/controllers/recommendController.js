const {buildUserMatrix} = require("../utils/userItemMatrixHelper")
const recommendService = require("../services/recommendService")
const { default: ApiError } = require("../utils/ApiError");

const getRecommendationHomeController = async (req, res, next) => {
   try {
        const userId = req.user.id
        if (!userId) return res.status(400).json({ message: "Thiếu userId" });

        // Tạo ma trận người dùng 
        const userItemMatrix = await buildUserMatrix(userId)
        console.log("Vector người dùng: ", userItemMatrix)

        const result = await recommendService.getRecommendationHomeService(userItemMatrix);
        res.status(200).json(result)

   } catch(error) {next(error)}
}

const getRecommendationCartController = async (req, res, next) => {
    try {
      const { cartProductIds } = req.body;
  
      if (!cartProductIds || !Array.isArray(cartProductIds) || cartProductIds.length === 0) {
        return res.status(400).json({ message: "Thiếu danh sách sản phẩm trong giỏ hàng" });
      }
  
      const result = await recommendService.getRecommendationCartService(cartProductIds);
  
      res.status(200).json(result);
  
    } catch (error) {
      next(error);
    }
  };


module.exports = {
    getRecommendationHomeController,
    getRecommendationCartController
}