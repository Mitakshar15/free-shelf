package com.freeshelf.api.data.repository;

import com.freeshelf.api.data.domain.user.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
