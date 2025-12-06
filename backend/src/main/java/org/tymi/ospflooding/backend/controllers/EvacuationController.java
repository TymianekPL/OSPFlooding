package org.tymi.ospflooding.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tymi.ospflooding.backend.exceptions.InvalidFormatDataException;
import org.tymi.ospflooding.backend.services.FloodService;
import org.tymi.ospflooding.backend.services.RoadNetworkService;
import org.tymi.ospflooding.backend.utilities.PathRecord;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evac")
@RequiredArgsConstructor
public class EvacuationController {
     private RoadNetworkService service;
     private FloodService floodService;

     @Autowired
     public EvacuationController(RoadNetworkService service, FloodService floodService) {
          this.service = service;
          this.floodService = floodService;
     }

     @GetMapping("/route")
     public Map<String, Object> getRoute(
             @RequestParam String start,  // "lat,lon"
             @RequestParam String end,     // "lat,lon"
             @RequestParam(defaultValue = "2024-01-01") String startDate, // TODO: Implement that
             @RequestParam(defaultValue = "2024-12-31") String endDate // TODO: Implement that
     ) {
          String[] startParts = start.split(",");
          String[] endParts = end.split(",");
          if (startParts.length != 2) {
               throw new InvalidFormatDataException("The correct format for the coordinate is latitude,longitude", "start");
          }
          if (endParts.length != 2) {
               throw new InvalidFormatDataException("The correct format for the coordinate is latitude,longitude", "end");
          }

          double startLat = Double.parseDouble(startParts[0]);
          double startLon = Double.parseDouble(startParts[1]);
          double endLat = Double.parseDouble(endParts[0]);
          double endLon = Double.parseDouble(endParts[1]);

          List<PathRecord> paths = service.findShortestPaths(startLat, startLon, endLat, endLon);
          JSONObject geoJson = service.buildGeoJson(paths);

          return Map.of(
                  "geojson", geoJson.toMap(),
                  "empty", paths.isEmpty()
          );
     }

     @GetMapping("/flood-polygons")
     public ResponseEntity<String> getFloodPolygons(
             @RequestParam(required = false) Double south,
             @RequestParam(required = false) Double west,
             @RequestParam(required = false) Double north,
             @RequestParam(required = false) Double east,
             @RequestParam(defaultValue = "2024-01-01") String startDate,
             @RequestParam(defaultValue = "2024-12-31") String endDate) {

          try {
               double finalSouth = south != null ? south : 50.045;
               double finalWest = west != null ? west : 19.900;
               double finalNorth = north != null ? north : 50.070;
               double finalEast = east != null ? east : 19.960;

               JSONObject geoJson = floodService.getFloodedPolygons(
                       finalSouth, finalWest, finalNorth, finalEast, startDate, endDate);

               JSONObject response = new JSONObject();
               response.put("geojson", geoJson);
               response.put("bounds", new JSONArray()
                       .put(finalSouth).put(finalWest)
                       .put(finalNorth).put(finalEast));

               return ResponseEntity.ok(response.toString());

          } catch (Exception e) {
               // TODO: Logging
               e.printStackTrace();
               return ResponseEntity.status(500).body("Error getting flood polygons: " + e.getMessage());
          }
     }
}
