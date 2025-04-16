Dự án Microservice Ecommerce

Tổng quan

Dự án này là một ứng dụng thương mại điện tử được xây dựng theo kiến trúc microservice. Mỗi dịch vụ được thiết kế để xử lý một chức năng cụ thể của nền tảng thương mại điện tử, giúp tăng khả năng mở rộng và bảo trì.

Các dịch vụ

Dự án bao gồm các microservice sau:

 + config-server: Quản lý cấu hình tập trung cho tất cả các dịch vụ.
 + discovery-server: Khám phá dịch vụ bằng Eureka để giao tiếp giữa các dịch vụ.
 + api-gateway: Đóng vai trò cổng vào, định tuyến các yêu cầu từ client đến các dịch vụ tương ứng
 + cart-service: Quản lý các hoạt động của giỏ hàng.
 + identity-service: Xử lý xác thực và phân quyền người dùng.
 + notification-service: Quản lý thông báo cho người dùng.
 + order-service: Xử lý và quản lý đơn hàng.
 + payment-service: Xử lý thanh toán.
 + product-service: Quản lý thông tin sản phẩm.
 + profile-service: Quản lý hồ sơ người dùng.

Yêu cầu

Docker và Docker Compose đã được cài đặt.
Java 17 hoặc cao hơn (nếu bạn muốn chạy từng dịch vụ riêng lẻ).
Maven để xây dựng dự án.

Hướng dẫn cài đặt và chạy
Clone dự án:

git clone <đường dẫn tới repository của bạn>
cd Ecommerce-Microservice
Chạy bằng Docker:
Đảm bảo bạn đang ở thư mục gốc của dự án (nơi chứa file docker-compose.yml).
Chạy lệnh sau để khởi động tất cả các dịch vụ:
docker-compose up -d



Các dịch vụ sẽ được khởi động và có thể giao tiếp với nhau thông qua discovery-server.

Kiểm tra dịch vụ:
Sau khi chạy, bạn có thể truy cập các dịch vụ qua các cổng được định nghĩa trong docker-compose.yml.



Ví dụ: truy cập discovery-server (thường là Eureka Dashboard) tại http://localhost:8761.

Lưu ý :
+ Dự án này api-gateway sẽ thực hiện xác thực bằng cách delegate cho identity-service(xác thực băng jwt),
+ Còn cac service khác thì thực hiện việc phân quyen.
+ Hãy mở swagger cổng http://localhost:8222/swagger-ui.html, để có thể request đến các api
+ Hãy vừa xem swagger và xem source code để hiểu hơn nhé! 


Ghi chú

Đảm bảo các cổng được sử dụng trong docker-compose.yml không bị xung đột trên máy của bạn.


Để chạy từng dịch vụ riêng lẻ, vào thư mục của dịch vụ đó và sử dụng lệnh:  mvn spring-boot:run

