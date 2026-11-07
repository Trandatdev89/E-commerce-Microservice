-- =====================================================================
-- E-commerce Microservice - MySQL schema
-- Yêu cầu: MySQL 8.0.16+ (cần CHECK constraint, utf8mb4_0900_ai_ci)
--
-- Quy ước:
--   * Mọi service dùng chung 1 database `ecommerce`, phân biệt bằng tiền tố bảng:
--       idt_ identity | loc_ location | prd_ product | inv_ inventory | crt_ cart
--       ord_ order    | pay_ payment  | shp_ shipping | prm_ promotion
--   * Service chỉ đọc/ghi bảng có tiền tố của mình.
--   * FOREIGN KEY chỉ tạo giữa các bảng CÙNG service. Tham chiếu sang service
--     khác chỉ lưu id/code (cột có comment "ref ...") và KHÔNG có FK.
--   * Bảng danh mục: có `code` duy nhất, không xóa cứng mà set is_active = 0.
--   * Tiền: DECIMAL(15,2). Thời gian: DATETIME(6).
-- =====================================================================

-- ---------------------------------------------------------------------
-- Tạo database
-- ---------------------------------------------------------------------
-- DROP DATABASE IF EXISTS ecommerce;   -- bỏ comment nếu muốn tạo lại từ đầu
CREATE DATABASE IF NOT EXISTS ecommerce
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_0900_ai_ci;

-- User riêng cho ứng dụng (khuyến nghị thay vì dùng root). Đổi mật khẩu trước khi chạy.
-- CREATE USER IF NOT EXISTS 'ecommerce_app'@'%' IDENTIFIED BY 'change_me';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON ecommerce.* TO 'ecommerce_app'@'%';
-- FLUSH PRIVILEGES;

USE ecommerce;


-- =====================================================================
-- IDENTITY SERVICE
-- =====================================================================

CREATE TABLE IF NOT EXISTS idt_role (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    code        VARCHAR(50)  NOT NULL COMMENT 'Mã vai trò, dùng trong JWT: ADMIN, CLIENT, SELLER, SHIPPER',
    name        VARCHAR(100) NOT NULL COMMENT 'Tên hiển thị của vai trò',
    description VARCHAR(255) NULL     COMMENT 'Mô tả vai trò',
    is_active   TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '1: đang dùng, 0: ngừng dùng',
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    created_by  VARCHAR(100) NULL     COMMENT 'Người tạo',
    updated_at  DATETIME(6)  NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    updated_by  VARCHAR(100) NULL     COMMENT 'Người cập nhật gần nhất',
    PRIMARY KEY (id),
    UNIQUE KEY uk_idt_role_code (code)
) ENGINE=InnoDB COMMENT='[Danh mục] Vai trò người dùng';

CREATE TABLE IF NOT EXISTS idt_permission (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    code        VARCHAR(100) NOT NULL COMMENT 'Mã quyền, dạng RESOURCE_ACTION, vd ORDER_READ',
    resource    VARCHAR(50)  NOT NULL COMMENT 'Tài nguyên được bảo vệ, vd ORDER, PRODUCT',
    action      VARCHAR(50)  NOT NULL COMMENT 'Hành động: READ, CREATE, UPDATE, DELETE',
    description VARCHAR(255) NULL     COMMENT 'Mô tả quyền',
    is_active   TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '1: đang dùng, 0: ngừng dùng',
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    created_by  VARCHAR(100) NULL     COMMENT 'Người tạo',
    updated_at  DATETIME(6)  NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    updated_by  VARCHAR(100) NULL     COMMENT 'Người cập nhật gần nhất',
    PRIMARY KEY (id),
    UNIQUE KEY uk_idt_permission_code (code),
    UNIQUE KEY uk_idt_permission_resource_action (resource, action)
) ENGINE=InnoDB COMMENT='[Danh mục] Quyền chi tiết trên từng tài nguyên';

CREATE TABLE IF NOT EXISTS idt_role_permission (
    role_id       BIGINT       NOT NULL COMMENT 'FK -> idt_role.id',
    permission_id BIGINT       NOT NULL COMMENT 'FK -> idt_permission.id',
    created_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm gán quyền',
    created_by    VARCHAR(100) NULL     COMMENT 'Người gán quyền',
    PRIMARY KEY (role_id, permission_id),
    KEY idx_idt_role_permission_permission (permission_id),
    CONSTRAINT fk_idt_role_permission_role       FOREIGN KEY (role_id)       REFERENCES idt_role (id),
    CONSTRAINT fk_idt_role_permission_permission FOREIGN KEY (permission_id) REFERENCES idt_permission (id)
) ENGINE=InnoDB COMMENT='[Danh mục] Bảng nối vai trò - quyền (role nào có quyền gì)';

CREATE TABLE IF NOT EXISTS idt_account (
    id             CHAR(36)     NOT NULL COMMENT 'Khóa chính, UUID - dùng làm userId trên toàn hệ thống',
    username       VARCHAR(50)  NOT NULL COMMENT 'Tên đăng nhập',
    email          VARCHAR(150) NOT NULL COMMENT 'Email đăng nhập / nhận thông báo',
    phone          VARCHAR(20)  NULL     COMMENT 'Số điện thoại',
    password_hash  VARCHAR(255) NOT NULL COMMENT 'Mật khẩu đã băm bằng BCrypt, không bao giờ lưu plain text',
    status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT 'Trạng thái: PENDING (chờ xác thực OTP), ACTIVE, LOCKED',
    email_verified TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '1: đã xác thực email bằng OTP',
    last_login_at  DATETIME(6)  NULL     COMMENT 'Lần đăng nhập thành công gần nhất',
    created_at     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    created_by     VARCHAR(100) NULL     COMMENT 'Người tạo',
    updated_at     DATETIME(6)  NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    updated_by     VARCHAR(100) NULL     COMMENT 'Người cập nhật gần nhất',
    PRIMARY KEY (id),
    UNIQUE KEY uk_idt_account_username (username),
    UNIQUE KEY uk_idt_account_email (email),
    KEY idx_idt_account_phone (phone),
    CONSTRAINT ck_idt_account_status CHECK (status IN ('PENDING', 'ACTIVE', 'LOCKED'))
) ENGINE=InnoDB COMMENT='[Data] Tài khoản đăng nhập';

