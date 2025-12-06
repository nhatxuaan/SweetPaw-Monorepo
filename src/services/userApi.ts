import { apiGetAuth, apiPostAuth, apiPutAuth } from './apiClient';

export async function apiGetUsers() {
  return apiGetAuth('/api/admin/customers');
}
export function apiCreateUsers(customerData: any) {
  return apiPostAuth("/api/admin/customers", customerData);
}

export async function apiUpdateUsers(id: string, data: any) {
  return apiPutAuth(`/api/admin/customers/${id}`, data);
}
