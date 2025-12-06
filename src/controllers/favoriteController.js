const { toggleFavoriteService } = require("../services/favoriteService");
const { getFavoritesService } = require("../services/favoriteService");

const getFavorites = async (req, res, next) => {
  try {
    const userId = req.user.id; 

    const result = await getFavoritesService({ userId });
    res.status(result.status).json(result);
  } catch (error) {
    next(error);
  }
};

const toggleFavorite = async (req, res, next) => {
  try {
    const userId = req.user.id;
    const { productId } = req.body; 

    const result = await toggleFavoriteService({ userId, productId });
    res.status(result.status).json(result);
  } catch (error) {
    next(error);
  }
};


module.exports = {
  getFavorites,
  toggleFavorite
};
