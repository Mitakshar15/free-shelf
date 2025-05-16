package com.freeshelf.api.data.domain.booking;

import com.freeshelf.api.data.domain.BaseEntity;
import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.utils.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"space", "renter", "payments"})
@Table(name = "bookings",
    indexes = {@Index(name = "idx_booking_space", columnList = "space_id"),
        @Index(name = "idx_booking_renter", columnList = "renter_id"),
        @Index(name = "idx_booking_status", columnList = "status"),
        @Index(name = "idx_booking_dates", columnList = "start_date,end_date")})
public class Booking extends BaseEntity {

  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "booking_id")
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "space_id", nullable = false)
  private StorageSpace space;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "renter_id", nullable = false)
  private User renter;

  @NotNull
  @Column(name = "start_date", nullable = false)
  private OffsetDateTime startDate;

  @NotNull
  @Column(name = "end_date", nullable = false)
  private OffsetDateTime endDate;

  @NotNull
  @Column(nullable = false, precision = 19, scale = 2)
  private Long totalPrice;

  @NotNull
  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private BookingStatus status = BookingStatus.PENDING;

  @Size(max = 1000)
  @Column(length = 1000)
  private String cancellationReason;

  @Column(name = "status_updated_at")
  private LocalDateTime statusUpdatedAt;

  @Builder.Default
  @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Payment> payments = new ArrayList<>();

  @Builder.Default
  @Column(name = "total_paid_amount", precision = 19, scale = 2)
  private Long totalPaidAmount = 0L;

  /**
   * Updates the booking status and records when the status was changed
   *
   * @param newStatus The new status to set
   */
  public void updateStatus(BookingStatus newStatus) {
    this.status = newStatus;
    this.statusUpdatedAt = LocalDateTime.now();
  }

  /**
   * Checks if the booking is fully paid
   *
   * @return true if the booking is fully paid, false otherwise
   */
  public boolean isFullyPaid() {
    return totalPaidAmount != null && totalPrice != null
        && totalPaidAmount.compareTo(totalPrice) >= 0;
  }
}
