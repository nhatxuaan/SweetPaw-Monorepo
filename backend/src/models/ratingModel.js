// models/ratingModel.js
const mongoose = require("mongoose");

const ratingSchema = new mongoose.Schema({
  productId: { 
    type: mongoose.Schema.Types.ObjectId, 
    ref: "Product", 
    required: true 
  },
  userId: { 
    type: mongoose.Schema.Types.ObjectId, 
    ref: "User", 
    required: true 
  },
  orderId: {  
    type: mongoose.Schema.Types.ObjectId,
    ref: "Order",  
    required: true
  },
  stars: { 
    type: Number, 
    min: 1, 
    max: 5, 
    required: true 
  },
  comment: { 
    type: String, 
    trim: true 
  },
  createdAt: { 
    type: Date, 
    default: Date.now 
  }
});

const Rating = mongoose.model("Rating", ratingSchema, "RATING");

module.exports = Rating; 
