package com.freeshelf.api.data.repository;

import com.freeshelf.api.data.domain.space.SpaceImage;
import com.freeshelf.api.data.domain.user.User;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpaceImageRepository extends JpaRepository<SpaceImage, Long> {


  Optional<SpaceImage> findByIdAndSpaceId(Long imageId, Long spaceId);

  @Modifying
  @Query("UPDATE SpaceImage s SET s.primary = false WHERE s.space.id = :spaceId")
  void resetPrimaryFlagForSpace(@Param("spaceId") Long spaceId);

  boolean existsBySpaceIdAndPrimaryTrue(Long spaceId);



}
