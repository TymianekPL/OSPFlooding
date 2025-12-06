package org.tymi.ospflooding.backend.cron;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tymi.ospflooding.backend.services.FloodService;
import org.tymi.ospflooding.backend.records.Node;
import org.tymi.ospflooding.backend.services.RoadNetworkService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class FloodDataUpdater {
     private final FloodService floodService;
     private final RoadNetworkService roadNetworkService;

     public FloodDataUpdater(FloodService floodService, RoadNetworkService roadNetworkService) {
          this.floodService = floodService;
          this.roadNetworkService = roadNetworkService;
     }

     @Scheduled(cron = "0 0 2 * * *")
     public void cronUpdateFloodData() {
          updateFloodData();
     }

     @EventListener(ApplicationReadyEvent.class)
     public void initUpdateFloodData() {
          updateFloodData();
     }

     public void updateFloodData() {
          // TODO: Use the database
          double south = roadNetworkService.nodes.values().stream().mapToDouble(Node::lat).min().orElse(50.045);
          double west  = roadNetworkService.nodes.values().stream().mapToDouble(Node::lon).min().orElse(19.900);
          double north = roadNetworkService.nodes.values().stream().mapToDouble(Node::lat).max().orElse(50.070);
          double east  = roadNetworkService.nodes.values().stream().mapToDouble(Node::lon).max().orElse(19.960);

          // fetch data for the last 24h
          LocalDate endDate = LocalDate.now();
          LocalDate startDate = endDate.minusDays(1);

          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

          try {
               floodService.downloadAndParseFloodZones(
                       south, west, north, east,
                       startDate.format(formatter),
                       endDate.format(formatter)
               );
               System.out.println("Flood data updated successfully");
          } catch (IOException | InterruptedException e) {
               System.err.println("Failed to update flood data: " + e.getMessage());
          }
     }
}
