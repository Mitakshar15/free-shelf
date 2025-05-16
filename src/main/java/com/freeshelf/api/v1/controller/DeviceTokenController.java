package com.freeshelf.api.v1.controller;

import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.dto.NotificationResponse;
import com.freeshelf.api.service.interfaces.PushNotificationService;
import com.freeshelf.api.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for managing device tokens for push notifications
 */
@RestController
@RequestMapping("/api/v1/device-tokens")
@RequiredArgsConstructor
@Slf4j
public class DeviceTokenController {

  private final PushNotificationService pushNotificationService;
  private final UserService userService;

  /**
   * Register a device token for the authenticated user
   *
   * @param request the registration request containing token and device info
   * @param user the authenticated user
   * @return response indicating success or failure
   */
  @PostMapping("/register")
  public ResponseEntity<NotificationResponse> registerDeviceToken(
      @RequestBody DeviceTokenRegistrationRequest request,
      @RequestHeader("Authorization") String authorization) {
    User user = userService.handleGetUserProfile(authorization);
    boolean success = pushNotificationService.registerDeviceToken(user, request.getToken(),
        request.getDeviceType(), request.getDeviceName());

    NotificationResponse response =
        NotificationResponse.builder().message(Map.of("success", String.valueOf(success), "message",
            success ? "Device registered successfully" : "Failed to register device")).build();

    return new ResponseEntity<>(response, success ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
  }

  /**
   * Unregister a device token
   *
   * @param token the token to unregister
   * @return response indicating success or failure
   */
  @DeleteMapping("/unregister")
  public ResponseEntity<NotificationResponse> unregisterDeviceToken(@RequestParam String token) {

    boolean success = pushNotificationService.unregisterDeviceToken(token);

    NotificationResponse response =
        NotificationResponse.builder()
            .message(Map.of("success", String.valueOf(success), "message",
                success ? "Device unregistered successfully" : "Failed to unregister device"))
            .build();

    return new ResponseEntity<>(response, success ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
  }

  /**
   * Request class for device token registration
   */
  public static class DeviceTokenRegistrationRequest {
    private String token;
    private String deviceType;
    private String deviceName;

    public String getToken() {
      return token;
    }

    public void setToken(String token) {
      this.token = token;
    }

    public String getDeviceType() {
      return deviceType;
    }

    public void setDeviceType(String deviceType) {
      this.deviceType = deviceType;
    }

    public String getDeviceName() {
      return deviceName;
    }

    public void setDeviceName(String deviceName) {
      this.deviceName = deviceName;
    }
  }
}
