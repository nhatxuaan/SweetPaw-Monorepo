import { apiGetAuth } from "./apiClient";


export async function getDashboard(type: "day" | "month" | "year" = "year") {
  const url = `/api/dashboard?type=${type}`;

  const res = await apiGetAuth(url);
  return res.data; 
}

