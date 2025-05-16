package com.freeshelf.api.service.impl;

import com.freeshelf.api.data.domain.booking.Booking;
import com.freeshelf.api.data.domain.message.Notification;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.data.repository.NotificationRepository;
import com.freeshelf.api.service.interfaces.NotificationService;
import com.freeshelf.api.service.interfaces.PushNotificationService;
import com.freeshelf.api.utils.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the NotificationService interface
 * Handles creating and managing notifications in the system
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final PushNotificationService pushNotificationService;

    @Override
    @Transactional
    public void sendBookingRequestNotification(Booking booking) {
        User host = booking.getSpace().getHost();
        User renter = booking.getRenter();
        String spaceName = booking.getSpace().getTitle();
        
        // Create notification for the host
        Notification notification = Notification.createBookingNotification(
            host,
            "New Booking Request",
            renter.getFirstName() + " " + renter.getLastName() + " has requested to book your space: " + spaceName,
            booking.getId()
        );
        
        notification.setType(NotificationType.BOOKING_REQUEST);
        notification = notificationRepository.save(notification);
        
        // Send push notification
        pushNotificationService.sendPushNotification(notification);
        
        log.info("Sent booking request notification to host ID: {} for booking ID: {}", host.getId(), booking.getId());
    }

    @Override
    @Transactional
    public void sendBookingStatusUpdateNotification(Booking booking) {
        User renter = booking.getRenter();
        String spaceName = booking.getSpace().getTitle();
        String hostName = booking.getSpace().getHost().getFirstName() + " " + booking.getSpace().getHost().getLastName();
        
        NotificationType notificationType;
        String title;
        String message;
        
        // Determine notification type and message based on booking status
        switch (booking.getStatus()) {
            case APPROVED:
                notificationType = NotificationType.BOOKING_APPROVED;
                title = "Booking Approved";
                message = "Your booking request for " + spaceName + " has been approved by " + hostName;
                break;
            case REJECTED:
                notificationType = NotificationType.BOOKING_REJECTED;
                title = "Booking Rejected";
                message = "Your booking request for " + spaceName + " has been rejected by " + hostName;
                break;
            case CANCELLED:
                notificationType = NotificationType.BOOKING_CANCELLED;
                title = "Booking Cancelled";
                message = "Your booking for " + spaceName + " has been cancelled";
                break;
            default:
                // Don't send notifications for other status changes
                return;
        }
        
        // Create notification for the renter
        Notification notification = Notification.createBookingNotification(
            renter,
            title,
            message,
            booking.getId()
        );
        
        notification.setType(notificationType);
        notification = notificationRepository.save(notification);
        
        // Send push notification
        pushNotificationService.sendPushNotification(notification);
        
        log.info("Sent booking status update notification to renter ID: {} for booking ID: {}, status: {}", 
                renter.getId(), booking.getId(), booking.getStatus());
    }

    @Override
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndReadFalseOrderByCreatedAtDesc(user);
    }

    @Override
    @Transactional
    public Notification markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
        
        // Verify the notification belongs to the user
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Notification does not belong to the user");
        }
        
        notification.markAsRead();
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(User user) {
        notificationRepository.markAllAsRead(user);
    }

    @Override
    public long getUnreadNotificationCount(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }
}
