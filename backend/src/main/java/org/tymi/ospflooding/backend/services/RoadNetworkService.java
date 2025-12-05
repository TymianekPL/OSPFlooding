package org.tymi.ospflooding.backend.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.tymi.ospflooding.backend.models.RoadSegment;
import org.tymi.ospflooding.backend.repositories.RoadSegmentRepository;
import org.tymi.ospflooding.backend.utilities.NodeDistance;
import org.tymi.ospflooding.backend.utilities.PathRecord;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.tymi.ospflooding.backend.utilities.Algorithm.Haversine;
import static org.tymi.ospflooding.backend.utilities.Algorithm.LatLonToXY;
import static org.tymi.ospflooding.backend.utilities.Algorithm.Swapped;

record Node(int id, double lat, double lon) {
}

/**
 * @param length        metres
 * @param originalWayId helpful for debugging
 * @param coordinates   [[lat,lon],[lat,lon]]
 */
record Edge(int id, int fromNode, int toNode, double length, int originalWayId, JSONArray coordinates) {
}

@Service
public class RoadNetworkService {
     private final RoadSegmentRepository roadRepo;

     private final AtomicInteger edgeIdCounter = new AtomicInteger(1);

     private final Map<Integer, Node> nodes = new HashMap<>();
     private final Map<Integer, Edge> edges = new HashMap<>();
     private final Map<Integer, List<Integer>> adjacency = new HashMap<>();

     public RoadNetworkService(RoadSegmentRepository roadRepo) {
          this.roadRepo = roadRepo;
     }

     public void loadOSMRoads(double south, double west, double north, double east) throws UncheckedIOException, InterruptedException, IOException {
          if (roadRepo.count() > 0) {
               edges.clear();
               nodes.clear();
               adjacency.clear();
               edgeIdCounter.set(1);

               for (RoadSegment seg : roadRepo.findAll()) {
                    seg.parseCoordinates();
                    JSONArray coords = new JSONArray(seg.getCoordinates());
                    if (coords.length() < 2) continue;
                    double lat1 = coords.getJSONArray(0).getDouble(0);
                    double lon1 = coords.getJSONArray(0).getDouble(1);
                    double lat2 = coords.getJSONArray(1).getDouble(0);
                    double lon2 = coords.getJSONArray(1).getDouble(1);

                    int n1 = nodeIdFor(lat1, lon1);
                    int n2 = nodeIdFor(lat2, lon2);

                    Node node1 = nodes.computeIfAbsent(n1, k -> new Node(n1, lat1, lon1));
                    Node node2 = nodes.computeIfAbsent(n2, k -> new Node(n2, lat2, lon2));

                    int id = seg.getId();
                    double length = seg.getLength();
                    Edge e = new Edge(id, node1.id(), node2.id(), length, id, coords);
                    edges.put(id, e);
                    adjacency.computeIfAbsent(node1.id(), k -> new ArrayList<>()).add(id);

                    edgeIdCounter.updateAndGet(curr -> Math.max(curr, id + 1));
               }
               return;
          }

          String query = "[out:json];way[\"highway\"](" + south + "," + west + "," + north + "," + east + ");out geom;";
          String url = "https://overpass-api.de/api/interpreter?data=" + URLEncoder.encode(query, StandardCharsets.UTF_8);

          try (HttpClient client = HttpClient.newHttpClient()) {
               HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
               HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

               JSONArray elements = new JSONObject(response.body()).getJSONArray("elements");

               Map<String, Integer> coordinateHashToNodeId = new HashMap<>();

               for (int i = 0; i < elements.length(); i++) {
                    JSONObject elem = elements.getJSONObject(i);
                    if (!"way".equals(elem.getString("type"))) continue;
                    int originalWayId = (int) elem.getLong("id");
                    JSONArray geom = elem.getJSONArray("geometry");
                    if (geom.length() < 2) continue;

                    JSONObject previous = geom.getJSONObject(0);
                    for (int j = 1; j < geom.length(); j++) {
                         JSONObject current = geom.getJSONObject(j);
                         double lat1 = previous.getDouble("lat");
                         double lon1 = previous.getDouble("lon");
                         double lat2 = current.getDouble("lat");
                         double lon2 = current.getDouble("lon");

                         // Make nodes (reuse them later on)
                         int nodeA = coordinateHashToNodeId.computeIfAbsent(key(lat1, lon1), k -> nodeIdFor(lat1, lon1));
                         int nodeB = coordinateHashToNodeId.computeIfAbsent(key(lat2, lon2), k -> nodeIdFor(lat2, lon2));

                         nodes.computeIfAbsent(nodeA, k -> new Node(nodeA, lat1, lon1));
                         nodes.computeIfAbsent(nodeB, k -> new Node(nodeB, lat2, lon2));

                         double length = Haversine(lat1, lon1, lat2, lon2);

                         int edgeId = edgeIdCounter.getAndIncrement();

                         JSONArray coords = new JSONArray();
                         JSONArray p1 = new JSONArray();
                         p1.put(lat1);
                         p1.put(lon1);
                         JSONArray p2 = new JSONArray();
                         p2.put(lat2);
                         p2.put(lon2);
                         coords.put(p1);
                         coords.put(p2);

                         Edge e = new Edge(edgeId, nodeA, nodeB, length, originalWayId, coords);
                         edges.put(edgeId, e);
                         adjacency.computeIfAbsent(nodeA, k -> new ArrayList<>()).add(edgeId);

                         RoadSegment roadSegment = new RoadSegment();
                         roadSegment.setId(edgeId);
                         roadSegment.setLength(length);
                         roadSegment.setCoordinates(coords.toString());
                         roadSegment.parseCoordinates();
                         roadRepo.save(roadSegment);

                         previous = current;
                    }
               }
          }
     }

