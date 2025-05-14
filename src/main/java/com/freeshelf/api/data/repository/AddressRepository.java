package com.freeshelf.api.data.repository;

import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.domain.user.Address;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
  @Override
  @Cacheable(value = "address", key = "#id")
  Optional<Address> findById(Long id);

  @Override
  @CachePut(value = "address", key = "#address")
  <S extends Address> S save(S address);
}
