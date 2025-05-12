package com.freeshelf.api.data.domain.booking;

import com.freeshelf.api.data.domain.BaseEntity;
import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "reviews",
    indexes = {@Index(name = "idx_review_space", columnList = "space_id"),
        @Index(name = "idx_review_booking", columnList = "booking_id"),
        @Index(name = "idx_review_author", columnList = "author_id"),
        @Index(name = "idx_review_rating", columnList = "rating")})
public class Review extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "review_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "space_id", nullable = false)
  private StorageSpace space;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rating_id", nullable = false, unique = true)
  private Rating rating;

  @Column(length = 1000)
  private String comment;

  @Column(nullable = false)
  private boolean approved = false;
}
