package org.tymi.ospflooding.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "flood_cache")
public class FloodCache {
     @Setter
     @Getter
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @Setter
     @Getter
     @Column(name = "south", nullable = false)
     private double south;

     @Setter
     @Getter
     @Column(name = "west", nullable = false)
     private double west;

     @Setter
     @Getter
     @Column(name = "north", nullable = false)
     private double north;

     @Setter
     @Getter
     @Column(name = "east", nullable = false)
     private double east;

     @Setter
     @Getter
     @Column(name = "start_date", nullable = false, length = 20)
     private String startDate;

     @Setter
     @Getter
     @Column(name = "end_date", nullable = false, length = 20)
     private String endDate;

     @Setter
     @Getter
     @Column(name = "last_updated", nullable = false)
     private LocalDateTime lastUpdated;

     @Setter
     @Getter
     @Column(name = "point_count", nullable = false)
     private int pointCount;

     @Setter
     @Getter
     @Column(name = "resolution_degrees", nullable = false)
     private double resolutionDegrees = 0.00005;

     @Setter
     @Getter
     @OneToMany(mappedBy = "floodCache", cascade = CascadeType.ALL, orphanRemoval = true)
     private List<FloodPoint> floodPoints = new ArrayList<>();
     @Column(unique = true, nullable = false)
     private String cacheKey;

     public static String buildCacheKey(double south, double west, double north, double east,
                                        String startDate, String endDate) {
          String bboxKey = String.format(Locale.ROOT, "%.2f,%.2f,%.2f,%.2f", south, west, north, east);
          String dateRangeKey = startDate + "/" + endDate;
          return bboxKey + "|" + dateRangeKey;
     }

     public void initCacheKey() {
          this.cacheKey = buildCacheKey(south, west, north, east, startDate, endDate);
     }

     public FloodCache() {
          this.lastUpdated = LocalDateTime.now();
     }

     public FloodCache(double south, double west, double north, double east,
                       String startDate, String endDate, double resolutionDegrees) {
          this.south = south;
          this.west = west;
          this.north = north;
          this.east = east;
          this.startDate = startDate;
          this.endDate = endDate;
          this.resolutionDegrees = resolutionDegrees;
          this.lastUpdated = LocalDateTime.now();
     }

     public boolean isStale() {
          return lastUpdated.isBefore(LocalDateTime.now().minusHours(24));
     }

     public String getBboxKey() {
          return String.format(Locale.ROOT, "%.2f,%.2f,%.2f,%.2f", south, west, north, east);
     }

     public String getDateRangeKey() {
          return startDate + "/" + endDate;
     }

     public String getCacheKey() {
          return getBboxKey() + "|" + getDateRangeKey();
     }
}
