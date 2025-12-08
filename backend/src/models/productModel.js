const mongoose = require("mongoose");

const productSchema = new mongoose.Schema({
  id: {
    type: Number,
    required: true,
    unique: true
  },
  name: {
    type: String,
    required: true
  },
  category: {
    type: String,
    required: true
  },
  flavor: {
    type: mongoose.Schema.Types.Mixed,
    default: {}
  },
  cost: {
    type: Number,
    required: true
  },
  price: {
    type: Number,
    required: true
  },
  url: {
    type: String
  },
  des: {
    type: String
  },
  stock: {
    type: Number,
    default: 0
  },
  sold_count: {
    type: Number,
    default: 0
  },
  rating_avg: {
    type: Number,
    default: 0
  },
  weight: {
    type: Number,
    default: 0.3
  }
},
  { timestamps: true }
);


const Product = mongoose.model("Product", productSchema, "SANPHAM");

module.exports = Product;
