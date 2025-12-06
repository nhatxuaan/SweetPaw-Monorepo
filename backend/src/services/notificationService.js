const admin = require("../config/firebaseAdmin");
const User = require("../models/userModel");
const Notification = require("../models/notificationModel");

// Gửi thông báo cho 1 user dựa trên FCM token và lưu vào DB
const sendToToken = async ({ fcmToken, title, body, data = {}, userId }) => {
  if (!fcmToken) {
    return { success: false, error: "FCM Token is required" };
  }

  const message = {
    token: fcmToken,
    notification: {
    title: title,
    body: body
    },
    data: {
      title: title,
      body: body,
      ...data
    }
  };

  const response = await admin.messaging().send(message);
  console.log("Gửi token thành công:", response);

  const notif = {
    title,
    body,
    data,
    type: "TOKEN",
    isRead: false,
    createdAt: new Date(),
    expireAt: new Date(Date.now() + 30*24*60*60*1000) 
  };

  let userNotifications = await Notification.findOne({ userId });
  if (!userNotifications) {
    userNotifications = new Notification({
      userId,
      fcmToken,
      notifications: [notif]
    });
  } else {
    if (fcmToken && userNotifications.fcmToken !== fcmToken) {
      userNotifications.fcmToken = fcmToken;
    }
    userNotifications.notifications.push(notif);
  }

  await userNotifications.save();

  return { success: true, messageId: response };
};

// Gửi thông báo theo Topic, lưu 1 notification cho từng user
const sendToTopic = async ({ topic, title, body, data = {} }) => {

  console.log("Input:", { topic, title, body, data });

  if (!topic) {
    console.log("LỖI: thiếu topic!");
    return { success: false, error: "Topic is required" };
  }

  const message = {
    topic,
    notification: {
    title: title,
    body: body
    },
    data: {
      title: title,
      body: body,
      ...data
    }
  };

  console.log("FCM message tạo ra:", message);

  const response = await admin.messaging().send(message);
  console.log("Gửi topic thành công:", response);

  const users = await User.find({
    subscribedTopics: topic,
    fcmToken: { $exists: true, $ne: "" }
  });

  console.log(`Số user thuộc topic '${topic}':`, users.length);


  const now = new Date();
  const expireAt = new Date(now.getTime() + 30*24*60*60*1000);

  for (const u of users) {

    console.log(`Lưu notification cho user: ${u._id}`);

    const notif = {
      title,
      body,
      data,
      type: "TOPIC",
      isRead: false,
      createdAt: now,
      expireAt
    };

    let userNotifications = await Notification.findOne({ userId: u._id });
    if (!userNotifications) {
      console.log(" -> User chưa có document Notifications → tạo mới");
      userNotifications = new Notification({
        userId: u._id,
        fcmToken: u.fcmToken,
        notifications: [notif]
      });
    } else {
      console.log(" -> User đã có document Notifications → thêm vào mảng");
      if (u.fcmToken && userNotifications.fcmToken !== u.fcmToken) {
        userNotifications.fcmToken = u.fcmToken;
      }
      userNotifications.notifications.push(notif);
    }

    await userNotifications.save();

    console.log(" -> Lưu xong vào MongoDB");
  }

  return { success: true, messageId: response, usersNotified: users.length };
};

// Lấy tất cả notification của user
const getNotificationsService = async ({ userId }) => {
  const userNotifications = await Notification.findOne({ userId });

  if (!userNotifications) {
    return { notificationsByDate: {}, total: 0, unreadCount: 0 };
  }

  const now = new Date();
  const notifications = userNotifications.notifications.filter(
    (notif) => !notif.expireAt || notif.expireAt > now
  );

  // Sắp xếp theo createdAt giảm dần
  notifications.sort((a, b) => b.createdAt - a.createdAt);

  // Nhóm theo ngày
  const notificationsByDate = {};
  notifications.forEach((notif) => {
    // Lấy ngày theo định dạng YYYY-MM-DD
    const dateKey = notif.createdAt.toISOString().split("T")[0];
    if (!notificationsByDate[dateKey]) {
      notificationsByDate[dateKey] = [];
    }
    notificationsByDate[dateKey].push(notif);
  });

  const total = notifications.length;
  const unreadCount = notifications.filter((n) => !n.isRead).length;

  console.log("Tổng số thông báo:", total, "Chưa đọc:", unreadCount);

  return { notificationsByDate, total, unreadCount };
};


// đánh dấu đã đọc
const MarkNotificationService = async ({ userId, notificationId }) => {
  // Lấy document notification của user
  const userNotifications = await Notification.findOne({ userId });
  if (!userNotifications) throw new Error("Không tìm thấy thông báo");

  // Tìm notification theo _id trong mảng
  const notif = userNotifications.notifications.id(notificationId);
  if (!notif) throw new Error("Không tìm thấy thông báo");

  // Đánh dấu là đã đọc nếu chưa đọc
  if (!notif.isRead) {
    notif.isRead = true;
    await userNotifications.save();
  }
  console.log("Đã đánh dấu thông báo là đã đọc:", notif);

  return notif;
};

// Lấy tất cả notification chưa đọc của user
const getUnreadNotificationsService = async ({ userId }) => {
  const userNotifications = await Notification.findOne({ userId });

  if (!userNotifications) {
    return { notificationsByDate: {} };
  }

  const now = new Date();

  // Lọc các thông báo chưa đọc và chưa hết hạn
  const unreadNotifications = userNotifications.notifications.filter(
    (notif) => !notif.isRead && (!notif.expireAt || notif.expireAt > now)
  );

  // Sắp xếp theo createdAt giảm dần
  unreadNotifications.sort((a, b) => b.createdAt - a.createdAt);

  // Nhóm theo ngày
  const notificationsByDate = {};
  unreadNotifications.forEach((notif) => {
    const dateKey = notif.createdAt.toISOString().split("T")[0]; // YYYY-MM-DD
    if (!notificationsByDate[dateKey]) {
      notificationsByDate[dateKey] = [];
    }
    notificationsByDate[dateKey].push(notif);
  });

  console.log("Thông báo chưa đọc:", notificationsByDate);

  return { notificationsByDate };
};


module.exports = { 
    sendToToken,
    sendToTopic,
    getNotificationsService,
    MarkNotificationService,
    getUnreadNotificationsService
};


