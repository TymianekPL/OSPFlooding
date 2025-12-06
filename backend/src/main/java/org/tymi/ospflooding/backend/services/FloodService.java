package org.tymi.ospflooding.backend.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tymi.ospflooding.backend.models.FloodCache;
import org.tymi.ospflooding.backend.models.FloodPoint;
import org.tymi.ospflooding.backend.repositories.FloodCacheRepository;
import org.tymi.ospflooding.backend.repositories.FloodPointRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@Service
public class FloodService {
     private final FloodCacheRepository floodCacheRepository;
     private final FloodPointRepository floodPointRepository;
     private final HttpClient httpClient;
     private final Map<String, Set<Long>> floodedPointsCache = new ConcurrentHashMap<>();

     public FloodService(FloodCacheRepository floodCacheRepository,
                         FloodPointRepository floodPointRepository) {
          this.floodCacheRepository = floodCacheRepository;
          this.floodPointRepository = floodPointRepository;
          this.httpClient = HttpClient.newBuilder()
                  .connectTimeout(java.time.Duration.ofSeconds(30))
                  .build();
     }

     @Transactional(readOnly = true)
     public JSONObject getFloodedPoints(double south, double west, double north, double east,
                                        String startDate, String endDate) {
          String cacheKey = FloodService.createCacheKey(south, west, north, east, startDate, endDate);
          Optional<FloodCache> cacheOpt = floodCacheRepository.findByCacheKey(cacheKey);
          if (cacheOpt.isEmpty()) return createEmptyGeoJson();

          FloodCache cache = cacheOpt.get();
          List<FloodPoint> points = floodPointRepository.findByFloodCacheId(cache.getId());

          if (points.isEmpty()) return createEmptyGeoJson();

          JSONArray features = getPointObjects(points);

          JSONObject geoJson = new JSONObject();
          geoJson.put("type", "FeatureCollection");
          geoJson.put("features", features);

          return geoJson;
     }

     private static JSONArray getPointObjects(List<FloodPoint> points) {
          JSONArray features = new JSONArray();
          for (FloodPoint point : points) {
               JSONObject feature = new JSONObject();
               feature.put("type", "Feature");

               JSONObject geometry = new JSONObject();
               geometry.put("type", "Point");

               JSONArray coordinates = new JSONArray();
               coordinates.put(point.getLongitude());
               coordinates.put(point.getLatitude());
               geometry.put("coordinates", coordinates);

               feature.put("geometry", geometry);

               JSONObject properties = new JSONObject();
               properties.put("flooded", true);
               feature.put("properties", properties);

               features.put(feature);
          }
          return features;
     }

     private JSONObject createPolygonFromCluster(List<int[]> cluster,
                                                 double maxLat,
                                                 double minLon,
                                                 double cellSize) {
          int minR = Integer.MAX_VALUE, maxR = Integer.MIN_VALUE;
          int minC = Integer.MAX_VALUE, maxC = Integer.MIN_VALUE;

          for (int[] cell : cluster) {
               minR = Math.min(minR, cell[0]);
               maxR = Math.max(maxR, cell[0]);
               minC = Math.min(minC, cell[1]);
               maxC = Math.max(maxC, cell[1]);
          }

          double buffer = cellSize * 0.1;
          double south = maxLat - (maxR + 1) * cellSize - buffer;
          double north = maxLat - minR * cellSize + buffer;
          double west = minLon + minC * cellSize - buffer;
          double east = minLon + (maxC + 1) * cellSize + buffer;

          JSONArray polygonCoords = new JSONArray();
          JSONArray exteriorRing = new JSONArray();

          exteriorRing.put(new JSONArray().put(west).put(north));  // NW
          exteriorRing.put(new JSONArray().put(east).put(north));  // NE
          exteriorRing.put(new JSONArray().put(east).put(south));  // SE
          exteriorRing.put(new JSONArray().put(west).put(south));  // SW
          exteriorRing.put(new JSONArray().put(west).put(north));  // Close ring

          polygonCoords.put(exteriorRing);

          JSONObject geometry = new JSONObject();
          geometry.put("type", "Polygon");
          geometry.put("coordinates", polygonCoords);

          JSONObject feature = new JSONObject();
          feature.put("type", "Feature");
          feature.put("geometry", geometry);

          JSONObject properties = new JSONObject();
          properties.put("flooded", true);
          properties.put("pointCount", cluster.size());
          feature.put("properties", properties);

          return feature;
     }

