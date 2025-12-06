import { apiGetAuth, apiPostAuth, apiPutAuth } from './apiClient';

export async function apiGetOrders() {
  return apiGetAuth('/api/admin/orders');
}

export function apiUpdateStatusPayment(id: string, data:any) {
  return apiPutAuth(`/api/admin/orders/${id}`, data);
}