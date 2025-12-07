package org.tymi.ospflooding.backend;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FloodCheckerTest {
     record Node(double lat, double lon) {}

     static class FloodChecker {
          static final int LAT_TOL = 1;
          static final int LON_TOL = 1;

          private int toIntCoord(double val) {
               return (int) Math.round(val * 1000);
          }

          private long pack(int lat, int lon) {
               return (((long) lat) << 32) | (lon & 0xFFFFFFFFL);
          }

          public boolean isFlooded(Set<Long> floodedPoints, Node n) {
               int latInt = toIntCoord(n.lat());
               int lonInt = toIntCoord(n.lon());

               for (int dy = -LAT_TOL; dy <= LAT_TOL; dy++) {
                    int y = latInt + dy;

                    for (int dx = -LON_TOL; dx <= LON_TOL; dx++) {
                         long k = pack(y, lonInt + dx);
                         if (floodedPoints.contains(k)) return true;
                    }
               }

               return false;
          }
     }

     @Test
     void testIsFlooded() {
          FloodChecker checker = new FloodChecker();
          Set<Long> flooded = new HashSet<>();

          Node n1 = new Node(1.0, 2.0);
          Node n2 = new Node(5.0, 5.0);

          int latInt = (int)(1.0 * 1000);
          int lonInt = (int)(2.0 * 1000);
          long floodedPoint = ((long)latInt << 32) | (lonInt & 0xFFFFFFFFL);
          flooded.add(floodedPoint);

          assertTrue(checker.isFlooded(flooded, n1));

          assertFalse(checker.isFlooded(flooded, n2));

          long nearbyPoint = ((long)(latInt + 1) << 32) | ((lonInt - 1) & 0xFFFFFFFFL);
          flooded.add(nearbyPoint);
          assertTrue(checker.isFlooded(flooded, n1));
     }
}
