const mongoose = require("mongoose");

const trafficSchema = new mongoose.Schema({
  userId: { type: mongoose.Types.ObjectId, ref: "User", required: false },
  timestamp: { type: Date, default: Date.now }
});

module.exports = mongoose.model("Traffic", trafficSchema);
