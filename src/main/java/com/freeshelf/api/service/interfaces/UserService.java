package com.freeshelf.api.service.interfaces;

import com.freeshelf.api.data.domain.user.Address;
import com.freeshelf.api.data.domain.user.User;
import jakarta.validation.Valid;
import org.producr.api.dtos.*;

import java.util.List;
import java.util.Set;

public interface UserService {
  AuthResponseDto handleSignIn(SignInRequest signInRequest);

  AuthResponseDto handleSignUp(SignUpRequest signUpRequest);

  User handleGetUserProfile(String authorization);

  void handleUpdateUserProfile(String authorization, UpdateProfileRequest updateProfileRequest);

  void handleAddNewAddress(User user, AddNewAddressRequest addNewAddressRequest);

  void handleDeleteAddress(User user, Long addressId);

  void handleEditAddress(EditAddressRequest editAddressRequest);

  Set<@Valid Address> handleGetAddresses(User user);
}
