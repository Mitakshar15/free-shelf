package com.freeshelf.api.data.repository;

import com.freeshelf.api.data.domain.space.AvailabilityPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailabilityPeriodRepository extends JpaRepository<AvailabilityPeriod, Long> {

}
