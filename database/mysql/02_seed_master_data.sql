-- =====================================================================
-- Dữ liệu khởi tạo cho các bảng DANH MỤC
-- Chạy sau 01_schema.sql. Chạy lại nhiều lần vẫn an toàn (INSERT IGNORE).
--
-- loc_province / loc_ward KHÔNG có ở đây: dữ liệu ~3.300 xã/phường sau sáp nhập
-- 01/07/2025 nên import từ danh mục chính thức của Tổng cục Thống kê / Cục Thống kê.
-- =====================================================================

USE ecommerce;

-- ---------------------------------------------------------------------
-- identity-service
-- ---------------------------------------------------------------------
-- Đổi tên role USER cũ thành CLIENT (giữ nguyên id nên idt_account_role không bị ảnh hưởng).
-- Bỏ qua nếu DB chưa có USER hoặc đã có CLIENT.
UPDATE IGNORE idt_role
SET code = 'CLIENT', name = 'Khách hàng', updated_by = 'SYSTEM'
WHERE code = 'USER';

INSERT IGNORE INTO idt_role (code, name, description, created_by) VALUES
    ('ADMIN',   'Quản trị viên',       'Toàn quyền quản trị hệ thống',                    'SYSTEM'),
    ('CLIENT',  'Khách hàng',          'Người mua hàng, vai trò mặc định khi đăng ký',    'SYSTEM'),
    ('SELLER',  'Người bán',           'Quản lý sản phẩm, đơn hàng và voucher của shop',  'SYSTEM'),
    ('SHIPPER', 'Nhân viên giao hàng', 'Cập nhật trạng thái vận đơn',                     'SYSTEM');

INSERT IGNORE INTO idt_permission (code, resource, action, description, created_by) VALUES
    ('PRODUCT_READ',   'PRODUCT',  'READ',   'Xem sản phẩm',              'SYSTEM'),
    ('PRODUCT_CREATE', 'PRODUCT',  'CREATE', 'Tạo sản phẩm',              'SYSTEM'),
    ('PRODUCT_UPDATE', 'PRODUCT',  'UPDATE', 'Sửa sản phẩm',              'SYSTEM'),
    ('PRODUCT_DELETE', 'PRODUCT',  'DELETE', 'Ẩn / xóa sản phẩm',         'SYSTEM'),
    ('ORDER_READ',     'ORDER',    'READ',   'Xem đơn hàng',              'SYSTEM'),
    ('ORDER_CREATE',   'ORDER',    'CREATE', 'Đặt hàng',                  'SYSTEM'),
    ('ORDER_UPDATE',   'ORDER',    'UPDATE', 'Đổi trạng thái / hủy đơn',  'SYSTEM'),
    ('SHIPMENT_READ',  'SHIPMENT', 'READ',   'Xem vận đơn',               'SYSTEM'),
    ('SHIPMENT_UPDATE','SHIPMENT', 'UPDATE', 'Cập nhật trạng thái vận đơn','SYSTEM'),
    ('VOUCHER_MANAGE', 'VOUCHER',  'UPDATE', 'Tạo / sửa voucher',         'SYSTEM'),
    ('ACCOUNT_MANAGE', 'ACCOUNT',  'UPDATE', 'Khóa / mở khóa tài khoản',  'SYSTEM');

-- ADMIN: tất cả quyền
INSERT IGNORE INTO idt_role_permission (role_id, permission_id, created_by)
SELECT r.id, p.id, 'SYSTEM'
FROM idt_role r CROSS JOIN idt_permission p
WHERE r.code = 'ADMIN';

-- CLIENT: xem sản phẩm, đặt và xem đơn của mình
INSERT IGNORE INTO idt_role_permission (role_id, permission_id, created_by)
SELECT r.id, p.id, 'SYSTEM'
FROM idt_role r JOIN idt_permission p
  ON p.code IN ('PRODUCT_READ', 'ORDER_READ', 'ORDER_CREATE')
WHERE r.code = 'CLIENT';

-- SELLER: quản lý sản phẩm, xử lý đơn, tạo voucher, xem vận đơn
INSERT IGNORE INTO idt_role_permission (role_id, permission_id, created_by)
SELECT r.id, p.id, 'SYSTEM'
FROM idt_role r JOIN idt_permission p
  ON p.code IN ('PRODUCT_READ', 'PRODUCT_CREATE', 'PRODUCT_UPDATE', 'PRODUCT_DELETE',
                'ORDER_READ', 'ORDER_UPDATE', 'VOUCHER_MANAGE', 'SHIPMENT_READ')
WHERE r.code = 'SELLER';

-- SHIPPER: xem và cập nhật vận đơn
INSERT IGNORE INTO idt_role_permission (role_id, permission_id, created_by)
SELECT r.id, p.id, 'SYSTEM'
FROM idt_role r JOIN idt_permission p
  ON p.code IN ('SHIPMENT_READ', 'SHIPMENT_UPDATE', 'ORDER_READ')
WHERE r.code = 'SHIPPER';

-- ---------------------------------------------------------------------
-- product-service
-- ---------------------------------------------------------------------
INSERT IGNORE INTO prd_attribute (code, name, sort_order, created_by) VALUES
    ('COLOR',   'Màu sắc',   1, 'SYSTEM'),
    ('SIZE',    'Kích cỡ',   2, 'SYSTEM'),
    ('STORAGE', 'Dung lượng',3, 'SYSTEM');

