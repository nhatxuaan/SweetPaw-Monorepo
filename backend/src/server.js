const express = require("express");
const connectDB = require("./config/db");
require('dotenv').config();
require("./cron/ghnStatusUpdater");
require('./cron/notificationCron');
require("./config/firebaseAdmin");
const authRoutes = require("./routes/authRoutes")
const userRoutes = require("./routes/userRoutes")
const productRoutes = require("./routes/productRoutes")
const cartRoutes = require("./routes/cartRoutes")
const orderRoutes = require("./routes/orderRoutes");
const ratingRoutes = require("./routes/ratingRoutes");
const chatRoutes = require("./routes/chatRoutes");
const discountRoutes = require("./routes/discountRoute");
const paymentRoutes = require("./routes/paymentRoutes");
const favoriteRoutes = require("./routes/favoriteRoutes");
const notificationRoutes = require("./routes/notificationRoutes");
const adminRoutes = require("./routes/adminRoutes");
const dashboardRoutes = require("./routes/dashboardRoutes");
const interactionRoutes = require("./routes/interactionRoutes");
const recommendRoutes = require("./routes/recommendRoutes")
const http = require("http");
const { initSocket } = require("./utils/socket");
const errorHandlingMiddleware = require("./middlewares/errorMiddleware")
const trafficMiddleware = require("./middlewares/trafficMiddleware");
const { database } = require("firebase-admin");

//Thêm mới
const cors = require("cors");

// KHỞI TẠO APP TRƯỚC
const app = express();

// THÊM CORS NGAY SAU app = express();
app.use(cors({
  origin: "*",
  methods: ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
  allowedHeaders: ["Content-Type", "Authorization"],
}));


const PORT = process.env.PORT || 3000;

//const app = express();

connectDB();
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

const server = http.createServer(app);

const io = initSocket(server); 
app.set("io", io);
console.log("Socket.io đã khởi tạo thành công!")

app.get("/", (req, res) => {
  res.send("Server đang chạy ok nè");
});

// Middleware ghi nhận traffic
app.use(trafficMiddleware);

app.use("/api/auth", authRoutes);

app.use("/api/user", userRoutes);

app.use("/api/products", productRoutes);

app.use("/api/cart", cartRoutes);

app.use("/api/orders", orderRoutes);

app.use("/api/ratings", ratingRoutes);

app.use("/api/chats", chatRoutes);

app.use("/api/discounts", discountRoutes);

app.use("/api/payment", paymentRoutes);

app.use("/api/favorite", favoriteRoutes);

app.use("/api/notification", notificationRoutes);

app.use("/api/dashboard", dashboardRoutes);

app.use("/api/admin", adminRoutes);

app.use("/api/recommend", recommendRoutes)

// app.use("/api/interactions",  interactionRoutes);


//Middleware xử lý lỗi tập trung
app.use(errorHandlingMiddleware);



server.listen(PORT,"0.0.0.0", () => {
    console.log(`Server is listening ${PORT}...`)
})