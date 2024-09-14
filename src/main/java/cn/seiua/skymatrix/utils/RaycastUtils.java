package cn.seiua.skymatrix.utils;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.rotation.RotationFaker;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.function.Predicate;

public class RaycastUtils {


    public static EntityHitResult raycast(double range, float dd) {
        Entity player = SkyMatrix.mc.player;
        Vec3d vec3d = player.getCameraPosVec(dd);
        Vec3d vec3d2 = RotationFaker.instance.getServerRotationVec3d();
        double d = range + 4;
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        Box box = player.getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = raycast(player, vec3d, vec3d3, box, entita -> true, d);
        return entityHitResult;
    }

    public static EntityHitResult raycast(Entity entity, Vec3d min, Vec3d max, Box box, Predicate<Entity> predicate, double d) {
        World world = entity.getWorld();
        double e = d;
        Entity entity2 = null;
        Vec3d vec3d = null;
        for (Entity entity3 : world.getOtherEntities(entity, box, predicate)) {
            Vec3d vec3d2;
            double f;
            Box box2 = entity3.getBoundingBox().expand(entity3.getTargetingMargin()).expand(-0.1, -0.1, -0.1);
            Optional<Vec3d> optional = box2.raycast(min, max);
            if (box2.contains(min)) {
                if (!(e >= 0.0)) continue;
                entity2 = entity3;
                vec3d = optional.orElse(min);
                e = 0.0;
                continue;
            }
            if (!optional.isPresent() || !((f = min.squaredDistanceTo(vec3d2 = optional.get())) < e) && e != 0.0)
                continue;
            if (entity3.getRootVehicle() == entity.getRootVehicle()) {
                if (e != 0.0) continue;
                entity2 = entity3;
                vec3d = vec3d2;
                continue;
            }
            entity2 = entity3;
            vec3d = vec3d2;
            e = f;
        }
        if (entity2 == null) {
            return null;
        }
        return new EntityHitResult(entity2, vec3d);
    }


}
