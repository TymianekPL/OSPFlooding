package org.tymi.ospflooding.backend;

import org.junit.jupiter.api.Test;
import org.json.JSONArray;
import org.tymi.ospflooding.backend.utilities.Algorithm;
import org.tymi.ospflooding.backend.utilities.math.Coordinate;

import static org.junit.jupiter.api.Assertions.*;

public class AlgorithmTest {
     @Test
     void testHaversine() {
          // two identical points should be zero metres apart
          double d0 = Algorithm.Haversine(0, 0, 0, 0);
          assertEquals(0.0, d0, 1e-6);

          // London (51.5074° N, 0.1278° W) <=>> Paris (48.8566° N, 2.3522° E)
          double londonLat = 51.5074, londonLon = -0.1278;
          double parisLat = 48.8566, parisLon = 2.3522;
          double distance = Algorithm.Haversine(londonLat, londonLon, parisLat, parisLon);

          // that's about 343 km (+/- 1km)
          assertEquals(343000, distance, 1000);
     }

     @Test
     void testLatLonToXY() {
          // Equator point
          Coordinate xy0 = Algorithm.LatLonToXY(0, 0);
          assertEquals(0.0, xy0.x(), 1e-6);
          assertEquals(0.0, xy0.y(), 1e-6);

          // Check that Y increases with latitude
          Coordinate xy1 = Algorithm.LatLonToXY(1, 0);
          assertTrue(xy1.y() > xy0.y());

          // Check that X increases with longitude at equator
          Coordinate xy2 = Algorithm.LatLonToXY(0, 1);
          assertTrue(xy2.x() > xy0.x());
     }

     @Test
     void testSwapped() {
          JSONArray arr = new JSONArray();
          arr.put(10.0);
          arr.put(20.0);

          double[] swapped = Algorithm.Swapped(arr);
          assertEquals(20.0, swapped[0], 1e-6);
          assertEquals(10.0, swapped[1], 1e-6);
     }
}
