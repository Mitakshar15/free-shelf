package com.freeshelf.api.service.interfaces;

import com.freeshelf.api.data.domain.user.User;
import org.producr.api.dtos.BookingRequest;

public interface BookingService {

  void handleBookStorageSpace(User renter, BookingRequest bookingRequest);
}
