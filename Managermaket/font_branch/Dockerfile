# Sử dụng một image Nginx nhẹ
FROM nginx:alpine

# Xóa file cấu hình mặc định của Nginx
RUN rm /etc/nginx/conf.d/default.conf

# Sao chép tệp cấu hình Nginx tùy chỉnh của bạn vào container
COPY nginx.conf /etc/nginx/conf.d/nginx.conf

# Sao chép tệp giao diện POS vào thư mục gốc của web server Nginx
COPY branch.html /usr/share/nginx/html/index.html

# Nginx lắng nghe trên cổng 80 theo mặc định bên trong container
EXPOSE 80