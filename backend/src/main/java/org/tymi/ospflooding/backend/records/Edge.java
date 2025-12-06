package org.tymi.ospflooding.backend.records;

import org.json.JSONArray;

/**
 * @param length        metres
 * @param originalWayId helpful for debugging
 * @param coordinates   [[lat,lon],[lat,lon]]
 */
public record Edge(int id, int fromNode, int toNode, double length, int originalWayId, JSONArray coordinates) {
}
