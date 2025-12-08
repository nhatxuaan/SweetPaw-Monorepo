const mongoose = require("mongoose");


const notificationItemSchema = new mongoose.Schema(
  {
    title: { type: String, required: true },
    body: { type: String, required: true },
    data: { type: Object, default: {} },
    type: { type: String, enum: ["TOKEN", "TOPIC"], required: true },
    isRead: { type: Boolean, default: false },
    createdAt: { type: Date, default: Date.now },
    expireAt: { type: Date, default: () => new Date(Date.now() + 30*24*60*60*1000) } // 30 ngày sau
  },
  { _id: true }
);

const notificationSchema = new mongoose.Schema(
  {
    userId: { type: mongoose.Schema.Types.ObjectId, ref: "User", required: true },
    fcmToken: { type: String, required: false },
    notifications: [notificationItemSchema]
  },
  { timestamps: true }
);

// Cron job hoặc script định kỳ sẽ chạy như sau:
notificationSchema.methods.removeExpiredNotifications = function () {
  const now = new Date();
  this.notifications = this.notifications.filter(
    (notif) => !notif.expireAt || notif.expireAt > now
  );
  return this.save();
};

module.exports = mongoose.model("Notification", notificationSchema, "THONGBAO");
