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
import cn.seiua.skymatrix.client.rotation.RotationFaker;
import cn.seiua.skymatrix.client.rotation.SmoothRotation;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.KeyBind;
import cn.seiua.skymatrix.config.option.SingleChoice;
import cn.seiua.skymatrix.config.option.ValueSlider;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.utils.MathUtils;
import cn.seiua.skymatrix.utils.ReflectUtils;
import cn.seiua.skymatrix.utils.RotationUtils;
import cn.seiua.skymatrix.utils.SkyBlockUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.List;

@Event
@Sign(sign = Signs.FREE)
@SModule(name = "shortBowAim", category = "combat")
public class ShortBowAim implements IToggle {
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

    private boolean flag;

    int tick;

    @EventTarget
    public void onTick(ClientTickEvent e) {
        if (way.isSkyblock()) {
            Vec3d plauerRotationVec3Server = rotationFaker.getServerRotationVec3d();
            Vec3d plauerRotationVec3 = SkyMatrix.mc.player.getRotationVecClient();
            Vec3d plauerPosVec3 = SkyMatrix.mc.player.getCameraPosVec(1.0f);
            boolean flag = false;
            if ((SkyMatrix.mc.currentScreen == null && (SkyMatrix.mc.options.attackKey.isPressed() || SkyMatrix.mc.options.useKey.isPressed()))) {
                tick++;
                if (tick < 4) return;
                assert SkyMatrix.mc.player != null;
                String type = SkyBlockUtils.getItemType(SkyMatrix.mc.player.getInventory().getMainHandStack());
                assert type != null;
                if ((type.equals("BOW"))) {
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
                                    double angle = MathUtils.calculateAngle(plauerRotationVec3Server, entityRotationVec);
                                    double angle1 = MathUtils.calculateAngle(plauerRotationVec3, entityRotationVec);
//                    System.out.println(angle);
                                    double distance = entityVec.distanceTo(plauerPosVec3);
                                    if (angle1 > this.angle.getValue().doubleValue()) continue;
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
                    }
                    target = entiti;
                    if (flag) {
                        LivingEntity entityf = (LivingEntity) target;
                        entityf.setStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20, 3, true, true), null);
                        smoothRotation.smoothLook(RotationUtils.getNeededRotations(target.getCameraPosVec(1.0f)), speed.getValue().floatValue(), null, false);
                    }
                }
            } else {
                tick = 0;
            }
            if (!flag && this.flag) {
                if (this.smoothRotation.running) {
                    smoothRotation.smoothLook(RotationUtils.toRotation(plauerRotationVec3), 2, null, false);
                    this.flag = false;
                }
            }
        }
    }

    public boolean canSee(Entity target, double distance) {

        return false;
    }

    @Override
    public void disable() {
        client.sendDebugMessage(Text.of("test"));
    }

    @Override
    public void enable() {

    }
}
