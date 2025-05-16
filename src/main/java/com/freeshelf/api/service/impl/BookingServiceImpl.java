package com.freeshelf.api.service.impl;

import com.freeshelf.api.data.domain.booking.Booking;
import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.data.repository.BookingRepository;
import com.freeshelf.api.data.repository.StorageSpaceRepository;
import com.freeshelf.api.service.interfaces.BookingService;
import com.freeshelf.api.service.interfaces.NotificationService;
import com.freeshelf.api.utils.BookingUtils;
import com.freeshelf.api.utils.enums.BookingStatus;
import com.freeshelf.api.utils.enums.SpaceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.producr.api.dtos.BookingRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

  private final StorageSpaceRepository storageSpaceRepository;
  private final BookingRepository bookingRepository;
  private final BookingUtils bookingUtils;
  private final NotificationService notificationService;

  @Override
  @PreAuthorize("hasRole('ROLE_RENTER')")
  @Transactional
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
    if (bookingUtils.checkAvailability(bookingRequest.getStartDate(), bookingRequest.getEndDate(),
        space)) {
      booking = bookingRepository.save(booking);

      // Send notification to the host about the new booking request
      notificationService.sendBookingRequestNotification(booking);
      log.info("Created new booking with ID: {} and sent notification to host", booking.getId());
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

  @Override
  @PreAuthorize("hasRole('ROLE_HOST')")
  @Transactional
  public void handleRejectBooking(Long bookingId, Long userId) {
    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new RuntimeException("Booking not found"));
    if (!Objects.equals(userId, booking.getSpace().getHost().getId())) {
      throw new RuntimeException("You are not the host of this space");
    }
    booking.setStatus(BookingStatus.REJECTED);
    booking.setStatusUpdatedAt(LocalDateTime.now());
    booking.getSpace().setStatus(SpaceStatus.ACTIVE);
    booking = bookingRepository.save(booking);

    // Send notification to the renter about the rejected booking
    notificationService.sendBookingStatusUpdateNotification(booking);
    log.info("Rejected booking with ID: {} and sent notification to renter", booking.getId());
  }

  @Override
  @PreAuthorize("hasRole('ROLE_HOST')")
  @Transactional
  public void handleAcceptBooking(Long bookingId, Long userId) {
    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new RuntimeException("Booking not found"));
    if (!Objects.equals(userId, booking.getSpace().getHost().getId())) {
      throw new RuntimeException("You are not the host of this space");
    }
    booking.getSpace().setStatus(SpaceStatus.BOOKED);
    booking.setStatus(BookingStatus.APPROVED);
    booking.setStatusUpdatedAt(LocalDateTime.now());
    booking = bookingRepository.save(booking);

    // Send notification to the renter about the approved booking
    notificationService.sendBookingStatusUpdateNotification(booking);
    log.info("Approved booking with ID: {} and sent notification to renter", booking.getId());
  }


  @Override
  @PreAuthorize("hasRole('ROLE_RENTER')")
  @Transactional
  public void handleCancelBooking(Long bookingId, Long userId) {
    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new RuntimeException("Booking not found"));
    if (!Objects.equals(userId, booking.getRenter().getId())) {
      throw new RuntimeException("You are not the renter of this booking");
    }
    booking.setStatus(BookingStatus.CANCELLED);
    booking.setStatusUpdatedAt(LocalDateTime.now());
    booking.getSpace().setStatus(SpaceStatus.ACTIVE);
    booking = bookingRepository.save(booking);

    // Send notification to the host about the cancelled booking
    notificationService.sendBookingStatusUpdateNotification(booking);
    log.info("Cancelled booking with ID: {} and sent notification to host", booking.getId());
  }



}
