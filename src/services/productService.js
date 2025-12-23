const Product = require("../models/productModel");
const Rating = require("../models/ratingModel");
const mongoose = require("mongoose");


const { default: ApiError } = require("../utils/ApiError");

// API Lấy danh sách sản phẩm 

const getAllProductService = async() => {
    // Tạo đối tượng sản phẩm 
    const products = await Product.find(); // lấy tất cả sản phẩm trong csdl

    //  if (!products || products.length === 0) {
    //     throw new ApiError(404, "Không tìm thấy sản phẩm");
    // }

    return products;

};

// API lấy danh sách sản phẩm theo danh mục: categoryName: tên loại sản phẩm
const getProductsByCategoryService = async (categoryName) => {
    const products = await Product.find({category: categoryName});

    // if (!products || products.length === 0) {
    //     throw new ApiError (404, "Không tìm thấy sản phẩm");
    // }

    return products;
}

// API tìm kiếm sản phẩm

const searchProductsService = async (keyword) => {
    const regex = new RegExp(keyword, "i"); // "i" = không phân biệt hoa thường
    const products = await Product.find({
      $or: [
        { name: regex },
        { category: regex }
      ],
    });

    return products;

    //if (!products || products.length === 0) throw new ApiError (404, "Không tìm thấy sản phẩm")
}  

const getProductDetailService = async (productId) => {
  // Lấy sản phẩm theo _id (MongoId)
  const product = await Product.findById(productId);

  if (!product) {
    throw new ApiError(404, "Không tìm thấy sản phẩm");
  }

  // Lấy danh sách rating của sản phẩm đó

  const ratings = await Rating.find({
    productId: new mongoose.Types.ObjectId(productId)
  })
  .populate("userId", "HoTen")   
  .sort({ createdAt: -1 });

  // Tính trung bình rating
  const avgRating =
    ratings.length > 0
      ? ratings.reduce((sum, r) => sum + r.stars, 0) / ratings.length
      : 0;

  // Có thể cập nhật lại vào sản phẩm (nếu muốn lưu lại trung bình)
  product.rating_avg = avgRating;
  await product.save();

  // Trả về dữ liệu kết hợp
  return {
    ...product.toObject(),
    ratings,
    avgRating,
  };
}

// API lọc sản phẩm 
const filterProducts = async (filters) => {
  const { priceRange, rating, keyword } = filters;

  const query = {};

  if (priceRange && priceRange.length > 0) {
    const priceConditions = priceRange.map(range => {
      const [min, max] = range.split('-').map(Number);

      return {
        price: {
          $gte: min * 1000,
          $lte: max * 1000,
        }
      };
    });

    query.$or = priceConditions; 
  }

  if (rating) {
    query.rating_avg = { $gte: Number(rating) };
  }

  if (keyword) {
    const regex = new RegExp(keyword, 'i'); 

    query.$and = [
      query.$or ? { $or: query.$or } : {}, 
      {
        $or: [
          { name: regex },
          { category: regex }
        ]
      }
    ];

    delete query.$or;  
  }

  console.log('[FilterService] Query:', JSON.stringify(query, null, 2));

  const products = await Product.find(query);
  console.log(`[FilterService] Found ${products.length} products`);

  return products;
};

// Lấy sản phẩm bán nhiều nhất
const getTopSellingProductsService = async () => {
    const products = await Product.find()
    .sort({sold_count: -1 }) // Sắp xếp giảm dần theo số lượng đã bán
    .limit(4); // Giới hạn lấy về 10 sản phẩm
    return products;
}

// Lấy sản phẩm mới nhất
const getNewArrivalProductsService = async () => {  
    const products = await Product.find()
    .sort({ createdAt: -1 }) // Sắp xếp giảm dần theo ngày tạo
    .limit(4);
    return products;
}






module.exports = {
    getAllProductService,
    getProductsByCategoryService,
    searchProductsService,
    getProductDetailService,
    filterProducts, 
    getTopSellingProductsService,
    getNewArrivalProductsService,
};



