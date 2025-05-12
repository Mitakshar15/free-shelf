package com.freeshelf.api.data.domain.booking;

import com.freeshelf.api.data.domain.BaseEntity;
import com.freeshelf.api.utils.enums.PaymentMethod;
import com.freeshelf.api.utils.enums.PaymentStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "payments",
    indexes = {@Index(name = "idx_payment_booking", columnList = "booking_id"),
        @Index(name = "idx_payment_status", columnList = "status"),
        @Index(name = "idx_payment_transaction", columnList = "transaction_id", unique = true)})
public class Payment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "payment_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "booking_id", nullable = false)
  private Booking booking;

  @Column(nullable = false, precision = 10, scale = 2)
  private Long amount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentStatus status = PaymentStatus.PENDING;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private PaymentMethod method;

  @Column(name = "transaction_id", unique = true)
  private String transactionId;

  @Column(length = 200)
  private String failureReason;

}
