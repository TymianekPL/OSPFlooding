package org.tymi.ospflooding.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tymi.ospflooding.backend.models.FloodPoint;
import java.util.List;

public interface FloodPointRepository extends JpaRepository<FloodPoint, Long> {

     @Query("SELECT fp FROM FloodPoint fp WHERE fp.floodCache.id = :cacheId")
     List<FloodPoint> findByFloodCacheId(@Param("cacheId") Long cacheId);

     @Query("SELECT CASE WHEN COUNT(fp) > 0 THEN true ELSE false END " +
             "FROM FloodPoint fp " +
             "WHERE fp.floodCache.id = :cacheId " +
             "AND ABS(fp.latitude - :latitude) < 0.0001 " +
             "AND ABS(fp.longitude - :longitude) < 0.0001")
     boolean existsInCache(@Param("cacheId") Long cacheId,
                           @Param("latitude") double latitude,
                           @Param("longitude") double longitude);

     void deleteByFloodCacheId(Long cacheId);
}
