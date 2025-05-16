package com.freeshelf.api.data.repository;

import com.freeshelf.api.data.domain.message.Notification;
import com.freeshelf.api.data.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

  /**
   * Find all notifications for a specific user ordered by creation date (newest first)
   *
   * @param user the user to find notifications for
   * @return list of notifications for the user
   */
  List<Notification> findByUserOrderByCreatedAtDesc(User user);

  /**
   * Find all unread notifications for a specific user
   *
   * @param user the user to find notifications for
   * @return list of unread notifications for the user
   */
  List<Notification> findByUserAndReadFalseOrderByCreatedAtDesc(User user);

  /**
   * Count unread notifications for a specific user
   *
   * @param user the user to count notifications for
   * @return count of unread notifications
   */
  long countByUserAndReadFalse(User user);

  /**
   * Mark all notifications as read for a specific user
   *
   * @param user the user to mark notifications as read for
   */
  @Query("UPDATE Notification n SET n.read = true WHERE n.user = :user AND n.read = false")
  void markAllAsRead(User user);
}