     /**
      * Create empty GeoJSON FeatureCollection
      */
     private JSONObject createEmptyGeoJson() {
          JSONObject geoJson = new JSONObject();
          geoJson.put("type", "FeatureCollection");
          geoJson.put("features", new JSONArray());
          return geoJson;
     }


     private JSONObject createPolygonsFromPoints(List<FloodPoint> points, double resolution) {
          if (points.isEmpty()) {
               return createEmptyGeoJson();
          }

          // bounds
          double minLat = points.getFirst().getLatitude();
          double maxLat = points.getFirst().getLatitude();
          double minLon = points.getFirst().getLongitude();
          double maxLon = points.getFirst().getLongitude();

          for (FloodPoint point : points) {
               minLat = Math.min(minLat, point.getLatitude());
               maxLat = Math.max(maxLat, point.getLatitude());
               minLon = Math.min(minLon, point.getLongitude());
               maxLon = Math.max(maxLon, point.getLongitude());
          }

          double cellSize = resolution * 3; // Larger cells for polygons
          int cols = (int) Math.ceil((maxLon - minLon) / cellSize);
          int rows = (int) Math.ceil((maxLat - minLat) / cellSize);

          boolean[][] grid = new boolean[rows][cols];

          for (FloodPoint point : points) {
               int col = (int) ((point.getLongitude() - minLon) / cellSize);
               int row = (int) ((maxLat - point.getLatitude()) / cellSize); // Inverted for array indexing

               if (col >= 0 && col < cols && row >= 0 && row < rows) {
                    grid[row][col] = true;
               }
          }

          JSONArray features = new JSONArray();
          boolean[][] visited = new boolean[rows][cols];

          for (int r = 0; r < rows; r++) {
               for (int c = 0; c < cols; c++) {
                    if (grid[r][c] && !visited[r][c]) {
                         List<int[]> cluster = new ArrayList<>();
                         Queue<int[]> queue = new LinkedList<>();
                         queue.add(new int[]{r, c});
                         visited[r][c] = true;

                         while (!queue.isEmpty()) {
                              int[] cell = queue.poll();
                              cluster.add(cell);

                              int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                              for (int[] dir : directions) {
                                   int nr = cell[0] + dir[0];
                                   int nc = cell[1] + dir[1];
                                   if (nr >= 0 && nr < rows && nc >= 0 && nc < cols &&
                                           grid[nr][nc] && !visited[nr][nc]) {
                                        visited[nr][nc] = true;
                                        queue.add(new int[]{nr, nc});
                                   }
                              }
                         }

                         if (!cluster.isEmpty()) {
                              JSONObject polygon = createPolygonFromCluster(cluster, maxLat, minLon, cellSize);
                              features.put(polygon);
                         }
                    }
               }
          }

          JSONObject geoJson = new JSONObject();
          geoJson.put("type", "FeatureCollection");
          geoJson.put("features", features);

          return geoJson;
     }

     @Transactional(readOnly = true)
     public JSONObject getFloodedPolygons(double south, double west, double north, double east,
                                          String startDate, String endDate) {

          Optional<FloodCache> cacheOpt = floodCacheRepository.findByBoundingBoxAndDateRange(
                  south, west, north, east, startDate, endDate);

          if (cacheOpt.isEmpty()) {
               return createEmptyGeoJson();
          }

          FloodCache cache = cacheOpt.get();
          List<FloodPoint> points = floodPointRepository.findByFloodCacheId(cache.getId());

          if (points.isEmpty()) {
               return createEmptyGeoJson();
          }

          return createPolygonsFromPoints(points, cache.getResolutionDegrees());
     }

