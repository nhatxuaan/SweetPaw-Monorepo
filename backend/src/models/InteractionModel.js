const mongoose = require("mongoose");

const interactionSchema = new mongoose.Schema(
  {
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
      index: true,
    },

    productId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Product",
      required: false, 
      index: true,
    },

    action: {
      type: String,
      enum: ["view", "add_to_cart", "favorite", "buy", "rate", "search"],
      required: true,
    },

    score: {
      type: Number,
      default: 1, // view=1, add_to_cart=2, favorite=3, buy=5, rate=ratingValue, search=1
    },

    metadata: {
      keyword: String, // dùng cho action="search" để lưu từ khóa
    },

    timestamp: {
      type: Date,
      default: Date.now,
    },
  }
);

const Interaction = mongoose.model("Interaction", interactionSchema, "INTERACTIONS");

module.exports = Interaction;
