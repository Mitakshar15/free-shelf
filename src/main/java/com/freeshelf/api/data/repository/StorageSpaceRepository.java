package com.freeshelf.api.data.repository;

import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface StorageSpaceRepository extends JpaRepository<StorageSpace, Long> {
    Optional<Set<StorageSpace>> findByHost(User host);
}
