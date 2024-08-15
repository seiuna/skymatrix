package cn.seiua.skymatrix.client.module.modules.combat;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.*;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.KeyBind;
import cn.seiua.skymatrix.config.option.SingleChoice;
import cn.seiua.skymatrix.config.option.ValueSlider;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import cn.seiua.skymatrix.utils.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


@Event
@Sign(sign = Signs.FREE)
@SModule(name = "aimassist", category = "combat")
public class Aimassist implements IToggle {
    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(Arrays.asList(), ReflectUtils.getModuleName(this));

    @Value(name = "range")
    @Sign(sign = Signs.BETA)
    public ValueSlider range = new ValueSlider(20, 0, 60, 0.5f);
    @Value(name = "angle")
    @Sign(sign = Signs.BETA)
    public ValueSlider angle = new ValueSlider(20, 0, 180, 0.1f);

    @Value(name = "aim speed")
    @Sign(sign = Signs.BETA)
    public ValueSlider speed = new ValueSlider(1.7f, 0, 3, 0.1f);
    @Value(name = "select mode")
    @Sign(sign = Signs.BETA)
    public SingleChoice<String> mode = new SingleChoice<>(List.of("angle", "distance"), SingleChoice.MODE);


    @Use
    private Client client;
    @Use
    private HypixelWay way;
    @Use
    private SmoothRotation smoothRotation;
    @Use
    private RotationFaker rotationFaker;

    private Entity target;
    private Entity realTarget;

    private boolean flag;

    int tick;


    @EventTarget
    public void onTick(ClientTickEvent event) {

        Vec3d plauerRotationVec3 = SkyMatrix.mc.player.getRotationVecClient();
        Vec3d plauerPosVec3 = SkyMatrix.mc.player.getCameraPosVec(1.0f);
        boolean flag = false;
        if ((SkyMatrix.mc.currentScreen == null && (SkyMatrix.mc.options.attackKey.isPressed() || SkyMatrix.mc.options.useKey.isPressed()))) {
            tick++;
            if (tick < 0) return;
            double lastDistance = 9999;
            double lastAngle = 9999;
            Entity entiti = null;
            Vec3d entityAVec = new Vec3d(0, 0, 0);
            for (Entity entity : SkyMatrix.mc.world.getEntities()) {
                if (entity instanceof MobEntity) {
                    if (entity.isAlive()) {
                        if (SkyMatrix.mc.player.canSee(entity)) {
                            Vec3d entityVec = entity.getCameraPosVec(1.0f);
                            Vec3d entityRotationVec = entityVec.subtract(SkyMatrix.mc.player.getCameraPosVec(1.0f)).multiply(1.0f / entityVec.length());
                            double angle = MathUtils.calculateAngle(plauerRotationVec3, entityRotationVec);
                            double distance = entityVec.distanceTo(plauerPosVec3);
                            if (angle > this.angle.getValue().doubleValue()) continue;
                            if (distance > this.range.getValue().doubleValue()) continue;
                            if (mode.selectedValue().equals("angle")) {
                                if (angle < lastAngle) {
                                    lastAngle = angle;
                                    entiti = entity;
                                    entityAVec = entityRotationVec;
                                    flag = true;
                                    this.flag = true;

                                }
                            }
                            if (mode.selectedValue().equals("distance")) {
                                if (distance < lastDistance) {
                                    lastDistance = distance;
                                    entiti = entity;
                                    entityAVec = entityRotationVec;
                                    this.flag = true;
                                    flag = true;

                                }
                            }
                        }
                    }

                }
                target = entiti;
                if (flag) {


                    LivingEntity entityf = (LivingEntity) target;

                    smoothRotation.smoothLook(RotationUtils.getNeededRotations(target.getPos().add(0, target.getHeight() / 2, 0)), this.speed.getValue().floatValue(), null, true);


                }
            }
            if (tick % 1 == 0) {
                Random r = new Random();
                float y = (float) (Math.abs(r.nextInt() % 50) - 18) / 140;
                float p = (float) (Math.abs(r.nextInt() % 50) - 18) / 140;
                SkyMatrix.mc.player.setYaw(SkyMatrix.mc.player.getYaw() + y);
                SkyMatrix.mc.player.setPitch(SkyMatrix.mc.player.getPitch() + p);
            }
            int rv = (Math.abs(new Random().nextInt()) % 4);
            if (rv == 0) {
                rv = 1;
            }

        } else {
            tick = 0;
        }

    }

    int cd;

    @EventTarget
    public void onRender(WorldRenderEvent e) {
//        EntityHitResult entityHitResult = RaycastUtils.raycast(this.range.getValue().doubleValue(), e.getTickDelta());
//        if (entityHitResult == null || entityHitResult.getEntity() != target) {
//            smoothRotation.enable();
//            realTarget = null;
//        } else {
//            realTarget = entityHitResult.getEntity();
//            RenderSystem.disableDepthTest();
//            RenderSystem.enableBlend();
//            RenderUtils.translateView(e.getMatrixStack());
//            RenderUtils.translatePos(e.getMatrixStack(), entityHitResult.getEntity().getEyePos());
//            RenderUtils.setColor(new Color(0, 255, 220, 70));
//            RenderUtils.drawSolidBox(new Box(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5), e.getMatrixStack());
//            e.getMatrixStack().pop();
//            RenderSystem.disableBlend();
//            RenderSystem.enableDepthTest();
//            smoothRotation.disable();
//
//        }
    }

    @Override
    public void disable() {
    }

    @Override
    public void enable() {

    }
}
