package cn.seiua.skymatrix.client.module.modules.test;


import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.IToggle;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.client.rotation.ClientRotation;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.KeyBind;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import cn.seiua.skymatrix.utils.ReflectUtils;
import cn.seiua.skymatrix.utils.RenderUtilsV2;
import cn.seiua.skymatrix.utils.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

@Event
@Sign(sign = Signs.FREE)
@SModule(name = "TestMobFinder", category = "test")
public class TestMobFinder implements IToggle {
    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(Arrays.asList(), ReflectUtils.getModuleName(this));
    private List<BlockPos> blocks = new CopyOnWriteArrayList<>();
    @Use
    private ClientRotation rotation;
    private int cd = 0;
    private Entity lastTarget;

    @EventTarget
    public void onTick(ClientTickEvent event) {

//        blocks.clear();
//        Entity target = findTarget();
//        if(target==null){
//            return;
//        }
//        BlockPos to = findPosition(target);
//        if(to==null){
//            return;
//        }
//        blocks.add(to);
//        blocks.add(target.getBlockPos());
//        facingAndPathing(target,to);
//        assert SkyMatrix.mc.player != null;
//        Vec3d vec3d = new Vec3d(SkyMatrix.mc.player.getX(), SkyMatrix.mc.player.getY()+1.4, SkyMatrix.mc.player.getZ());
//        Vec3d vec3d2 = new Vec3d(target.getX(), target.getEyeY(), target.getZ());
//        boolean flag= SkyMatrix.mc.world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, SkyMatrix.mc.player)).getType() != HitResult.Type.MISS;
//        if(flag){
//            SkyMatrix.mc.options.useKey.setPressed(true);
//        }else {
//            SkyMatrix.mc.options.useKey.setPressed(false);
//        }
    }

    public Entity findTarget() {
        Entity best = null;
        double bestDistance = 0;
        for (Entity entity : SkyMatrix.mc.world.getEntities()) {
            if (entity instanceof LivingEntity) {
                if (entity == SkyMatrix.mc.player) {
                    continue;
                }
                if (!(entity instanceof ArmorStandEntity)) {
                    continue;
                }
                if (Math.abs(entity.getY() - SkyMatrix.mc.player.getY()) > 3.5) continue;
                if (!entity.getDisplayName().getString().contains("[")) continue;
                if (entity.getDisplayName().getString().contains(" 0/")) continue;
                double distance = entity.distanceTo(SkyMatrix.mc.player);
                if (best == null || distance < bestDistance) {
                    best = entity;
                    bestDistance = distance;
                }
            }
        }
        if (lastTarget != null) {
            BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
        }
        lastTarget = best;
        return best;
    }

    public BlockPos findPosition(Entity entity) {
        int range = 2;
        int t = range + 0;
        BlockPos entityPos = entity.getBlockPos();
        Entity player = SkyMatrix.mc.player;
        BlockPos best = null;
        for (int x = -t; x < t; x++) {
            for (int y = -t; y < t; y++) {
                for (int z = -t; z < t; z++) {
                    BlockPos blockPos = new BlockPos(entityPos.getX() + x, entityPos.getY() + y, entityPos.getZ() + z);
                    if (SkyMatrix.mc.world.getBlockState(blockPos).isAir()) continue;
                    if (!SkyMatrix.mc.world.getBlockState(blockPos.up()).isAir()) continue;
                    if (!SkyMatrix.mc.world.getBlockState(blockPos.up().up()).isAir()) continue;
                    if (blockPos.getSquaredDistance(entityPos) < 3.5) continue;
                    Vec3d vec3d = new Vec3d(blockPos.getX(), blockPos.getY() + 1.4, blockPos.getZ());
                    Vec3d vec3d2 = new Vec3d(entity.getX(), entity.getEyeY(), entity.getZ());
                    boolean flag = SkyMatrix.mc.world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player)).getType() == HitResult.Type.MISS;
                    if (!flag) {
                        continue;
                    }
                    if (best == null || SkyMatrix.mc.player.getBlockPos().getSquaredDistance(best.toCenterPos()) > SkyMatrix.mc.player.getBlockPos().getSquaredDistance(blockPos.toCenterPos())) {
                        best = blockPos;
                    }

                }
            }
        }
        return best;
    }

    public void facingAndPathing(Entity target, BlockPos to) {
        Random random = new Random(114514);
        double offX = random.nextDouble() - 0.5;
        double offZ = random.nextDouble() - 0.5;
        double offY = random.nextDouble() - 0.5 - 1;
        Vec3d v = target.getEyePos().add(offX, offY, offZ);
        rotation.smoothRotation.smoothLook(RotationUtils.getNeededRotationsFix18(v), 3.5f, null, false);
        rotation.clientLook(RotationUtils.getNeededRotationsFix18(v), 100, null);
        doPath(to);
    }

    public void doPath(BlockPos to) {
        if (!BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().isActive()) {
            BaritoneAPI.getSettings().allowSprint.value = false;
            BaritoneAPI.getSettings().avoidance.value = false;
            BaritoneAPI.getSettings().allowWalkOnBottomSlab.value = true;
            BaritoneAPI.getSettings().antiCheatCompatibility.value = true;
            BaritoneAPI.getSettings().allowBreak.value = false;
            BaritoneAPI.getSettings().allowParkour.value = false;
            BaritoneAPI.getSettings().allowPlace.value = false;
            BaritoneAPI.getSettings().allowParkourAscend.value = true;
            BaritoneAPI.getSettings().renderGoal.value = false;
            BaritoneAPI.getSettings().freeLook.value = true;
            BaritoneAPI.getSettings().assumeSafeWalk.value = false;
            BaritoneAPI.getSettings().renderPath.value = false;
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalBlock(to.getX(), to.getY() + 1, to.getZ()));
        }
    }


    @EventTarget
    public void onRender(WorldRenderEvent event) {
        for (BlockPos block : blocks) {
            RenderUtilsV2.renderOutlineBlock(event.getMatrixStack(), block, new Color(248, 255, 122, 255));
        }
    }

    @EventTarget
    public void disable() {
        rotation.cancelClientLook();
        rotation.cancelServerLook();
        BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
    }
}