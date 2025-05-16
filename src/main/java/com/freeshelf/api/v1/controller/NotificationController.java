package com.freeshelf.api.v1.controller;

import com.freeshelf.api.data.domain.message.Notification;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.dto.NotificationResponse;
import com.freeshelf.api.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing notifications
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Get all notifications for the authenticated user
     * 
     * @param user the authenticated user
     * @return list of notifications
     */
    @GetMapping
    public ResponseEntity<NotificationResponse> getUserNotifications(
            @AuthenticationPrincipal User user) {
        List<Notification> notifications = notificationService.getUserNotifications(user);
        long unreadCount = notificationService.getUnreadNotificationCount(user);
        
        NotificationResponse response = NotificationResponse.builder()
                .notifications(notifications)
                .unreadCount(unreadCount)
                .build();
                
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get unread notifications for the authenticated user
     * 
     * @param user the authenticated user
     * @return list of unread notifications
     */
    @GetMapping("/unread")
    public ResponseEntity<NotificationResponse> getUnreadNotifications(
            @AuthenticationPrincipal User user) {
        List<Notification> notifications = notificationService.getUnreadNotifications(user);
        long unreadCount = notifications.size();
        
        NotificationResponse response = NotificationResponse.builder()
                .notifications(notifications)
                .unreadCount(unreadCount)
                .build();
                
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get the count of unread notifications for the authenticated user
     * 
     * @param user the authenticated user
     * @return count of unread notifications
     */
    @GetMapping("/unread/count")
    public ResponseEntity<NotificationResponse> getUnreadNotificationCount(
            @AuthenticationPrincipal User user) {
        long count = notificationService.getUnreadNotificationCount(user);
        
        NotificationResponse response = NotificationResponse.builder()
                .unreadCount(count)
                .build();
                
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Mark a notification as read
     * 
     * @param notificationId the ID of the notification to mark as read
     * @param user the authenticated user
     * @return the updated notification
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markNotificationAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal User user) {
        Notification notification = notificationService.markAsRead(notificationId, user);
        long unreadCount = notificationService.getUnreadNotificationCount(user);
        
        NotificationResponse response = NotificationResponse.builder()
                .notifications(List.of(notification))
                .unreadCount(unreadCount)
                .build();
                
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Mark all notifications as read for the authenticated user
     * 
     * @param user the authenticated user
     * @return success message
     */
    @PatchMapping("/read-all")
    public ResponseEntity<NotificationResponse> markAllNotificationsAsRead(
            @AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user);
        
        NotificationResponse response = NotificationResponse.builder()
                .unreadCount(0)
                .message(Map.of("message", "All notifications marked as read"))
                .build();
                
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
