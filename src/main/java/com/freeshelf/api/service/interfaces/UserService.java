package com.freeshelf.api.service.interfaces;

import com.freeshelf.api.data.domain.user.User;
import org.producr.api.dtos.*;

public interface UserService {
  AuthResponseDto handleSignIn(SignInRequest signInRequest);

  AuthResponseDto handleSignUp(SignUpRequest signUpRequest);

  User handleGetUserProfile(String authorization);

  void handleUpdateUserProfile(User user, UpdateProfileRequest updateProfileRequest);
}
