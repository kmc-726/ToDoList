package com.list.todo.todos.fcm.dto;

import com.list.todo.todos.fcm.entity.FcmTokenEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDto {
    private Long id;
    private String deviceInfo;
    private boolean enabled;
    private LocalDateTime lastUpdated;

    public static DeviceDto fromEntity(FcmTokenEntity entity) {
        return new DeviceDto(
                entity.getId(),
                entity.getDeviceInfo(),
                entity.isEnabled(),
                entity.getLastUpdated()
        );
    }
}
