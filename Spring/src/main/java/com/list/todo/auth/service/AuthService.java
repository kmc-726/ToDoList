package com.list.todo.auth.service;

import com.list.todo.auth.config.PasswordConfig;
import com.list.todo.auth.dto.UserSignupRequestDto;
import com.list.todo.auth.entity.UserEntity;
import com.list.todo.auth.exception.DuplicateUserException;
import com.list.todo.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(UserSignupRequestDto dto){
        if (userRepository.existsByLoginId(dto.getLoginId())){
            throw new DuplicateUserException("이미 사용중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(dto.getEmail())){
            throw new DuplicateUserException("이미 사용중인 이메일입니다.");
        }
        if (userRepository.existsByPhoneNumber(dto.getPhoneNumber())){
            throw new DuplicateUserException("이미 사용중인 번호입니다.");
        }

        UserEntity user = new UserEntity();
        user.setLoginId(dto.getLoginId());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUserName(dto.getUserName());
        user.setNickName(dto.getNickName());
        user.setPhoneNumber(dto.getPhoneNumber());

        userRepository.save(user);
    }

}