CREATE TABLE IF NOT EXISTS idt_account_role (
    account_id CHAR(36)     NOT NULL COMMENT 'FK -> idt_account.id',
    role_id    BIGINT       NOT NULL COMMENT 'FK -> idt_role.id',
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm gán vai trò',
    created_by VARCHAR(100) NULL     COMMENT 'Người gán vai trò',
    PRIMARY KEY (account_id, role_id),
    KEY idx_idt_account_role_role (role_id),
    CONSTRAINT fk_idt_account_role_account FOREIGN KEY (account_id) REFERENCES idt_account (id),
    CONSTRAINT fk_idt_account_role_role    FOREIGN KEY (role_id)    REFERENCES idt_role (id)
) ENGINE=InnoDB COMMENT='[Data] Vai trò của từng tài khoản (role do server gán, không nhận từ client)';

CREATE TABLE IF NOT EXISTS idt_refresh_token (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    account_id CHAR(36)     NOT NULL COMMENT 'FK -> idt_account.id',
    token_hash CHAR(64)     NOT NULL COMMENT 'SHA-256 của refresh token, không lưu token gốc',
    expires_at DATETIME(6)  NOT NULL COMMENT 'Thời điểm hết hạn',
    revoked    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '1: đã thu hồi (logout / đổi mật khẩu)',
    user_agent VARCHAR(255) NULL     COMMENT 'Thiết bị / trình duyệt cấp token',
    ip_address VARCHAR(45)  NULL     COMMENT 'IP lúc cấp token (đủ dài cho IPv6)',
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cấp token',
    PRIMARY KEY (id),
    UNIQUE KEY uk_idt_refresh_token_hash (token_hash),
    KEY idx_idt_refresh_token_account (account_id),
    CONSTRAINT fk_idt_refresh_token_account FOREIGN KEY (account_id) REFERENCES idt_account (id)
) ENGINE=InnoDB COMMENT='[Data] Refresh token đã cấp (OTP và JWT blacklist vẫn lưu ở Redis)';


-- =====================================================================
-- LOCATION (danh mục dùng chung - do profile-service quản lý)
-- Từ 01/07/2025 Việt Nam bỏ cấp huyện, chỉ còn tỉnh/thành và xã/phường.
-- =====================================================================

CREATE TABLE IF NOT EXISTS loc_province (
    code       VARCHAR(10)  NOT NULL COMMENT 'Mã tỉnh/thành theo danh mục hành chính',
    name       VARCHAR(100) NOT NULL COMMENT 'Tên tỉnh/thành',
    is_active  TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '1: đang hiệu lực, 0: đã giải thể/sáp nhập',
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    updated_at DATETIME(6)  NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    PRIMARY KEY (code)
) ENGINE=InnoDB COMMENT='[Danh mục] Tỉnh / thành phố';

CREATE TABLE IF NOT EXISTS loc_ward (
    code          VARCHAR(10)  NOT NULL COMMENT 'Mã xã/phường theo danh mục hành chính',
    province_code VARCHAR(10)  NOT NULL COMMENT 'FK -> loc_province.code',
    name          VARCHAR(150) NOT NULL COMMENT 'Tên xã/phường/đặc khu',
    is_active     TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '1: đang hiệu lực, 0: đã giải thể/sáp nhập',
    created_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    updated_at    DATETIME(6)  NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    PRIMARY KEY (code),
    KEY idx_loc_ward_province (province_code),
    CONSTRAINT fk_loc_ward_province FOREIGN KEY (province_code) REFERENCES loc_province (code)
) ENGINE=InnoDB COMMENT='[Danh mục] Xã / phường / đặc khu';


-- =====================================================================
-- PRODUCT SERVICE
-- =====================================================================

CREATE TABLE IF NOT EXISTS prd_category (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    parent_id   BIGINT       NULL     COMMENT 'FK -> prd_category.id, NULL nếu là danh mục gốc',
    name        VARCHAR(150) NOT NULL COMMENT 'Tên danh mục',
    slug        VARCHAR(180) NOT NULL COMMENT 'Đường dẫn thân thiện SEO, vd dien-thoai',
    image_url   VARCHAR(500) NULL     COMMENT 'Ảnh đại diện danh mục',
    sort_order  INT          NOT NULL DEFAULT 0 COMMENT 'Thứ tự hiển thị, nhỏ đứng trước',
    is_active   TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '1: hiển thị, 0: ẩn',
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    created_by  VARCHAR(100) NULL     COMMENT 'Người tạo',
    updated_at  DATETIME(6)  NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    updated_by  VARCHAR(100) NULL     COMMENT 'Người cập nhật gần nhất',
    PRIMARY KEY (id),
    UNIQUE KEY uk_prd_category_slug (slug),
    KEY idx_prd_category_parent (parent_id),
    CONSTRAINT fk_prd_category_parent FOREIGN KEY (parent_id) REFERENCES prd_category (id)
) ENGINE=InnoDB COMMENT='[Danh mục] Danh mục sản phẩm dạng cây (cha - con)';

CREATE TABLE IF NOT EXISTS prd_brand (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    name       VARCHAR(150) NOT NULL COMMENT 'Tên thương hiệu',
    slug       VARCHAR(180) NOT NULL COMMENT 'Đường dẫn thân thiện SEO',
    logo_url   VARCHAR(500) NULL     COMMENT 'Logo thương hiệu',
    is_active  TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '1: đang dùng, 0: ngừng dùng',
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    created_by VARCHAR(100) NULL     COMMENT 'Người tạo',
    updated_at DATETIME(6)  NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    updated_by VARCHAR(100) NULL     COMMENT 'Người cập nhật gần nhất',
    PRIMARY KEY (id),
    UNIQUE KEY uk_prd_brand_slug (slug)
) ENGINE=InnoDB COMMENT='[Danh mục] Thương hiệu';

CREATE TABLE IF NOT EXISTS prd_attribute (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    code       VARCHAR(50)  NOT NULL COMMENT 'Mã thuộc tính: COLOR, SIZE, STORAGE',
    name       VARCHAR(100) NOT NULL COMMENT 'Tên hiển thị: Màu sắc, Kích cỡ, Dung lượng',
    sort_order INT          NOT NULL DEFAULT 0 COMMENT 'Thứ tự hiển thị',
    is_active  TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '1: đang dùng, 0: ngừng dùng',
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    created_by VARCHAR(100) NULL     COMMENT 'Người tạo',
    updated_at DATETIME(6)  NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    updated_by VARCHAR(100) NULL     COMMENT 'Người cập nhật gần nhất',
    PRIMARY KEY (id),
    UNIQUE KEY uk_prd_attribute_code (code)
) ENGINE=InnoDB COMMENT='[Danh mục] Loại thuộc tính để tạo biến thể sản phẩm';

