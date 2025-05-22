package com.freeshelf.api.config.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.freeshelf.api.builder.ApiResponseBuilder;
import com.freeshelf.api.config.security.jwt.JwtTokenUtil;
import com.freeshelf.api.config.security.jwt.UserPrincipal;
import com.freeshelf.api.mapper.UserMgmtMapper;
import com.freeshelf.api.utils.Constants;
import com.freeshelf.api.utils.enums.AuthProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.producr.api.dtos.AuthResponse;
import org.producr.api.dtos.AuthResponseDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtTokenUtil jwtTokenUtil;
  private final UserMgmtMapper mapper;
  private final ApiResponseBuilder builder;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
    clearAuthenticationAttributes(request);
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

    String token = jwtTokenUtil.generateToken(userPrincipal);

    // Set token in HTTP-only cookie
    Cookie tokenCookie = new Cookie("free-shelf-token", token);
//    tokenCookie.setHttpOnly(true);
    tokenCookie.setSecure(true); // for HTTPS
    tokenCookie.setPath("/");
    tokenCookie.setMaxAge(7*24 * 60 * 60 * 1000);
    tokenCookie.setDomain("localhost");
    response.addCookie(tokenCookie);

    // Redirect to frontend application
    response.sendRedirect("http://localhost:4200/");
  }
}
