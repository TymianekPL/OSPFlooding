package org.tymi.ospflooding.backend.records;

import org.tymi.ospflooding.backend.utilities.Algorithm;
import org.tymi.ospflooding.backend.utilities.math.Coordinate;
import org.tymi.ospflooding.backend.utilities.math.ITreeNode;

public record Node(int id, double lat, double lon) implements ITreeNode {
     @Override
     public double GetX() {
          return lat;
     }

     @Override
     public double GetY() {
          return lon;
     }

     @Override
     public Coordinate ToCoordinate() {
          return Algorithm.LatLonToXY(lat, lon);
     }
}
