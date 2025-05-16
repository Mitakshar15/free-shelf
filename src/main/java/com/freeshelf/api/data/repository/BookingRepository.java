package com.freeshelf.api.data.repository;

import com.freeshelf.api.data.domain.booking.Booking;
import com.freeshelf.api.data.domain.space.StorageSpace;
import com.freeshelf.api.data.domain.user.User;
import com.freeshelf.api.utils.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookingRepository extends JpaRepository<Booking, Long> {

  /**
   * Find bookings for a specific space that overlap with the given date range and are not in the
   * specified statuses
   *
   * @param spaceId The ID of the storage space
   * @param excludedStatus1 First status to exclude from results
   * @param excludedStatus2 Second status to exclude from results
   * @param startDate Start date of the requested booking period
   * @param endDate End date of the requested booking period
   * @return List of overlapping bookings
   */
  @Query("SELECT b FROM Booking b WHERE b.space.id = :spaceId AND b.status NOT IN (:excludedStatus1, :excludedStatus2) AND "
      + "((b.startDate <= :endDate) AND (b.endDate >= :startDate))")
  List<Booking> findOverlappingBookings(@Param("spaceId") Long spaceId,
      @Param("excludedStatus1") BookingStatus excludedStatus1,
      @Param("excludedStatus2") BookingStatus excludedStatus2,
      @Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);


  @Query("select  b from Booking  b where b.space=:space and b.space.host=:host")
  Optional<Set<Booking>> findAllBySpace(@NotNull @Param("space") StorageSpace space,
      @Param("host") User host);
}