CREATE TABLE IF NOT EXISTS prd_attribute_value (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    attribute_id BIGINT       NOT NULL COMMENT 'FK -> prd_attribute.id',
    value        VARCHAR(100) NOT NULL COMMENT 'Giá trị: Đỏ, XL, 128GB',
    sort_order   INT          NOT NULL DEFAULT 0 COMMENT 'Thứ tự hiển thị',
    is_active    TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '1: đang dùng, 0: ngừng dùng',
    created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    created_by   VARCHAR(100) NULL     COMMENT 'Người tạo',
    updated_at   DATETIME(6)  NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    updated_by   VARCHAR(100) NULL     COMMENT 'Người cập nhật gần nhất',
    PRIMARY KEY (id),
    UNIQUE KEY uk_prd_attribute_value (attribute_id, value),
    CONSTRAINT fk_prd_attribute_value_attribute FOREIGN KEY (attribute_id) REFERENCES prd_attribute (id)
) ENGINE=InnoDB COMMENT='[Danh mục] Giá trị cụ thể của từng thuộc tính';

CREATE TABLE IF NOT EXISTS prd_product (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    category_id BIGINT       NOT NULL COMMENT 'FK -> prd_category.id',
    brand_id    BIGINT       NULL     COMMENT 'FK -> prd_brand.id, NULL nếu không có thương hiệu',
    name        VARCHAR(255) NOT NULL COMMENT 'Tên sản phẩm',
    slug        VARCHAR(280) NOT NULL COMMENT 'Đường dẫn thân thiện SEO',
    description TEXT         NULL     COMMENT 'Mô tả chi tiết (HTML/Markdown)',
    status      VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT 'Trạng thái: DRAFT (nháp), ACTIVE (đang bán), HIDDEN (ẩn)',
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    created_by  VARCHAR(100) NULL     COMMENT 'Người tạo',
    updated_at  DATETIME(6)  NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    updated_by  VARCHAR(100) NULL     COMMENT 'Người cập nhật gần nhất',
    PRIMARY KEY (id),
    UNIQUE KEY uk_prd_product_slug (slug),
    KEY idx_prd_product_category (category_id, status),
    KEY idx_prd_product_brand (brand_id),
    FULLTEXT KEY ftx_prd_product_name (name),
    CONSTRAINT fk_prd_product_category FOREIGN KEY (category_id) REFERENCES prd_category (id),
    CONSTRAINT fk_prd_product_brand    FOREIGN KEY (brand_id)    REFERENCES prd_brand (id),
    CONSTRAINT ck_prd_product_status CHECK (status IN ('DRAFT', 'ACTIVE', 'HIDDEN'))
) ENGINE=InnoDB COMMENT='[Data] Sản phẩm gốc (SPU), vd "Áo polo nam"';

CREATE TABLE IF NOT EXISTS prd_sku (
    id               BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính - các service khác tham chiếu sản phẩm qua cột này',
    product_id       BIGINT        NOT NULL COMMENT 'FK -> prd_product.id',
    sku_code         VARCHAR(64)   NOT NULL COMMENT 'Mã SKU duy nhất, vd POLO-RED-L',
    name             VARCHAR(255)  NOT NULL COMMENT 'Tên biến thể, vd "Đỏ / L"',
    price            DECIMAL(15,2) NOT NULL COMMENT 'Giá bán hiện tại',
    compare_at_price DECIMAL(15,2) NULL     COMMENT 'Giá gốc để hiển thị gạch ngang, NULL nếu không giảm',
    weight_gram      INT           NULL     COMMENT 'Khối lượng (gram), dùng tính phí vận chuyển',
    status           VARCHAR(20)   NOT NULL DEFAULT 'ACTIVE' COMMENT 'Trạng thái: ACTIVE, INACTIVE',
    version          INT           NOT NULL DEFAULT 0 COMMENT 'Optimistic lock (JPA @Version)',
    created_at       DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    created_by       VARCHAR(100)  NULL     COMMENT 'Người tạo',
    updated_at       DATETIME(6)   NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    updated_by       VARCHAR(100)  NULL     COMMENT 'Người cập nhật gần nhất',
    PRIMARY KEY (id),
    UNIQUE KEY uk_prd_sku_code (sku_code),
    KEY idx_prd_sku_product (product_id),
    CONSTRAINT fk_prd_sku_product FOREIGN KEY (product_id) REFERENCES prd_product (id),
    CONSTRAINT ck_prd_sku_price CHECK (price >= 0),
    CONSTRAINT ck_prd_sku_compare_price CHECK (compare_at_price IS NULL OR compare_at_price >= price),
    CONSTRAINT ck_prd_sku_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
) ENGINE=InnoDB COMMENT='[Data] Biến thể bán được (SKU) - giá nằm ở đây, tồn kho nằm ở inv_stock';

CREATE TABLE IF NOT EXISTS prd_sku_attribute_value (
    sku_id             BIGINT NOT NULL COMMENT 'FK -> prd_sku.id',
    attribute_value_id BIGINT NOT NULL COMMENT 'FK -> prd_attribute_value.id',
    PRIMARY KEY (sku_id, attribute_value_id),
    KEY idx_prd_sku_attr_value (attribute_value_id),
    CONSTRAINT fk_prd_sku_attr_sku   FOREIGN KEY (sku_id)             REFERENCES prd_sku (id),
    CONSTRAINT fk_prd_sku_attr_value FOREIGN KEY (attribute_value_id) REFERENCES prd_attribute_value (id)
) ENGINE=InnoDB COMMENT='[Data] Bảng nối SKU - giá trị thuộc tính (SKU này là màu gì, size gì)';

CREATE TABLE IF NOT EXISTS prd_product_image (
    id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    product_id BIGINT       NOT NULL COMMENT 'FK -> prd_product.id',
    sku_id     BIGINT       NULL     COMMENT 'FK -> prd_sku.id, NULL nếu là ảnh chung của sản phẩm',
    url        VARCHAR(500) NOT NULL COMMENT 'URL ảnh (Cloudinary)',
    public_id  VARCHAR(255) NULL     COMMENT 'public_id trên Cloudinary, dùng để xóa ảnh',
    sort_order INT          NOT NULL DEFAULT 0 COMMENT 'Thứ tự hiển thị, 0 là ảnh đại diện',
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    created_by VARCHAR(100) NULL     COMMENT 'Người tạo',
    PRIMARY KEY (id),
    KEY idx_prd_product_image_product (product_id, sort_order),
    KEY idx_prd_product_image_sku (sku_id),
    CONSTRAINT fk_prd_product_image_product FOREIGN KEY (product_id) REFERENCES prd_product (id),
    CONSTRAINT fk_prd_product_image_sku     FOREIGN KEY (sku_id)     REFERENCES prd_sku (id)
) ENGINE=InnoDB COMMENT='[Data] Ảnh sản phẩm / ảnh riêng của từng biến thể';


