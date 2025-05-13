package com.freeshelf.api.data.domain.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.freeshelf.api.data.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Address extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "address_id")
  private Long id;

  @Column
  private String addressLine1;

  @Column
  private String addressLine2;

  @Column(length = 100)
  private String street;

  @Column(length = 50)
  private String city;

  @Column(length = 50)
  private String state;

  @Column(length = 10)
  private String zipCode;

  @Column(length = 50)
  private String country;

  @Column(precision = 10, scale = 7)
  private BigDecimal latitude;

  @Column(precision = 10, scale = 7)
  private BigDecimal longitude;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_profile_id", nullable = false)
  @ToString.Exclude
  private UserProfile userProfile;
}