INSERT IGNORE INTO prd_attribute_value (attribute_id, value, sort_order, created_by)
SELECT a.id, v.value, v.sort_order, 'SYSTEM'
FROM prd_attribute a
JOIN (
    SELECT 'COLOR' AS attr, 'Đen' AS value, 1 AS sort_order UNION ALL
    SELECT 'COLOR', 'Trắng', 2 UNION ALL
    SELECT 'COLOR', 'Đỏ', 3 UNION ALL
    SELECT 'COLOR', 'Xanh dương', 4 UNION ALL
    SELECT 'SIZE', 'S', 1 UNION ALL
    SELECT 'SIZE', 'M', 2 UNION ALL
    SELECT 'SIZE', 'L', 3 UNION ALL
    SELECT 'SIZE', 'XL', 4 UNION ALL
    SELECT 'STORAGE', '128GB', 1 UNION ALL
    SELECT 'STORAGE', '256GB', 2 UNION ALL
    SELECT 'STORAGE', '512GB', 3
) v ON v.attr = a.code;

INSERT IGNORE INTO prd_category (parent_id, name, slug, sort_order, created_by) VALUES
    (NULL, 'Điện thoại', 'dien-thoai', 1, 'SYSTEM'),
    (NULL, 'Laptop',     'laptop',     2, 'SYSTEM'),
    (NULL, 'Thời trang', 'thoi-trang', 3, 'SYSTEM');

INSERT IGNORE INTO prd_category (parent_id, name, slug, sort_order, created_by)
SELECT c.id, v.name, v.slug, v.sort_order, 'SYSTEM'
FROM prd_category c
JOIN (
    SELECT 'thoi-trang' AS parent_slug, 'Thời trang nam' AS name, 'thoi-trang-nam' AS slug, 1 AS sort_order UNION ALL
    SELECT 'thoi-trang', 'Thời trang nữ', 'thoi-trang-nu', 2
) v ON v.parent_slug = c.slug;

INSERT IGNORE INTO prd_brand (name, slug, created_by) VALUES
    ('Apple',   'apple',   'SYSTEM'),
    ('Samsung', 'samsung', 'SYSTEM'),
    ('Dell',    'dell',    'SYSTEM'),
    ('Coolmate','coolmate','SYSTEM');

-- ---------------------------------------------------------------------
-- inventory-service
-- ---------------------------------------------------------------------
INSERT IGNORE INTO inv_warehouse (code, name, address_detail, created_by) VALUES
    ('HN01',  'Kho Hà Nội',     'Cập nhật địa chỉ thật', 'SYSTEM'),
    ('HCM01', 'Kho TP. Hồ Chí Minh', 'Cập nhật địa chỉ thật', 'SYSTEM');

-- ---------------------------------------------------------------------
-- order-service
-- ---------------------------------------------------------------------
INSERT IGNORE INTO ord_status (code, name, description, is_final, sort_order) VALUES
    ('PENDING',   'Chờ xử lý',      'Đơn vừa tạo, đang giữ hàng và chờ thanh toán', 0, 1),
    ('CONFIRMED', 'Đã xác nhận',    'Đơn COD đã được xác nhận',                     0, 2),
    ('PAID',      'Đã thanh toán',  'Thanh toán online thành công',                 0, 3),
    ('SHIPPING',  'Đang giao',      'Đã bàn giao cho đơn vị vận chuyển',            0, 4),
    ('DELIVERED', 'Đã giao',        'Khách đã nhận hàng',                           1, 5),
    ('CANCELLED', 'Đã hủy',         'Đơn bị hủy, hàng đã được nhả về kho',          1, 6),
    ('RETURNED',  'Đã trả hàng',    'Khách trả hàng sau khi nhận',                  1, 7);

INSERT IGNORE INTO ord_cancel_reason (code, name, actor, sort_order) VALUES
    ('CUSTOMER_CHANGED_MIND', 'Khách đổi ý, không muốn mua nữa', 'CUSTOMER', 1),
    ('CUSTOMER_WRONG_ADDRESS','Khách nhập sai địa chỉ',          'CUSTOMER', 2),
    ('CUSTOMER_FOUND_CHEAPER','Khách tìm được giá rẻ hơn',       'CUSTOMER', 3),
    ('OUT_OF_STOCK',          'Hết hàng',                        'ADMIN',    4),
    ('SUSPECTED_FRAUD',       'Nghi ngờ gian lận',               'ADMIN',    5),
    ('PAYMENT_FAILED',        'Thanh toán thất bại',             'SYSTEM',   6),
    ('PAYMENT_TIMEOUT',       'Quá hạn thanh toán',              'SYSTEM',   7),
    ('DELIVERY_FAILED',       'Giao hàng không thành công',      'SYSTEM',   8);

-- ---------------------------------------------------------------------
-- payment-service
-- ---------------------------------------------------------------------
INSERT IGNORE INTO pay_method (code, name, description, is_online, sort_order) VALUES
    ('COD',           'Thanh toán khi nhận hàng', 'Trả tiền mặt cho shipper',               0, 1),
    ('VNPAY',         'VNPAY',                    'Thẻ ATM / QR qua cổng VNPAY',            1, 2),
    ('MOMO',          'Ví MoMo',                  'Thanh toán bằng ví điện tử MoMo',        1, 3),
    ('BANK_TRANSFER', 'Chuyển khoản ngân hàng',   'Chuyển khoản theo nội dung là mã đơn',   0, 4);

-- ---------------------------------------------------------------------
-- shipping-service
-- ---------------------------------------------------------------------
INSERT IGNORE INTO shp_carrier (code, name, sort_order) VALUES
    ('GHN',      'Giao Hàng Nhanh',       1),
    ('GHTK',     'Giao Hàng Tiết Kiệm',   2),
    ('VTP',      'Viettel Post',          3),
    ('INTERNAL', 'Shipper nội bộ',        4);
