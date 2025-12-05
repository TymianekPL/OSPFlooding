package org.tymi.ospflooding.backend.utilities;

import java.util.List;

public record PathRecord(List<Integer> roadIds, double totalLength) {}
