package com.list.todo.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
// 향후 정규식 패턴 추가
@Getter
@Setter
public class UserSignupRequestDto {
    @NotBlank(message = "Id는 필수입니다.")
    private String loginId;

    @Email(message = "email 형식이 올바르지 않습니다.")
    @NotBlank(message = "email은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String confirmPassword;

    @NotBlank(message = "이름은 필수입니다.")
    private String userName;

    private String nickName;

    @NotBlank(message = "휴대폰번호는 필수입니다.")
    private String phoneNumber;
}
