package com.freeshelf.api.data.domain.message;

import com.freeshelf.api.data.domain.BaseEntity;
import com.freeshelf.api.data.domain.user.User;
import jakarta.persistence.*;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "messages",
    indexes = {@Index(name = "idx_msg_conversation", columnList = "conversation_id"),
        @Index(name = "idx_msg_sender", columnList = "sender_id"),
        @Index(name = "idx_msg_read", columnList = "read_status"),
        @Index(name = "idx_msg_created", columnList = "created_at")})
public class Message extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "message_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "conversation_id", nullable = false)
  private Conversation conversation;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sender_id", nullable = false)
  private User sender;

  @Column(nullable = false, length = 4000)
  private String content;

  @Column(nullable = false, name = "read_status")
  private boolean read = false;

  // Transactional method
  @Transactional
  public void markAsRead() {
    this.read = true;
  }
}
