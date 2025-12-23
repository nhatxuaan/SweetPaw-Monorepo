const Order = require("../models/orderModel");
const Counter = require("../models/counterModel");
const { default: ApiError } = require("../utils/ApiError");
const payos = require("../utils/payos");
const axios = require("axios");
const crypto = require("crypto");
require("dotenv").config();
const PaymentPending = require("../models/paymentPendingModel");
const User = require("../models/userModel");
const { sendToToken } = require("./notificationService");

//Hàm lấy số VA tiếp theo
async function getNextVASequence() {
  const counter = await Counter.findOneAndUpdate(
    { name: "va_counter" },
    { $inc: { value: 1 } },
    { new: true, upsert: true } // tạo mới nếu chưa có
  );

  return counter.value;
}



const createPaymentService = async (orderId, amount) => {
  if (!orderId || !amount) {
    throw new ApiError(400, "Thiếu thông tin tạo thanh toán");
  }

  // Nội dung chuyển khoản duy nhất cho đơn
  const description = `ORDER_${orderId}`;

  const nextVASeq = await getNextVASequence();
  const vaNumber = `${process.env.CASSO_VA}-${String(nextVASeq).padStart(6, "0")}`;
  // const qrUrl =
  // `https://img.vietqr.io/image/BIDV-${vaNumber}-compact.png` +
  // `?amount=${amount}`;

   const qrUrl =
    `https://img.vietqr.io/image/BIDV-${vaNumber}-compact.png` +
    `?amount=${amount}&addInfo=${encodeURIComponent(description)}`;
  


  // Lưu thông tin thanh toán chờ vào DB
  const paymentRecord = await PaymentPending.create({
    orderId,
    amount,
    qrUrl,
    description,
    status: "PENDING",
  });

  return paymentRecord;
};


const handleCassoWebhookService = async (webhookData) => {
  const { error, data } = webhookData;

  console.log("Dữ liệu webhook nhận được:", webhookData);

  // 1) Nếu webhook từ Casso báo lỗi
  if (error !== 0 || !Array.isArray(data)) {
    console.log("Webhook lỗi từ Casso");
    return;
  }

  // 2) Duyệt từng giao dịch
  for (const tx of data) {
    try {
      const tid = tx.tid;
      const amount = tx.amount;
      const description = tx.description;

      console.log("Giao dịch:", tx);

      // const payment = await PaymentPending.findOne({
      //   amount,
      //   status: "PENDING",
      // });

      // const orderIdMatch = tx.description?.match(/ORDER_([a-f0-9]{24})/i);
      // if (!orderIdMatch) continue;

      // const orderId = orderIdMatch[1];

      const orderIdMatch = tx.description?.match(/ORDER\s*[_-]?([a-f0-9]{24})/i);
      if (!orderIdMatch) {
        console.log("Không parse được orderId từ description:", tx.description);
        continue;
      }

      const orderId = orderIdMatch[1];

      console.log("Description:", tx.description);
      console.log("Parsed orderId:", orderId);



      const payment = await PaymentPending.findOne({
        orderId,
        status: "PENDING",
      });


      if (!payment) {
        console.log("Không tìm thấy payment pending phù hợp");
        continue;
      }

      if (payment.transactionId) {
        console.log("Giao dịch đã xử lý trước đó");
        continue;
      }

      payment.transactionId = tid;
      payment.status = "SUCCESS";
      payment.description = payment.description || tx.description;
      await payment.save();

      const order = await Order.findById(payment.orderId);
      if (!order) {
        console.log("Không tìm thấy Order tương ứng");
        continue;
      }

      // order.status = "Đang xử lý"; 
      order.paymentStatus = "SUCCESS";
      await order.save();

      const user = await User.findById(order.user);
      if (user?.fcmToken) {
        await sendToToken({
          fcmToken: user.fcmToken,
          title: "Thanh toán thành công!",
          body: `Đơn hàng ${order._id.toString()} đã được thanh toán.`,
          data: {
            type: "payment_success",
            orderId: order._id.toString(),
            amount: payment.amount.toString()
          },
          userId: user._id.toString()
        });
      }

      console.log("Thanh toán thành công cho Order:", payment.orderId);

    } catch (err) {
      console.error("Lỗi xử lý từng giao dịch:", err);
    }
  }

  return { success: true };
};

const getPaymentStatusService = async (orderId) => {
  if (!orderId) {
    throw new ApiError(400, "Thiếu orderId để kiểm tra trạng thái thanh toán");
  }
  const order = await Order.findById(orderId);
  if (!order) {
    throw new ApiError(404, "Không tìm thấy đơn hàng");
  }
  return {
    orderId: order._id,
    paymentStatus: order.paymentStatus,
  };

}



module.exports = {
  createPaymentService,
  handleCassoWebhookService,
  getPaymentStatusService,

};
