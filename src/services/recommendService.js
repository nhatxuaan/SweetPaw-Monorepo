const axios = require ("axios")
const Product = require("../models/productModel")
const { getRecommendationAI } = require("./AIService");


// const getRecommendationHomeService  = async(userItemMatrix) => {
//     // const response = await axios.post("http://127.0.0.1:5000/predict", {
//     //     user_item_matrix
//     // });

//     // return response.data;

//     try {
//         // Gửi ma trận sang API Python
//         const response = await axios.post(
//             "http://127.0.0.1:8000/recommend",
//             { user_vector: userItemMatrix },
//         );
//         console.log(response.status, response.data);

//         const topProducts = response.data.top_products;
//         const productIds = topProducts.map(p => p.product_id);

//         const products = await Product.find({ _id: { $in: productIds } });

//         // Merge score vào sản phẩm (nếu muốn)
//         const productsWithScore = products.map(p => {
//         const score = topProducts.find(tp => tp.product_id === p._id.toString())?.score || 0;
//         return {
//             ...p.toObject(),
//             score
//         };
//         });

//         return {
//             success: true,
//             products
//         };

//     } catch (error) {
//         console.error("Lỗi Service:", error);
//         throw error;
//     }
// }

/**
 * Lấy recommendation cho trang chủ
 * @param {Array} userItemMatrix - user vector
 */
const getRecommendationHomeService = async (userItemMatrix) => {
  // 1. Gọi AIService
  const topProducts = await getRecommendationAI(userItemMatrix);

  const productIds = topProducts.map(p => p.product_id);

  // 2. Lấy sản phẩm từ DB
  const products = await Product.find({ _id: { $in: productIds } });

  // 3. Merge score vào sản phẩm
  const productsWithScore = products.map(p => {
    const score = topProducts.find(tp => tp.product_id === p._id.toString())?.score || 0;
    return { ...p.toObject(), score };
  });

  return {
    success: true,
    products: productsWithScore
  };
};




module.exports = {
    getRecommendationHomeService
}