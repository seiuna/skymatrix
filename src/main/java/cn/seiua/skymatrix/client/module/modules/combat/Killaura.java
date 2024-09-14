package cn.seiua.skymatrix.client.module.modules.combat;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.Client;
import cn.seiua.skymatrix.client.HypixelWay;
import cn.seiua.skymatrix.client.IToggle;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.client.rotation.Rotation;
import cn.seiua.skymatrix.client.rotation.RotationFaker;
import cn.seiua.skymatrix.client.rotation.SmoothRotation;
import cn.seiua.skymatrix.config.Hide;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.*;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import cn.seiua.skymatrix.utils.MathUtils;
import cn.seiua.skymatrix.utils.ReflectUtils;
import cn.seiua.skymatrix.utils.RotationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


@Event
@Sign(sign = Signs.FREE)
@SModule(name = "killaura", category = "combat")
public class Killaura implements IToggle {
    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(Arrays.asList(), ReflectUtils.getModuleName(this));
    @Value(name = "target")
    @Sign(sign = Signs.BETA)
    public TargetSelect targetSelect = new TargetSelect();
    @Value(name = "mode")
    @Sign(sign = Signs.BETA)
    public SingleChoice<String> mode = new SingleChoice(Arrays.asList("legit", "normal"), SingleChoice.MODE);
    @Value(name = "targetMode")
    @Sign(sign = Signs.BETA)
    public SingleChoice<String> targetMode = new SingleChoice(Arrays.asList("Single", "Switch"), SingleChoice.MODE);
    @Value(name = "select mode")
    @Sign(sign = Signs.BETA)
    public SingleChoice<String> selectMode = new SingleChoice<>(List.of("angle", "distance"), SingleChoice.MODE);
    @Value(name = "mouse down")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch md = new ToggleSwitch(true);
    @Value(name = "switch delay")
    @Sign(sign = Signs.BETA)
    @Hide(following = "targetMode", value = "Switch")
    public ValueSlider delay = new ValueSlider(20, 0, 60, 0.5f);
    @Value(name = "invisible")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch invisible = new ToggleSwitch(true);
    @Value(name = "range")
    @Sign(sign = Signs.BETA)
    public ValueSlider range = new ValueSlider(4, 0, 6, 0.1f);
    @Value(name = "angle")
    @Sign(sign = Signs.BETA)
    public ValueSlider angle = new ValueSlider(20, 0, 180, 0.1f);

    @Value(name = "advance")
    public ToggleSwitch advance = new ToggleSwitch(false);
    @Value(name = "aim speed")
    @Sign(sign = Signs.BETA)
    public ValueSlider speed = new ValueSlider(1.7f, 0, 3, 0.1f);


    @Use
    private Client client;

    @Use
    private AntiBot antiBot;
    @Use
    private HypixelWay way;
    @Use
    private SmoothRotation smoothRotation;
    @Use
    private RotationFaker rotationFaker;

    private Entity target;
    private Vec3d targetVec;
    private Entity realTarget;

    private boolean flag;

    int tick;


    @EventTarget
    public void onTick(ClientTickEvent event) {
        if (!SkyMatrix.mc.options.attackKey.isPressed()) {
            if (tick != 0) {
                tick = 0;
                this.target = null;
                smoothRotation.enable();
                smoothRotation.smoothLook(new Rotation(SkyMatrix.mc.player.getYaw(), SkyMatrix.mc.player.getPitch()), 2, null, false);
            }
        }

        tick++;
        if (canDo()) {
            update();
        }

        LivingEntity entityf = (LivingEntity) target;
        if (entityf != null) {
            Random r = new Random();
            if (!this.mode.selectedValue().equals("legit")) {

//                Rotation rotation = RotationUtils.getNeededRotations(target.getPos());
                smoothRotation.smoothLook(RotationUtils.getNeededRotations(target.getEyePos().add(0, -0.2, 0).add((r.nextDouble() % 9f) / 10, (r.nextDouble() % 9f) / 10, (r.nextDouble() % 9f) / 10)), this.speed.getValue().floatValue(), null, false, false, false);
            }
            int a = Math.abs(r.nextInt());
            if (a == 0) {
                a = 11;
            }
            if (a % 2 == 0) {

                attackTarget();
            }
        }


    }

