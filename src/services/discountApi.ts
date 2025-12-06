import { apiGetAuth, apiPostAuth, apiPutAuth, apiDeleteAuth } from './apiClient';

export function apiGetPromotion() {
  return apiGetAuth('/api/admin/promotions');
}
export function apiCreatePromotion(promotionData: any) {
  return apiPostAuth('/api/admin/promotions', promotionData);
}
export async function apiUpdatePromotion(id: string, data:any) {
  return apiPutAuth(`/api/admin/promotions/${id}`, data);
}

export async function apiDeletePromotion(id: string) {
  return apiDeleteAuth(`/api/admin/promotions/${id}`)
    .then((res) => res.data)   //chỉ trả về data
    .catch((error) => {
      throw error;             //ném lỗi để component bắt
    });
}