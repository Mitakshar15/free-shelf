package com.freeshelf.api.service.impl;

import com.freeshelf.api.data.domain.booking.Booking;
import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.data.repository.BookingRepository;
import com.freeshelf.api.data.repository.StorageSpaceRepository;
import com.freeshelf.api.service.interfaces.BookingService;
import com.freeshelf.api.utils.enums.BookingStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.producr.api.dtos.BookingRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

  private final StorageSpaceRepository storageSpaceRepository;
  private final BookingRepository bookingRepository;

  @Override
  @PreAuthorize("hasRole('ROLE_RENTER')")
  public void handleBookStorageSpace(User renter, BookingRequest bookingRequest) {

    Booking booking = new Booking();
    StorageSpace space = storageSpaceRepository.findById(bookingRequest.getSpaceId())
        .orElseThrow(() -> new RuntimeException(
            "Storage space not found with id: " + bookingRequest.getSpaceId()));
    booking.setSpace(space);
    booking.setRenter(renter);
    booking.setStartDate(bookingRequest.getStartDate());
    booking.setEndDate(bookingRequest.getEndDate());
    booking.setTotalPrice(calculateTotalPriceForBooking(bookingRequest.getStartDate(),
        bookingRequest.getEndDate(), space.getPricePerMonth()));
    booking.setStatus(BookingStatus.PENDING);
    if(checkAvailability(bookingRequest, space)) {
      bookingRepository.save(booking);
    }
    else throw new RuntimeException("Space is not available for booking");
  }

  private boolean checkAvailability(BookingRequest bookingRequest, StorageSpace space) {
    //TODO: check if space is available for booking in the given period;
    return true;
  }

  private @NotNull Long calculateTotalPriceForBooking(@Valid OffsetDateTime startDate,
      @Valid OffsetDateTime endDate, Double pricePerMonth) {
   //TODO: calculate total price for booking by analysing the priceper month of space, and start date and end date of the booking
    return 0L;
  }
}
