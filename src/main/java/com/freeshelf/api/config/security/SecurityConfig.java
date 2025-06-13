package com.freeshelf.api.config.security;

import com.freeshelf.api.config.security.jwt.JwtAuthenticationEntryPoint;
import com.freeshelf.api.config.security.jwt.JwtRequestFilter;
import com.freeshelf.api.config.security.oauth.CustomOAuth2UserService;
import com.freeshelf.api.config.security.oauth.OAuth2AuthenticationSuccessHandler;
import com.freeshelf.api.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomUserDetailsService userDetailsService;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtRequestFilter jwtRequestFilter;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable).cors(cors -> cors.configurationSource(request -> {
      CorsConfiguration cfg = new CorsConfiguration();
      cfg.setAllowedOrigins(
          Arrays.asList("http://localhost:3000", "http://localhost:4000", "http://localhost:4202",
              "https://mitakshar-ecom.vercel.app/", "https://free-shelf-app.vercel.app",
              "https://free-shelf-app-git-stg-mitakshar15s-projects.vercel.app"));
      cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
      cfg.setAllowCredentials(true);
      cfg.setAllowedHeaders(Collections.singletonList("*"));
      cfg.setExposedHeaders(List.of("Authorization"));
      cfg.setMaxAge(3600L);
      return cfg;
    })).authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/**", "/oauth2/**", "/**")
        .permitAll().anyRequest().authenticated())
        .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2Login(oauth2 -> oauth2
            .authorizationEndpoint(authEndpoint -> authEndpoint.baseUri("/oauth2/authorize"))
            .redirectionEndpoint(redirectEndpoint -> redirectEndpoint.baseUri("/api/v1/oauth2/**"))
            .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
            .successHandler(oAuth2AuthenticationSuccessHandler)
            .failureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error=true")));

    // Add JWT filter
    http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
      throws Exception {
    return authConfig.getAuthenticationManager();
  }
}
