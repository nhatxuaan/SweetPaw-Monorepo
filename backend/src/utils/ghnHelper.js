const ghnService = require("../services/ghnService");
const { default: ApiError } = require("./ApiError");

const getAddressCodesFromNames = async (shipAddress) => {

  const province_name = shipAddress.ThanhPho || shipAddress.province_name;
  const district_name = shipAddress.QuanHuyen || shipAddress.district_name;
  const ward_name = shipAddress.PhuongXa || shipAddress.ward_name;

//   console.log(">>> shipAddress nhận được:", shipAddress);


  // Lấy danh sách tỉnh
  const provinces = await ghnService.getProvincesGHN();
  const province = provinces.find(p =>
    p.ProvinceName.trim().toLowerCase() === province_name.trim().toLowerCase()
  );
//   console.log(">>> [LOG] Tổng số tỉnh:", provinces.length);
//   console.log(">>> [LOG] Kết quả tỉnh tìm được:", province);
  if (!province) throw new ApiError(404, `Không tìm thấy tỉnh: ${province_name}`);

  // Lấy danh sách quận/huyện
  const districts = await ghnService.getDistrictsGHN(province.ProvinceID);
  const district = districts.find(d =>
    d.DistrictName.trim().toLowerCase() === district_name.trim().toLowerCase()
  );

    // console.log(">>> [LOG] Tổng số quận/huyện:", districts.length);
    // console.log(">>> [LOG] Một vài quận/huyện:", districts.slice(0, 10).map(d => d.DistrictName));
  if (!district) throw new ApiError(404, `Không tìm thấy quận/huyện: ${district_name}`);

  // Lấy danh sách phường/xã
  const wards = await ghnService.getWardsGHN(district.DistrictID);
  const ward = wards.find(w =>
    w.WardName.trim().toLowerCase() === ward_name.trim().toLowerCase()
  );
    // console.log(">>> [LOG] Tổng số phường/xã GHN trả về:", wards.length);
    // console.log(">>> [LOG] Một vài phường đầu tiên:", wards.slice(0, 10).map(w => w.WardName));
    // console.log(">>> [LOG] Đang tìm phường:", ward_name);

  if (!ward) throw new ApiError(404,`Không tìm thấy phường/xã: ${ward_name}`);

  return {
    to_province_id: province.ProvinceID,
    to_district_id: district.DistrictID,
    to_ward_code: ward.WardCode,
  };
};

module.exports = { getAddressCodesFromNames };
