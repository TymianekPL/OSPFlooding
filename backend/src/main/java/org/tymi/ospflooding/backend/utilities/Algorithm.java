package org.tymi.ospflooding.backend.utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public final class Algorithm {
     public static final double EarthRadiusMetres = 6371000;

     public static double Haversine(double latitude1, double longitude1, double latitude2, double longitude2) {
          double deltaLatitude = Math.toRadians(latitude2 - latitude1);
          double deltaLongitude = Math.toRadians(longitude2 - longitude1);

          double squareOfHalfChordLength = Math.sin(deltaLatitude / 2) * Math.sin(deltaLatitude / 2)
                  + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2))
                  * Math.sin(deltaLongitude / 2) * Math.sin(deltaLongitude / 2);

          double centralAngleRad = 2 * Math.atan2(Math.sqrt(squareOfHalfChordLength), Math.sqrt(1 - squareOfHalfChordLength));
          return EarthRadiusMetres * centralAngleRad;
     }

     public static double[] LatLonToXY(double lat, double lon) {
          double x = Math.toRadians(lon) * EarthRadiusMetres * Math.cos(Math.toRadians(lat));
          double y = Math.toRadians(lat) * EarthRadiusMetres;
          return new double[]{x, y};
     }

     public static double[] Swapped(JSONArray point) {
          return new double[]{point.getDouble(1), point.getDouble(0)};
     }

     // Graham scan for convex hull
     public static List<Point2D.Double> computeConvexHull(List<Point2D.Double> points) {
          if (points.size() <= 3) return new ArrayList<>(points);

          points.sort(Comparator.comparingDouble((Point2D.Double p) -> p.x)
                  .thenComparingDouble(p -> p.y));

          Stack<Point2D.Double> lower = new Stack<>();
          for (Point2D.Double p : points) {
               while (lower.size() >= 2 &&
                       cross(lower.get(lower.size()-2), lower.peek(), p) <= 0) {
                    lower.pop();
               }
               lower.push(p);
          }

          Stack<Point2D.Double> upper = new Stack<>();
          for (int i = points.size() - 1; i >= 0; i--) {
               Point2D.Double p = points.get(i);
               while (upper.size() >= 2 &&
                       cross(upper.get(upper.size()-2), upper.peek(), p) <= 0) {
                    upper.pop();
               }
               upper.push(p);
          }

          lower.pop();
          upper.pop();
          lower.addAll(upper);
          return new ArrayList<>(lower);
     }

     public static double cross(Point2D.Double o, Point2D.Double a, Point2D.Double b) {
          return (a.x - o.x) * (b.y - o.y) - (a.y - o.y) * (b.x - o.x);
     }
}
