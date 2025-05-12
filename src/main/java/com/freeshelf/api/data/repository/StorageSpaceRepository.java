package com.freeshelf.api.data.repository;

import com.freeshelf.api.data.domain.space.StorageSpace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageSpaceRepository extends JpaRepository<StorageSpace, Long> {
}
