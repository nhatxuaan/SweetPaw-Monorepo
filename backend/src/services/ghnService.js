// services/ghnService.js
const { default: ApiError } = require("../utils/ApiError");
const { default: GHN_API } = require("../utils/ghnAPI");

const createOrderGHN = async (payload) => {
  try {
    const res = await GHN_API.post("/v2/shipping-order/create", payload);

    if (!res?.data) {
      throw new ApiError(500, "Không nhận được phản hồi từ GHN");
    }

    if (res.data.code !== 200) {
      // GHN trả lỗi => in ra để debug
      console.error("GHN lỗi code:", res.data.code);
      console.error("GHN lỗi message:", res.data.message);
      console.error("GHN lỗi data:", JSON.stringify(res.data, null, 2));

      throw new ApiError(
        res.data.code || 400,
        res.data.message || "GHN trả về lỗi không xác định"
      );
    }

    return res.data;
  } catch (err) {
    console.error("GHN lỗi status:", err.response?.status);
    console.error("GHN lỗi message:", err.response?.data?.message);
    console.error("GHN lỗi code:", err.response?.data?.code_message);
    console.error(
      "GHN lỗi chi tiết:",
      JSON.stringify(err.response?.data, null, 2)
    );

    throw new ApiError(
      err.response?.status || 500,
      err.response?.data?.message || "Lỗi khi gọi GHN"
    );
  }
};

const getFeeGHN = async (payload) => {

  // console.log(">>> [LOG] Payload gửi lên GHN tính phí:", payload);
  // console.log(">>> [LOG] GHN token:", process.env.GHN_TOKEN);
  // console.log(">>> [LOG] GHN baseURL:", process.env.GHN_BASE_URL);

  const res = await GHN_API.post("/v2/shipping-order/fee", payload);

  if (!res?.data) {
        throw new ApiError(500, "Không nhận được phản hồi từ GHN");
    }

    if (res.data.code !== 200) {
      throw new ApiError(
        response.data.code || 400,
        response.data.message || "GHN trả về lỗi không xác định"
      );
    }

  return res.data;
};

const getOrderInfoGHN = async (order_code) => {
  const res = await GHN_API.post("/v2/shipping-order/detail", { order_code });

  if (!res?.data) {
        throw new ApiError(500, "Không nhận được phản hồi từ GHN");
    }

    if (res.data.code !== 200) {
      throw new ApiError(
        response.data.code || 400,
        response.data.message || "GHN trả về lỗi không xác định"
      );
    }

  return res.data;
};

const getProvincesGHN = async () => {
  const res = await GHN_API.get("/master-data/province");
  if (!res?.data) throw new ApiError(500, "Không nhận được phản hồi từ GHN");
  if (res.data.code !== 200) throw new ApiError(res.data.code, res.data.message);
  return res.data.data; // 
};

const getDistrictsGHN = async (province_id) => {
  const res = await GHN_API.post("/master-data/district", { province_id });
  if (!res?.data) throw new ApiError(500, "Không nhận được phản hồi từ GHN");
  if (res.data.code !== 200) throw new ApiError(res.data.code, res.data.message);
  return res.data.data;
};

// 3. Lấy danh sách PHƯỜNG/XÃ theo QUẬN/HUYỆN
const getWardsGHN = async (district_id) => {
  const res = await GHN_API.post(`/master-data/ward?district_id`, { district_id });
  if (!res?.data) throw new ApiError(500, "Không nhận được phản hồi từ GHN");
  if (res.data.code !== 200) throw new ApiError(res.data.code, res.data.message);
  return res.data.data;
};

module.exports = {
    createOrderGHN, 
    getOrderInfoGHN, 
    getFeeGHN, 
    getProvincesGHN, 
    getDistrictsGHN, 
    getWardsGHN,

}