-- =====================================================================
-- INVENTORY SERVICE
-- =====================================================================

CREATE TABLE IF NOT EXISTS inv_warehouse (
    id             BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    code           VARCHAR(50)  NOT NULL COMMENT 'Mã kho, vd HN01, HCM01',
    name           VARCHAR(150) NOT NULL COMMENT 'Tên kho',
    province_code  VARCHAR(10)  NULL     COMMENT 'ref loc_province.code (không FK)',
    ward_code      VARCHAR(10)  NULL     COMMENT 'ref loc_ward.code (không FK)',
    address_detail VARCHAR(255) NULL     COMMENT 'Số nhà, đường',
    is_active      TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '1: đang hoạt động, 0: ngừng hoạt động',
    created_at     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    created_by     VARCHAR(100) NULL     COMMENT 'Người tạo',
    updated_at     DATETIME(6)  NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    updated_by     VARCHAR(100) NULL     COMMENT 'Người cập nhật gần nhất',
    PRIMARY KEY (id),
    UNIQUE KEY uk_inv_warehouse_code (code)
) ENGINE=InnoDB COMMENT='[Danh mục] Kho hàng';

CREATE TABLE IF NOT EXISTS inv_stock (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    sku_id       BIGINT       NOT NULL COMMENT 'ref prd_sku.id (không FK - khác service)',
    warehouse_id BIGINT       NOT NULL COMMENT 'FK -> inv_warehouse.id',
    on_hand      INT          NOT NULL DEFAULT 0 COMMENT 'Số lượng thực tế đang có trong kho',
    reserved     INT          NOT NULL DEFAULT 0 COMMENT 'Số lượng đang được giữ cho đơn chưa thanh toán. Có thể bán = on_hand - reserved',
    version      INT          NOT NULL DEFAULT 0 COMMENT 'Optimistic lock (JPA @Version) - chống bán quá tồn kho',
    created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    updated_at   DATETIME(6)  NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    updated_by   VARCHAR(100) NULL     COMMENT 'Người cập nhật gần nhất',
    PRIMARY KEY (id),
    UNIQUE KEY uk_inv_stock_sku_warehouse (sku_id, warehouse_id),
    KEY idx_inv_stock_warehouse (warehouse_id),
    CONSTRAINT fk_inv_stock_warehouse FOREIGN KEY (warehouse_id) REFERENCES inv_warehouse (id),
    CONSTRAINT ck_inv_stock_on_hand  CHECK (on_hand >= 0),
    CONSTRAINT ck_inv_stock_reserved CHECK (reserved >= 0 AND reserved <= on_hand)
) ENGINE=InnoDB COMMENT='[Data] Tồn kho của từng SKU tại từng kho';

CREATE TABLE IF NOT EXISTS inv_reservation (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    order_code   VARCHAR(30)  NOT NULL COMMENT 'ref ord_order.order_code (không FK)',
    sku_id       BIGINT       NOT NULL COMMENT 'ref prd_sku.id (không FK)',
    warehouse_id BIGINT       NOT NULL COMMENT 'FK -> inv_warehouse.id - kho giữ hàng',
    quantity     INT          NOT NULL COMMENT 'Số lượng giữ',
    status       VARCHAR(20)  NOT NULL DEFAULT 'RESERVED' COMMENT 'RESERVED (đang giữ), CONFIRMED (đã trừ kho khi thanh toán xong), RELEASED (đã nhả - bước bù trừ Saga)',
    expires_at   DATETIME(6)  NOT NULL COMMENT 'Hết hạn giữ hàng, job sẽ tự RELEASED nếu quá hạn',
    created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm giữ hàng',
    updated_at   DATETIME(6)  NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm đổi trạng thái gần nhất',
    PRIMARY KEY (id),
    UNIQUE KEY uk_inv_reservation_order_sku (order_code, sku_id, warehouse_id),
    KEY idx_inv_reservation_status_expire (status, expires_at),
    CONSTRAINT fk_inv_reservation_warehouse FOREIGN KEY (warehouse_id) REFERENCES inv_warehouse (id),
    CONSTRAINT ck_inv_reservation_quantity CHECK (quantity > 0),
    CONSTRAINT ck_inv_reservation_status CHECK (status IN ('RESERVED', 'CONFIRMED', 'RELEASED'))
) ENGINE=InnoDB COMMENT='[Data] Giữ hàng cho đơn đang xử lý - bước chính của Saga đặt hàng';

CREATE TABLE IF NOT EXISTS inv_stock_movement (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    sku_id       BIGINT       NOT NULL COMMENT 'ref prd_sku.id (không FK)',
    warehouse_id BIGINT       NOT NULL COMMENT 'FK -> inv_warehouse.id',
    type         VARCHAR(20)  NOT NULL COMMENT 'IN (nhập), OUT (xuất bán), RETURN (khách trả), ADJUST (kiểm kê điều chỉnh)',
    quantity     INT          NOT NULL COMMENT 'Số lượng thay đổi, luôn dương; chiều tăng/giảm xác định theo type',
    ref_code     VARCHAR(50)  NULL     COMMENT 'Mã chứng từ liên quan: order_code, mã phiếu nhập...',
    note         VARCHAR(255) NULL     COMMENT 'Ghi chú',
    created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm phát sinh',
    created_by   VARCHAR(100) NULL     COMMENT 'Người thực hiện',
    PRIMARY KEY (id),
    KEY idx_inv_movement_sku (sku_id, created_at),
    KEY idx_inv_movement_ref (ref_code),
    CONSTRAINT fk_inv_movement_warehouse FOREIGN KEY (warehouse_id) REFERENCES inv_warehouse (id),
    CONSTRAINT ck_inv_movement_quantity CHECK (quantity > 0),
    CONSTRAINT ck_inv_movement_type CHECK (type IN ('IN', 'OUT', 'RETURN', 'ADJUST'))
) ENGINE=InnoDB COMMENT='[Data] Lịch sử nhập / xuất kho (chỉ thêm, không sửa / xóa)';


-- =====================================================================
-- CART SERVICE (Redis vẫn dùng làm cache phía trước)
-- =====================================================================

CREATE TABLE IF NOT EXISTS crt_cart (
    id         BIGINT      NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    user_id    CHAR(36)    NOT NULL COMMENT 'ref idt_account.id (không FK) - mỗi user 1 giỏ',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo giỏ',
    updated_at DATETIME(6) NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Lần thay đổi giỏ gần nhất',
    PRIMARY KEY (id),
    UNIQUE KEY uk_crt_cart_user (user_id)
) ENGINE=InnoDB COMMENT='[Data] Giỏ hàng của người dùng';

