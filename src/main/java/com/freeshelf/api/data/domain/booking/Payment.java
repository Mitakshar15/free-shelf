package com.freeshelf.api.data.domain.booking;

import com.freeshelf.api.data.domain.BaseEntity;
import com.freeshelf.api.utils.enums.PaymentMethod;
import com.freeshelf.api.utils.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"booking"})
@Table(name = "payments",
    indexes = {@Index(name = "idx_payment_booking", columnList = "booking_id"),
        @Index(name = "idx_payment_status", columnList = "status"),
        @Index(name = "idx_payment_transaction", columnList = "transaction_id", unique = true),
        @Index(name = "idx_payment_date", columnList = "payment_date")})
public class Payment extends BaseEntity {

  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "payment_id")
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "booking_id", nullable = false)
  private Booking booking;

  @NotNull
  @Positive
  @Column(nullable = false, precision = 19, scale = 2)
  private Long amount;

  @NotNull
  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentStatus status = PaymentStatus.PENDING;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentMethod method;

  @Column(name = "transaction_id", unique = true)
  private String transactionId;

  @Size(max = 200)
  @Column(length = 200)
  private String failureReason;

  @Column(name = "payment_date")
  private LocalDateTime paymentDate;

  @Column(name = "processed_date")
  private LocalDateTime processedDate;

  /**
   * Marks the payment as completed and updates the booking's total paid amount
   */
  public void markAsCompleted() {
    if (status != PaymentStatus.COMPLETED) {
      status = PaymentStatus.COMPLETED;
      processedDate = LocalDateTime.now();

      // Update the booking's total paid amount
      if (booking != null && amount != null) {
        if (booking.getTotalPaidAmount() == null) {
          booking.setTotalPaidAmount(amount);
        } else {
          booking.setTotalPaidAmount(booking.getTotalPaidAmount() + (amount));
        }
      }
    }
  }

  /**
   * Marks the payment as failed with a reason
   *
   * @param reason The reason for the payment failure
   */
  public void markAsFailed(String reason) {
    status = PaymentStatus.FAILED;
    failureReason = reason;
    processedDate = LocalDateTime.now();
  }
}
