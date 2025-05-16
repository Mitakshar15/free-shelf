package com.freeshelf.api.data.domain.user;

import com.freeshelf.api.data.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;

/**
 * Entity to store user device tokens for push notifications
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "device_tokens",
    indexes = {@Index(name = "idx_device_token_user", columnList = "user_id")})
public class DeviceToken extends BaseEntity {

  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "device_token_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "token", nullable = false, unique = true, length = 255)
  private String token;

  @Column(name = "device_type", length = 50)
  private String deviceType;

  @Column(name = "device_name", length = 100)
  private String deviceName;

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private boolean active = true;
}