CREATE TABLE IF NOT EXISTS crt_cart_item (
    id         BIGINT      NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    cart_id    BIGINT      NOT NULL COMMENT 'FK -> crt_cart.id',
    sku_id     BIGINT      NOT NULL COMMENT 'ref prd_sku.id (không FK). Không lưu giá - luôn lấy giá hiện tại từ product-service',
    quantity   INT         NOT NULL COMMENT 'Số lượng muốn mua',
    added_at   DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm thêm vào giỏ',
    updated_at DATETIME(6) NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Lần đổi số lượng gần nhất',
    PRIMARY KEY (id),
    UNIQUE KEY uk_crt_cart_item_sku (cart_id, sku_id),
    CONSTRAINT fk_crt_cart_item_cart FOREIGN KEY (cart_id) REFERENCES crt_cart (id) ON DELETE CASCADE,
    CONSTRAINT ck_crt_cart_item_quantity CHECK (quantity > 0)
) ENGINE=InnoDB COMMENT='[Data] Sản phẩm trong giỏ hàng';


-- =====================================================================
-- ORDER SERVICE
-- =====================================================================

CREATE TABLE IF NOT EXISTS ord_status (
    code        VARCHAR(30)  NOT NULL COMMENT 'Mã trạng thái: PENDING, CONFIRMED, PAID, SHIPPING, DELIVERED, CANCELLED, RETURNED',
    name        VARCHAR(100) NOT NULL COMMENT 'Tên hiển thị',
    description VARCHAR(255) NULL     COMMENT 'Ý nghĩa trạng thái',
    is_final    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '1: trạng thái kết thúc, không chuyển tiếp được nữa',
    sort_order  INT          NOT NULL DEFAULT 0 COMMENT 'Thứ tự trong vòng đời đơn hàng',
    is_active   TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '1: đang dùng, 0: ngừng dùng',
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    updated_at  DATETIME(6)  NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    PRIMARY KEY (code)
) ENGINE=InnoDB COMMENT='[Danh mục] Trạng thái đơn hàng';

CREATE TABLE IF NOT EXISTS ord_cancel_reason (
    code        VARCHAR(50)  NOT NULL COMMENT 'Mã lý do, vd CUSTOMER_CHANGED_MIND, PAYMENT_FAILED',
    name        VARCHAR(150) NOT NULL COMMENT 'Tên hiển thị',
    actor       VARCHAR(20)  NOT NULL DEFAULT 'CUSTOMER' COMMENT 'Ai được chọn lý do này: CUSTOMER, ADMIN, SYSTEM',
    sort_order  INT          NOT NULL DEFAULT 0 COMMENT 'Thứ tự hiển thị',
    is_active   TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '1: đang dùng, 0: ngừng dùng',
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    updated_at  DATETIME(6)  NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    PRIMARY KEY (code),
    CONSTRAINT ck_ord_cancel_reason_actor CHECK (actor IN ('CUSTOMER', 'ADMIN', 'SYSTEM'))
) ENGINE=InnoDB COMMENT='[Danh mục] Lý do hủy / trả đơn hàng';

CREATE TABLE IF NOT EXISTS ord_order (
    id                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính (chỉ dùng nội bộ order-service)',
    order_code          VARCHAR(30)   NOT NULL COMMENT 'Mã đơn hàng nghiệp vụ, vd ORD-20261101-8F3K2A - các service khác tham chiếu qua cột này',
    customer_id         CHAR(36)      NOT NULL COMMENT 'ref idt_account.id (không FK)',
    status              VARCHAR(30)   NOT NULL DEFAULT 'PENDING' COMMENT 'FK -> ord_status.code',
    subtotal            DECIMAL(15,2) NOT NULL COMMENT 'Tổng tiền hàng = SUM(ord_order_item.line_total), server tự tính',
    discount_amount     DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT 'Số tiền được giảm từ voucher',
    shipping_fee        DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT 'Phí vận chuyển',
    total_amount        DECIMAL(15,2) NOT NULL COMMENT 'Khách phải trả = subtotal - discount_amount + shipping_fee',
    payment_method_code VARCHAR(30)   NOT NULL COMMENT 'ref pay_method.code (không FK)',
    voucher_code        VARCHAR(50)   NULL     COMMENT 'ref prm_voucher.code (không FK), NULL nếu không dùng voucher',
    receiver_name       VARCHAR(150)  NOT NULL COMMENT 'Snapshot: tên người nhận lúc đặt hàng',
    receiver_phone      VARCHAR(20)   NOT NULL COMMENT 'Snapshot: SĐT người nhận',
    province_code       VARCHAR(10)   NOT NULL COMMENT 'Snapshot: ref loc_province.code',
    province_name       VARCHAR(100)  NOT NULL COMMENT 'Snapshot: tên tỉnh/thành lúc đặt hàng',
    ward_code           VARCHAR(10)   NOT NULL COMMENT 'Snapshot: ref loc_ward.code',
    ward_name           VARCHAR(150)  NOT NULL COMMENT 'Snapshot: tên xã/phường lúc đặt hàng',
    address_detail      VARCHAR(255)  NOT NULL COMMENT 'Snapshot: số nhà, đường',
    note                VARCHAR(500)  NULL     COMMENT 'Ghi chú của khách',
    cancel_reason_code  VARCHAR(50)   NULL     COMMENT 'FK -> ord_cancel_reason.code, chỉ có khi đơn bị hủy',
    placed_at           DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm khách đặt hàng',
    version             INT           NOT NULL DEFAULT 0 COMMENT 'Optimistic lock (JPA @Version) - tránh 2 luồng cùng đổi trạng thái',
    created_at          DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    created_by          VARCHAR(100)  NULL     COMMENT 'Người tạo',
    updated_at          DATETIME(6)   NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    updated_by          VARCHAR(100)  NULL     COMMENT 'Người cập nhật gần nhất',
    PRIMARY KEY (id),
    UNIQUE KEY uk_ord_order_code (order_code),
    KEY idx_ord_order_customer (customer_id, placed_at),
    KEY idx_ord_order_status (status, placed_at),
    CONSTRAINT fk_ord_order_status        FOREIGN KEY (status)             REFERENCES ord_status (code),
    CONSTRAINT fk_ord_order_cancel_reason FOREIGN KEY (cancel_reason_code) REFERENCES ord_cancel_reason (code),
    CONSTRAINT ck_ord_order_amounts CHECK (subtotal >= 0 AND discount_amount >= 0 AND shipping_fee >= 0 AND total_amount >= 0)
) ENGINE=InnoDB COMMENT='[Data] Đơn hàng';

