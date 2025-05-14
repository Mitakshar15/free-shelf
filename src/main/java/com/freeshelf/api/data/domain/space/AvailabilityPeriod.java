package com.freeshelf.api.data.domain.space;

import com.freeshelf.api.data.domain.BaseEntity;
import com.freeshelf.api.data.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "availability_periods",
    indexes = {@Index(name = "idx_avail_space", columnList = "space_id"),
        @Index(name = "idx_avail_dates", columnList = "start_date,end_date")})
public class AvailabilityPeriod extends BaseEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "availability_period_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @ToString.Exclude
  private StorageSpace space;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @Column(name = "start_date", nullable = false)
  private OffsetDateTime startDate;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  @Column(name = "end_date", nullable = false)
  private OffsetDateTime endDate;



  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    AvailabilityPeriod availabilityPeriod = (AvailabilityPeriod) o;
    return getId() != null && getId().equals(availabilityPeriod.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }

}
