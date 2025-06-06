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
import lombok.extern.slf4j.Slf4j;
import org.producr.api.dtos.AuthResponse;
import org.producr.api.dtos.AuthResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtTokenUtil jwtTokenUtil;
  private final UserMgmtMapper mapper;
  private final ApiResponseBuilder builder;


  @Value("${oauth.cookie.redirect.url}")
  private String redirectUrl;

  @Value("${oauth.cookie.domain}")
  private String cookieDomain;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    log.info("INSIDE OAUTH AUTH SUCCESS HANDLER :: onAuthenticationSuccess method");
    clearAuthenticationAttributes(request);
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

    String token = jwtTokenUtil.generateToken(userPrincipal);

    // Set token in HTTP-only cookie
    Cookie tokenCookie = new Cookie("free-shelf-token", token);
    tokenCookie.setHttpOnly(false);
    tokenCookie.setSecure(true); // for HTTPS
    tokenCookie.setPath("/");
    tokenCookie.setMaxAge(7 * 24 * 60 * 60 * 1000);
    tokenCookie.setDomain(cookieDomain);
    tokenCookie.setAttribute("SameSite", "None");
    response.addCookie(tokenCookie);

    // Log cookie details for debugging
    log.debug("OAuthSuccessHandler - Setting cookie: free-shelf-token");
    log.debug("Token length: " + token.length());
    log.debug("Cookie domain: " + cookieDomain);
    log.debug("Set-Cookie header: " + response.getHeader("Set-Cookie"));
    // Set CORS headers
    response.setHeader("Access-Control-Allow-Origin", "https://free-shelf-app.vercel.app");
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
    response.setHeader("Access-Control-Allow-Headers", "*");
    // Redirect to frontend application
    response.sendRedirect(redirectUrl);
    log.info("EXITING OAUTH AUTH SUCCESS HANDLER :: onAuthenticationSuccess method");
  }
}
