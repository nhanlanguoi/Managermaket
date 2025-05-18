# Stage 1: Build a JAR
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Create the final image with the JAR
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# Sửa tên file JAR ở đây
COPY --from=builder /app/target/warehouse-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]