     @Transactional
     public void downloadAndParseFloodZones(double south, double west, double north, double east,
                                            String startDate, String endDate) throws IOException, InterruptedException {
          LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
          Optional<FloodCache> freshCache = floodCacheRepository.findBySouthAndWestAndNorthAndEastAndLastUpdatedAfter(
                  south, west, north, east, twentyFourHoursAgo);

          // TODO: Logging
          if (freshCache.isPresent()) {
               System.out.println("Using cached flood data (last updated: " +
                       freshCache.get().getLastUpdated() + ")");
               loadFromCache(freshCache.get());
               return;
          }

          // stale cache (>24h)
          Optional<FloodCache> staleCache = floodCacheRepository.findByBoundingBoxAndDateRange(
                  south, west, north, east, startDate, endDate);

          if (staleCache.isPresent()) {
               // TODO: Logging
               System.out.println("Cache is stale (>24h), updating flood data...");
               floodPointRepository.deleteByFloodCacheId(staleCache.get().getId());
               floodCacheRepository.delete(staleCache.get());
          } else {
               // TODO: Logging
               System.out.println("No cache found, downloading new flood data...");
          }

          Set<FloodPoint> floodPoints = downloadAndProcessFloodData(south, west, north, east, startDate, endDate);

          String cacheKey = createCacheKey(south, west, north, east, startDate, endDate);
          Set<Long> pointKeys = new HashSet<>();
          for (FloodPoint point : floodPoints) {
               pointKeys.add(point.getKey());
          }
          floodedPointsCache.put(cacheKey, pointKeys);
     }

     private Set<FloodPoint> downloadAndProcessFloodData(double south, double west, double north, double east,
                                                         String startDate, String endDate) throws IOException, InterruptedException {
          String url = String.format(Locale.ROOT,
                  "https://services.sentinel-hub.com/ogc/wcs/79251ab1-0e04-4e55-a9ee-96e4a422ab01" +
                          "?SERVICE=WCS&VERSION=1.0.0&REQUEST=GetCoverage&COVERAGE=FLOODLAYER&FORMAT=image/png" +
                          "&BBOX=%f,%f,%f,%f&CRS=EPSG:4326&TIME=%s/%s&RESX=0.00005&RESY=0.00005",
                  west, south, east, north, "2025-12-04", "2025-12-05");

          System.out.println("Downloading flood data from: " + url);

          HttpRequest request = HttpRequest.newBuilder()
                  .uri(URI.create(url))
                  .header("Accept", "image/png")
                  .GET()
                  .build();

          HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

          if (response.statusCode() != 200) {
               // TODO: Logging
               throw new IOException("Failed to download flood data. Status: " + response.statusCode() +
                       ", Body: " + new String(response.body()));
          }

          BufferedImage image;
          try {
               image = ImageIO.read(new ByteArrayInputStream(response.body()));
          } catch (Exception e) {
               String responseBody = new String(response.body());
               if (responseBody.contains("Error") || responseBody.contains("Exception")) {
                    throw new IOException("Sentinel Hub returned error: " + responseBody);
               }
               throw new IOException("Failed to read flood image. Response might not be a valid PNG.");
          }

          if (image == null) {
               throw new IOException("Failed to read flood image. ImageIO.read returned null.");
          }

          int width = image.getWidth();
          int height = image.getHeight();

          double latStep = (north - south) / height;
          double lonStep = (east - west) / width;

          Set<FloodPoint> floodPoints = Collections.synchronizedSet(new HashSet<>());
          final int sampleRate = 2;

          FloodCache floodCache = new FloodCache(south, west, north, east, startDate, endDate, 0.00005);
          floodCache.initCacheKey();
          floodCache.setPointCount(floodPoints.size());
          floodCache.setLastUpdated(LocalDateTime.now());
          floodCache = floodCacheRepository.save(floodCache);

          FloodCache finalFloodCache = floodCache;
          IntStream.range(0, height).parallel().forEach(y -> {
               if (y % sampleRate != 0) return;
               double lat = north - y * latStep;
               for (int x = 0; x < width; x += sampleRate) {
                    int rgb = image.getRGB(x, y) & 0xFFFFFF;
                    if (isFlooded(rgb)) {
                         double lon = west + x * lonStep;
                         double roundedLat = Math.round(lat * 1_000_000.0) / 1_000_000.0;
                         double roundedLon = Math.round(lon * 1_000_000.0) / 1_000_000.0;
                         floodPoints.add(new FloodPoint(roundedLat, roundedLon, finalFloodCache));
                    }
               }
          });
          floodPointRepository.saveAll(floodPoints);

          return floodPoints;
     }

