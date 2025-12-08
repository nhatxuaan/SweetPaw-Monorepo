const express = require("express");
const router = express.Router();
const { sendToTokenController, sendToTopicController, getNotificationsController, MarkNotificationController, getUnreadNotificationsController } = require("../controllers/notificationController");
const authMiddleware = require("../middlewares/authMiddleware");

router.get("/", authMiddleware, getNotificationsController);

router.post("/token", authMiddleware, sendToTokenController);

router.post("/topic", authMiddleware, sendToTopicController);

router.post("/mark-as-read/:notificationId", authMiddleware, MarkNotificationController);

router.get("/unread", authMiddleware, getUnreadNotificationsController);

module.exports = router;
