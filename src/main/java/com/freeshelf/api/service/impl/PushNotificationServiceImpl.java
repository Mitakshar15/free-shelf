package com.freeshelf.api.service.impl;

import com.freeshelf.api.data.domain.message.Notification;
import com.freeshelf.api.data.domain.user.DeviceToken;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.data.repository.DeviceTokenRepository;
import com.freeshelf.api.service.interfaces.PushNotificationService;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the PushNotificationService interface Handles sending push notifications via
 * Firebase Cloud Messaging
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PushNotificationServiceImpl implements PushNotificationService {

  private final DeviceTokenRepository deviceTokenRepository;

  @Override
  @Transactional
  public boolean registerDeviceToken(User user, String token, String deviceType,
      String deviceName) {
    try {
      // Check if token already exists
      Optional<DeviceToken> existingToken = deviceTokenRepository.findByToken(token);

      if (existingToken.isPresent()) {
        DeviceToken deviceToken = existingToken.get();

        // Update existing token if it belongs to the same user
        if (deviceToken.getUser().getId().equals(user.getId())) {
          deviceToken.setActive(true);
          deviceToken.setDeviceType(deviceType);
          deviceToken.setDeviceName(deviceName);
          deviceTokenRepository.save(deviceToken);
          log.info("Updated device token for user ID: {}", user.getId());
          return true;
        } else {
          // Token exists but belongs to another user - deactivate old token and create new one
          deviceToken.setActive(false);
          deviceTokenRepository.save(deviceToken);

          createNewDeviceToken(user, token, deviceType, deviceName);
          log.info("Transferred device token from another user to user ID: {}", user.getId());
          return true;
        }
      } else {
        // Create new token
        createNewDeviceToken(user, token, deviceType, deviceName);
        log.info("Registered new device token for user ID: {}", user.getId());
        return true;
      }
    } catch (Exception e) {
      log.error("Error registering device token: {}", e.getMessage(), e);
      return false;
    }
  }

  private DeviceToken createNewDeviceToken(User user, String token, String deviceType,
      String deviceName) {
    DeviceToken newToken = DeviceToken.builder().user(user).token(token).deviceType(deviceType)
        .deviceName(deviceName).active(true).build();

    return deviceTokenRepository.save(newToken);
  }

  @Override
  @Transactional
  public boolean unregisterDeviceToken(String token) {
    try {
      Optional<DeviceToken> deviceToken = deviceTokenRepository.findByToken(token);

      if (deviceToken.isPresent()) {
        DeviceToken tokenEntity = deviceToken.get();
        tokenEntity.setActive(false);
        deviceTokenRepository.save(tokenEntity);
        log.info("Unregistered device token for user ID: {}", tokenEntity.getUser().getId());
        return true;
      }

      return false;
    } catch (Exception e) {
      log.error("Error unregistering device token: {}", e.getMessage(), e);
      return false;
    }
  }

  @Override
  public boolean sendPushNotification(User user, String title, String body, Object data) {
    if (FirebaseApp.getApps().isEmpty()) {
      log.warn("Firebase is not initialized. Push notification not sent.");
      return false;
    }

    try {
      List<DeviceToken> deviceTokens = deviceTokenRepository.findByUserAndActiveTrue(user);

      if (deviceTokens.isEmpty()) {
        log.info("No active device tokens found for user ID: {}", user.getId());
        return false;
      }

      List<String> tokens =
          deviceTokens.stream().map(DeviceToken::getToken).collect(Collectors.toList());

      // Create notification payload
      Builder notificationBuilder =
          com.google.firebase.messaging.Notification.builder().setTitle(title).setBody(body);

      // Create multicast message to send to multiple devices
      MulticastMessage.Builder messageBuilder = MulticastMessage.builder()
          .setNotification(notificationBuilder.build()).addAllTokens(tokens);

      // Add data payload if provided
      if (data != null) {
        Map<String, String> dataMap = convertToStringMap(data);
        messageBuilder.putAllData(dataMap);
      }

      // Send the message
      FirebaseMessaging.getInstance().sendEachForMulticast(messageBuilder.build());
      log.info("Push notification sent to {} devices for user ID: {}", tokens.size(), user.getId());

      return true;
    } catch (FirebaseMessagingException e) {
      log.error("Error sending push notification: {}", e.getMessage(), e);
      return false;
    }
  }

  @Override
  public boolean sendPushNotification(Notification notification) {
    if (notification == null) {
      return false;
    }

    // Create data payload with notification details
    Map<String, Object> data = new HashMap<>();
    data.put("notificationId", notification.getId().toString());
    data.put("type", notification.getType().toString());
    data.put("referenceId",
        notification.getReferenceId() != null ? notification.getReferenceId().toString() : null);
    data.put("referenceType", notification.getReferenceType());
    data.put("actionUrl", notification.getActionUrl());

    return sendPushNotification(notification.getUser(), notification.getTitle(),
        notification.getMessage(), data);
  }

  /**
   * Convert an object to a string map for FCM data payload
   *
   * @param data the data object
   * @return map with string values
   */
  private Map<String, String> convertToStringMap(Object data) {
    Map<String, String> result = new HashMap<>();

    if (data instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, Object> dataMap = (Map<String, Object>) data;

      dataMap.forEach((key, value) -> {
        if (value != null) {
          result.put(key, value.toString());
        }
      });
    }

    return result;
  }
}
