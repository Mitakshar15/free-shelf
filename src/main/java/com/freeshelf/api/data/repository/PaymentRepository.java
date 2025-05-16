package com.freeshelf.api.data.repository;

import com.freeshelf.api.data.domain.booking.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
