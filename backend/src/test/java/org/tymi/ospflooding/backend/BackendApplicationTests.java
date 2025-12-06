package org.tymi.ospflooding.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.tymi.ospflooding.backend.repositories.FloodCacheRepository;
import org.tymi.ospflooding.backend.repositories.FloodPointRepository;
import org.tymi.ospflooding.backend.repositories.RoadSegmentRepository;

@SpringBootTest
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
class BackendApplicationTests {

     @MockitoBean
     private RoadSegmentRepository roadSegmentRepository;

     @MockitoBean
     private FloodCacheRepository floodCacheRepository;

     @MockitoBean
     private FloodPointRepository floodPointRepository;

     @Test
     void contextLoads() {
     }

}
