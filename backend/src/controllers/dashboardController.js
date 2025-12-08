const dashboardService = require("../services/dashboardService");
const { getDateRange } = require("../utils/dateHelper")

const getDashboardController = async (req, res, next) => {
  try {
    const { type} = req.query;

    if (!type) return res.status(400).json({ message: "Missing type" });

    const { start, end } = getDateRange(type);

    const data = await dashboardService.getDashboardService({
      type, start, end
    });

    //console.log("data:", data);

    res.status(200).json({
      message: "Lấy dữ liệu dashboard thành công",
      data,
    });

  } catch (error) {
    next(error);
  }
};

module.exports = {
  getDashboardController,
};
