package com.freeshelf.api.config.security.jwt;

import com.freeshelf.api.data.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class UserPrincipal implements OAuth2User, UserDetails {
  private final Long id;
  private final String username;
  private final String email;
  private final String password;
  private final boolean accountVerified;
  private final Collection<? extends GrantedAuthority> authorities;
  @Setter
  private Map<String, Object> attributes;

  public UserPrincipal(Long id, String username, String email, String password,
      boolean accountVerified, Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
    this.accountVerified = accountVerified;
    this.authorities = authorities;
  }

  public static UserPrincipal create(User user) {
    List<GrantedAuthority> authorities =
        user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
            .collect(Collectors.toList());

    return new UserPrincipal(user.getId(), user.getUserName(), user.getEmail(), user.getPassword(),
        user.isAccountVerified(), authorities);
  }

  public static UserPrincipal create(User user, Map<String, Object> attributes) {
    UserPrincipal userPrincipal = UserPrincipal.create(user);
    userPrincipal.setAttributes(attributes);
    return userPrincipal;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return accountVerified;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public String getName() {
    return String.valueOf(id);
  }
}
