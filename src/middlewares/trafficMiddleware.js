const Traffic = require("../models/trafficModel");

const trafficMiddleware = async function (req, res, next) {
  try {
    if (req.method === "GET" && req.originalUrl.startsWith("/api/products")) {
      await Traffic.create({
        userId: req.user?._id || null,
        timestamp: new Date()
      });

      console.log("IP " + req.ip + " đã truy cập " + req.originalUrl);
    }
  } catch (err) {
    console.error("Traffic log error:", err);
  }

  next();
};

module.exports = trafficMiddleware;
