package com.freeshelf.api.config;

import com.freeshelf.api.config.security.jwt.UserPrincipal;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // If authentication is null or anonymous, i.e Signup/Register etc
    if (authentication == null || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
      return Optional.of("system");
    }

    Object principal = authentication.getPrincipal();

    if (principal instanceof UserPrincipal) {
      return Optional.of(((UserPrincipal) principal).getUsername());
    } else if (principal instanceof OAuth2User oAuth2User) {
      String email = oAuth2User.getAttribute("email");
      if (email != null) {
        return Optional.of(email);
      }
      String name = oAuth2User.getAttribute("name");
      return Optional.ofNullable(name);
    } else if (principal instanceof String) {
      // Simple username authentication
      return Optional.of(principal.toString());
    }

    return Optional.of(authentication.getName());
  }

}
