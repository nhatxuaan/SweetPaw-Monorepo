const mongoose = require("mongoose");

const OrderSchema = new mongoose.Schema(
  {
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
    },
    to_name: { type: String, required: true },
    to_phone: { type: String, required: true },
    to_address: { type: String, required: true },
    to_ward_name: { type: String },
    to_district_name: { type: String },
    to_province_name: { type: String },
    note: { type: String },

    items: [
      {
        productId: { type: mongoose.Schema.Types.ObjectId, ref: "Product" },
        name: { type: String, required: true },
        quantity: { type: Number, required: true },
        price: { type: Number, required: true },
        weight: { type: Number },
      },
    ],

    ghn_order_code: { type: String },
    shipping_fee: { type: Number, default: 0 },
    tongtien: { type: Number, default: 0 },
    discountAmount: { type: Number, default: 0 },
    total_price: { type: Number, default: 0 },
    payment_method: { type: String, enum: ["Chuyển khoản", "Thanh toán khi nhận hàng"] },

    status: {
      type: String,
      enum: [
        // "pending", // chờ xác nhận
        // "processing", // đang xử lý
        // "delivering", // đang giao
        // "delivered", // đã giao
        // "cancelled", // đã hủy

        "Đang xử lý", 
        "Đang giao hàng", 
        "Đã giao thành công"

      ],
      // default: "pending",
      default: "Đang xử lý",
    },
    paymentStatus: {
      type: String,
      enum: ["PENDING", "SUCCESS"],
      default: "PENDING"
    },

  },
  { timestamps: true }
);

module.exports = mongoose.model("Order", OrderSchema, "DONHANG");
