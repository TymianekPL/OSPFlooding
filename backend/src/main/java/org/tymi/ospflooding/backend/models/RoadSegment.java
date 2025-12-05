package org.tymi.ospflooding.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;

@Entity
@Getter
@Setter
public class RoadSegment {
     @Id
     private Integer id;
     private Double length;
     @Column(columnDefinition = "TEXT")
     private String coordinates;


     @Transient
     private double startLat;
     @Transient
     private double startLon;
     @Transient
     private double endLat;
     @Transient

     private double endLon;
     public void parseCoordinates() {
          JSONArray arr = new JSONArray(coordinates);
          JSONArray start = arr.getJSONArray(0);
          JSONArray end = arr.getJSONArray(arr.length() - 1);

          this.startLon = start.getDouble(0);
          this.startLat = start.getDouble(1);
          this.endLon = end.getDouble(0);
          this.endLat = end.getDouble(1);
     }
}
