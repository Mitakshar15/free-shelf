package com.freeshelf.api.data.repository;

import com.freeshelf.api.data.domain.user.DeviceToken;
import com.freeshelf.api.data.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing device tokens
 */
@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {
    
    /**
     * Find all active device tokens for a user
     * 
     * @param user the user to find tokens for
     * @return list of active device tokens
     */
    List<DeviceToken> findByUserAndActiveTrue(User user);
    
    /**
     * Find a device token by its token value
     * 
     * @param token the token value
     * @return optional containing the device token if found
     */
    Optional<DeviceToken> findByToken(String token);
    
    /**
     * Delete all device tokens for a user
     * 
     * @param user the user to delete tokens for
     */
    void deleteAllByUser(User user);
}
