package com.freeshelf.api.data.repository;

import com.freeshelf.api.data.domain.user.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

}
