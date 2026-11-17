package com.project01.identityservice.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dữ liệu đăng ký. Không có field role: role do server gán (mặc định CLIENT),
 * client không được tự chọn. Sau khi xác thực OTP, object này (đã xóa password)
 * được gửi sang profile-service để tạo hồ sơ.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9._]{4,50}$", message = "Tên đăng nhập 4-50 ký tự, chỉ gồm chữ, số, dấu chấm, gạch dưới")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 8, max = 100, message = "Mật khẩu phải từ 8 ký tự")
    private String password;

    @NotBlank(message = "Họ không được để trống")
    private String firstName;

    @NotBlank(message = "Tên không được để trống")
    private String lastName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @Pattern(regexp = "^(\\+84|0)\\d{9,10}$", message = "Số điện thoại không hợp lệ")
    private String phone;

    private String address;

    /** Server tự gán sau khi tạo tài khoản */
    private String userid;
}
