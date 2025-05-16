package com.freeshelf.api.dto;

import com.freeshelf.api.data.domain.message.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Response DTO for notification-related endpoints
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private List<Notification> notifications;
    private long unreadCount;
    private Map<String, String> message;
}
