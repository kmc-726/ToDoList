package com.list.todo.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(nullable = false)
    private String role = "USER";

//    @Column(name = "fcm_token", length = 512)
//    private String fcmToken;
}