CREATE TABLE IF NOT EXISTS ord_order_item (
    id           BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    order_id     BIGINT        NOT NULL COMMENT 'FK -> ord_order.id',
    sku_id       BIGINT        NOT NULL COMMENT 'ref prd_sku.id (không FK)',
    sku_code     VARCHAR(64)   NOT NULL COMMENT 'Snapshot: mã SKU lúc mua',
    product_name VARCHAR(255)  NOT NULL COMMENT 'Snapshot: tên sản phẩm lúc mua',
    sku_name     VARCHAR(255)  NOT NULL COMMENT 'Snapshot: tên biến thể lúc mua, vd "Đỏ / L"',
    image_url    VARCHAR(500)  NULL     COMMENT 'Snapshot: ảnh đại diện lúc mua',
    unit_price   DECIMAL(15,2) NOT NULL COMMENT 'Snapshot: đơn giá lúc mua (lấy từ product-service, không nhận từ client)',
    quantity     INT           NOT NULL COMMENT 'Số lượng mua',
    line_total   DECIMAL(15,2) NOT NULL COMMENT 'Thành tiền = unit_price * quantity',
    created_at   DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    PRIMARY KEY (id),
    UNIQUE KEY uk_ord_order_item_sku (order_id, sku_id),
    KEY idx_ord_order_item_sku (sku_id),
    CONSTRAINT fk_ord_order_item_order FOREIGN KEY (order_id) REFERENCES ord_order (id),
    CONSTRAINT ck_ord_order_item_quantity CHECK (quantity > 0),
    CONSTRAINT ck_ord_order_item_price CHECK (unit_price >= 0 AND line_total >= 0)
) ENGINE=InnoDB COMMENT='[Data] Chi tiết sản phẩm trong đơn hàng';

CREATE TABLE IF NOT EXISTS ord_status_history (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    order_id    BIGINT       NOT NULL COMMENT 'FK -> ord_order.id',
    from_status VARCHAR(30)  NULL     COMMENT 'FK -> ord_status.code, NULL nếu là lúc tạo đơn',
    to_status   VARCHAR(30)  NOT NULL COMMENT 'FK -> ord_status.code',
    reason_code VARCHAR(50)  NULL     COMMENT 'FK -> ord_cancel_reason.code (khi hủy / trả)',
    note        VARCHAR(500) NULL     COMMENT 'Ghi chú thêm',
    changed_by  VARCHAR(100) NOT NULL COMMENT 'Ai đổi trạng thái: userId, adminId hoặc SYSTEM',
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm đổi trạng thái',
    PRIMARY KEY (id),
    KEY idx_ord_status_history_order (order_id, created_at),
    CONSTRAINT fk_ord_status_history_order  FOREIGN KEY (order_id)    REFERENCES ord_order (id),
    CONSTRAINT fk_ord_status_history_from   FOREIGN KEY (from_status) REFERENCES ord_status (code),
    CONSTRAINT fk_ord_status_history_to     FOREIGN KEY (to_status)   REFERENCES ord_status (code),
    CONSTRAINT fk_ord_status_history_reason FOREIGN KEY (reason_code) REFERENCES ord_cancel_reason (code)
) ENGINE=InnoDB COMMENT='[Data] Lịch sử chuyển trạng thái đơn hàng (chỉ thêm, không sửa / xóa)';

CREATE TABLE IF NOT EXISTS ord_outbox (
    id             BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    event_id       CHAR(36)     NOT NULL COMMENT 'UUID của event - consumer dùng để chống xử lý trùng',
    aggregate_type VARCHAR(50)  NOT NULL DEFAULT 'ORDER' COMMENT 'Loại đối tượng phát sinh event',
    aggregate_id   VARCHAR(50)  NOT NULL COMMENT 'Mã đối tượng, thường là order_code - dùng làm Kafka key',
    event_type     VARCHAR(100) NOT NULL COMMENT 'Tên event, vd OrderCreated, OrderCancelled',
    topic          VARCHAR(100) NOT NULL COMMENT 'Kafka topic sẽ gửi tới',
    payload        JSON         NOT NULL COMMENT 'Nội dung event dạng JSON',
    status         VARCHAR(20)  NOT NULL DEFAULT 'NEW' COMMENT 'NEW (chờ gửi), SENT (đã gửi), FAILED (gửi lỗi quá số lần)',
    retry_count    INT          NOT NULL DEFAULT 0 COMMENT 'Số lần đã thử gửi',
    created_at     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm ghi event (cùng transaction với nghiệp vụ)',
    sent_at        DATETIME(6)  NULL     COMMENT 'Thời điểm gửi Kafka thành công',
    PRIMARY KEY (id),
    UNIQUE KEY uk_ord_outbox_event (event_id),
    KEY idx_ord_outbox_status (status, created_at),
    CONSTRAINT ck_ord_outbox_status CHECK (status IN ('NEW', 'SENT', 'FAILED'))
) ENGINE=InnoDB COMMENT='[Data] Outbox pattern: event chờ gửi Kafka của order-service';


-- =====================================================================
-- PAYMENT SERVICE
-- =====================================================================

CREATE TABLE IF NOT EXISTS pay_method (
    code        VARCHAR(30)  NOT NULL COMMENT 'Mã phương thức: COD, VNPAY, MOMO, BANK_TRANSFER',
    name        VARCHAR(100) NOT NULL COMMENT 'Tên hiển thị',
    description VARCHAR(255) NULL     COMMENT 'Mô tả / hướng dẫn thanh toán',
    is_online   TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '1: thanh toán qua cổng online (có callback), 0: offline (COD)',
    sort_order  INT          NOT NULL DEFAULT 0 COMMENT 'Thứ tự hiển thị',
    is_active   TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '1: đang cho phép chọn, 0: tạm tắt',
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    updated_at  DATETIME(6)  NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    PRIMARY KEY (code)
) ENGINE=InnoDB COMMENT='[Danh mục] Phương thức thanh toán';

