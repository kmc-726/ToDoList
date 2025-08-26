package com.list.todo.todos.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.list.todo.auth.entity.UserEntity;
import com.list.todo.todos.entity.FcmTokenEntity;
import com.list.todo.todos.repository.FcmTokenRepository;
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
        List<FcmTokenEntity> tokens = fcmTokenRepository.findAllByUser(user);

        // 이미 등록된 토큰이면 갱신
        Optional<FcmTokenEntity> existing = tokens.stream()
                .filter(t -> t.getToken().equals(token))
                .findFirst();

        if (existing.isPresent()) {
            FcmTokenEntity t = existing.get();
            t.setDeviceInfo(deviceInfo);
            t.setLastUpdated(LocalDateTime.now());
            t.setEnabled(true);
            fcmTokenRepository.save(t);
            return;
        }

        // 등록된 토큰이 2개 이상이면 등록 차단 또는 사용자에게 알려주기
        if (tokens.size() >= 2) {
            throw new IllegalStateException("알림 수신 디바이스는 최대 2개까지 등록할 수 있습니다.");
        }

        // 새 토큰 저장
        FcmTokenEntity newToken = new FcmTokenEntity();
        newToken.setUser(user);
        newToken.setToken(token);
        newToken.setDeviceInfo(deviceInfo);
        newToken.setLastUpdated(LocalDateTime.now());
        newToken.setEnabled(true);

        fcmTokenRepository.save(newToken);
    }
}
