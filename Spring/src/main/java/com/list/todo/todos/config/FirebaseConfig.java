package com.list.todo.todos.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            InputStream serviceAccount = getClass().getResourceAsStream("/firebase/firebase-service-account.json");

            if (serviceAccount == null) {
                throw new IllegalStateException("❌ Firebase 서비스 계정 키를 찾을 수 없습니다. 경로를 확인하세요.");
            }

            FirebaseOptions firebaseOptions = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(firebaseOptions);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
