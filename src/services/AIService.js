const AI_API = require("../utils/AIAPI");
const { default: ApiError } = require("../utils/ApiError");

// ------------------- Gọi FastAPI lấy recommendation -------------------
const getRecommendationAI = async (userItemMatrix) => {
  try {
    console.log("Gửi request đến:", AI_API.defaults.baseURL + "/recommend");

    const response = await AI_API.post("/recommend", { user_vector: userItemMatrix });

    if (!response?.data?.top_products) {
      throw new ApiError(500, "Không nhận được dữ liệu từ AI API");
    }

    return response.data.top_products;
  } catch (err) {
    console.error("AIService lỗi:", err.message);
    throw new ApiError(err.response?.status || 500, err.message || "Lỗi khi gọi AI API");
  }
};
// ------------------------ GỢI Ý CART-----------------------------------------------------

const getRecommendationCartAI = async (cartProductIds) => {
    try {
      const response = await AI_API.post("/recommend_cart", { cart_product_ids: cartProductIds });
  
      if (!response?.data?.top_products) {
        throw new ApiError(500, "Không nhận được dữ liệu từ AI API");
      }
  
      return response.data.top_products;
    } catch (err) {
      console.error("AIService cart lỗi:", err.message);
      throw new ApiError(err.response?.status || 500, err.message || "Lỗi khi gọi AI API");
    }
  };

// ------------------- Gọi FastAPI retrain model (nền) -------------------
const retrainModelAI = async () => {
  try {
    const response = await AI_API.post("/retrain", {});
    return response.data;
  } catch (err) {
    console.error("AIService retrain lỗi:", err.message);
    throw new ApiError(err.response?.status || 500, err.message || "Lỗi khi gọi retrain AI");
  }
};

module.exports = {
  getRecommendationAI,
  retrainModelAI, 
  getRecommendationCartAI,
};
