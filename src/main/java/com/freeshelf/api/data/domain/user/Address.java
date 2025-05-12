package com.freeshelf.api.data.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;

@Embeddable
public class Address {
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
}
