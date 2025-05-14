package com.freeshelf.api.data.domain.space;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
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
public class StorageSpace extends BaseEntity {

  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "space_id")
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "host_id", nullable = false)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @JsonIdentityReference(alwaysAsId = true)
  @ToString.Exclude
  private User host;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(nullable = false, length = 2000)
  private String description;

  @Column(nullable = false, name = "price_per_month")
  private Double pricePerMonth;

  @Column(nullable = false, scale = 2)
  private Double sizeInSquareFeet;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20, name = "space_type")
  private SpaceType spaceType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private SpaceStatus status = SpaceStatus.DRAFT;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "address_id")
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @JsonIdentityReference(alwaysAsId = true)
  private Address address;

  @ElementCollection
  @CollectionTable(name = "space_features", joinColumns = @JoinColumn(name = "space_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "feature", length = 30)
  private Set<SpaceFeature> features = new HashSet<>();

  @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true,
      fetch = FetchType.EAGER)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @JsonIdentityReference(alwaysAsId = true)
  private List<SpaceImage> images = new ArrayList<>();

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @JsonIdentityReference(alwaysAsId = true)
  private AvailabilityPeriod availabilityPeriod;

  @OneToMany(mappedBy = "space", cascade = CascadeType.ALL)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @JsonIdentityReference(alwaysAsId = true)
  private Set<Rating> ratings = new HashSet<>();

  @OneToMany(mappedBy = "space", cascade = CascadeType.ALL)
  @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
  @JsonIdentityReference(alwaysAsId = true)
  private Set<Review> reviews = new HashSet<>();

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    StorageSpace space = (StorageSpace) o;
    return getId() != null && getId().equals(space.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

}
