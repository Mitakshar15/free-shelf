package com.freeshelf.api.v1.controller;

import com.freeshelf.api.builder.ApiResponseBuilder;
import com.freeshelf.api.config.security.jwt.JwtTokenUtil;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.mapper.UserMgmtMapper;
import com.freeshelf.api.service.interfaces.UserService;
import com.freeshelf.api.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.producr.api.UserControllerV1Api;
import org.producr.api.dtos.UpdateProfileRequest;
import org.producr.api.dtos.UserMgmtBaseApiResponse;
import org.producr.api.dtos.UserProfileResponse;
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
  public ResponseEntity<UserMgmtBaseApiResponse> updateUser(String authorization,
      UpdateProfileRequest updateProfileRequest) throws Exception {
    UserMgmtBaseApiResponse response = mapper.toUserMgmtBaseApiResponse(
        apiResponseBuilder.buildSuccessApiResponse(Constants.UPDATE_USER_PROFILE_SUCCESS_MESSAGE));
    User user= userService.handleGetUserProfile(authorization);
    userService.handleUpdateUserProfile(user, updateProfileRequest);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<UserProfileResponse> getUser(String authorization) throws Exception {
    UserProfileResponse response = mapper.toUserProfileResponse(
        apiResponseBuilder.buildSuccessApiResponse(Constants.GET_USER_PROFILE_SUCCESS_MESSAGE));
    response.data(mapper.toUserDto(userService.handleGetUserProfile(authorization)));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
