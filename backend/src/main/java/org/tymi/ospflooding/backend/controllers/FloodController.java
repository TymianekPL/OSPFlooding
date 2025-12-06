package org.tymi.ospflooding.backend.controllers;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tymi.ospflooding.backend.records.Node;
import org.tymi.ospflooding.backend.services.FloodService;
import org.tymi.ospflooding.backend.services.RoadNetworkService;

@RestController
@RequestMapping("/api/flood")
public class FloodController {
     private final FloodService floodService;
     private final RoadNetworkService roadNetworkService;

     public FloodController(FloodService floodService, RoadNetworkService roadNetworkService) {
          this.floodService = floodService;
          this.roadNetworkService = roadNetworkService;
     }

     @GetMapping("/polygons")
     public ResponseEntity<String> getFloodedPolygons(
             @RequestParam double south,
             @RequestParam double west,
             @RequestParam double north,
             @RequestParam double east,
             @RequestParam String startDate,
             @RequestParam String endDate) {

          try {
               double maxSouth = roadNetworkService.nodes.values().stream().mapToDouble(Node::lat).min().orElse(50.045);
               double maxWest  = roadNetworkService.nodes.values().stream().mapToDouble(Node::lon).min().orElse(19.900);
               double maxNorth = roadNetworkService.nodes.values().stream().mapToDouble(Node::lat).max().orElse(50.070);
               double maxEast  = roadNetworkService.nodes.values().stream().mapToDouble(Node::lon).max().orElse(19.960);

               south = south == 0 ? maxSouth : Math.min(maxSouth, south);
               west = west == 0 ? maxWest : Math.min(maxWest, west);
               north = north == 0 ? maxNorth : Math.min(maxNorth, north);
               east = east == 0 ? maxEast : Math.min(maxEast, east);

               JSONObject geoJson = floodService.getFloodedPolygons(south, west, north, east, startDate, endDate);
               return ResponseEntity.ok(geoJson.toString());

          } catch (Exception e) {
               e.printStackTrace();
               return ResponseEntity.status(500).body("Error getting flood polygons: " + e.getMessage());
          }
     }

     /**
      * Get flooded areas as points for visualization
      */
     @GetMapping("/points")
     public ResponseEntity<String> getFloodedPoints(
             @RequestParam double south,
             @RequestParam double west,
             @RequestParam double north,
             @RequestParam double east,
             @RequestParam String startDate,
             @RequestParam String endDate) {

          try {
               double maxSouth = roadNetworkService.nodes.values().stream().mapToDouble(Node::lat).min().orElse(50.045);
               double maxWest  = roadNetworkService.nodes.values().stream().mapToDouble(Node::lon).min().orElse(19.900);
               double maxNorth = roadNetworkService.nodes.values().stream().mapToDouble(Node::lat).max().orElse(50.070);
               double maxEast  = roadNetworkService.nodes.values().stream().mapToDouble(Node::lon).max().orElse(19.960);

               south = south == 0 ? maxSouth : Math.min(maxSouth, south);
               west = west == 0 ? maxWest : Math.min(maxWest, west);
               north = north == 0 ? maxNorth : Math.min(maxNorth, north);
               east = east == 0 ? maxEast : Math.min(maxEast, east);

               JSONObject geoJson = floodService.getFloodedPoints(south, west, north, east, startDate, endDate);
               return ResponseEntity.ok(geoJson.toString());

          } catch (Exception e) {
               e.printStackTrace();
               return ResponseEntity.status(500).body("Error getting flood points: " + e.getMessage());
          }
     }

     /**
      * Check if a specific point is flooded
      */
     @GetMapping("/check")
     public ResponseEntity<String> checkFloodStatus(
             @RequestParam double lat,
             @RequestParam double lon,
             @RequestParam double south,
             @RequestParam double west,
             @RequestParam double north,
             @RequestParam double east,
             @RequestParam String startDate,
             @RequestParam String endDate) {

          try {
               boolean isFlooded = floodService.isFloodedDirect(lat, lon, south, west, north, east, startDate, endDate);

               JSONObject response = new JSONObject();
               response.put("latitude", lat);
               response.put("longitude", lon);
               response.put("flooded", isFlooded);

               return ResponseEntity.ok(response.toString());

          } catch (Exception e) {
               e.printStackTrace();
               return ResponseEntity.status(500).body("Error checking flood status: " + e.getMessage());
          }
     }
}
