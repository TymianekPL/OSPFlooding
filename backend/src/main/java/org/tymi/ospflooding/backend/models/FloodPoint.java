package org.tymi.ospflooding.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.tymi.ospflooding.backend.services.FloodService;

@Setter
@Getter
@Entity
@Table(name = "flood_point",
        indexes = {
                @Index(name = "idx_flood_cache_id", columnList = "flood_cache_id"),
                @Index(name = "idx_coordinates", columnList = "latitude,longitude")
        })
public class FloodPoint {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @Column(name = "latitude", nullable = false)
     private double latitude;

     @Column(name = "longitude", nullable = false)
     private double longitude;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "flood_cache_id", nullable = false)
     private FloodCache floodCache;

     public FloodPoint() {}

     public FloodPoint(double latitude, double longitude, FloodCache floodCache) {
          this.latitude = latitude;
          this.longitude = longitude;
          this.floodCache = floodCache;
     }

     public Long getKey() {
          return FloodService.pack(FloodService.toIntCoord(latitude), FloodService.toIntCoord(longitude));
     }
}