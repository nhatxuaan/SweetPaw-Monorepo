const mongoose = require("mongoose");

const PaymentPendingSchema = new mongoose.Schema({
  orderId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: "Order", // liên kết tới model Order
    required: true,
  },

  amount: { type: Number, required: true },
  description: { type: String, required: true },
  qrUrl: { type: String, required: true },

  status: { 
    type: String, 
    enum: ["PENDING", "SUCCESS", "FAILED"],
    default: "PENDING",
  },

  transactionId: { type: String }, // tid từ webhook (tuỳ chọn)
  createdAt: { type: Date, default: Date.now },
});

module.exports = mongoose.model("PaymentPending", PaymentPendingSchema);
