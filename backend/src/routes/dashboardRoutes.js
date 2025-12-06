const express = require("express");
const router = express.Router();
const authMiddleware = require("../middlewares/authMiddleware");
const { getDashboardController } = require("../controllers/dashboardController");

// /admin/dashboard?type=day&time=2025-11-26
// /admin/dashboard?type=month&time=2025-11
// /admin/dashboard?type=year&time=2025
// /admin/dashboard?type=range&from=2025-11-01&to=2025-11-30

// Lấy thống kê dashboard
router.get("/", authMiddleware, getDashboardController);


module.exports = router;