     @Transactional(readOnly = true)
     protected void loadFromCache(FloodCache floodCache) {
          List<FloodPoint> points = floodPointRepository.findByFloodCacheId(floodCache.getId());

          Set<Long> pointKeys = new HashSet<>();
          for (FloodPoint point : points) {
               pointKeys.add(point.getKey());
          }

          String cacheKey = floodCache.getCacheKey();
          floodedPointsCache.put(cacheKey, pointKeys);

          // TODO: Logging
          System.out.println("Loaded " + points.size() + " flood points from database cache");
     }

     private boolean isFlooded(int rgb) {
          int r = (rgb >> 16) & 0xFF;
          int g = (rgb >> 8) & 0xFF;
          int b = rgb & 0xFF;
          return b > 128 && r < 100 && g < 100;
     }

     public static String createCacheKey(double south, double west, double north, double east,
                                         String startDate, String endDate) {
          return FloodCache.buildCacheKey(south, west, north, east, startDate, endDate);
     }

     @Transactional(readOnly = true)
     public boolean isFloodedDirect(double lat, double lon, double south, double west,
                                    double north, double east, String startDate, String endDate) {
          Optional<FloodCache> cache = floodCacheRepository.findByBoundingBoxAndDateRange(
                  south, west, north, east, startDate, endDate);

          return cache.filter(floodCache -> floodPointRepository.existsInCache(floodCache.getId(), lat, lon)).isPresent();
     }

     @Transactional
     public int cleanupOldCache(int daysToKeep) {
          LocalDateTime cutoff = LocalDateTime.now().minusDays(daysToKeep);

          List<FloodCache> oldCaches = floodCacheRepository.findAll().stream()
                  .filter(cache -> cache.getLastUpdated().isBefore(cutoff))
                  .toList();

          int count = oldCaches.size();
          for (FloodCache cache : oldCaches) {
               floodPointRepository.deleteByFloodCacheId(cache.getId());
               floodCacheRepository.delete(cache);
          }

          // TODO: Logging
          System.out.println("Cleaned up " + count + " old cache entries");
          return count;
     }
     public static int toIntCoord(double value) {
          return (int) Math.round(value * 1_000_000.0);
     }

     public static long pack(int latInt, int lonInt) {
          return (((long) latInt) << 32) | (lonInt & 0xFFFFFFFFL);
     }

     public Set<Long> loadFloodedPoints(double south, double west, double north, double east) {
          // last-24h date range
          LocalDateTime end = LocalDateTime.now();
          LocalDateTime start = end.minusHours(24);
          String startDate = start.toLocalDate().toString();
          String endDate = end.toLocalDate().toString();

          String cacheKey = FloodCache.buildCacheKey(south, west, north, east, startDate, endDate);

          Optional<FloodCache> cachedOpt = floodCacheRepository.findByCacheKey(cacheKey);
          if (cachedOpt.isPresent()) {
               FloodCache cached = cachedOpt.get();
               Set<Long> keys = new HashSet<>(cached.getPointCount() == 0 ? 1024 : cached.getPointCount());
               for (FloodPoint p : cached.getFloodPoints()) {
                    keys.add(pack(toIntCoord(p.getLatitude()), toIntCoord(p.getLongitude())));
               }
               return keys;
          }

          Optional<FloodCache> maybe = floodCacheRepository.findAll().stream()
                  .filter(fc -> Math.abs(fc.getSouth() - south) < 0.01
                          && Math.abs(fc.getWest() - west) < 0.01
                          && Math.abs(fc.getNorth() - north) < 0.01
                          && Math.abs(fc.getEast() - east) < 0.01
                          && (fc.getLastUpdated() != null && !fc.isStale()))
                  .findFirst();

          if (maybe.isPresent()) {
               FloodCache cached = maybe.get();
               Set<Long> keys = new HashSet<>(cached.getPointCount() == 0 ? 1024 : cached.getPointCount());
               for (FloodPoint p : cached.getFloodPoints()) keys.add(pack(toIntCoord(p.getLatitude()), toIntCoord(p.getLongitude())));

               floodedPointsCache.put(cached.getCacheKey(), keys);
               return keys;
          }

          FloodCache newCache = new FloodCache(south, west, north, east, startDate, endDate, 0.001);
          newCache.initCacheKey();
          newCache.setLastUpdated(LocalDateTime.now());
          floodCacheRepository.save(newCache);

          return Collections.emptySet();
     }
}
