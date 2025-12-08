const cron = require("node-cron");
const Notification = require("../models/notificationModel");

cron.schedule('0 * * * *', async () => { // chạy mỗi giờ
  const allUsers = await Notification.find();
  for (const user of allUsers) {
    await user.removeExpiredNotifications();
  }
  console.log("Đã xóa các thông báo quá hạn");
});
