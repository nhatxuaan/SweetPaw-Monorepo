const Rating = require("../models/ratingModel");
const Order = require("../models/orderModel");
const User = require("../models/userModel");

const getRatingsByFilter = async ({ orderId, productId, userId }) => {
    // Build query object
    const query = {};
    if (orderId) query.orderId = orderId;
    if (productId) query.productId = productId;
    if (userId) query.userId = userId;

    // Lấy rating + populate tên khách hàng
    const ratings = await Rating.find(query)
        .populate({ path: "userId", select: "HoTen -_id" })
        .sort({ createdAt: -1 });

    const formattedRatings = ratings.map(r => ({
        ratingId: r._id,
        orderId: r.orderId,
        productId: r.productId,
        comment: r.comment,
        stars: r.stars,
        createdAt: r.createdAt
    }));

    return formattedRatings;
};

const createRatingService = async ({ productId, userId, orderId, stars, comment }) => {
    // Kiểm tra đầu vào
    if (!productId || !userId || !orderId || stars== null) {
      return {
        success: false,
        message: "Thiếu dữ liệu bắt buộc",
        data: null,
      };
    }

    if (stars < 1 || stars > 5) {
      return {
        success: false,
        message: "Số sao phải từ 1 đến 5",
        data: null,
      };
    }

    // Kiểm tra đơn hàng có tồn tại và thuộc user
    const order = await Order.findOne({ _id: orderId, user: userId }).lean();
    if (!order) {
        return {
        success: false,
        message: "Đơn hàng không tồn tại hoặc không thuộc người dùng",
        data: null,
        };
    }

    // Kiểm tra productId có trong items của đơn hàng không
    const productInOrder = order.items.some(item => item.productId.toString() === productId);
    if (!productInOrder) {
        return {
        success: false,
        message: "Sản phẩm không có trong đơn hàng này",
        data: null,
        };
    }

    // Tạo rating mới
    const newRating = new Rating({
      productId,
      userId,
      orderId,
      stars,
      comment: comment || "",
    });

    // Lưu vào DB
    const savedRating = await newRating.save();

    console.log("Saved Rating:", savedRating);

    return {
      success: true,
      message: "Đánh giá sản phẩm thành công",
      data: savedRating,
    };

};


const updateRatingService = async ({ ratingId, userId, stars, comment }) => {
  // Kiểm tra rating tồn tại
  const rating = await Rating.findById(ratingId);
  if (!rating) {
    return {
      success: false,
      message: "Rating không tồn tại",
      data: null,
    };
  }

  // Kiểm tra quyền sửa
  if (rating.userId.toString() !== userId.toString()) {
    return {
      success: false,
      message: "Bạn không có quyền sửa đánh giá này",
      data: null,
    };
  }

  //  Kiểm tra stars hợp lệ
  if (stars != null && (stars < 1 || stars > 5)) {
    return {
      success: false,
      message: "Số sao phải từ 1 đến 5",
      data: null,
    };
  }

  //Cập nhật rating 
  rating.stars = stars ?? rating.stars;
  rating.comment = comment ?? rating.comment;

  const updatedRating = await rating.save();

  return {
    success: true,
    message: "Cập nhật đánh giá thành công",
    data: updatedRating,
  };
};


const deleteRatingService = async ({ ratingId, userId }) => {
  // Kiểm tra rating tồn tại
  const rating = await Rating.findById(ratingId);
  if (!rating) {
    return {
      success: false,
      message: "Rating không tồn tại",
      data: null,
    };
  }

  // Kiểm tra quyền xóa
  if (rating.userId.toString() !== userId.toString()) {
    return {
      success: false,
      message: "Bạn không có quyền xóa đánh giá này",
      data: null,
    };
  }

  // Xóa rating
  await Rating.deleteOne({ _id: ratingId });

  return {
    success: true,
    message: "Xóa đánh giá thành công",
    data: null,
  };
};


module.exports = {
    createRatingService,
    updateRatingService,
    deleteRatingService,
    getRatingsByFilter,
};




