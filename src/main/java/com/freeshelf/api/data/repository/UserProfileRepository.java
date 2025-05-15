package com.freeshelf.api.data.repository;

import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.data.domain.user.UserProfile;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

}
