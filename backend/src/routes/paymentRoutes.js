const express = require("express");
const {createPaymentController, handleCassoWebhookController, getPaymentStatusController} = require("../controllers/paymentController")
const authMiddleware = require("../middlewares/authMiddleware");
const { get } = require("../config/mail");
const router = express.Router();


router.post("/create-payment/:orderId", authMiddleware, createPaymentController);

router.post("/webhook", handleCassoWebhookController);

router.get("/status/:orderId", authMiddleware, getPaymentStatusController);

module.exports = router;
