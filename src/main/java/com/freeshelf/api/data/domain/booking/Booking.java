package com.freeshelf.api.data.domain.booking;

import com.freeshelf.api.data.domain.BaseEntity;
import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.utils.enums.BookingStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings",
    indexes = {@Index(name = "idx_booking_space", columnList = "space_id"),
        @Index(name = "idx_booking_renter", columnList = "renter_id"),
        @Index(name = "idx_booking_status", columnList = "status"),
        @Index(name = "idx_booking_dates", columnList = "start_date,end_date")})
public class Booking extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "booking_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "space_id", nullable = false)
  private StorageSpace space;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "renter_id", nullable = false)
  private User renter;

  @Column(name = "start_date", nullable = false)
  private LocalDateTime startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDateTime endDate;

  @Column(nullable = false, precision = 10, scale = 2)
  private Long totalPrice;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private BookingStatus status = BookingStatus.PENDING;

  @Column(length = 1000)
  private String cancellationReason;

  @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
  private List<Payment> payments = new ArrayList<>();


}
