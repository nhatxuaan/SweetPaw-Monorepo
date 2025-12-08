const express = require("express");
const router = express.Router();
const { getCartByUserController, addToCartController, updateCartQuantityController, updateCartCheckController, updateCartCheckAllController, removeCheckedItemsController, removeSingleItemController, removeSelectedItemsController } = require("../controllers/cartController");
const authMiddleware = require("../middlewares/authMiddleware");

//  Lấy giỏ hàng của 1 user cụ thể
// GET /api/cart/:userId
router.get("/:userId",authMiddleware, getCartByUserController);

// POST /api/cart/add
router.post("/add", authMiddleware, addToCartController);

// PUT /api/cart/quantity
router.put("/quantity", authMiddleware, updateCartQuantityController);
// PUT /api/cart/check
router.put("/check", authMiddleware, updateCartCheckController);
// PUT /api/cart/check-all
router.put("/check-all", authMiddleware, updateCartCheckAllController)
// DELETE /api/cart/remove
router.delete("/remove", authMiddleware, removeSelectedItemsController)
// DELETE /api/cart/clear/690714d2deb7368225b8caea
router.delete("/clear/:productId", authMiddleware, removeSingleItemController)
module.exports = router;