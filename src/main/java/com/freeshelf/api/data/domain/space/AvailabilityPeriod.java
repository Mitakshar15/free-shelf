package com.freeshelf.api.data.domain.space;

import com.freeshelf.api.data.domain.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "availability_periods",
    indexes = {@Index(name = "idx_avail_space", columnList = "space_id"),
        @Index(name = "idx_avail_dates", columnList = "start_date,end_date")})
public class AvailabilityPeriod extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "availability_period_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  private StorageSpace space;

  @Column(name = "start_date", nullable = false)
  private LocalDateTime startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDateTime endDate;


}
