package com.freeshelf.api.service.interfaces;

import com.freeshelf.api.data.domain.message.Notification;
import com.freeshelf.api.data.domain.user.User;

/**
 * Service for sending push notifications to user devices
 */
public interface PushNotificationService {
    
    /**
     * Register a device token for a user
     * 
     * @param user the user to register the token for
     * @param token the device token
     * @param deviceType the type of device (e.g., android, ios)
     * @param deviceName the name of the device
     * @return true if registration was successful
     */
    boolean registerDeviceToken(User user, String token, String deviceType, String deviceName);
    
    /**
     * Unregister a device token
     * 
     * @param token the device token to unregister
     * @return true if unregistration was successful
     */
    boolean unregisterDeviceToken(String token);
    
    /**
     * Send a push notification to a user
     * 
     * @param user the user to send the notification to
     * @param title the notification title
     * @param body the notification body
     * @param data additional data to include in the notification
     * @return true if the notification was sent successfully
     */
    boolean sendPushNotification(User user, String title, String body, Object data);
    
    /**
     * Send a push notification based on a notification entity
     * 
     * @param notification the notification entity
     * @return true if the notification was sent successfully
     */
    boolean sendPushNotification(Notification notification);
}
