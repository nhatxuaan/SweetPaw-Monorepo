## --------------------------
## BUILD STAGE
## --------------------------
FROM node:22-alpine AS build

# Thư mục làm việc
WORKDIR /app

# Copy package để install
COPY package.json yarn.lock ./

# Install dependencies
RUN yarn install --frozen-lockfile

# Copy toàn bộ source
COPY . .

# Build TypeScript + Vite
RUN yarn build

## --------------------------
## RUN STAGE
## --------------------------
FROM nginx:1.27-alpine

# Copy file build sang thư mục NGINX
COPY --from=build /app/dist /usr/share/nginx/html

# Nếu bạn dùng SPA Router (React Router) — BẮT BUỘC dùng file nginx này
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
