const express = require("express");
const router = express.Router();
const authMiddleware = require("../middlewares/authMiddleware");
const {getAllProductController, getProductsByCategoryController, searchProductsController, getProductDetailController,filterProducts} = require("../controllers/productController");

// GET /api/products

// Lọc sản phẩm
router.post('/filter', filterProducts);

// Lấy tất cả sản phẩm
router.get("/", getAllProductController);

// Lấy sản phẩm theo danh mục 
router.get("/category/:categoryName", getProductsByCategoryController);

// Tìm kiếm sản phẩm 
router.get("/search", searchProductsController);

// Lấy chi tiết thông tin sản phẩm 
router.get("/:productId", getProductDetailController)



module.exports = router;