    private boolean canDo() {
        if (SkyMatrix.mc.currentScreen == null) {
            return true;
        }
        return false;
    }

    private void attackTarget() {
        SkyMatrix.mc.interactionManager.attackEntity(SkyMatrix.mc.player, target);
        SkyMatrix.mc.player.swingHand(Hand.MAIN_HAND);
    }


    public void update() {
        this.updatePlayerVec();
        for (Entity entity : SkyMatrix.mc.world.getEntities()) {
            if (this.isTarget(entity)) {

                this.filter(entity);
            }
        }
    }

    private void updatePlayerVec() {
        playerRotationVec3 = SkyMatrix.mc.player.getRotationVecClient();
        playerPosVec3 = SkyMatrix.mc.player.getCameraPosVec(1.0f);
    }

    private Vec3d playerRotationVec3;
    private Vec3d playerPosVec3;
    private double lastDistance = 9999;
    private double lastAngle = 9999;

    private void filter(Entity entity) {

        Vec3d entityVec = entity.getCameraPosVec(1.0f);
        Vec3d entityRotationVec = entityVec.subtract(SkyMatrix.mc.player.getCameraPosVec(1.0f)).multiply(1.0f / entityVec.length());
        double angle = MathUtils.calculateAngle(playerRotationVec3, entityRotationVec);
        double distance = entityVec.distanceTo(playerPosVec3);
        if (angle > this.angle.getValue().doubleValue() || distance > this.range.getValue().doubleValue()) {
            if (entity == this.target) {

                lastAngle = 9999;
                lastDistance = 9999;
                target = null;


            }
            return;
        }

        if (selectMode.selectedValue().equals("angle")) {
            if (angle <= lastAngle) {
                lastAngle = angle;
                target = entity;
                targetVec = entityRotationVec;
                this.flag = true;

            }
        }
        if (selectMode.selectedValue().equals("distance")) {
            if (distance <= lastDistance) {
                lastDistance = distance;
                target = entity;
                targetVec = entityRotationVec;
                this.flag = true;

            }


        }
    }

    private boolean isTarget(Entity entity) {
        boolean flag = false;
        if (entity instanceof LivingEntity) {
            if (SkyMatrix.mc.player.canSee(entity)) {
                if (entity.isAlive()) {
                    if (this.targetSelect.canBeTarget(entity)) {

                        if (entity instanceof PlayerEntity) {
                            PlayerEntity entity1 = (PlayerEntity) entity;
                            if (entity1.getTeamColorValue() == SkyMatrix.mc.player.getTeamColorValue()) {
                                return false;
                            }
                            flag = antiBot.isPlayer(entity.getUuid().toString());


                        } else {
                            flag = true;
                        }

                    }
                    if (flag) {
                        if (this.invisible.isValue()) {
                            if (entity.isInvisible()) {
                                flag = false;
                            }
                        }
                    }
                }
            } else {
                if (entity == this.target) {

                    lastAngle = 9999;
                    lastDistance = 9999;
                    target = null;


                }
            }
        }

        return flag;
    }


    private EntityHitResult entityHitResult;
    int cd;

    @EventTarget
    public void onRender(WorldRenderEvent e) {
//
//        if (target == null || !target.isAlive() || antiBot.isBot(target.getUuid().toString()) || target.getWorld() != SkyMatrix.mc.world) {
//            lastAngle = 9999;
//            lastDistance = 9999;
//            target = null;
//            smoothRotation.enable();
//            return;
//        }
//        RenderSystem.disableDepthTest();
//        RenderSystem.enableBlend();
//        RenderUtils.translateView(e.getMatrixStack());
//        RenderUtils.translatePos(e.getMatrixStack(), target.getEyePos());
//        RenderUtils.setColor(new Color(0, 255, 220, 70));
//        RenderUtils.drawSolidBox(new Box(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5), e.getMatrixStack());
//        e.getMatrixStack().pop();
//        RenderSystem.disableBlend();
//        RenderSystem.enableDepthTest();
//        EntityHitResult entityHitResult = RaycastUtils.raycast(this.range.getValue().doubleValue(), e.getTickDelta());
//
//        if (entityHitResult != null && entityHitResult.getEntity() == this.target) {
//            smoothRotation.disable();
//        } else {
//            smoothRotation.enable();
//        }
//        if (SkyMatrix.mc.player.fishHook != null) {
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
