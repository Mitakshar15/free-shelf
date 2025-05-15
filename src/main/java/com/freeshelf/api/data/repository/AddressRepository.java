package com.freeshelf.api.data.repository;

import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.domain.user.Address;
import com.freeshelf.api.data.domain.user.UserProfile;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface AddressRepository extends JpaRepository<Address, Long> {




    @Query("select ad from  Address ad where ad.userProfile=:userProfile")
    Set<@Valid Address> findAllByUserProfile(@Param("userProfile") UserProfile userProfile);

    @Override
    @CacheEvict(value = "address", key = "#address.id")
    void delete(Address address);

}
