package org.tymi.ospflooding.backend;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.tymi.ospflooding.backend.services.RoadNetworkService;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer cors() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("*");
            }
        };
    }
     @Bean
     public ApplicationRunner runner(RoadNetworkService roadNetworkService) {
          return args -> {
               // TODO: Load that information from the database
               double south = 50.045;
               double west  = 19.900;
               double north = 50.070;
               double east  = 19.960;

               roadNetworkService.loadOSMRoads(south, west, north, east);
          };
     }
}
