# Hướng dẫn tích hợp cấu trúc multi-module (parent POM + common modules)

## 1. Cấu trúc thư mục sau khi migrate

```
project01/                              <-- copy pom.xml (parent) vào đây
├── pom.xml                             <-- MỚI: parent POM, packaging=pom
├── common/                             <-- MỚI
│   ├── common-core/                    (ApiResponse, AppException, AbstractEntity...)
│   ├── common-security-jwt/            (WebSecurityConfig, JwtDecoder...)
│   ├── common-web/                     (Swagger)
│   ├── common-feign/                   (Feign interceptor)
│   └── common-kafka/                   (Kafka producer)
├── api-gateway/
├── cart-service/
├── chat-service/
├── config-server/
├── discovery-server/
├── identity-service/
├── notification-service/
├── order-service/
├── payment-service/
├── product-service/
└── profile-service/
```

Copy toàn bộ thư mục `common/` và file `pom.xml` (parent) vào đúng gốc `project01/`, ngang hàng với các service.

## 2. Bảng: service nào cần module common nào

Không phải service nào cũng cần add cả 5 module — đây chính là điểm khác biệt so với bản `common-lib` gộp chung trước đó:

| Service | common-core | common-security-jwt | common-web | common-feign | common-kafka |
|---|:---:|:---:|:---:|:---:|:---:|
| identity-service | ✔ | ✔ | ✔ | ✔ | ✔ |
| cart-service | ✔ | ✔ | ✔ | | |
| product-service | ✔ | ✔ | ✔ | | |
| profile-service | ✔ | ✔ | ✔ | | |
| chat-service | ✔ | ✔ | ✔ | | |
| order-service | ✔ | ✔ | ✔ | ✔ | ✔ |
| payment-service | ✔ | ✔ | ✔ | | ✔ |
| notification-service | ✔ | | | | ✔ |
| api-gateway | ✔ (chỉ ApiResponse) | ✗ (WebFlux, không dùng được) | | | |
| config-server / discovery-server | | | | | |

*(Bạn tự đối chiếu lại với code thật — bảng này dựa trên những gì tôi thấy trong file dump bạn gửi ban đầu, có thể lệch nếu bạn đã sửa thêm.)*

## 3. Bước bắt buộc trước tiên: đồng bộ version `chat-service`

Parent POM chỉ hoạt động đúng khi mọi module chung 1 phiên bản Spring Boot nền. Hiện `chat-service` đang ở `3.5.3`/Java 21, khác với `3.2.4`/Java 17 của các service còn lại.

Mở `chat-service/pom.xml`, xóa hẳn khối `<parent>spring-boot-starter-parent...</parent>` và `<properties><java.version>21</java.version></properties>`, thay bằng:

```xml
<parent>
    <groupId>com.project01</groupId>
    <artifactId>project01-parent</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <relativePath>../pom.xml</relativePath>
</parent>
```

Sau đó `mvn clean compile` riêng `chat-service` để bắt lỗi API nào giữa Boot 3.5.3 và 3.2.4 bị khác (khả năng cao là không có gì đáng kể vì đây cùng dòng Boot 3.x, nhưng phải tự kiểm tra vì tôi không có source thật của `chat-service` để test).

**Nếu không muốn đụng vào `chat-service` lúc này:** bạn có thể tạm KHÔNG liệt kê `chat-service` vào `<modules>` của parent POM (xóa dòng `<module>chat-service</module>`), để nó đứng độc lập như cũ, join sau khi rảnh. Đây cũng là cách làm rất thật trong thực tế — không phải lúc nào công ty cũng migrate hết 1 lần, thường làm cuốn chiếu từng service.

## 4. Sửa `pom.xml` từng service

Xem file mẫu `cart-service.pom.xml.EXAMPLE` đính kèm — đó là bản đầy đủ sau khi migrate. Các thay đổi chính, áp dụng cho MỌI service:

