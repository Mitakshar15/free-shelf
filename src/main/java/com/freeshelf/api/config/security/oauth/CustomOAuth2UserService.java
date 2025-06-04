package com.freeshelf.api.config.security.oauth;

import com.freeshelf.api.config.security.jwt.UserPrincipal;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.data.domain.user.UserProfile;
import com.freeshelf.api.data.repository.UserRepository;
import com.freeshelf.api.utils.enums.AuthProvider;
import com.freeshelf.api.utils.enums.UserRole;
import com.freeshelf.api.utils.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest)
      throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

    try {
      return processOAuth2User(oAuth2UserRequest, oAuth2User);
    } catch (Exception ex) {
      throw new OAuth2AuthenticationException(ex.getMessage());
    }
  }

  private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
    GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());

    if (StringUtils.isEmpty(userInfo.getEmail())) {
      throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
    }

    Optional<User> userOptional = userRepository.findByEmail(userInfo.getEmail());
    User user;

    if (userOptional.isPresent()) {
      user = userOptional.get();

      if (user.getProvider() == AuthProvider.LOCAL) {
        user.setProvider(AuthProvider.GOOGLE);
        user.setProviderId(userInfo.getId());
        user = userRepository.save(user);
      }
      user.setLastLoginAt(LocalDateTime.now());
      userRepository.save(user);
    } else {
      user = registerNewUser(oAuth2UserRequest, userInfo);
    }

    return UserPrincipal.create(user, oAuth2User.getAttributes());
  }

  private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, GoogleOAuth2UserInfo userInfo) {
    User user = new User();

    user.setProvider(AuthProvider.GOOGLE);
    user.setProviderId(userInfo.getId());
    // Todo: Handle the Existing Username Properly while trying to login with google
    if (userRepository.existsByUserName(userInfo.getName())) {
      user.setUserName(userInfo.getName() + UUID.randomUUID().toString().substring(0, 2));
    } else {
      user.setUserName(userInfo.getName());
    }
    user.setEmail(userInfo.getEmail());
    user.setPassword("");
    user.setAccountVerified(true);
    user.setFirstName(userInfo.getName());
    user.setStatus(UserStatus.ACTIVE);
    // Initial Role is set as Host by Default, later implement a service to choose roles for the
    // user
    user.setRoles(Set.of(UserRole.UNASSIGNED));
    UserProfile profile = new UserProfile();
    profile.setUser(user);
    profile.setProfileImageUrl(userInfo.getImageUrl());
    user.setProfile(profile);

    return userRepository.save(user);
  }
}
