const cartService = require("../services/cartService");

const getCartByUserController = async(req, res, next) => {
    try {
         // userId có thể lấy từ token hoặc params
        const userId = req.params.userId || req.user?._id;

        const result = await cartService.getCartByUserService(userId);

        return res.status(200).json({
        status: 200,
        message: "Lấy giỏ hàng thành công",
        data: result,
        });
    } catch(error) {next(error)}
}

const addToCartController = async (req, res, next) => {
  try {
    const {productId, quantity } = req.body;
     const userId = req.user.id;

    if (!userId || !productId) {
      return res.status(400).json({
        status: 400,
        message: "Thiếu userId hoặc productId",
      });
    }

    const result = await cartService.addToCartService(userId, productId, quantity);

    res.status(200).json({
      status: 200,
      message: "Thêm sản phẩm vào giỏ hàng thành công",
      data: result,
    });
  } catch (error) {
    next(error);
  }
};

const updateCartQuantityController = async (req, res, next) => {
    try {
        const userId = req.user.id; // từ middleware auth
        const { productId, change } = req.body; // change = +1 hoặc -1

        const cart = await cartService.updateCartQuantityService(userId, productId, change);

        res.status(200).json({
            status: 200,
            message: "Cập nhật số lượng thành công",
            data: cart,
        });
    } catch(error) {next(error)}
}

const updateCartCheckController = async (req, res, next) => {
  try {
    const userId = req.user.id;
    const { productId, checked } = req.body;

    const cart = await cartService.updateCartCheckService(userId, productId, checked);

    res.status(200).json({
        status: 200,
        message: checked
        ? "Đã chọn sản phẩm để thanh toán"
        : "Đã bỏ chọn sản phẩm",
        data: cart,
    });
  } catch(error) {next(error)}
}

const updateCartCheckAllController = async (req, res, next) => {
  const userId = req.user.id;
  const { checked } = req.body;

  const cart = await cartService.updateCartCheckAllService(userId, checked);

  res.status(200).json({
    status: 200,
    message: checked
      ? "Đã chọn tất cả sản phẩm trong giỏ hàng"
      : "Đã bỏ chọn tất cả sản phẩm trong giỏ hàng",
    data: cart,
  });
};

const removeSelectedItemsController = async (req, res, next) => {
  try {
    const userId = req.user.id; // lấy từ middleware auth
    const { selectedItems } = req.body; // FE gửi danh sách productId cần xóa

    const result = await cartService.removeSelectedItemsService(userId, selectedItems);

    res.status(200).json({
      status: 200,
      message: "Đã xóa các sản phẩm được chọn khỏi giỏ hàng",
      data: result.cart,
    });
  } catch (error) {
    next(error);
  }
};

const removeSingleItemController = async (req, res, next) => {
  try {
    console.log(req.body)
    const userId = req.user.id;
    const { productId } = req.params;

    const cart = await cartService.removeSingleItemService(userId, productId);

    res.status(200).json({
        status: 200,
        message: "Đã xóa sản phẩm khỏi giỏ hàng",
        data: cart,
    });
  } catch(error) {next(error)}
}


module.exports = {
    getCartByUserController,
    addToCartController,
    updateCartQuantityController,
    updateCartCheckController,
    updateCartCheckAllController,
    removeSelectedItemsController,
    removeSingleItemController,

}