     public List<PathRecord> findShortestPaths(double startLat, double startLon,
                                               double endLat, double endLon) {
          if (nodes.isEmpty() || edges.isEmpty()) return Collections.emptyList();

          int startNode = findNearestNode(startLat, startLon);
          int endNode = findNearestNode(endLat, endLon);

          if (startNode == -1 || endNode == -1) return Collections.emptyList();

          Map<Integer, Double> dist = new HashMap<>();
          Map<Integer, Integer> prevEdge = new HashMap<>();
          Set<Integer> visited = new HashSet<>();
          PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));

          for (Integer nid : nodes.keySet()) dist.put(nid, Double.POSITIVE_INFINITY);
          dist.put(startNode, 0.0);
          pq.add(new NodeDistance(startNode, 0.0));

          while (!pq.isEmpty()) {
               NodeDistance nodeDistance = pq.poll();
               if (visited.contains(nodeDistance.node)) continue;
               visited.add(nodeDistance.node);

               if (nodeDistance.node == endNode) break;

               List<Integer> out = adjacency.getOrDefault(nodeDistance.node, List.of());
               for (int edgeId : out) {
                    Edge edge = edges.get(edgeId);
                    if (edge == null) continue;
                    int to = edge.toNode();
                    double alt = dist.get(nodeDistance.node) + edge.length();
                    if (alt < dist.getOrDefault(to, Double.POSITIVE_INFINITY)) {
                         dist.put(to, alt);
                         prevEdge.put(to, edgeId);
                         pq.add(new NodeDistance(to, alt));
                    }
               }
          }

          if (!prevEdge.containsKey(endNode) && startNode != endNode) {
               return Collections.emptyList();
          }

          LinkedList<Integer> edgePath = new LinkedList<>();
          int cur = endNode;
          while (cur != startNode) {
               Integer eId = prevEdge.get(cur);
               if (eId == null) break;
               edgePath.addFirst(eId);
               Edge e = edges.get(eId);
               cur = e.fromNode();
          }

          double totalLen = edgePath.stream().mapToDouble(id -> edges.get(id).length()).sum();
          PathRecord result = new PathRecord(new LinkedList<>(edgePath), totalLen);
          return List.of(result);
     }

     public JSONObject buildGeoJson(List<PathRecord> paths) {
          JSONArray features = new JSONArray();
          for (PathRecord path : paths) {
               List<Integer> ids = path.roadIds();
               if (ids.isEmpty()) continue;

               JSONArray coords = new JSONArray();
               boolean first = true;
               for (int eid : ids) {
                    Edge e = edges.get(eid);
                    if (e == null) continue;

                    if (first) {
                         coords.put(Swapped(e.coordinates().getJSONArray(0)));
                         coords.put(Swapped(e.coordinates().getJSONArray(1)));
                         first = false;
                    } else {
                         coords.put(Swapped(e.coordinates().getJSONArray(1)));
                    }
               }

               JSONObject feature = new JSONObject();
               feature.put("type", "Feature");
               feature.put("geometry", new JSONObject()
                       .put("type", "LineString")
                       .put("coordinates", coords)
               );

               JSONObject props = new JSONObject();
               props.put("roadIds", ids);
               props.put("totalLength", path.totalLength());
               feature.put("properties", props);
               features.put(feature);
          }
          JSONObject geoJson = new JSONObject();
          geoJson.put("type", "FeatureCollection");
          geoJson.put("features", features);
          return geoJson;
     }

     private static String key(double lat, double lon) {
          return String.format(Locale.ROOT, "%.7f,%.7f", lat, lon);
     }

     private static int nodeIdFor(double lat, double lon) {
          return Math.abs(Objects.hash(key(lat, lon))) & 0x7fffffff;
     }

     private int findNearestNode(double lat, double lon) {
          double[] p = LatLonToXY(lat, lon);
          double best = Double.POSITIVE_INFINITY;
          int bestId = -1;
          for (Node n : nodes.values()) {
               double[] xy = LatLonToXY(n.lat(), n.lon());
               double d = Math.hypot(p[0] - xy[0], p[1] - xy[1]);
               if (d < best) {
                    best = d;
                    bestId = n.id();
               }
          }
          return bestId;
     }
}
