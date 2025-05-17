# build jar với tên builder
FROM maven:3.8.5-openjdk-17 AS builder
# chỉ định thư mục app các câu lệnh tiếp theo sẽ được hoạt động trong folder app 
WORKDIR /app
COPY pom.xml .
# Tải trước tất cả dependencies
RUN mvn dependency:go-offline -B
# copy toàn bộ source
COPY src ./src
# Build project và tạo file .jar bỏ qua việc chạy unit test tránh lỗi khi chưa có đường truyền
RUN mvn package -DskipTests


FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Sao chép file JAR được build từ stage builder vào container final với tên app.jar
COPY --from=builder /app/target/branch-service-0.0.1-SNAPSHOT.jar app.jar
# cộng dịch vụ 8081
EXPOSE 8081
# lệnh thực thi khi container chạy chạy ứng dụng được build app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]