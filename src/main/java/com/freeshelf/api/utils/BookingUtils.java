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
import org.producr.api.dtos.BookingRequest;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingUtils {

  private final BookingRepository bookingRepository;

  public boolean checkAvailability(BookingRequest bookingRequest, StorageSpace space) {
    // Check if the space is in an available status
    if (space.getStatus() != SpaceStatus.ACTIVE) {
      throw new IllegalStateException("Storage space is not currently active for booking");
    }

    // Check if the requested dates are valid
    OffsetDateTime requestStartDate = bookingRequest.getStartDate();
    OffsetDateTime requestEndDate = bookingRequest.getEndDate();

    if (requestStartDate == null || requestEndDate == null
        || requestStartDate.isAfter(requestEndDate)) {
      throw new IllegalArgumentException(
          "Invalid booking dates: start date must be before end date");
    }

    // Check if the dates are within the space's availability period
    AvailabilityPeriod availabilityPeriod = space.getAvailabilityPeriod();
    if (availabilityPeriod != null) {
      OffsetDateTime spaceStartDate = availabilityPeriod.getStartDate();
      OffsetDateTime spaceEndDate = availabilityPeriod.getEndDate();

      if (spaceStartDate != null && requestStartDate.isBefore(spaceStartDate)) {
        throw new IllegalArgumentException(
            "Requested start date is before space availability period");
      }
      if (spaceEndDate != null && requestEndDate.isAfter(spaceEndDate)) {
        throw new IllegalArgumentException("Requested end date is after space availability period");
      }
    }

    // Find any bookings for this space that overlap with the requested period
    List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(space.getId(),
        BookingStatus.CANCELLED, BookingStatus.REJECTED, requestStartDate, requestEndDate);

    // Space is available only if there are no overlapping bookings
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

    // Calculate the duration in days
    long durationInDays = ChronoUnit.DAYS.between(startDate, endDate) + 1; // +1 to include both
                                                                           // start and end days

    // Calculate the price per day (pricePerMonth / 30)
    double pricePerDay = pricePerMonth / 30.0;

    // Calculate total price (days * price per day)
    double totalPrice = durationInDays * pricePerDay;

    // Round to the nearest whole number and return as Long
    return Math.round(totalPrice);
  }

}
