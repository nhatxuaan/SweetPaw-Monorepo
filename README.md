## Cấu trúc thư mục tham khảo (có thể thay đổi trong quá trình viết code sao cho phù hợp)

```bash
Sweet-Backend/
│
├── node_modules/
│
├── src/
│   ├── config/                        # Cấu hình hệ thống
│   │   ├── db.js                        # Kết nối MongoDB Atlas
│   │   ├── mail.js                      # Cấu hình gửi email
│   │   └── cloud.js                     # Upload ảnh sản phẩm (ko bt có nên thêm vào ko)
│   │
│   ├── models/                        # Mô hình dữ liệu (MongoDB Schema)
│   │   ├── userModel.js                 # KHACHHANG
│   │   ├── employeeModel.js             # NHANVIEN
│   │   ├── productModel.js              # SANPHAM
│   │   ├── orderModel.js                # DONHANG
│   │   ├── invoiceModel.js              # HOADON
│   │   ├── cartModel.js                 # GIOHANG
│   │   ├── reviewModel.js               # DANHGIA
│   │   ├── promotionModel.js            # KHUYENMAI
│   │   ├── promotionDetailModel.js      # CT_KM
│   │   ├── reportModel.js               # BAOCAO
│   │   ├── importNoteModel.js           # PHIEUNHAP
│   │   ├── chatRoomModel.js             # Chat room (3 thk cúi ko chắc có nên thêm ko, có thể sẽ tạo thêm bảng chat, chatroom trong database)
│   │   ├── messageModel.js              # Chat
│   │   └── notificationModel.js         # Notification
│   │
│   ├── services/                      # Xử lý logic API
│   │   ├── authService.js               # Đăng ký,Đăng nhập, Quên mật khẩu
│   │   ├── userService.js               # thông tin người dùng, đổi mật khẩu, xóa tài khoản
│   │   ├── adminService.js              # Quản lý KH, SP, ĐH, Báo cáo
│   │   ├── productService.js            # Lấy DS SP, tìm kiếm, chi tiết
│   │   ├── cartService.js               # Giỏ hàng (thêm, xóa, cập nhật)
│   │   ├── orderService.js              # Tạo, hủy, xác nhận đơn hàng
│   │   ├── paymentService.js            # API Thanh toán
│   │   ├── reviewService.js             # Đánh giá sản phẩm
│   │   ├── chatService.js               # Gửi, nhận tin nhắn
│   │   ├── notificationService.js       # Quản lý thông báo
│   │   ├── promotionService.js          # API khuyến mãi
│   │   └── emailService.js              # Gửi email xác nhận, quên mật khẩu ( này không biết nên thêm vào ko hoặc có thể viết logic của nó vào authService.js)
│   │
│   ├── controllers/                   # Điều khiển request/response (REST API)
│   │   ├── authController.js
│   │   ├── userController.js
│   │   ├── adminController.js
│   │   ├── productController.js
│   │   ├── cartController.js
│   │   ├── orderController.js
│   │   ├── paymentController.js
│   │   ├── reviewController.js
│   │   ├── chatController.js
│   │   ├── notificationController.js
│   │   └── promotionController.js
│   │
│   ├── routes/                        # Định nghĩa đường dẫn API
│   │   ├── authRoutes.js                # /api/auth
│   │   ├── userRoutes.js                # /api/users
│   │   ├── adminRoutes.js               # /api/admin
│   │   ├── productRoutes.js             # /api/products
│   │   ├── cartRoutes.js                # /api/cart
│   │   ├── orderRoutes.js               # /api/orders
│   │   ├── paymentRoutes.js             # /api/payments
│   │   ├── reviewRoutes.js              # /api/reviews
│   │   ├── chatRoutes.js                # /api/chat
│   │   ├── notificationRoutes.js        # /api/notifications
│   │   └── promotionRoutes.js           # /api/promotions
│   │
│   ├── sockets/                       # xử lý Socket.IO (dùng cho tính năng thời gian thực, sau này sẽ thêm thư mục này)
│   │   ├── socketManager.js             # Quản lý socket server
│   │   ├── chatSocket.js                # Chat realtime
│   │   └── notificationSocket.js        # Thông báo realtime (đơn hàng, khuyến mãi, thông báo)
│   │
│   ├── middlewares/                     # Kiểm tra & xử lý chung
│   │   ├── authMiddleware.js            # JWT xác thực người dùng
│   │   ├── adminMiddleware.js           # Kiểm tra quyền admin
│   │   ├── validateMiddleware.js        # Validate dữ liệu
│   │   └── errorMiddleware.js           # Bắt lỗi toàn hệ thống
│   │
│   ├── utils/                         # Hàm tiện ích
│   │   ├── tokenUtils.js                # tạo, giải mã, kiểm tra hạn sử dụng của JWT token
│   │   ├── emailUtils.js                # gửi email xác nhận, khôi phục mật khẩu, hoặc gửi mã OTP
│   │   ├── responseHandler.js           # tạo format thống nhất khi trả dữ liệu về cho client (đang si nghĩ có nên thêm vào ko)
│   │   ├── fileUtils.js                 # upload, xoá, đổi tên, xử lý đường dẫn file hoặc hình ảnh
│   │   └── dateUtils.js                 # Xử lý thời gian, định dạng ngày
│   │
│   ├── app.js                         # Cấu hình express, middleware, routes
│   └── server.js                      # Khởi động server + Socket.IO
│
├── .env                               # Biến môi trường (DB_URI, JWT_SECRET,...)
├── .env.example                       # Mẫu file cấu hình (file này đang si nghĩ có nên thêm zô hay ko)
├── .gitignore                         # Bỏ qua node_modules, .env, log.
├── package.json                       # Thông tin & dependencies
└── README.md                          # Hướng dẫn sử dụng
```

