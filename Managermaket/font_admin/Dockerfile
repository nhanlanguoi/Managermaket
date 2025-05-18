# Sử dụng một image Nginx nhẹ làm base
FROM nginx:alpine

# Xóa nội dung mặc định của Nginx (nếu có)
# RUN rm -rf /usr/share/nginx/html/* # Sao chép tệp admin.html vào thư mục gốc của web server Nginx
# Đổi tên thành index.html để nó được phục vụ mặc định khi truy cập /
COPY admin.html /usr/share/nginx/html/index.html

# Nginx lắng nghe trên cổng 80 theo mặc định bên trong container
EXPOSE 80

