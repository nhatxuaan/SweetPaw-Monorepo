// import { Discount } from 'src/model/discount';

import { apiGetAuth, apiPostAuth, apiPutAuth, apiDeleteAuth} from './apiClient';


export function apiGetDiscounts() {
  return apiGetAuth('/api/discounts');
}
export function apiGetPromotion() {
  return apiGetAuth('/api/admin/promotions');
}
export function apiCreatePromotion(Discount) {
  return apiPostAuth('/api/admin/promotions', Discount);
}
export function apiUpdatePromotion(id, discount) {
  return apiPutAuth(`/api/admin/promotions/${id}`, discount);
}
export async function apiDeletePromotion(id) {
  return apiDeleteAuth(`/api/admin/promotions/${id}`)
    .then((res) => res.data)   //chỉ trả về data
    .catch((error) => {
      throw error;             //ném lỗi để component bắt
    });
}