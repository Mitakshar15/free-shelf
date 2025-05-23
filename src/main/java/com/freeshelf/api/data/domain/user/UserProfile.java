package com.freeshelf.api.data.domain.user;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.freeshelf.api.data.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_profiles")
public class UserProfile extends BaseEntity {
  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_profile_id")
  private Long id;

  @OneToOne(fetch = FetchType.EAGER)
  @MapsId
  @JoinColumn(name = "user_id", nullable = false)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @JsonIdentityReference(alwaysAsId = true)
  @ToString.Exclude
  private User user;

  @Column(length = 1000)
  private String bio;

  @Column(name = "profile_image_url")
  private String profileImageUrl;

  @OneToMany(mappedBy = "userProfile", cascade = CascadeType.ALL, orphanRemoval = true,
      fetch = FetchType.EAGER)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @JsonIdentityReference(alwaysAsId = true)
  private Set<Address> addresses = new HashSet<>();

  @ElementCollection
  @CollectionTable(name = "user_preferences", joinColumns = @JoinColumn(name = "user_id"))
  @MapKeyColumn(name = "preference_key")
  @Column(name = "preference_value")
  @ToString.Exclude
  private Map<String, String> preferences = new HashMap<>();

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UserProfile user = (UserProfile) o;
    return getId() != null && getId().equals(user.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
