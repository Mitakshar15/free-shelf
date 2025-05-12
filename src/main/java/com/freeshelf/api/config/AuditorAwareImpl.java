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

    // If authentication is null or anonymous, return a default value or empty
    if (authentication == null || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
      return Optional.of("system");
    }

    // Handle different authentication types
    Object principal = authentication.getPrincipal();

    if (principal instanceof UserPrincipal) {
      // JWT authentication with custom UserDetails
      return Optional.of(((UserPrincipal) principal).getUsername());
    } else if (principal instanceof OAuth2User oAuth2User) {
      // OAuth2 authentication
      String email = oAuth2User.getAttribute("email");
      if (email != null) {
        return Optional.of(email);
      }
      // Fallback to name attribute if email isn't available
      String name = oAuth2User.getAttribute("name");
      return Optional.ofNullable(name);
    } else if (principal instanceof String) {
      // Simple username authentication
      return Optional.of(principal.toString());
    }

    // If we can't determine a specific user, use the principal's string representation
    return Optional.of(authentication.getName());
  }

}
