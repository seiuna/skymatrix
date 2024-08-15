package cn.seiua.skymatrix.ai;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.component.Component;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import cn.seiua.skymatrix.gui.ClickGui;
import cn.seiua.skymatrix.gui.Theme;
import cn.seiua.skymatrix.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.awt.*;
import java.util.List;
import java.util.*;

@Component
@Event(register = true)
public class PathFinder {
    private ClientWorld world;

    private Node startNode, endNode;

    public static PathFinder instance;

    public PathFinder() {

        instance = this;
    }

    private PriorityQueue<Node> openSet;
    private List<Node> closedSet;

    public List<Node> findPath(Node start, Node end) {
        this.world = MinecraftClient.getInstance().world;
        openSet = new PriorityQueue<>();
        closedSet = new ArrayList<>();


        startNode = start;
        endNode = end;


        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            if (current == endNode) {
                return reconstructPath(current);
            }

            closedSet.add(current);

            for (Node neighbor : getNeighbors(current)) {
                if (closedSet.contains(neighbor))
                    continue;

                int tentativeGScore = current.g + 1;

                if (!openSet.contains(neighbor) || tentativeGScore < neighbor.g) {
                    neighbor.parent = current;
                    neighbor.g = tentativeGScore;
                    neighbor.h = calculateHeuristic(neighbor, endNode);

                    if (!openSet.contains(neighbor))
                        openSet.add(neighbor);
                }
            }
        }

        return null; // No path found
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0)
                        continue;
                    BlockPos blockPos = new BlockPos(node.x, node.y, node.z);
                    blockPos.add(dx, dy, dz);

                    if (isValidPosition(blockPos)) {
                        Node neighbor = new Node(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                        neighbors.add(neighbor);
                    }
                }
            }
        }

        return neighbors;
    }

    private boolean isValidPosition(BlockPos blockPos) {
        return blockPos.getX() >= 0 && blockPos.getY() < 50 && blockPos.getY() >= 0 && blockPos.getY() < 50 && blockPos.getZ() >= 0 && blockPos.getZ() < 50;
    }

    private int calculateHeuristic(Node current, Node end) {
        return Math.abs(current.x - end.x) + Math.abs(current.y - end.y) + Math.abs(current.z - end.z);
    }

    private List<Node> reconstructPath(Node node) {
        List<Node> path = new ArrayList<>();

        while (node != null) {
            path.add(node);
            node = node.parent;
        }

        Collections.reverse(path);

        return path;
    }

    @EventTarget
    public void onRender(WorldRenderEvent e) {
        if (this.openSet != null) {
//            RenderSystem.disableDepthTest();
//            ClickGui.fontRenderer28.setColor(Theme.getInstance().THEME.geColor());
//            ClickGui.fontRenderer28.centeredH();
//            ClickGui.fontRenderer28.centeredV();
//            RenderUtils.translateView(e.getMatrixStack());
//            RenderUtils.setColor(new Color(74, 235, 241, 199));
//            HashSet<Node> render = new HashSet<>(openSet);
//            LivingEntity player = SkyMatrix.mc.player;
//            for (Node node : render) {
//
//                RenderUtils.translatePos(e.getMatrixStack(), new BlockPos(node.x, node.y, node.z));
//
//                RenderUtils.drawSolidBox(new Box(0, 0, 0, 1, 1, 1), e.getMatrixStack());
//                e.getMatrixStack().pop();
//
//            }
//            RenderSystem.disableBlend();
//            RenderSystem.enableDepthTest();
        }


    }

}