CREATE TABLE IF NOT EXISTS pay_payment (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    payment_code    VARCHAR(40)   NOT NULL COMMENT 'Mã thanh toán gửi sang cổng thanh toán (vnp_TxnRef...)',
    order_code      VARCHAR(30)   NOT NULL COMMENT 'ref ord_order.order_code (không FK)',
    customer_id     CHAR(36)      NOT NULL COMMENT 'ref idt_account.id (không FK)',
    method_code     VARCHAR(30)   NOT NULL COMMENT 'FK -> pay_method.code',
    amount          DECIMAL(15,2) NOT NULL COMMENT 'Số tiền cần thanh toán',
    currency        CHAR(3)       NOT NULL DEFAULT 'VND' COMMENT 'Mã tiền tệ ISO 4217',
    status          VARCHAR(20)   NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, SUCCESS, FAILED, CANCELLED, REFUNDED',
    provider_txn_id VARCHAR(100)  NULL     COMMENT 'Mã giao dịch phía cổng thanh toán trả về',
    idempotency_key VARCHAR(64)   NOT NULL COMMENT 'Khóa chống tạo trùng thanh toán khi request bị gửi lại',
    failure_reason  VARCHAR(255)  NULL     COMMENT 'Lý do thất bại (nếu có)',
    paid_at         DATETIME(6)   NULL     COMMENT 'Thời điểm thanh toán thành công',
    version         INT           NOT NULL DEFAULT 0 COMMENT 'Optimistic lock - callback của cổng có thể tới nhiều lần',
    created_at      DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    created_by      VARCHAR(100)  NULL     COMMENT 'Người tạo',
    updated_at      DATETIME(6)   NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    updated_by      VARCHAR(100)  NULL     COMMENT 'Người cập nhật gần nhất',
    PRIMARY KEY (id),
    UNIQUE KEY uk_pay_payment_code (payment_code),
    UNIQUE KEY uk_pay_payment_idempotency (idempotency_key),
    KEY idx_pay_payment_order (order_code),
    KEY idx_pay_payment_customer (customer_id, created_at),
    KEY idx_pay_payment_provider_txn (provider_txn_id),
    CONSTRAINT fk_pay_payment_method FOREIGN KEY (method_code) REFERENCES pay_method (code),
    CONSTRAINT ck_pay_payment_amount CHECK (amount >= 0),
    CONSTRAINT ck_pay_payment_status CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED', 'REFUNDED'))
) ENGINE=InnoDB COMMENT='[Data] Thanh toán của đơn hàng (1 đơn có thể có nhiều lần thanh toán nếu lần trước thất bại)';

CREATE TABLE IF NOT EXISTS pay_transaction (
    id           BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    payment_id   BIGINT        NOT NULL COMMENT 'FK -> pay_payment.id',
    type         VARCHAR(20)   NOT NULL COMMENT 'CHARGE (yêu cầu thu tiền), CALLBACK (cổng gọi về / IPN), REFUND (hoàn tiền)',
    amount       DECIMAL(15,2) NULL     COMMENT 'Số tiền của giao dịch',
    status       VARCHAR(20)   NOT NULL COMMENT 'Kết quả: SUCCESS, FAILED, PENDING',
    raw_request  JSON          NULL     COMMENT 'Dữ liệu gửi đi / nhận vào nguyên bản, phục vụ đối soát',
    raw_response JSON          NULL     COMMENT 'Dữ liệu phản hồi nguyên bản',
    created_at   DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm phát sinh',
    PRIMARY KEY (id),
    KEY idx_pay_transaction_payment (payment_id, created_at),
    CONSTRAINT fk_pay_transaction_payment FOREIGN KEY (payment_id) REFERENCES pay_payment (id),
    CONSTRAINT ck_pay_transaction_type CHECK (type IN ('CHARGE', 'CALLBACK', 'REFUND')),
    CONSTRAINT ck_pay_transaction_status CHECK (status IN ('SUCCESS', 'FAILED', 'PENDING'))
) ENGINE=InnoDB COMMENT='[Data] Nhật ký giao dịch với cổng thanh toán (chỉ thêm, không sửa / xóa)';

CREATE TABLE IF NOT EXISTS pay_outbox (
    id             BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    event_id       CHAR(36)     NOT NULL COMMENT 'UUID của event - consumer dùng để chống xử lý trùng',
    aggregate_type VARCHAR(50)  NOT NULL DEFAULT 'PAYMENT' COMMENT 'Loại đối tượng phát sinh event',
    aggregate_id   VARCHAR(50)  NOT NULL COMMENT 'Mã đối tượng, thường là order_code - dùng làm Kafka key',
    event_type     VARCHAR(100) NOT NULL COMMENT 'Tên event, vd PaymentSucceeded, PaymentFailed',
    topic          VARCHAR(100) NOT NULL COMMENT 'Kafka topic sẽ gửi tới',
    payload        JSON         NOT NULL COMMENT 'Nội dung event dạng JSON',
    status         VARCHAR(20)  NOT NULL DEFAULT 'NEW' COMMENT 'NEW (chờ gửi), SENT (đã gửi), FAILED (gửi lỗi quá số lần)',
    retry_count    INT          NOT NULL DEFAULT 0 COMMENT 'Số lần đã thử gửi',
    created_at     DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm ghi event (cùng transaction với nghiệp vụ)',
    sent_at        DATETIME(6)  NULL     COMMENT 'Thời điểm gửi Kafka thành công',
    PRIMARY KEY (id),
    UNIQUE KEY uk_pay_outbox_event (event_id),
    KEY idx_pay_outbox_status (status, created_at),
    CONSTRAINT ck_pay_outbox_status CHECK (status IN ('NEW', 'SENT', 'FAILED'))
) ENGINE=InnoDB COMMENT='[Data] Outbox pattern: event chờ gửi Kafka của payment-service';


-- =====================================================================
-- SHIPPING SERVICE
-- =====================================================================

CREATE TABLE IF NOT EXISTS shp_carrier (
    code         VARCHAR(30)  NOT NULL COMMENT 'Mã đơn vị vận chuyển: GHN, GHTK, VTP, INTERNAL',
    name         VARCHAR(100) NOT NULL COMMENT 'Tên hiển thị',
    tracking_url VARCHAR(500) NULL     COMMENT 'Mẫu URL tra cứu vận đơn, vd https://.../{tracking_code}',
    sort_order   INT          NOT NULL DEFAULT 0 COMMENT 'Thứ tự hiển thị',
    is_active    TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '1: đang hợp tác, 0: ngừng',
    created_at   DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    updated_at   DATETIME(6)  NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    PRIMARY KEY (code)
) ENGINE=InnoDB COMMENT='[Danh mục] Đơn vị vận chuyển';

