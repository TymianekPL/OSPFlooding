package org.tymi.ospflooding.backend.utilities;

import org.tymi.ospflooding.backend.utilities.math.Coordinate;
import org.tymi.ospflooding.backend.utilities.math.ITreeNodeConverter;

import static org.tymi.ospflooding.backend.utilities.Algorithm.LatLonToXY;

public class NodeConverter implements ITreeNodeConverter {
     @Override
     public Coordinate ToCoordinate(double x, double y) {
          return LatLonToXY(x, y);
     }
}
