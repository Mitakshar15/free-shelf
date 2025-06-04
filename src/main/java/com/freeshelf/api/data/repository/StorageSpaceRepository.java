package com.freeshelf.api.data.repository;

import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.domain.user.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

public interface StorageSpaceRepository extends JpaRepository<StorageSpace, Long> {
  Optional<Set<StorageSpace>> findByHost(User host);

  @Query("select s from StorageSpace s JOIN  Address a ON s.address = a "
      + "WHERE a.latitude IS NOT NULL AND a.longitude IS NOT NULL  and s.host!=:user "
      + "AND (6371 * acos(cos(radians(:latitude)) * cos(radians(a.latitude)) * "
      + "cos(radians(a.longitude) - radians(:longitude)) + "
      + "sin(radians(:latitude)) * sin(radians(a.latitude)))) <= :radius AND s.status = 'ACTIVE'")
  Optional<Set<StorageSpace>> findNearbyStorageSpaces(BigDecimal latitude, BigDecimal longitude,
      BigDecimal radius, @Param("user") User user);

  @Override
  @Cacheable(value = "space", key = "#id")
  Optional<StorageSpace> findById(Long id);

  @Override
  @CachePut(value = "space", key = "#space.id")
  <S extends StorageSpace> S save(S space);

  @Query("select s from  StorageSpace s order by s.createdAt desc limit 4")
  Set<StorageSpace> getFeaturedSpaces();
}
