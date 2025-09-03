package com.list.todo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class FirebaseConfig {

    @PostConstruct
    public void initialize() throws IOException {
        String configPath = System.getenv("FIREBASE_CONFIG_PATH");

        if (configPath == null || configPath.isEmpty()) {
            throw new IllegalStateException("FIREBASE_CONFIG_PATH 환경변수가 설정되지 않았습니다.");
        }

        FileInputStream serviceAccount = new FileInputStream(configPath);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase initialized successfully");
        }
    }
}
