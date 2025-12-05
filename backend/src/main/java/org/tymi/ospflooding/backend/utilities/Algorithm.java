package org.tymi.ospflooding.backend.utilities;

import org.json.JSONArray;

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
}
