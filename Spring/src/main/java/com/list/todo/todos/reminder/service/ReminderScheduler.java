package com.list.todo.todos.reminder.service;

import com.list.todo.auth.entity.UserEntity;
import com.list.todo.todos.fcm.entity.FcmTokenEntity;
import com.list.todo.todos.reminder.entity.RemindersEntity;
import com.list.todo.todos.todo.entity.TodosEntity;
import com.list.todo.todos.fcm.repository.FcmTokenRepository;
import com.list.todo.todos.reminder.repository.ReminderRepository;
import com.list.todo.todos.fcm.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final ReminderRepository reminderRepository;
    private final FcmTokenRepository fcmTokenRepository;
    private final FcmService fcmService;

    @Scheduled(fixedRate = 60000)
    public void sendReminders(){
        List<RemindersEntity> dueReminders = reminderRepository.findBySentFalseAndRemindAtBefore(LocalDateTime.now());


        for (RemindersEntity reminder : dueReminders) {
            TodosEntity todo = reminder.getTodos();
            UserEntity user = todo.getUser(); // 연관된 유저 가져오기

            List<FcmTokenEntity> tokens = fcmTokenRepository.findAllByUserAndIsEnabledTrue(user);

            if (tokens.isEmpty()) {
                System.out.println("⚠️ 유저의 FCM 토큰 없음, 알림 생략: " + user.getLoginId());
                continue;
            }

            for (FcmTokenEntity token : tokens) {
                fcmService.sendMessage(
                        token.getToken(),
                        "⏰ 투두 마감 알림",
                        "💡 [" + todo.getTitle() + "] 마감 시간이 다가왔어요!"
                );
            }

            // 전송 여부 저장
            reminder.setSent(true);
            reminderRepository.save(reminder);
        }
    }
}
