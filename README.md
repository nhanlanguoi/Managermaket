# Dự án Quản Lý Tiệm Tạp Hóa (ManagerMaket)

Dự án này mô phỏng một hệ thống quản lý cho một chuỗi tiệm tạp hóa, bao gồm quản lý sản phẩm, quản lý kho hàng theo từng chi nhánh, quản lý thông tin chi nhánh và giao diện bán hàng tại điểm bán (POS) cho từng chi nhánh.

## Mục lục

1.  [Tổng quan Công nghệ](#tổng-quan-công-nghệ)
2.  [Kiến trúc Hệ thống](#kiến-trúc-hệ-thống)
3.  [Cấu trúc Dự án](#cấu-trúc-dự-án)
4.  [Yêu cầu Hệ thống](#yêu-cầu-hệ-thống)
5.  [Hướng dẫn Cài đặt và Chạy Dự án](#hướng-dẫn-cài-đặt-và-chạy-dự-án)
    * [Clone Repository](#clone-repository)
    * [Chạy bằng Docker Compose (Khuyến nghị)](#chạy-bằng-docker-compose-khuyến-nghị)
    * [Chạy Thủ công (Tùy chọn)](#chạy-thủ-công-tùy-chọn)
6.  [Các Giao diện Người dùng](#các-giao-diện-người-dùng)
7.  [API Endpoints](#api-endpoints)
8.  [Giám sát (Monitoring)](#giám-sát-monitoring)
9.  [Đóng góp](#đóng-góp)

## Tổng quan Công nghệ

Hệ thống được xây dựng dựa trên kiến trúc microservices với các công nghệ chính sau:

* **Backend:**
    * **Java 17**
    * **Spring Boot 3.x:** Framework chính để xây dựng các microservices.
    * **Spring Data JPA (cho `branch-service`):** Tương tác với cơ sở dữ liệu quan hệ.
    * **Spring Data Elasticsearch (cho `warehouse-service`):** Tương tác với Elasticsearch để lưu trữ và tìm kiếm sản phẩm.
    * **Spring MVC:** Xây dựng các RESTful API.
* **Cơ sở dữ liệu:**
    * **PostgreSQL 15:** Lưu trữ thông tin chi nhánh (`branch-service`).
    * **Elasticsearch 8.18.1:** Lưu trữ và tìm kiếm thông tin sản phẩm, tồn kho theo chi nhánh (`warehouse-service`).
* **Frontend:**
    * **HTML, CSS, JavaScript (Vanilla JS):** Xây dựng giao diện người dùng đơn giản cho các tác vụ quản lý và POS.
* **Containerization & Orchestration:**
    * **Docker & Docker Compose:** Đóng gói và quản lý môi trường chạy cho các service.
* **Web Server cho Frontend (trong Docker):**
    * **NGINX (hoặc web server tương tự):** Phục vụ các tệp HTML, CSS, JS tĩnh của frontend. (Giả định dựa trên Dockerfile của các thư mục `font_*`)
* **Build Tool:**
    * **Apache Maven:** Quản lý thư viện và build các project Java.
* **Giám sát (Monitoring - theo kế hoạch):**
    * **Spring Boot Actuator:** Expose metrics của ứng dụng.
    * **Micrometer:** Thư viện thu thập metrics.
    * **Prometheus:** Hệ thống giám sát và cảnh báo, scrape metrics từ Actuator.
    * **Grafana:** (Tùy chọn) Dashboard hiển thị metrics từ Prometheus.

## Kiến trúc Hệ thống

Hệ thống bao gồm các thành phần chính sau, được đóng gói trong các container Docker:

1.  **`postgres-db`**: Container chạy PostgreSQL, lưu dữ liệu cho `branch-service`.
2.  **`e1`, `e2`, `e3`**: Cụm 3 node Elasticsearch, lưu dữ liệu sản phẩm cho `warehouse-service`.
3.  **`branch-service`**: Microservice quản lý thông tin chi nhánh (CRUD), kết nối với `postgres-db`.
4.  **`warehouse-service`**: Microservice quản lý thông tin sản phẩm, tồn kho, giá bán tại từng chi nhánh, kết nối với cụm Elasticsearch.
5.  **`admin-ui`**: Giao diện web cho quản trị viên, cho phép quản lý (thêm, sửa, xóa) sản phẩm. Giao tiếp với `warehouse-service`.
6.  **`branch-manager-ui`**: Giao diện web cho phép quản lý (thêm, sửa, xóa) thông tin chi nhánh. Giao tiếp với `branch-service`.
7.  **`branch-pos-ui`**: Giao diện web Point-of-Sale (POS) cho từng chi nhánh, cho phép xem sản phẩm, tồn kho và thực hiện thao tác "bán" (giảm tồn kho). Giao tiếp với `warehouse-service` và `branch-service`.

Các service backend (`branch-service`, `warehouse-service`) được xây dựng bằng Spring Boot và expose các RESTful API. Các giao diện frontend (`admin-ui`, `branch-manager-ui`, `branch-pos-ui`) là các ứng dụng HTML/CSS/JS tĩnh, gọi đến các API này.

## Cấu trúc Dự án

Thư mục gốc `Managermaket` chứa các thành phần sau:

```json
Managermaket/
├── branch-service/             # Source code cho Branch Service (Spring Boot)
│   ├── src/
│   └── pom.xml                 # File Maven
│   └── Dockerfile              # Dockerfile cho Branch Service
├── warehouse-service/          # Source code cho Warehouse Service (Spring Boot)
│   ├── src/
│   └── pom.xml                 # File Maven
│   └── Dockerfile              # Dockerfile cho Warehouse Service
├── font_admin/                 # Source code cho giao diện Admin
│   ├── admin.html              #
│   └── Dockerfile              # Dockerfile cho giao diện Admin (phục vụ bằng NGINX)
├── font_branch/                # Source code cho giao diện POS của Chi nhánh
│   ├── branch.html             #
│   └── Dockerfile              # Dockerfile cho giao diện POS
├── font_manager_branch/        # Source code cho giao diện Quản lý Chi nhánh
│   ├── managerbranch.html      #
│   └── Dockerfile              # Dockerfile cho giao diện Quản lý Chi nhánh
└── docker-compose.yml          # File Docker Compose để quản lý và chạy tất cả các services
```

## Yêu cầu Hệ thống

* Docker
* Docker Compose
* Java 17 (nếu muốn build lại các service Java thủ công)
* Apache Maven (nếu muốn build lại các service Java thủ công)
* Trình duyệt web hiện đại (Chrome, Firefox, Edge, Safari)

## Hướng dẫn Cài đặt và Chạy Dự án

### Clone Repository

```bash
git clone <URL_REPOSITORY_CUA_BAN>
cd Managermaket
```

## Chạy bằng Docker Compose (Khuyến nghị)

### Đây là cách đơn giản nhất để khởi chạy toàn bộ hệ thống.

#### ***1:*** Build các images (Nếu chưa có hoặc có thay đổi trong mã nguồn các service/frontend):
#### Từ thư mục gốc Managermaket, chạy lệnh:

```
docker-compose build
```

##### Lệnh này sẽ build các image Docker cho branch-service, warehouse-service, admin-ui, branch-manager-ui, và branch-pos-ui dựa trên các Dockerfile tương ứng.


#### ***2:*** Khởi động tất cả các services:

```
docker-compose up -d
```

##### Cờ -d để chạy các container ở chế độ detached (chạy nền).

#### ***3:*** Kiểm tra trạng thái của các containers:

```
docker-compose ps
```

##### Bạn sẽ thấy tất cả các service đã được định nghĩa trong docker-compose.yml đang chạy.

#### ***4:*** Dừng hệ thống

```
docker-compose down
```

##### Lệnh này sẽ dừng và xóa các container. Nếu bạn muốn xóa cả volumes (dữ liệu của Postgres và Elasticsearch), sử dụng docker-compose down -v. CẨN THẬN: thao tác này sẽ xóa hết dữ liệu.


## Các Giao diện Người dùng

Sau khi khởi chạy hệ thống bằng `docker-compose up -d`:

* **Admin UI (Quản lý Sản phẩm):** `http://localhost:8000` (Container `admin`)
    * Chức năng: Thêm, xem, sửa, xóa sản phẩm và thông tin tồn kho của sản phẩm tại các chi nhánh.
* **Branch Manager UI (Quản lý Chi nhánh):** `http://localhost:8002` (Container `branch_manager`)
    * Chức năng: Thêm, xem, sửa, xóa thông tin chi nhánh.
* **Branch POS UI (Bán hàng tại Chi nhánh):** `http://localhost:8003/pos/{branchCode}` (Container `branch`)
    * Ví dụ: `http://localhost:8003/pos/CN_HCM_1`
    * Chức năng: Hiển thị sản phẩm của chi nhánh, tồn kho, và thực hiện thao tác "bán" (giảm tồn kho).

## API Endpoints

* **Branch Service (chạy trên `http://localhost:8081` bên trong Docker network, hoặc cổng tương ứng nếu chạy thủ công):**
    * `POST /api/branches`: Tạo chi nhánh mới.
    * `GET /api/branches`: Lấy danh sách tất cả chi nhánh.
    * `GET /api/branches?active=true`: Lấy danh sách chi nhánh đang hoạt động.
    * `GET /api/branches/{id}`: Lấy chi nhánh theo ID CSDL.
    * `GET /api/branches/by-code/{branchCode}`: Lấy chi nhánh theo mã chi nhánh.
    * `PUT /api/branches/{id}`: Cập nhật chi nhánh.
    * `DELETE /api/branches/{id}`: Xóa chi nhánh.

* **Warehouse Service (chạy trên `http://localhost:8080` bên trong Docker network, hoặc cổng tương ứng nếu chạy thủ công):**
    * `POST /api/warehouse/products`: Thêm sản phẩm mới.
    * `GET /api/warehouse/products`: Lấy tất cả sản phẩm.
    * `GET /api/warehouse/products/by-product-id/{productId}`: Lấy sản phẩm theo mã sản phẩm tùy chỉnh.
    * `GET /api/warehouse/products/{internalId}`: Lấy sản phẩm theo ID nội bộ của Elasticsearch.
    * `PUT /api/warehouse/products/{internalId}`: Cập nhật sản phẩm.
    * `DELETE /api/warehouse/products/{internalId}`: Xóa sản phẩm.
    * `GET /api/warehouse/products/branch/{branchId}`: Lấy danh sách sản phẩm (bao gồm tồn kho, giá bán) cho một chi nhánh cụ thể.
    * `POST /api/warehouse/products/inventory/decrement`: Giảm tồn kho sản phẩm tại một chi nhánh.

## Giám sát (Monitoring)

(Phần này mô tả kế hoạch hoặc các bước để tích hợp giám sát. Cập nhật nếu bạn đã triển khai.)

Để giám sát các Spring Boot services (`warehouse-service`, `branch-service`):

1.  **Thêm dependencies:**
    Trong `pom.xml` của mỗi service, thêm:
    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>
    ```

2.  **Cấu hình Actuator (trong `application.properties`):**
    Đảm bảo endpoint Prometheus được expose:
    ```properties
    management.endpoints.web.exposure.include=health,info,prometheus
    management.endpoint.health.show-details=always
    ```

3.  **Cấu hình Prometheus:**
    Thêm một service Prometheus vào `docker-compose.yml` (hoặc chạy riêng) và cấu hình file `prometheus.yml` để scrape (thu thập) metrics từ các endpoint sau (bên trong Docker network):
    * `http://warehouse-service:8080/actuator/prometheus`
    * `http://branch-service:8081/actuator/prometheus`

4.  **(Tùy chọn) Cấu hình Grafana:**
    Sử dụng Grafana để kết nối tới Prometheus làm data source và tạo các dashboard để trực quan hóa metrics.

## Đóng góp

Nếu bạn muốn đóng góp cho dự án này, vui lòng fork repository, tạo một nhánh mới cho tính năng hoặc sửa lỗi của bạn, và sau đó tạo một Pull Request.
