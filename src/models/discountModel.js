const mongoose = require("mongoose");

const DiscountSchema = new mongoose.Schema(
  {
    code: {
      type: String,
      required: true,
      unique: true,
      uppercase: true,
      trim: true,
    },

    name: {
      type: String,
      required: true, // Tên khuyến mãi — form đang có
      trim: true,
    },

    description: {
      type: String,
      default: "",
    },

    type: {
      type: String,
      enum: ["percent", "fixed"], // percent = giảm %, fixed = giảm tiền cố định
      required: true,
    },

    value: {
      type: Number,
      required: true, // nếu type=percent thì value=10 => 10%
    },

    maxDiscount: {
      type: Number,
      default: 0, // Giới hạn số tiền tối đa được giảm (nếu muốn)
    },

    minOrderValue: {
      type: Number,
      default: 0, // Giá trị đơn hàng tối thiểu để áp dụng
    },

    startDate: {
      type: Date,
      default: Date.now,
    },

    endDate: {
      type: Date,
      required: true,
    },

    quantity: {
      type: Number,
      default: 0, // Số lượng mã còn lại
    },

    isActive: {
      type: Boolean,
      default: true,
    },

    createdBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Admin", // nếu có admin tạo
    },
  },
  { timestamps: true }
);

//   Middleware kiểm tra hết hạn tự động
DiscountSchema.pre("save", function (next) {
  if (this.endDate < new Date()) {
    this.isActive = false;
  }
  next();
});

module.exports = mongoose.model("Discount", DiscountSchema, "KHUYENMAI");
