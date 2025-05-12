package com.freeshelf.api.data.domain.booking;

import com.freeshelf.api.data.domain.BaseEntity;
import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "ratings",
    indexes = {@Index(name = "idx_rating_space", columnList = "space_id"),
        @Index(name = "idx_rating_user", columnList = "user_id"),
        @Index(name = "idx_rating_overall", columnList = "overall_score"),
        @Index(name = "idx_rating_created", columnList = "created_at")})
public class Rating extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "rating_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "space_id", nullable = false)
  private StorageSpace space;

  // Overall rating score (1-5)
  @Column(name = "overall_score", nullable = false)
  @Min(1)
  @Max(5)
  private Integer overallScore;

  // Individual category ratings
  @Column(name = "cleanliness_score")
  @Min(1)
  @Max(5)
  private Integer cleanlinessScore;

  @Column(name = "security_score")
  @Min(1)
  @Max(5)
  private Integer securityScore;

  @Column(name = "accessibility_score")
  @Min(1)
  @Max(5)
  private Integer accessibilityScore;

  @Column(name = "value_score")
  @Min(1)
  @Max(5)
  private Integer valueScore;

  @Column(name = "accuracy_score")
  @Min(1)
  @Max(5)
  private Integer accuracyScore;

}
