package com.freeshelf.api.data.domain.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.freeshelf.api.data.domain.BaseEntity;
import com.freeshelf.api.utils.enums.AuthProvider;
import com.freeshelf.api.utils.enums.UserRole;
import com.freeshelf.api.utils.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users",
    indexes = {@Index(name = "idx_user_email", columnList = "email", unique = true),
        @Index(name = "idx_user_status_role", columnList = "status,role")})
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @Column(nullable = false, length = 50, name = "first_name")
  private String firstName;

  @Column(nullable = true, length = 50, name = "last_name")
  private String lastName;

  @Column(nullable = false, unique = true, name = "email")
  private String email;

  @Column(nullable = false, unique = true, name = "user_name")
  private String userName;

  @Column(nullable = false, name = "password_hash")
  private String password;

  @Column(length = 15, name = "phone_number")
  private String phoneNumber;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20, name = "user_status")
  private UserStatus status = UserStatus.PENDING;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20, name = "user_role")
  private UserRole role;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
  @JsonManagedReference
  private UserProfile profile;

  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  @Column(name = "account_verified")
  private boolean accountVerified = false;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, name = "auth_provider")
  private AuthProvider provider = AuthProvider.LOCAL;

  @Column(name = "provider_id")
  private String providerId;


  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    User user = (User) o;
    return getId() != null && getId().equals(user.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

}


