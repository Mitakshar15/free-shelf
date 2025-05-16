package com.freeshelf.api.service.impl;

import com.freeshelf.api.data.domain.booking.Booking;
import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.data.repository.BookingRepository;
import com.freeshelf.api.data.repository.StorageSpaceRepository;
import com.freeshelf.api.service.interfaces.BookingService;
import com.freeshelf.api.utils.BookingUtils;
import com.freeshelf.api.utils.enums.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.producr.api.dtos.BookingRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

  private final StorageSpaceRepository storageSpaceRepository;
  private final BookingRepository bookingRepository;
  private final BookingUtils bookingUtils;

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
    booking.setTotalPrice(bookingUtils.calculateTotalPriceForBooking(bookingRequest.getStartDate(),
        bookingRequest.getEndDate(), space.getPricePerMonth()));
    booking.setStatus(BookingStatus.PENDING);
    if (bookingUtils.checkAvailability(bookingRequest, space)) {
      bookingRepository.save(booking);
    } else
      throw new RuntimeException("Space is not available for booking");
  }

  @Override
  @PreAuthorize("hasRole('ROLE_HOST')")
  public Set<Booking> handleGetStorageSpaceBookings(Long spaceId, User host) {
    StorageSpace space = storageSpaceRepository.findById(spaceId)
        .orElseThrow(() -> new RuntimeException("Space not found"));
    return bookingRepository.findAllBySpace(space, host).orElse(Set.of());
  }

}
