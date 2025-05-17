# Sử dụng một image Nginx nhẹ
FROM nginx:alpine

# Sao chép tệp giao diện quản lý chi nhánh vào thư mục gốc của web server Nginx
# Đặt tên là managerbranch.html để nó được phục vụ mặc định khi truy cập /
COPY managerbranch.html /usr/share/nginx/html/index.html

# Nginx lắng nghe trên cổng 80 theo mặc định bên trong container
EXPOSE 80