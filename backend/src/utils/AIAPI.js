// utils/AIAPI.js
const axios = require("axios");
require("dotenv").config();

const AI_API = axios.create({
  baseURL: process.env.BASE_URL_AI,
  headers: { "Content-Type": "application/json" },
});

module.exports = AI_API;
