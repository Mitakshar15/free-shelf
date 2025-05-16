package com.freeshelf.api.service.interfaces;

import com.freeshelf.api.data.domain.booking.Booking;
import com.freeshelf.api.data.domain.user.User;
import org.producr.api.dtos.BookingRequest;

import java.util.Set;

public interface BookingService {

  void handleBookStorageSpace(User renter, BookingRequest bookingRequest);

  Set<Booking> handleGetStorageSpaceBookings(Long spaceId, User host);

  void handleCancelBooking(Long bookingId);

  void handleAcceptBooking(Long bookingId);

  void handleRejectBooking(Long bookingId);
}
