package com.freeshelf.api.service.interfaces;

import com.freeshelf.api.data.domain.booking.Booking;
import com.freeshelf.api.data.domain.message.Notification;
import com.freeshelf.api.data.domain.user.User;

import java.util.List;

/**
 * Service for managing notifications in the system
 */
public interface NotificationService {

  /**
   * Send a notification for a new booking request to the host
   *
   * @param booking the booking that was created
   */
  void sendBookingRequestNotification(Booking booking);

  /**
   * Send a notification for a booking status update to the renter
   *
   * @param booking the booking that was updated
   */
  void sendBookingStatusUpdateNotification(Booking booking);

  /**
   * Get all notifications for a user
   *
   * @param user the user to get notifications for
   * @return list of notifications for the user
   */
  List<Notification> getUserNotifications(User user);

  /**
   * Get unread notifications for a user
   *
   * @param user the user to get unread notifications for
   * @return list of unread notifications for the user
   */
  List<Notification> getUnreadNotifications(User user);

  /**
   * Mark a notification as read
   *
   * @param notificationId the ID of the notification to mark as read
   * @param user the user who owns the notification
   * @return the updated notification
   */
  Notification markAsRead(Long notificationId, User user);

  /**
   * Mark all notifications as read for a user
   *
   * @param user the user to mark all notifications as read for
   */
  void markAllAsRead(User user);

  /**
   * Get the count of unread notifications for a user
   *
   * @param user the user to count unread notifications for
   * @return count of unread notifications
   */
  long getUnreadNotificationCount(User user);
}
