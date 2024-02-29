package cn.seiua.skymatrix.client.module.modules.autphotm;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public transient double g;
    public transient double f;
    public transient double h;

    public int x;
    public int y;
    public int z;
    public String id;

    public List<String> tags;
    public transient List<Node> rounds;
    public List<String> roundssav;

    public Node(int x, int y, int z, List<Node> rounds, List<String> tags) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rounds = rounds;
        this.tags = tags;
        roundssav = new ArrayList<>();
    }

    public double distanceTo(Node other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2) + Math.pow(this.z - other.z, 2));
    }

    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node other = (Node) obj;
            return this.x == other.x && this.y == other.y && this.z == other.z;
        }
        return false;
    }

    public List<Node> getRounds() {
        return this.rounds;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public BlockPos toBlockPos() {
        return new BlockPos(x, y, z);
    }
}