1. Đổi `<parent>` từ `spring-boot-starter-parent` sang `project01-parent`.
2. Xóa `<properties><java.version>...</java.version></properties>` (đã có ở parent).
3. Thêm các module common cần thiết (theo bảng ở mục 2) — **không cần ghi `<version>`**.
4. **Xóa các dependency đã được kéo theo (transitive) qua common module** — đây là khác biệt lớn nhất so với bản trước: giờ được phép xóa thật.

   Cụ thể, nếu service đã add `common-security-jwt`, xóa 2 dòng này khỏi `pom.xml` của service (không cần nữa, `common-security-jwt` đã kéo theo):
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-security</artifactId>
   </dependency>
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
   </dependency>
   ```
   Nếu đã add `common-core`, xóa `spring-boot-starter-web`. Nếu đã add `common-web`, xóa `springdoc-openapi-starter-webmvc-ui`. Nếu đã add `common-feign`, xóa `spring-cloud-starter-openfeign`. Nếu đã add `common-kafka`, xóa `spring-kafka`. Nếu đã add `common-core`/bất kỳ module nào, xóa luôn khai báo `lombok` riêng lẻ (parent đã khai chung ở gốc).

   **Cách kiểm tra không sót/không xóa nhầm:** chạy `mvn dependency:tree` trong service đó, tìm dòng có group `com.project01` — nó sẽ liệt kê hết những gì được kéo theo qua common module, đối chiếu với danh sách dependency cũ để biết cái nào an toàn để xóa.

## 5. Xóa file `.java` trùng lặp (giống bước trước, không đổi)

Danh sách file cần xóa ở từng service giữ nguyên như hướng dẫn lần trước — chỉ khác package import bây giờ đổi thành `com.project01.common.core.*` / `com.project01.common.security.jwt.*` thay vì `com.project01.common.*`:

```
config/JwtDecoderCustom.java              -> xóa (dùng common-security-jwt)
config/JwtAuthenticationEntryPoint.java   -> xóa (dùng common-security-jwt)
config/AccessDenied.java                  -> xóa (dùng common-security-jwt)
config/WebSecurityConfig.java             -> xóa (dùng common-security-jwt)
config/SwaggerConfig.java                 -> xóa (dùng common-web)
exception/GolbalException.java            -> xóa (dùng common-core)
exception/custom/AppException.java        -> xóa (dùng common-core)
dto/response/ApiResponse.java             -> xóa (dùng common-core)
config/AuthenticationRequestIntercepter.java -> xóa nếu có (dùng common-feign)
```

`ErrorCode.java` giữ nguyên, chỉ đổi:
```java
import com.project01.common.core.exception.BaseErrorCode;

public enum ErrorCode implements BaseErrorCode {
    // ... giữ nguyên các giá trị enum
}
```

`AbstractEntity` nếu có, đổi import sang `com.project01.common.core.entity.AbstractEntity`, xóa file cũ.

## 6. Build toàn bộ hệ thống bằng 1 lệnh

Từ thư mục gốc `project01/`:

```bash
mvn clean install
```

Maven Reactor sẽ tự tính thứ tự build đúng (common modules trước, service sau) dựa vào dependency graph, không cần bạn tự cài từng module bằng tay như cách cũ.

Muốn build/chạy riêng 1 service (ví dụ đang code dở `cart-service`), 2 cách:

```bash
# Cách 1: build riêng module đó (nhưng common phải build trước ít nhất 1 lần)
mvn clean install -pl cart-service -am

# Cách 2: nếu đã install common vào .m2 rồi, không cần -am nữa
cd cart-service && mvn spring-boot:run
```

`-pl` = chỉ build module chỉ định, `-am` = "also make" - tự build luôn các module mà nó phụ thuộc (ở đây là các common module).

## 7. Thứ tự làm để tránh vỡ trận

1. Copy `pom.xml` (parent) + thư mục `common/` vào gốc project.
2. `cd` vào gốc, chạy `mvn clean install -pl common/common-core,common/common-security-jwt,common/common-web,common/common-feign,common/common-kafka -am` để build riêng 5 module common trước, chắc chắn chúng tự compile được.
3. Migrate **1 service duy nhất** (khuyên chọn `cart-service` trước, xem ví dụ `.EXAMPLE`) theo mục 3-5. Build + chạy thử service đó.
4. Nếu ổn, lặp lại cho các service còn lại, mỗi lần 1 service, build/test ngay sau khi sửa.
5. `chat-service` để cuối cùng (cần đồng bộ version trước, xem mục 3).
