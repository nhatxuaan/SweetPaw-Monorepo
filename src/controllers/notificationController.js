const User = require("../models/userModel");
const Notification = require("../models/notificationModel");
const { sendToToken, sendToTopic, getNotificationsService, MarkNotificationService, getUnreadNotificationsService  } = require("../services/notificationService");

// Gửi thông báo cho 1 user (FCM token riêng) và lưu vào DB
const sendToTokenController = async (req, res, next) => {
  try {
    const userId = req.user?.id || req.body.userId;
    if (!userId) return res.status(400).json({ success: false, error: "Thiếu userId" });

    const user = await User.findById(userId);
    if (!user) return res.status(404).json({ success: false, error: "Người dùng không tồn tại" });
    if (!user.fcmToken) return res.status(400).json({ success: false, error: "Người dùng chưa có FCM token" });

    const { title, body, data } = req.body;
    if (!title || !body) return res.status(400).json({ success: false, error: "Vui lòng nhập tiêu đề và nội dung thông báo" });

    const result = await sendToToken({
      fcmToken: user.fcmToken,
      title,
      body,
      data,
      userId: user._id
    });

    return res.status(200).json({
      success: true,
      message: "Gửi thông báo đến người dùng thành công",
      firebaseMessageId: result.messageId
    });

  } catch (error) {
    next(error);
  }
};

// Gửi thông báo theo topic, lưu 1 notification cho mỗi user
const sendToTopicController = async (req, res, next) => {
  try {
    const { topic, title, body, data } = req.body;
    if (!topic || !title || !body) return res.status(400).json({ success: false, error: "Vui lòng nhập topic, tiêu đề và nội dung thông báo" });

    const result = await sendToTopic({ topic, title, body, data });

    return res.status(200).json({
      success: true,
      message: `Gửi thông báo theo topic thành công, số người nhận: ${result.usersNotified}`,
      firebaseMessageId: result.messageId
    });

  } catch (error) {
    next(error);
  }
};

// Lấy tất cả notification của user
const getNotificationsController = async (req, res, next) => {
  try {
    const userId = req.user?.id;
    const result = await getNotificationsService({ userId });

    return res.status(200).json({ success: true, ...result });
  } catch (error) {
    next(error);
  }
};

// Lấy 1 notification theo ID và đánh dấu là đã đọc
const MarkNotificationController = async (req, res, next) => {
  try {
    const userId = req.user?.id;
    const { notificationId } = req.params;

    if (!notificationId) {
      return res.status(400).json({ success: false, error: "Thiếu notificationId" });
    }

    const notification = await MarkNotificationService({ userId, notificationId });

    return res.status(200).json({
      success: true,
      message: "Đã đánh dấu thông báo là đã đọc",
      notification
    });
  } catch (error) {
    return res.status(404).json({ success: false, error: error.message });
  }
};

// Lấy tất cả notification chưa đọc của user
const getUnreadNotificationsController = async (req, res, next) => {
  try {
    const userId = req.user?.id;
    if (!userId) return res.status(400).json({ success: false, error: "Thiếu userId" });

    const unreadNotifications = await getUnreadNotificationsService({ userId });

    return res.status(200).json({
      success: true,
      totalUnread: unreadNotifications.length,
      notifications: unreadNotifications
    });
  } catch (error) {
    next(error);
  }
};


module.exports = { 
    sendToTokenController,
    sendToTopicController,
    getNotificationsController,
    MarkNotificationController,
    getUnreadNotificationsController
};