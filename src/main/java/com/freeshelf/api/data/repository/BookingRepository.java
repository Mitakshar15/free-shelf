package com.freeshelf.api.data.repository;

import com.freeshelf.api.data.domain.booking.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

}
