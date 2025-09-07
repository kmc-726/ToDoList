package com.list.todo.todos.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.list.todo.auth.entity.UserEntity;
import com.list.todo.todos.fcm.entity.FcmTokenEntity;
import com.list.todo.todos.fcm.repository.FcmTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenRepository fcmTokenRepository;

    public void sendMessage(String token, String title, String body) {
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("✅ FCM 전송 성공: {}", response);
        } catch (Exception e) {
            log.error("❌ FCM 전송 실패:", e);
//            e.printStackTrace();
        }
    }

    public void registerFcmToken(UserEntity user, String token, String deviceInfo) {
        // 1. 토큰 단위로 먼저 조회해서 기존 토큰 있으면 갱신
        Optional<FcmTokenEntity> existingTokenOpt = fcmTokenRepository.findByToken(token);

        if (existingTokenOpt.isPresent()) {
            FcmTokenEntity existingToken = existingTokenOpt.get();
            existingToken.setUser(user);
            existingToken.setDeviceInfo(deviceInfo);
            existingToken.setLastUpdated(LocalDateTime.now());
            existingToken.setEnabled(true);
            fcmTokenRepository.save(existingToken);
            return;
        }

        // 2. 새 토큰이면, 먼저 해당 유저가 가진 등록된 토큰 개수 확인
        int userTokenCount = fcmTokenRepository.countByUser(user);

        if (userTokenCount >= 2) {
            throw new IllegalStateException("알림 수신 디바이스는 최대 2개까지 등록할 수 있습니다.");
        }

        // 3. 제한에 걸리지 않으면 새 토큰 등록
        FcmTokenEntity newToken = new FcmTokenEntity();
        newToken.setToken(token);
        newToken.setDeviceInfo(deviceInfo);
        newToken.setUser(user);
        newToken.setLastUpdated(LocalDateTime.now());
        newToken.setEnabled(true);

        fcmTokenRepository.save(newToken);
    }

}
