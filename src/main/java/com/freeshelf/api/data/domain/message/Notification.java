package com.freeshelf.api.data.domain.message;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.freeshelf.api.data.domain.BaseEntity;
import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.utils.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;



@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications",
    indexes = {@Index(name = "idx_notification_user", columnList = "user_id"),
        @Index(name = "idx_notification_read", columnList = "is_read"),
        @Index(name = "idx_notification_created", columnList = "created_at")})
public class Notification extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "notification_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @JsonIdentityReference(alwaysAsId = true)
  @ToString.Exclude
  private User user;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(nullable = false, length = 1000)
  private String message;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationType type;

  @Column(name = "is_read", nullable = false)
  private boolean read = false;

  @Column(name = "action_url")
  private String actionUrl;

  @Column(name = "reference_id")
  private Long referenceId;

  @Column(name = "reference_type", length = 50)
  private String referenceType;



  // Business methods
  public void markAsRead() {
    this.read = true;
  }

  // Factory methods
  public static Notification createBookingNotification(User user, String title, String message,
      Long bookingId) {

    Notification notification = new Notification();
    notification.setUser(user);
    notification.setTitle(title);
    notification.setMessage(message);
    notification.setType(NotificationType.BOOKING_REQUEST);
    notification.setReferenceId(bookingId);
    notification.setReferenceType("booking");
    notification.setActionUrl("/bookings/" + bookingId);

    return notification;
  }

  public static Notification createMessageNotification(User user, User sender,
      Conversation conversation) {

    Notification notification = new Notification();
    notification.setUser(user);
    notification.setTitle("New message from " + sender.getFirstName() + sender.getLastName());
    notification.setMessage(
        sender.getFirstName() + " " + sender.getLastName() + " has sent you a new message");
    notification.setType(NotificationType.NEW_MESSAGE);
    notification.setReferenceId(conversation.getId());
    notification.setReferenceType("conversation");
    notification.setActionUrl("/messages/conversation/" + conversation.getId());

    return notification;
  }

  public static Notification createReviewNotification(User user, User reviewer,
      StorageSpace space) {

    Notification notification = new Notification();
    notification.setUser(user);
    notification
        .setTitle("New review from " + reviewer.getFirstName() + " " + reviewer.getLastName());
    notification.setMessage(reviewer.getFirstName() + " " + reviewer.getLastName()
        + " has left a review for your space " + space.getTitle());
    notification.setType(NotificationType.NEW_REVIEW);
    notification.setReferenceId(space.getId());
    notification.setReferenceType("space");
    notification.setActionUrl("/spaces/" + space.getId() + "/reviews");

    return notification;
  }
}
