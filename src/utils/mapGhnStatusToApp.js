// src/utils/mapGhnStatus.js

/**
 * Hàm chuyển đổi trạng thái GHN sang trạng thái hiển thị của app
 * Dùng cho đồng bộ đơn hàng, hiển thị UI, và webhook.
 */

const mapGhnStatusToApp = (status) => {
  if (!status) return "Không xác định";

  const map = {
    ready_to_pick: "Đang xử lý",
    picking: "Đang xử lý",
    picked: "Đang xử lý",
    storing: "Đang xử lý",
    transporting: "Đang xử lý",
    sorting: "Đang xử lý",
    delivering: "Đang giao hàng",
    money_collect_delivering: "Đang giao hàng",
    delivered: "Đã giao thành công",
    delivery_fail: "Đang hoàn hàng",
    waiting_to_return: "Đang hoàn hàng",
    return: "Đang hoàn hàng",
    return_transporting: "Đang hoàn hàng",
    return_sorting: "Đang hoàn hàng",
    returning: "Đang hoàn hàng",
    return_fail: "Đã hoàn hàng",
    returned: "Đã hoàn hàng",
    cancel: "Đã hủy",
  };
  // Nếu không khớp thì trả lại status raw
  return map[status] || status;
};

module.exports = { mapGhnStatusToApp };
