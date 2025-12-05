package org.tymi.ospflooding.backend.controllers;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tymi.ospflooding.backend.exceptions.InvalidFormatDataException;
import org.tymi.ospflooding.backend.services.RoadNetworkService;
import org.tymi.ospflooding.backend.utilities.PathRecord;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evac")
@RequiredArgsConstructor
public class EvacuationController {
     private RoadNetworkService service;

     @Autowired
     public EvacuationController(RoadNetworkService service) {
          this.service = service;
     }

     @GetMapping("/route")
     public Map<String, Object> getRoute(
             @RequestParam String start,  // "lat,lon"
             @RequestParam String end     // "lat,lon"
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
}
