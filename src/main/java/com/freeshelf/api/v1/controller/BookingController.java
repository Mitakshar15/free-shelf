package com.freeshelf.api.v1.controller;

import com.freeshelf.api.builder.ApiResponseBuilder;
import com.freeshelf.api.data.domain.booking.Booking;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.mapper.StorageSpaceMapper;
import com.freeshelf.api.service.interfaces.BookingService;
import com.freeshelf.api.service.interfaces.UserService;
import com.freeshelf.api.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.producr.api.BookingControllerV1Api;
import org.producr.api.dtos.BookingRequest;
import org.producr.api.dtos.BookingResponse;
import org.producr.api.dtos.FreeShelfApiBaseApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BookingController implements BookingControllerV1Api {

  private final StorageSpaceMapper mapper;
  private final ApiResponseBuilder apiResponseBuilder;
  private final UserService userService;
  private final BookingService bookingService;

  @Override
  public ResponseEntity<FreeShelfApiBaseApiResponse> bookStorageSpace(String authorization,
      BookingRequest bookingRequest) throws Exception {
    FreeShelfApiBaseApiResponse response = mapper.toBaseApiResponse(
        apiResponseBuilder.buildSuccessApiResponse(Constants.BOOK_STORAGE_SPACE_SUCCESS_MESSAGE));
    User renter = userService.handleGetUserProfile(authorization);
    bookingService.handleBookStorageSpace(renter, bookingRequest);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<BookingResponse> getStorageSpaceBookings(String authorization, Long spaceId)
      throws Exception {
    BookingResponse response = mapper.toBookingResponse(apiResponseBuilder
        .buildSuccessApiResponse(Constants.GET_STORAGE_SPACE_BOOKINGS_SUCCESS_MESSAGE));
    User host = userService.handleGetUserProfile(authorization);
    response.data(mapper.toBookingSet(bookingService.handleGetStorageSpaceBookings(spaceId, host)));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<FreeShelfApiBaseApiResponse> payForStorageSpace(String authorization,
      BookingRequest bookingRequest) throws Exception {
    return BookingControllerV1Api.super.payForStorageSpace(authorization, bookingRequest);
  }
}
