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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
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
    User user = userRepository.findById(userPrincipal.getId())
        .orElseThrow(() -> new RuntimeException("User not found"));
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
    Set<UserRole> roles = new HashSet<>();
    signUpRequest.getRole().stream().forEach(role -> {
      switch (role) {
        case RENTER:
          roles.add(UserRole.RENTER);
          break;
        case HOST:
          roles.add(UserRole.HOST);
          break;
        default:
          roles.add(UserRole.HOST);
          break;
      }
    });
    user.setRoles(roles);
    user.setUserName(signUpRequest.getUsername());
    user.setEmail(signUpRequest.getEmail());
    // For demo purposes, set account as verified In production, you would implement email
    // verification logic
    user.setAccountVerified(true);

    // Create User Profile
    UserProfile profile = new UserProfile();
    profile.setUser(user);
    user.setProfile(profile);
    User savedUser = userRepository.save(user);

    String jwt = jwtTokenUtil.generateTokenFromUser(savedUser);
    AuthResponseDto authResponseDto = new AuthResponseDto();
    authResponseDto.setToken(jwt);
    authResponseDto.setUserName(user.getUserName());
    authResponseDto.setProvider(user.getProvider().toString());
    return authResponseDto;
  }

  @Override
  public User handleGetUserProfile(String authorization) {
    Long userId = jwtTokenUtil.getUserIdFromToken(authorization);
    return userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

  @Override
  @Transactional
  public void handleUpdateUserProfile(String authorization,
      UpdateProfileRequest updateProfileRequest) {
    User user = handleGetUserProfile(authorization);
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
      Address address = addressRepository.findById(addressId)
          .orElseThrow(() -> new RuntimeException("Address not found with ID: " + addressId));
      UserProfile profile = user.getProfile();
      profile.getAddresses().remove(address);
      address.setUserProfile(null);
      userRepository.save(user);
    } catch (Exception e) {
      throw new RuntimeException("Address not found");
    }
  }

  @Override
  @Transactional
  public void handleEditAddress(EditAddressRequest editAddressRequest, User user) {
    try {
      Long addressId = editAddressRequest.getId().longValue();
      Address existingAddress = addressRepository.findById(addressId)
          .orElseThrow(() -> new RuntimeException("Address not found with ID: " + addressId));
      if (existingAddress.getUserProfile() == null
          || !existingAddress.getUserProfile().getId().equals(user.getProfile().getId())) {
        throw new RuntimeException("Address does not belong to the current user");
      }
      existingAddress.setStreet(editAddressRequest.getStreet());
      existingAddress.setCity(editAddressRequest.getCity());
      existingAddress.setState(editAddressRequest.getState());
      existingAddress.setZipCode(editAddressRequest.getZipCode());
      existingAddress.setCountry(editAddressRequest.getCountry());
      if (editAddressRequest.getAddressLine1() != null) {
        existingAddress.setAddressLine1(editAddressRequest.getAddressLine1());
      }
      if (editAddressRequest.getAddressLine2() != null) {
        existingAddress.setAddressLine2(editAddressRequest.getAddressLine2());
      }
      addressRepository.save(existingAddress);
      userRepository.save(user);

    } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
      throw new RuntimeException(
          "Could not update address due to concurrent modification. Please try again.", e);
    } catch (Exception e) {
      throw new RuntimeException("Failed to update address: " + e.getMessage(), e);
    }
  }

  @Override
  public Set<@Valid Address> handleGetAddresses(User user) {
    return addressRepository.findAllByUserProfile(user.getProfile());
  }

  @Override
  @PreAuthorize("hasRole('ROLE_UNASSIGNED')")
  public void assignUserRole(User user, List<String> roles) {
    user.getRoles().clear();
    roles.forEach(role -> {
      switch (role) {
        case "RENTER":
          user.getRoles().add(UserRole.RENTER);
          break;
        case "HOST":
          user.getRoles().add(UserRole.HOST);
          break;
      }
    });
    userRepository.save(user);
  }
}
