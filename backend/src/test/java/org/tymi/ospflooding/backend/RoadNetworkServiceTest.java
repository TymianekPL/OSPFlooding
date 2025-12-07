package org.tymi.ospflooding.backend;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tymi.ospflooding.backend.records.Edge;
import org.tymi.ospflooding.backend.records.Node;
import org.tymi.ospflooding.backend.repositories.RoadSegmentRepository;
import org.tymi.ospflooding.backend.services.FloodService;
import org.tymi.ospflooding.backend.services.RoadNetworkService;
import org.tymi.ospflooding.backend.utilities.PathRecord;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoadNetworkServiceTest {
     RoadSegmentRepository roadRepo;
     FloodService floodService;
     RoadNetworkService service;

     @BeforeEach
     void setup() {
          roadRepo = mock(RoadSegmentRepository.class);
          floodService = mock(FloodService.class);
          service = new RoadNetworkService(roadRepo, floodService);
     }

     @Test
     void testIsFlooded() {
          Set<Long> flooded = new HashSet<>();
          Node n = new Node(1, 50.0, 20.0);

          long k = FloodService.pack(FloodService.toIntCoord(n.lat()), FloodService.toIntCoord(n.lon()));
          flooded.add(k);

          assertTrue(service.isFlooded(flooded, n));

          Node n2 = new Node(2, 0.0, 0.0);
          assertFalse(service.isFlooded(flooded, n2));
     }

     @Test
     void testBuildGeoJson() {
          JSONArray coords = new JSONArray();
          JSONArray p1 = new JSONArray(); p1.put(0); p1.put(0);
          JSONArray p2 = new JSONArray(); p2.put(0); p2.put(1);
          coords.put(p1).put(p2);

          Edge e = new Edge(1, 1, 2, 1.0, 1, coords);
          service.edges.put(1, e);

          PathRecord path = new PathRecord(List.of(1), 1.0);

          JSONObject geoJson = service.buildGeoJson(List.of(path));

          assertEquals("FeatureCollection", geoJson.getString("type"));
          assertEquals(1, geoJson.getJSONArray("features").length());

          JSONObject feature = geoJson.getJSONArray("features").getJSONObject(0);
          assertEquals("Feature", feature.getString("type"));
          assertEquals("LineString", feature.getJSONObject("geometry").getString("type"));
          assertEquals(2, feature.getJSONObject("geometry").getJSONArray("coordinates").length());
     }

     @Test
     void testFindShortestPathsSimple() {
          Node n1 = new Node(1, 0, 0);
          Node n2 = new Node(2, 0, 1);
          service.nodes.put(1, n1);
          service.nodes.put(2, n2);

          JSONArray coords = new JSONArray();
          JSONArray p1 = new JSONArray(); p1.put(0); p1.put(0);
          JSONArray p2 = new JSONArray(); p2.put(0); p2.put(1);
          coords.put(p1).put(p2);

          Edge e = new Edge(1, 1, 2, 1.0, 1, coords);
          service.edges.put(1, e);
          service.adjacency.put(1, List.of(1));

          when(floodService.loadFloodedPoints(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                  .thenReturn(Set.of());

          List<PathRecord> paths = service.findShortestPaths(0, 0, 0, 1);
          assertEquals(1, paths.size());
          assertEquals(List.of(1), paths.getFirst().roadIds());
          assertEquals(1.0, paths.getFirst().totalLength());
     }
}
