package org.tymi.ospflooding.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tymi.ospflooding.backend.models.RoadSegment;

public interface RoadSegmentRepository extends JpaRepository<RoadSegment, Integer> {
}
