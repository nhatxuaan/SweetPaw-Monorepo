## --------------------------
## BUILD STAGE
## --------------------------
FROM node:22-alpine AS build

# Đặt thư mục làm việc
WORKDIR /app

# Copy file cấu hình npm
COPY package*.json ./

# Cài dependencies (chỉ production)
RUN npm install --production

# Copy toàn bộ mã nguồn vào container
COPY . .

## --------------------------
## RUN STAGE
## --------------------------
FROM node:22-alpine

WORKDIR /app

# Copy từ stage build sang
COPY --from=build /app /app

# Cài thêm tini để quản lý tiến trình tốt hơn (tùy chọn, khuyên dùng cho Azure)
RUN apk add --no-cache tini

# Đặt biến môi trường (có thể bị override bởi Azure hoặc docker run)
ENV NODE_ENV=production
ENV PORT=3000

# Expose cổng của app (Azure sẽ map PORT này)
EXPOSE 3000

# Dùng tini để đảm bảo process clean khi container dừng
ENTRYPOINT ["/sbin/tini", "--"]

# Lệnh khởi động app
CMD ["node", "src/server.js"]
