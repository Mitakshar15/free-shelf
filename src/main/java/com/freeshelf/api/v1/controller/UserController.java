package com.freeshelf.api.v1.controller;

import com.freeshelf.api.builder.ApiResponseBuilder;
import com.freeshelf.api.config.security.jwt.JwtTokenUtil;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.mapper.UserMgmtMapper;
import com.freeshelf.api.service.interfaces.UserService;
import com.freeshelf.api.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.producr.api.UserControllerV1Api;
import org.producr.api.dtos.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerV1Api {

  private final UserMgmtMapper mapper;
  private final ApiResponseBuilder apiResponseBuilder;
  private final UserService userService;
  private final JwtTokenUtil jwtTokenUtil;

  @Override
  public ResponseEntity<FreeShelfApiBaseApiResponse> updateUser(String authorization,
      UpdateProfileRequest updateProfileRequest) throws Exception {
    FreeShelfApiBaseApiResponse response = mapper.toUserMgmtBaseApiResponse(
        apiResponseBuilder.buildSuccessApiResponse(Constants.UPDATE_USER_PROFILE_SUCCESS_MESSAGE));
    userService.handleUpdateUserProfile(authorization, updateProfileRequest);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<UserProfileResponse> getUser(String authorization) throws Exception {
    UserProfileResponse response = mapper.toUserProfileResponse(
        apiResponseBuilder.buildSuccessApiResponse(Constants.GET_USER_PROFILE_SUCCESS_MESSAGE));
    response.data(mapper.toUserDto(userService.handleGetUserProfile(authorization)));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<FreeShelfApiBaseApiResponse> addAddress(String authorization,
      AddNewAddressRequest addNewAddressRequest) throws Exception {
    FreeShelfApiBaseApiResponse response = mapper.toUserMgmtBaseApiResponse(
        apiResponseBuilder.buildSuccessApiResponse(Constants.ADD_ADDRESS_SUCCESS_MESSAGE));
    User user = userService.handleGetUserProfile(authorization);
    userService.handleAddNewAddress(user, addNewAddressRequest);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<FreeShelfApiBaseApiResponse> deleteAddress(String authorization,
      Long addressId) throws Exception {
    FreeShelfApiBaseApiResponse response = mapper.toUserMgmtBaseApiResponse(
        apiResponseBuilder.buildSuccessApiResponse(Constants.DELETE_ADDRESS_SUCCESS_MESSAGE));
    User user = userService.handleGetUserProfile(authorization);
    userService.handleDeleteAddress(user, addressId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<FreeShelfApiBaseApiResponse> editAddress(String authorization,
      EditAddressRequest editAddressRequest) throws Exception {
    FreeShelfApiBaseApiResponse response = mapper.toUserMgmtBaseApiResponse(
        apiResponseBuilder.buildSuccessApiResponse(Constants.EDIT_ADDRESS_SUCCESS_MESSAGE));
    User user = userService.handleGetUserProfile(authorization);
    userService.handleEditAddress(editAddressRequest,user);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<AddressResponse> getAddresses(String authorization) throws Exception {
    AddressResponse response = mapper.toAddressResponse(
        apiResponseBuilder.buildSuccessApiResponse(Constants.GET_ADDRESS_SUCCUESS_MESSAGE));
    User user = userService.handleGetUserProfile(authorization);
    response.data(mapper.toAddressSet(userService.handleGetAddresses(user)));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
