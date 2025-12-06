
import axios from "axios";
import dotenv from "dotenv";

dotenv.config();

const GHN_API = axios.create({
  baseURL: process.env.GHN_BASE_URL, // ví dụ: https://dev-online-gateway.ghn.vn/shiip/public-api
  headers: {
    Token: process.env.GHN_TOKEN,
    "Content-Type": "application/json",
  },
});

export default GHN_API;
