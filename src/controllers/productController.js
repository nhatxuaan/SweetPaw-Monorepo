const productService = require("../services/productService")

// controller lấy full sản phẩm 

const getAllProductController = async (req, res, next) => {
    try {
        const products = await productService.getAllProductService();

        // Nếu không tìm thấy sản phẩm
        if (!products || products.length === 0) {
            return res.status(404).json({
                message: "Không tìm thấy sản phẩm nào phù hợp"
            });
        }

        res.status(200).json({
            message: "Lấy danh sách sản phẩm thành công",
            data: products,
    });
    }
    catch(error) {next(error)}

};

const getProductsByCategoryController = async (req, res, next) => {
    try {
        // Lấy tên loại sản phẩm từ body
        let categoryName = decodeURIComponent(req.params.categoryName).trim();
        const products = await productService.getProductsByCategoryService(categoryName)

        // Nếu không tìm thấy sản phẩm
        if (!products || products.length === 0) {
            return res.status(404).json({
                message: "Không tìm thấy sản phẩm nào phù hợp"
            });
        }

        res.status(200).json ({
            message: "Lấy danh sách sản phẩm thành công", 
            data: products,
        });
    } catch (error) {next(error)}
}

const searchProductsController = async (req, res, next) => {
    try {
        //laays keyword từ query (q?) 
        const keyword = req.query.q?.trim();

        // Nếu không có keyword, trả về lỗi
        if (!keyword) {
            return res.status(400).json({
                message: "Vui lòng nhập từ khóa tìm kiếm"
            });
        }

        // Gọi service để tìm kiếm
        const products = await productService.searchProductsService(keyword);

        // Nếu không tìm thấy sản phẩm
        if (!products || products.length === 0) {
            return res.status(404).json({
                message: "Không tìm thấy sản phẩm nào phù hợp"
            });
        }

        // Trả về kết quả
        res.status(200).json({
            message: "Tìm kiếm sản phẩm thành công",
            data: products
        });


        
    } catch (error) {next (error)}
}

const getProductDetailController = async (req, res, next) => {
    try {
        const {productId} = req.params 
        const product = await productService.getProductDetailService(productId);

        res.status(200).json({
        message: "Lấy chi tiết sản phẩm thành công",
        data: product,
    });

    } catch (error) {next(error)}
}

// Controller lọc sản phẩm
const filterProducts = async (req, res, next) => {
  try {
    const { priceRange, rating, keyword } = req.body;

    console.log('Toàn bộ body FE gửi lên:', req.body);
    console.log('Mức giá được chọn:', priceRange);
    console.log('Đánh giá tối thiểu:', rating);
    console.log('Từ khóa tìm kiếm:', keyword);


    const filters = { priceRange, rating, keyword };

    const products = await productService.filterProducts(filters);

    console.log(`[Controller] Tìm thấy ${products.length} sản phẩm phù hợp`);
    
    res.status(200).json({
      success: true,
      message: 'Lọc sản phẩm thành công',
      data: products
    });

  } catch (error) {
    next(error);
  }
};



module.exports = {
    getAllProductController,
    getProductsByCategoryController,
    searchProductsController,
    getProductDetailController,
    filterProducts
};