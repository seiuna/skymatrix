package cn.seiua.skymatrix.client.module.modules.render;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.client.module.modules.combat.AntiBot;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.ColorHolder;
import cn.seiua.skymatrix.config.option.KeyBind;
import cn.seiua.skymatrix.config.option.TargetSelect;
import cn.seiua.skymatrix.config.option.ToggleSwitch;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.RenderLabelEvent;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import cn.seiua.skymatrix.gui.ClickGui;
import cn.seiua.skymatrix.utils.MathUtils;
import cn.seiua.skymatrix.utils.ReflectUtils;
import cn.seiua.skymatrix.utils.RenderUtils;
import cn.seiua.skymatrix.utils.RenderUtilsV2;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.Arrays;

@Event
@Sign(sign = Signs.FREE)
@SModule(name = "nametag", category = "render")
public class NameTag {
    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(Arrays.asList(), ReflectUtils.getModuleName(this));
    @Value(name = "target")
    @Sign(sign = Signs.BETA)
    public TargetSelect select = new TargetSelect();
    @Value(name = "name color")
    @Sign(sign = Signs.BETA)
    public ColorHolder nameColor = new ColorHolder(new Color(0, 255, 220, 255));
    @Value(name = "show health")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch health = new ToggleSwitch(true);
    @Value(name = "self")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch self = new ToggleSwitch(false);

    @Value(name = "show distance")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch distance = new ToggleSwitch(true);

    @Use
    private AntiBot antiBot;

    @EventTarget
    public void onRender(RenderLabelEvent e) {
        Entity entity = e.getEntity();

        if (select.canBeTarget(entity)) {
            if (entity instanceof PlayerEntity) {
                if (!antiBot.isPlayer(entity.getUuidAsString())) return;
            }
            if (!self.isValue()) {
                if (entity.equals(SkyMatrix.mc.player)) return;
            }
            e.setCancelled(true);
        }

    }

    @EventTarget
    public void onRender(WorldRenderEvent e) {


//        RenderUtils.translateView(e.getMatrixStack());
        for (Entity entity : SkyMatrix.mc.world.getEntities()) {
            if (entity instanceof LivingEntity) {
                LivingEntity entity1 = (LivingEntity) entity;
                if (select.canBeTarget(entity1)) {
                    if (entity instanceof PlayerEntity) {
                        if (!antiBot.isPlayer(entity1.getUuidAsString())) continue;
                    }
                    if (!self.isValue()) {
                        if (entity.equals(SkyMatrix.mc.player)) continue;
                    }
                    int i = 1;
                    if (SkyMatrix.mc.options.getPerspective().isFrontView()) {
                        i = -1;
                    }
                    double angle = MathUtils.calculateAngle(SkyMatrix.mc.player.getRotationVec(e.getTickDelta()).multiply(i), entity1.getPos().subtract(SkyMatrix.mc.gameRenderer.getCamera().getPos()));
                    if (angle > 90) continue;

                    float scale = 0.008F;
                    double distance = SkyMatrix.mc.player.getCameraPosVec(e.getTickDelta()).distanceTo(entity1.getCameraPosVec(e.getTickDelta()));
                    Color color = this.nameColor.geColor();
                    ClickGui.fontRenderer24.setColors(entity1.isSneaking() ? color.darker() : color, new Color(255, 89, 51, 255), new Color(169, 169, 169, 255));
                    String text = entity1.getDisplayName().getString();
                                        if (this.health.isValue()) {
                        text += "§[" + entity1.getHealth() + "]";
                    }
                    if (this.distance.isValue()) {
                        text += "§[" + (int) distance + "]";
                    }

                    Vec3d vec3d=new Vec3d(
                            MathHelper.lerp((double)e.getTickDelta(), entity.lastRenderX, entity.getX()),
                            MathHelper.lerp((double)e.getTickDelta(), entity.lastRenderY, entity.getY())+2.2,
                            MathHelper.lerp((double)e.getTickDelta(), entity.lastRenderZ, entity.getZ())

                    );
                    RenderUtilsV2.renderText(e.getMatrixStack(), ClickGui.fontRenderer24,text,vec3d , new Color(1,1,1,255),e.getTickDelta());
                }
            }



        }


    }

}
