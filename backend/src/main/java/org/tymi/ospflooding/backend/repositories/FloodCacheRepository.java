package org.tymi.ospflooding.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tymi.ospflooding.backend.models.FloodCache;
import java.time.LocalDateTime;
import java.util.Optional;

public interface FloodCacheRepository extends JpaRepository<FloodCache, Long> {

     @Query("SELECT DISTINCT fc FROM FloodCache fc " +
             "WHERE ABS(fc.south - :south) < 0.0001 " +
             "AND ABS(fc.west - :west) < 0.0001 " +
             "AND ABS(fc.north - :north) < 0.0001 " +
             "AND ABS(fc.east - :east) < 0.0001 " +
             "AND fc.startDate = :startDate " +
             "AND fc.endDate = :endDate")
     Optional<FloodCache> findByBoundingBoxAndDateRange(
             @Param("south") double south,
             @Param("west") double west,
             @Param("north") double north,
             @Param("east") double east,
             @Param("startDate") String startDate,
             @Param("endDate") String endDate);

     Optional<FloodCache> findByCacheKey(String cacheKey);

     Optional<FloodCache> findBySouthAndWestAndNorthAndEastAndLastUpdatedAfter(double south, double west, double north, double east, LocalDateTime cutoff);
}
