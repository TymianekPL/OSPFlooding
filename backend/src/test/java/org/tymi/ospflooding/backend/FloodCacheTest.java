package org.tymi.ospflooding.backend;

import org.junit.jupiter.api.Test;
import org.tymi.ospflooding.backend.models.FloodCache;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FloodCacheTest {
     @Test
     void testBuildCacheKey() {
          String key = FloodCache.buildCacheKey(10.1234, 20.5678, 30.9876, 40.5432, "2025-12-01", "2025-12-02");
          assertEquals("10.12,20.57,30.99,40.54|2025-12-01/2025-12-02", key);
     }

     @Test
     void testInitCacheKey() {
          FloodCache cache = new FloodCache(10, 20, 30, 40, "2025-12-01", "2025-12-02", 0.00005);
          cache.initCacheKey();
          assertEquals("10.00,20.00,30.00,40.00|2025-12-01/2025-12-02", cache.getCacheKey());
     }

     @Test
     void testIsStale() {
          FloodCache fresh = new FloodCache();
          fresh.setLastUpdated(LocalDateTime.now().minusHours(1));
          assertFalse(fresh.isStale());

          FloodCache stale = new FloodCache();
          stale.setLastUpdated(LocalDateTime.now().minusHours(25));
          assertTrue(stale.isStale());
     }

     @Test
     void testBboxAndDateRangeKeys() {
          FloodCache cache = new FloodCache(10, 20, 30, 40, "2025-12-01", "2025-12-02", 0.00005);

          assertEquals("10.00,20.00,30.00,40.00", cache.getBboxKey());
          assertEquals("2025-12-01/2025-12-02", cache.getDateRangeKey());
          assertEquals("10.00,20.00,30.00,40.00|2025-12-01/2025-12-02", cache.getCacheKey());
     }

     @Test
     void testConstructorSetsLastUpdated() {
          FloodCache cache = new FloodCache();
          assertNotNull(cache.getLastUpdated());
     }
}
