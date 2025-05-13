package com.freeshelf.api.v1.controller;

import com.freeshelf.api.builder.ApiResponseBuilder;
import com.freeshelf.api.mapper.UserMgmtMapper;
import com.freeshelf.api.service.interfaces.UserService;
import com.freeshelf.api.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.producr.api.AuthControllerV1Api;
import org.producr.api.dtos.AuthResponse;
import org.producr.api.dtos.SignInRequest;
import org.producr.api.dtos.SignUpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerV1Api {

  private final UserMgmtMapper mapper;
  private final ApiResponseBuilder builder;
  private final UserService userService;

  @Override
  public ResponseEntity<AuthResponse> signIn(SignInRequest signInRequest) throws Exception {
    AuthResponse authResponse =
        mapper.toAuthResponse(builder.buildSuccessApiResponse(Constants.SIGN_IN_SUCCESS_MESSAGE));
    authResponse.setData(userService.handleSignIn(signInRequest));
    return new ResponseEntity<>(authResponse, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<AuthResponse> signUp(SignUpRequest signUpRequest) throws Exception {
    AuthResponse response =
        mapper.toAuthResponse(builder.buildSuccessApiResponse(Constants.SIGN_UP_SUCCESS_MESSAGE));
    response.setData(userService.handleSignUp(signUpRequest));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
