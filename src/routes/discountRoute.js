const express = require("express");
const router = express.Router();
const discountController = require("../controllers/discountController");
const authMiddleware = require("../middlewares/authMiddleware");
const { verifyToken, verifyAdmin } = require("../middlewares/adminMiddleware");

router.post("/create", verifyToken, verifyAdmin, discountController.createDiscount);

router.get("/detail/:id", authMiddleware, discountController.getDiscountDetail);

router.get("/", authMiddleware, discountController.getDiscountList);

router.delete("/delete/:id", authMiddleware, discountController.deleteDiscount);

router.put("/update/:id", authMiddleware, discountController.updateDiscount);

module.exports = router;