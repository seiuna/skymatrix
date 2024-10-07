package cn.seiua.skymatrix.utils;

import cn.seiua.skymatrix.client.waypoint.WaypointEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.time.LocalTime;
import java.util.List;

public class MathUtils {
    public static double calculateAngle(Vec2f v1, Vec2f v2) {
        if (v1.equals(v2)) return 0;

        double dotProduct = v1.x * v2.x + v1.y * v2.y;

        double magnitudeV1 = v1.length();
        double magnitudeV2 = v2.length();

        double angleInRadians = Math.acos(dotProduct / (magnitudeV1 * magnitudeV2));


        double angleInDegrees = Math.toDegrees(angleInRadians);
        if (Double.isNaN(angleInDegrees)) return 0;
        return angleInDegrees;
    }

    public static Vec2f getIntersectionPoint(Vec2f p1, Vec2f p2) {
        float x1 = p1.x;
        float y1 = p1.y;
        float x2 = p2.x;
        float y2 = p2.y;

        // Assuming the second vector is from origin (0,0) to (x2, y2)
        float denominator = y2 * x1 - x2 * y1;


        float ua = (x2 * y1 - y2 * x1) / denominator;

        float intersectionX = x1 + ua * (x2 - x1);
        float intersectionY = y1 + ua * (y2 - y1);
        return new Vec2f(intersectionX, intersectionY);

    }

    public static Vec2f getIntersectionPoint(Vec2f p1, Vec2f p2, Vec2f p3, Vec2f p4) {
        float x1 = p1.x;
        float y1 = p1.y;
        float x2 = p2.x;
        float y2 = p2.y;
        float x3 = p3.x;
        float y3 = p3.y;
        float x4 = p4.x;
        float y4 = p4.y;

        float denominator = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if (denominator == 0) {
            return null; // Lines are parallel or coincident
        }

        float ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denominator;
        float ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denominator;

        if (ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1) {
            float intersectionX = x1 + ua * (x2 - x1);
            float intersectionY = y1 + ua * (y2 - y1);
            return new Vec2f(intersectionX, intersectionY);
        }

        return null; // No intersection within the line segments
    }
    public static Vec3d calculateCenter(List<WaypointEntity> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return null;
        }

        double totalX = 0;
        double totalY = 0;
        double totalZ = 0;
        int numPoints = entityList.size();

        for (WaypointEntity point : entityList) {
            totalX += point.getX();
            totalY += point.getY();
            totalZ += point.getZ();
        }

        double centerX = totalX / numPoints;
        double centerY = totalY / numPoints;
        double centerZ = totalZ / numPoints;

        return new Vec3d(centerX, centerY, centerZ);
    }

    public static double calculateAngle(Vec3d v1, Vec3d v2) {

        double dotProduct = v1.getX() * v2.getX() + v1.getY() * v2.getY() + v1.getZ() * v2.getZ();


        double magnitudeV1 = Math.sqrt(v1.getX() * v1.getX() + v1.getY() * v1.getY() + v1.getZ() * v1.getZ());
        double magnitudeV2 = Math.sqrt(v2.getX() * v2.getX() + v2.getY() * v2.getY() + v2.getZ() * v2.getZ());


        double angleInRadians = Math.acos(dotProduct / (magnitudeV1 * magnitudeV2));


        double angleInDegrees = Math.toDegrees(angleInRadians);

        return angleInDegrees;
    }

    public static double findClosest(double minValue, double maxValue, double step, double currentValue) {
        double closestValue = minValue;
        double minDistance = Math.abs(currentValue - closestValue);
        double currentValueMod = currentValue % step;


        for (double value = minValue; value <= maxValue; value += step) {
            double distance = Math.abs(currentValue - value);
            if (distance < minDistance) {
                closestValue = value;
                minDistance = distance;
            } else if (distance == minDistance) {
                double valueMod = value % step;
                if (valueMod < currentValueMod) {
                    closestValue = value;
                    currentValueMod = valueMod;
                }
            }
        }

        return closestValue;
    }

    public static boolean isTimeInRange(LocalTime startTime, LocalTime endTime) {
        LocalTime currentTime = LocalTime.now();
        boolean crossesMidnight = endTime.isBefore(startTime);

        if (crossesMidnight) {
            return currentTime.isAfter(startTime) || currentTime.isBefore(endTime);
        } else {
            return currentTime.isAfter(startTime) && currentTime.isBefore(endTime);
        }
    }

}
