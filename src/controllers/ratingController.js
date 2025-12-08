const ratingService = require("../services/ratingService");


const getRatingsController = async (req, res, next) => {
  try {
    // Lấy filter từ query hoặc body
    const { orderId, productId} = req.query; // hoặc req.body

    const userId = req.user.id

    const ratings = await ratingService.getRatingsByFilter({ orderId, productId, userId });

    return res.status(200).json({
      status: 200,
      message: "Lấy danh sách đánh giá thành công",
      data: ratings,
    });
  } catch (error) {
    next(error);
  }
};


const createRatingController = async (req, res, next) => {
  try {
    const { productId, orderId, stars, comment } = req.body;

    const userId = req.user.id

    if (!userId) {
      return res.status(401).json({
        status: 401,
        message: "Bạn cần đăng nhập để đánh giá sản phẩm",
        data: null,
      });
    }

    const result = await ratingService.createRatingService({
        productId,
        userId,
        orderId,
        stars,
        comment,
    });

    return res.status(200).json({
        status: result.success ? 200 : 400,
        message: result.message,
        data: result.data,
    });

  } catch (error) {
    next(error);
  }
};

const updateRatingController = async (req, res, next) => {
  try {
    const { ratingId, stars, comment } = req.body;

    const userId = req.user.id;
    if (!userId) {
      return res.status(401).json({
        status: 401,
        message: "Bạn cần đăng nhập để cập nhật đánh giá",
        data: null,
      });
    }

    const result = await ratingService.updateRatingService({
      ratingId,
      userId,
      stars,
      comment,
    });

    return res.status(result.success ? 200 : 400).json({
      status: result.success ? 200 : 400,
      message: result.message,
      data: result.data,
    });
    
  } catch (error) {
    next(error);
  }
};

const deleteRatingController = async (req, res, next) => {
  try {

    const { ratingId } = req.body;

    const userId = req.user.id;
    if (!userId) {
      return res.status(401).json({
        status: 401,
        message: "Bạn cần đăng nhập để xóa đánh giá",
        data: null,
      });
    }

    const result = await ratingService.deleteRatingService({ ratingId, userId });

    return res.status(result.success ? 200 : 400).json({
      status: result.success ? 200 : 400,
      message: result.message,
      data: result.data,
    });

  } catch (error) {
    next(error);
  }
};


module.exports = {
    createRatingController,
    updateRatingController,
    deleteRatingController,
    getRatingsController,
};
