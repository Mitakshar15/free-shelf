package com.freeshelf.api.data.domain.space;

import com.freeshelf.api.data.domain.BaseEntity;
import com.freeshelf.api.data.domain.booking.Rating;
import com.freeshelf.api.data.domain.booking.Review;
import com.freeshelf.api.data.domain.user.Address;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.utils.enums.SpaceFeature;
import com.freeshelf.api.utils.enums.SpaceStatus;
import com.freeshelf.api.utils.enums.SpaceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "storage_spaces",
    indexes = {@Index(name = "idx_space_host", columnList = "host_id"),
        @Index(name = "idx_space_status", columnList = "status"),
        @Index(name = "idx_space_type", columnList = "space_type"),
        @Index(name = "idx_space_location", columnList = "latitude,longitude"),
        @Index(name = "idx_space_price", columnList = "price_per_month")})
public class StorageSpace extends BaseEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "space_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "host_id", nullable = false)
  private User host;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(nullable = false, length = 2000)
  private String description;

  @Column(nullable = false, precision = 10, scale = 2, name = "price_per_month")
  private Long pricePerMonth;

  @Column(nullable = false, precision = 8, scale = 2)
  private Long sizeInSquareFeet;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20, name = "space_type")
  private SpaceType spaceType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private SpaceStatus status = SpaceStatus.DRAFT;

  @Embedded
  @AttributeOverrides({
      @AttributeOverride(name = "street", column = @Column(name = "address_street")),
      @AttributeOverride(name = "city", column = @Column(name = "address_city")),
      @AttributeOverride(name = "state", column = @Column(name = "address_state")),
      @AttributeOverride(name = "zipCode", column = @Column(name = "address_zip_code")),
      @AttributeOverride(name = "country", column = @Column(name = "address_country"))})
  private Address address;

  @ElementCollection
  @CollectionTable(name = "space_features", joinColumns = @JoinColumn(name = "space_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "feature", length = 30)
  private Set<SpaceFeature> features = new HashSet<>();

  @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<SpaceImage> images = new ArrayList<>();

  @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<AvailabilityPeriod> availabilityPeriods = new ArrayList<>();

  @OneToMany(mappedBy = "space", cascade = CascadeType.ALL)
  private Set<Rating> ratings = new HashSet<>();

  @OneToMany(mappedBy = "space", cascade = CascadeType.ALL)
  private Set<Review> reviews = new HashSet<>();

}
