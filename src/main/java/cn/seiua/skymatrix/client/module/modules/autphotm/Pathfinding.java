package cn.seiua.skymatrix.client.module.modules.autphotm;

import cn.seiua.skymatrix.SkyMatrix;

import java.util.*;

// 寻路类
public class Pathfinding {
    // 使用 A* 算法寻找从 start 到 end 的最短路径，返回路径中的节点列表
    public static List<Node> findPath(Node start, Node end, List<Node> nodes) {
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(a -> a.f));

        HashSet<Node> closed = new HashSet<>();

        HashMap<Node, Node> prev = new HashMap<>();

        HashMap<Node, Double> g = new HashMap<>();

        start.g = 0;
        start.h = (float) start.distanceTo(end);
        start.f = start.g + start.h;
        open.add(start);
        g.put(start, start.g);

        while (!open.isEmpty()) {
            Node current = open.poll();
            if (current.equals(end)) {
                return backtrack(prev, start, end);
            }
            closed.add(current);
            for (Node next : current.rounds) {
                if (next.equals(current)) continue;
                assert SkyMatrix.mc.world != null;
//                BlockHitResult hitResult = SkyMatrix.mc.world.raycast(new RaycastContext(current.toBlockPos().toCenterPos().add(0, 2, 0), next.toBlockPos().toCenterPos().add(0, 0.48, 0), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, SkyMatrix.mc.player));
//                if (!hitResult.getBlockPos().equals(next.toBlockPos())) {
//                    continue;
//                }
                if (current.toBlockPos().toCenterPos().distanceTo(next.toBlockPos().toCenterPos()) > 60) {
                    continue;
                }
                if (closed.contains(next)) {
                    continue;
                }
                double nextG = current.g + current.distanceTo(next);
                if (!open.contains(next) || nextG < g.getOrDefault(next, Double.MAX_VALUE)) {
                    next.g = nextG;
                    next.h = next.distanceTo(end);
                    next.f = next.g + next.h;
//                    next.f = Math.abs(next.g - next.h);
                    prev.put(next, current);
                    g.put(next, next.g);
                    if (!open.contains(next)) {
                        open.add(next);
                    }
                }
            }
        }

        // 如果优先队列为空，说明没有找到路径，返回空列表
        return new ArrayList<>();
    }

    // 回溯路径，从终点开始，根据前驱节点回溯到起点，返回节点列表
    public static List<Node> backtrack(HashMap<Node, Node> prev, Node start, Node end) {
        // 创建一个链表，用于存储路径中的节点
        LinkedList<Node> path = new LinkedList<>();

        // 从终点开始回溯
        Node current = end;

        // 当当前节点不是起点时，循环执行
        while (!current.equals(start)) {
            // 将当前节点加入链表的头部
            path.addFirst(current);

            // 获取当前节点的前驱节点，作为下一个节点
            current = prev.get(current);
        }

        // 将起点加入链表的头部
        path.addFirst(start);

        // 返回链表
        return path;
    }

}

