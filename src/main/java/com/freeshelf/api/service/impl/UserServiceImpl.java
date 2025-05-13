package com.freeshelf.api.service.impl;

import com.freeshelf.api.builder.ApiResponseBuilder;
import com.freeshelf.api.config.security.jwt.JwtTokenUtil;
import com.freeshelf.api.config.security.jwt.UserPrincipal;
import com.freeshelf.api.data.domain.user.Address;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.data.domain.user.UserProfile;
import com.freeshelf.api.data.repository.AddressRepository;
import com.freeshelf.api.data.repository.UserProfileRepository;
import com.freeshelf.api.data.repository.UserRepository;
import com.freeshelf.api.mapper.UserMgmtMapper;
import com.freeshelf.api.service.interfaces.UserService;
import com.freeshelf.api.utils.enums.AuthProvider;
import com.freeshelf.api.utils.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.producr.api.dtos.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final AuthenticationManager authenticationManager;
  private final JwtTokenUtil jwtTokenUtil;
  private final UserRepository userRepository;
  private final ApiResponseBuilder builder;
  private final UserMgmtMapper mapper;
  private final PasswordEncoder passwordEncoder;
  private final UserProfileRepository userProfileRepository;
  private final AddressRepository addressRepository;

  @Override
  public AuthResponseDto handleSignIn(SignInRequest signInRequest) {
    Authentication authentication =
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            signInRequest.getEmail(), signInRequest.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    String jwt = jwtTokenUtil.generateToken(userPrincipal);
    User user = userRepository.findById(userPrincipal.getId()).get();
    user.setLastLoginAt(LocalDateTime.now());
    userRepository.save(user);
    AuthResponseDto authResponseDto = new AuthResponseDto();
    authResponseDto.setToken(jwt);
    authResponseDto.setUserName(user.getUserName());
    authResponseDto.setProvider(user.getProvider().toString());
    return authResponseDto;
  }

  @Override
  public AuthResponseDto handleSignUp(SignUpRequest signUpRequest) {
    if (userRepository.existsByUserName(signUpRequest.getUsername())) {
      throw new RuntimeException("UserName already in use"); // Change it to Custom Exception
    }
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      throw new RuntimeException("Email already in use");
    }

    // Create New User
    User user = new User();
    user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
    user.setProvider(AuthProvider.LOCAL);
    // default Renter

    user.setFirstName(signUpRequest.getFirstName());
    user.setLastName(signUpRequest.getLastName());
    switch (signUpRequest.getRole()) {
      case RENTER:
        user.setRole(UserRole.RENTER);
        break;
      case HOST:
        user.setRole(UserRole.HOST);
        break;
      default:
        user.setRole(UserRole.ADMIN);
        break;
    }

    user.setUserName(signUpRequest.getUsername());
    user.setEmail(signUpRequest.getEmail());
    // For demo purposes, set account as verified In production, you would implement email
    // verification logic
    user.setAccountVerified(true);

    // Create User Profile
    UserProfile profile = new UserProfile();
    profile.setUser(user);
    // UserProfile savedProfile = userProfileRepository.save(profile);
    user.setProfile(profile);
    User savedUser = userRepository.save(user);

    // Generate Jwt for immediate login
    String jwt = jwtTokenUtil.generateTokenFromUser(savedUser);
    AuthResponseDto authResponseDto = new AuthResponseDto();
    authResponseDto.setToken(jwt);
    authResponseDto.setUserName(user.getUserName());
    authResponseDto.setProvider(user.getProvider().toString());
    return authResponseDto;
  }

  @Override
  public User handleGetUserProfile(String authorization) {
    String userId = jwtTokenUtil.getUsernameFromToken(authorization);
    return userRepository.findByEmailOrUserName(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

  @Override
  @Transactional
  public void handleUpdateUserProfile(User user, UpdateProfileRequest updateProfileRequest) {
    user.setFirstName(updateProfileRequest.getFirstName());
    user.setLastName(updateProfileRequest.getLastName());
    user.getProfile().setBio(updateProfileRequest.getBio());
    user.getProfile().setProfileImageUrl(updateProfileRequest.getProfileImageUrl());
    userRepository.save(user);
  }


  @Override
  public void handleAddNewAddress(User user, AddNewAddressRequest addNewAddressRequest) {
    UserProfile profile = user.getProfile();
    Address address = fillAddress(addNewAddressRequest);
    address.setUserProfile(profile);
    profile.getAddresses().add(address);
    userProfileRepository.save(profile);
  }

  private Address fillAddress(AddNewAddressRequest addNewAddressRequest) {
    Address address = mapper.toAddressEntity(addNewAddressRequest);
    // TODO: apply logic to set the longitude and latitude automatically
    address.setLatitude(BigDecimal.valueOf(0));
    address.setLongitude(BigDecimal.valueOf(0));
    return address;
  }

  @Override
  public void handleDeleteAddress(User user, Long addressId) {
    try {
      user.getProfile().getAddresses().remove(addressRepository.findById(addressId)
          .orElseThrow(() -> new RuntimeException("Address not found")));
      userRepository.save(user);
    } catch (Exception e) {
      throw new RuntimeException("Address not found");
    }
  }

  @Override
  public void handleEditAddress(EditAddressRequest editAddressRequest) {
    Address address = mapper.toAddressEntity(editAddressRequest);
    addressRepository.save(address);
  }

  @Override
  public Set<@Valid Address> handleGetAddresses(User user) {
    Set<Address> addresses = user.getProfile().getAddresses();
    return addresses;
  }
}
