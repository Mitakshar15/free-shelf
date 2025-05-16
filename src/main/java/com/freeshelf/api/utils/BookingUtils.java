package com.freeshelf.api.utils;

import com.freeshelf.api.data.domain.booking.Booking;
import com.freeshelf.api.data.domain.space.AvailabilityPeriod;
import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.repository.BookingRepository;
import com.freeshelf.api.utils.enums.BookingStatus;
import com.freeshelf.api.utils.enums.SpaceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingUtils {

  private final BookingRepository bookingRepository;

  public boolean checkAvailability(OffsetDateTime startDate, OffsetDateTime endDate,
      StorageSpace space) {
    if (space.getStatus() != SpaceStatus.ACTIVE) {
      throw new IllegalStateException("Storage space is not currently active for booking");
    }

    if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
      throw new IllegalArgumentException(
          "Invalid booking dates: start date must be before end date");
    }
    AvailabilityPeriod availabilityPeriod = space.getAvailabilityPeriod();
    if (availabilityPeriod != null) {
      OffsetDateTime spaceStartDate = availabilityPeriod.getStartDate();
      OffsetDateTime spaceEndDate = availabilityPeriod.getEndDate();

      if (spaceStartDate != null && startDate.isBefore(spaceStartDate)) {
        throw new IllegalArgumentException(
            "Requested start date is before space availability period");
      }
      if (spaceEndDate != null && endDate.isAfter(spaceEndDate)) {
        throw new IllegalArgumentException("Requested end date is after space availability period");
      }
    }
    List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(space.getId(),
        BookingStatus.CANCELLED, BookingStatus.REJECTED, startDate, endDate);

    return overlappingBookings.isEmpty();
  }

  public @NotNull Long calculateTotalPriceForBooking(@Valid OffsetDateTime startDate,
      @Valid OffsetDateTime endDate, Double pricePerMonth) {
    // Validate input parameters
    if (startDate == null || endDate == null || pricePerMonth == null) {
      throw new IllegalArgumentException(
          "Start date, end date, and price per month must not be null");
    }

    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date must be before end date");
    }

    long durationInDays = ChronoUnit.DAYS.between(startDate, endDate) + 1; // +1 to include both
                                                                           // start and end days
    double pricePerDay = pricePerMonth / 30.0;
    double totalPrice = durationInDays * pricePerDay;

    return Math.round(totalPrice);
  }

}
