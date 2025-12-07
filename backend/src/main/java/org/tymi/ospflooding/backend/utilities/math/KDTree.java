package org.tymi.ospflooding.backend.utilities.math;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class KDTree<TElement extends ITreeNode, TConverter extends ITreeNodeConverter> {
     private class KDNode {
          TElement element;
          double x;
          double y;
          KDNode left;
          KDNode right;
          int depth;
          KDNode(TElement element, double x, double y, int depth) {
               this.element = element;
               this.x = x;
               this.y = y;
               this.depth = depth;
          }
     }

     private KDNode root;
     @Getter
     private int size;
     private final TConverter converter;

     private KDNode buildTree(List<KDNode> nodes, int depth) {
          if (nodes == null || nodes.isEmpty()) {
               return null;
          }

          int axis = depth % 2;

          if (axis == 0) {
               nodes.sort(Comparator.comparingDouble(n -> n.x));
          } else {
               nodes.sort(Comparator.comparingDouble(n -> n.y));
          }

          int medianIndex = nodes.size() / 2;
          KDNode median = nodes.get(medianIndex);
          median.depth = depth;

          List<KDNode> leftNodes = nodes.subList(0, medianIndex);
          List<KDNode> rightNodes = nodes.subList(medianIndex + 1, nodes.size());

          median.left = buildTree(leftNodes, depth + 1);
          median.right = buildTree(rightNodes, depth + 1);

          return median;
     }

     private void findNearest(KDNode node, double targetX, double targetY, NearestNeighborResult result) {
          if (node == null) {
               return;
          }

          double dx = node.x - targetX;
          double dy = node.y - targetY;
          double distance = Math.hypot(dx, dy);

          if (distance < result.minDistance) {
               result.minDistance = distance;
               result.nearestNode = node.element;
          }

          int axis = node.depth % 2;
          double axisDiff = (axis == 0) ? (targetX - node.x) : (targetY - node.y);

          KDNode first = axisDiff < 0 ? node.left : node.right;
          KDNode second = axisDiff < 0 ? node.right : node.left;

          findNearest(first, targetX, targetY, result);

          if (Math.abs(axisDiff) < result.minDistance) {
               findNearest(second, targetX, targetY, result);
          }
     }

     private class NearestNeighborResult {
          TElement nearestNode = null;
          double minDistance = Double.POSITIVE_INFINITY;
     }

     public TElement findNearest(double x, double y) {
          Coordinate targetCoordinate = converter.ToCoordinate(x, y);
          NearestNeighborResult result = new NearestNeighborResult();
          findNearest(root, targetCoordinate.x(), targetCoordinate.y(), result);
          return result.nearestNode;
     }

     public void build(Collection<TElement> nodes) {
          List<KDNode> kdNodes = new ArrayList<>();
          for (TElement node : nodes) {
               var coordinate = node.ToCoordinate();
               kdNodes.add(new KDNode(node, coordinate.x(), coordinate.y(), 0));
          }
          this.root = buildTree(kdNodes, 0);
          this.size = nodes.size();
     }

     public KDTree(TConverter converter) {
          this.converter = converter;
     }
}
