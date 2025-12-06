const Favorite = require("../models/favoriteModels");

// Lấy tất cả sản phẩm yêu thích của user
const getFavoritesService = async ({ userId }) => {
  const favorite = await Favorite.findOne({ userId }).populate("products");

  if (!favorite || favorite.products.length === 0) {
    return { status: 200, message: "Chưa có sản phẩm yêu thích", data: [] };
  }

  return { status: 200, message: "Danh sách sản phẩm yêu thích", data: favorite.products };
};

// thêm và xóa sản phẩm yêu thích
const toggleFavoriteService = async ({ userId, productId }) => {
  let favorite = await Favorite.findOne({ userId });

  if (!favorite) {
    favorite = new Favorite({
      userId,
      products: [productId],
      updatedAt: Date.now()
    });

    await favorite.save();
    return {
      status: 200,
      message: "Đã thêm vào danh sách yêu thích",
      data: favorite
    };
  }

  const exists = favorite.products.some(
    p => p.toString() === productId.toString()
  );

  if (exists) {
    favorite.products = favorite.products.filter(
      p => p.toString() !== productId.toString()
    );
    favorite.updatedAt = Date.now();
    await favorite.save();

    return {
      status: 200,
      message: "Đã xóa khỏi danh sách yêu thích",
      data: favorite
    };
  } else {
    favorite.products.push(productId);
    favorite.updatedAt = Date.now();
    await favorite.save();

    return {
      status: 200,
      message: "Đã thêm vào danh sách yêu thích",
      data: favorite
    };
  }
};


module.exports = { 
  getFavoritesService,
  toggleFavoriteService
};