CREATE TABLE IF NOT EXISTS shp_shipment (
    id            BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    order_code    VARCHAR(30)   NOT NULL COMMENT 'ref ord_order.order_code (không FK)',
    carrier_code  VARCHAR(30)   NOT NULL COMMENT 'FK -> shp_carrier.code',
    warehouse_id  BIGINT        NULL     COMMENT 'ref inv_warehouse.id (không FK) - kho xuất hàng',
    tracking_code VARCHAR(100)  NULL     COMMENT 'Mã vận đơn do đơn vị vận chuyển cấp',
    fee           DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT 'Phí vận chuyển thực tế phải trả cho đơn vị vận chuyển',
    cod_amount    DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT 'Số tiền shipper thu hộ (đơn COD), 0 nếu đã thanh toán online',
    status        VARCHAR(30)   NOT NULL DEFAULT 'CREATED' COMMENT 'CREATED, PICKED_UP, IN_TRANSIT, DELIVERED, FAILED, RETURNED',
    shipped_at    DATETIME(6)   NULL     COMMENT 'Thời điểm bàn giao cho đơn vị vận chuyển',
    delivered_at  DATETIME(6)   NULL     COMMENT 'Thời điểm giao thành công',
    created_at    DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    created_by    VARCHAR(100)  NULL     COMMENT 'Người tạo',
    updated_at    DATETIME(6)   NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    updated_by    VARCHAR(100)  NULL     COMMENT 'Người cập nhật gần nhất',
    PRIMARY KEY (id),
    UNIQUE KEY uk_shp_shipment_order (order_code),
    KEY idx_shp_shipment_tracking (carrier_code, tracking_code),
    KEY idx_shp_shipment_status (status),
    CONSTRAINT fk_shp_shipment_carrier FOREIGN KEY (carrier_code) REFERENCES shp_carrier (code),
    CONSTRAINT ck_shp_shipment_status CHECK (status IN ('CREATED', 'PICKED_UP', 'IN_TRANSIT', 'DELIVERED', 'FAILED', 'RETURNED'))
) ENGINE=InnoDB COMMENT='[Data] Vận đơn giao hàng (mỗi đơn hàng 1 vận đơn)';

CREATE TABLE IF NOT EXISTS shp_tracking (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    shipment_id BIGINT       NOT NULL COMMENT 'FK -> shp_shipment.id',
    status      VARCHAR(30)  NOT NULL COMMENT 'Trạng thái tại mốc này (theo shp_shipment.status)',
    location    VARCHAR(255) NULL     COMMENT 'Vị trí / bưu cục',
    note        VARCHAR(500) NULL     COMMENT 'Mô tả từ đơn vị vận chuyển',
    occurred_at DATETIME(6)  NOT NULL COMMENT 'Thời điểm sự kiện xảy ra (theo đơn vị vận chuyển)',
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm hệ thống ghi nhận',
    PRIMARY KEY (id),
    KEY idx_shp_tracking_shipment (shipment_id, occurred_at),
    CONSTRAINT fk_shp_tracking_shipment FOREIGN KEY (shipment_id) REFERENCES shp_shipment (id)
) ENGINE=InnoDB COMMENT='[Data] Hành trình vận đơn (chỉ thêm, không sửa / xóa)';


-- =====================================================================
-- PROMOTION SERVICE
-- =====================================================================

CREATE TABLE IF NOT EXISTS prm_voucher (
    id                  BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    code                VARCHAR(50)   NOT NULL COMMENT 'Mã voucher khách nhập, vd SALE11',
    name                VARCHAR(150)  NOT NULL COMMENT 'Tên chương trình',
    type                VARCHAR(20)   NOT NULL COMMENT 'PERCENT (giảm %), FIXED (giảm số tiền cố định)',
    value               DECIMAL(15,2) NOT NULL COMMENT 'Giá trị giảm: % nếu PERCENT, số tiền nếu FIXED',
    max_discount        DECIMAL(15,2) NULL     COMMENT 'Giảm tối đa (dùng cho PERCENT), NULL là không giới hạn',
    min_order_amount    DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT 'Giá trị đơn tối thiểu để áp dụng',
    start_at            DATETIME(6)   NOT NULL COMMENT 'Bắt đầu hiệu lực',
    end_at              DATETIME(6)   NOT NULL COMMENT 'Hết hiệu lực',
    usage_limit         INT           NULL     COMMENT 'Tổng số lượt dùng tối đa, NULL là không giới hạn',
    usage_limit_per_user INT          NOT NULL DEFAULT 1 COMMENT 'Số lượt mỗi user được dùng',
    used_count          INT           NOT NULL DEFAULT 0 COMMENT 'Số lượt đã dùng',
    is_active           TINYINT(1)    NOT NULL DEFAULT 1 COMMENT '1: đang bật, 0: tắt',
    version             INT           NOT NULL DEFAULT 0 COMMENT 'Optimistic lock - nhiều người dùng voucher cùng lúc',
    created_at          DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm tạo',
    created_by          VARCHAR(100)  NULL     COMMENT 'Người tạo',
    updated_at          DATETIME(6)   NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm cập nhật gần nhất',
    updated_by          VARCHAR(100)  NULL     COMMENT 'Người cập nhật gần nhất',
    PRIMARY KEY (id),
    UNIQUE KEY uk_prm_voucher_code (code),
    KEY idx_prm_voucher_active_time (is_active, start_at, end_at),
    CONSTRAINT ck_prm_voucher_type CHECK (type IN ('PERCENT', 'FIXED')),
    CONSTRAINT ck_prm_voucher_value CHECK (value > 0 AND (type <> 'PERCENT' OR value <= 100)),
    CONSTRAINT ck_prm_voucher_time CHECK (end_at > start_at),
    CONSTRAINT ck_prm_voucher_usage CHECK (used_count >= 0 AND (usage_limit IS NULL OR used_count <= usage_limit))
) ENGINE=InnoDB COMMENT='[Data] Voucher / mã giảm giá';

CREATE TABLE IF NOT EXISTS prm_voucher_usage (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'Khóa chính',
    voucher_id      BIGINT        NOT NULL COMMENT 'FK -> prm_voucher.id',
    user_id         CHAR(36)      NOT NULL COMMENT 'ref idt_account.id (không FK)',
    order_code      VARCHAR(30)   NOT NULL COMMENT 'ref ord_order.order_code (không FK)',
    discount_amount DECIMAL(15,2) NOT NULL COMMENT 'Số tiền thực tế đã giảm cho đơn này',
    status          VARCHAR(20)   NOT NULL DEFAULT 'APPLIED' COMMENT 'APPLIED (đã dùng), REVERTED (hoàn lượt khi đơn bị hủy - bù trừ Saga)',
    created_at      DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm dùng voucher',
    updated_at      DATETIME(6)   NULL     ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Thời điểm đổi trạng thái',
    PRIMARY KEY (id),
    UNIQUE KEY uk_prm_voucher_usage_order (order_code),
    KEY idx_prm_voucher_usage_voucher_user (voucher_id, user_id),
    CONSTRAINT fk_prm_voucher_usage_voucher FOREIGN KEY (voucher_id) REFERENCES prm_voucher (id),
    CONSTRAINT ck_prm_voucher_usage_status CHECK (status IN ('APPLIED', 'REVERTED'))
) ENGINE=InnoDB COMMENT='[Data] Lịch sử sử dụng voucher';
