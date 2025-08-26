package com.list.todo.auth.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDto {
    private Long id;
    private String loginId;
    private String email;
    private String password;
    private String userName;
    private String nickName;
    private String phoneNumber;
    private String fcmToken;
}