## Quy tắt đặt tên

```bash
# Biến & Hàm
  - Cách viết: chữ đầu viết thường, viết hóa chữ cái đầu các chữ tiếp theo, viết liền ko cách, ko gạch nối.
  - Cấu trúc:
    + Biến là [danh từ] mô tả dữ liệu, ví dụ: userName.
    + Hàm là [động từ + danh từ] mô tả chức năng, getUser().
  - Chú ý:
    + Biến/Hàm là có kiểu boolean thì thêm "is" ở trc.
    + Mảng thì thêm s.
# Biến tạm
  - cách viết: viết như cách viết biến và hàm, nhưng mà ngắn gọn dễ hiểu, viết một chữ thâu nhiều nhất là hai chữ nhưng mà hạn chế viết hai chữ.
  - ví dụ: count, item.
# Hằng số
  - cách viết: tất cả chữ in hoa, ngăn cách bằng dấu gạch dưới, mô tả rõ chức năng/ý nghĩa của biến, ví dụ: SERVER_PORT.
# Class (thằng này trong backend hình như là model/Schema hay sao á)
  - cách viết: mỗi từ viết hoa chữ cái đầu.
  - Cấu trúc: [Tên đối tượng] + [mô tả].
```

## Push code lên github (bữa tui nghe mấy fen ko bt sài lệnh git nên tui để lệnh ở đây chạy từng lệnh là push lên đc)

- git add .
- git commit -m "sửa cái gì thì ghi commit zô dấu ngoặc kép này"
- git push origin master (thường thì ko push thẳng lên nhánh master đâu mà push vào các nhanh khác, nào code chạy oke ko lỗi thì mới push thẳng lên nhánh master nên là nếu push lên nhánh nào thì đỗi master thành tên nhánh đó nha)

## Các lệnh khác (nhớ xem)

- sau khi clone code dề, thì chạy lệnh "cd SweetPaw-Backend" rồi chạy lệnh "npm install" để nó cài lại hết node_modules/ (vì push code lên git sẽ bỏ qua node_modules/ tại nó nặng nên clone code về phải cài lại nó).
- tui có sửa lại phần scripts nên mọi người chạy code bằng lệnh "npm start" còn debug bằng lệnh "npm run dev"
