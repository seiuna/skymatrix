package cn.seiua.skymatrix.client.module.modules.render;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.ConfigManager;
import cn.seiua.skymatrix.client.HypixelWay;
import cn.seiua.skymatrix.client.IToggle;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.*;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.event.events.ServerPacketEvent;
import cn.seiua.skymatrix.event.events.WorldChangeEvent;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import cn.seiua.skymatrix.render.BlockTarget;
import cn.seiua.skymatrix.utils.ReflectUtils;
import cn.seiua.skymatrix.utils.RenderUtils;
import cn.seiua.skymatrix.utils.TickTimer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Event
@Sign(sign = Signs.FREE)
@SModule(name = "FairySoulEsp", category = "render")
public class FairyEsp implements IToggle {

//Objects.hash(chunkX, chunkZ);

    public boolean tempDisable;

    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(Arrays.asList(), ReflectUtils.getModuleName(this));

    private final ColorHolder colorHolder1 = new ColorHolder(new Color(127, 225, 255, 255));

    private final ColorHolder colorHolder = new ColorHolder(new Color(253, 121, 168, 255));
    @Value(name = "range")
    private DoubleValueSlider range = new DoubleValueSlider(0, 100, 0, 200, 0.5);
    @Value(name = "aura")
    private ToggleSwitch aura = new ToggleSwitch(false);
    @Value(name = "map")
    private ValueHolder<Map<String, Boolean>> valueHolder = new ValueHolder<>(new HashMap<>());
    private TickTimer tickTimer = TickTimer.build(80, this::clear);
    @Use
    private ConfigManager configManager;
    @Use
    private HypixelWay way;

    private HashSet<BlockTarget> renderList = new HashSet<>();
    private HashSet<BlockPos> temp = new HashSet<>();
    private HashSet<BlockPos> temp1 = new HashSet<>();

    @EventTarget
    public void onBlock(WorldChangeEvent e) {
        this.renderList.clear();

    }

    private void clear() {
        this.renderList = new HashSet<>();
    }

    int tick;
    private Object last;

    @EventTarget
    public void onTick(ClientTickEvent e) {
        if (tickTimer != null) {
            tickTimer.update();
        }
        boolean flag = true;
        if (tick++ % 20 == 0) {
            for (Entity entity : SkyMatrix.mc.world.getEntities()) {
                if (entity instanceof ArmorStandEntity) {
                    ArmorStandEntity entity1 = (ArmorStandEntity) entity;
                    if (isTarget(entity1) && aura.isValue()) {
                        if (SkyMatrix.mc.player.getEyePos().distanceTo(entity.getPos().add(0, 2, 0)) < 3.5) {
                            if (!this.valueHolder.value.getOrDefault(entity.getBlockPos().hashCode() + HypixelWay.getInstance().way(), false)) {
                                SkyMatrix.mc.interactionManager.attackEntity(SkyMatrix.mc.player, entity1);
                                SkyMatrix.mc.player.swingHand(Hand.MAIN_HAND);
                                this.last = entity1;
                                flag = false;
                            }

                        }
                    }

                }
            }
        }
        if (flag && SkyMatrix.mc.targetedEntity instanceof ArmorStandEntity) {
            if (isTarget((ArmorStandEntity) SkyMatrix.mc.targetedEntity)) {
                last = SkyMatrix.mc.targetedEntity;
            }

        }
    }

    @EventTarget
    public void onWorldChange(WorldChangeEvent e) {
        tickTimer.reset();
    }

    @EventTarget
    public void onPacket(ServerPacketEvent e) {
        if (e.getPacket() instanceof GameMessageS2CPacket) {
            GameMessageS2CPacket eventPacket = (GameMessageS2CPacket) e.getPacket();
            Entity entity = (Entity) this.last;
            if (entity != null) {
                if (eventPacket.content().getString().contains("found") && eventPacket.content().getString().contains("Fairy Soul")) {
                    this.valueHolder.value.put(entity.getBlockPos().hashCode() + HypixelWay.getInstance().way(), true);
                    this.renderList.remove(new BlockTarget(entity.getBlockPos().add(0, 2, 0), this.colorHolder::geColor));
                    this.renderList.add(new BlockTarget(entity.getBlockPos().add(0, 2, 0), this.colorHolder1::geColor));
                }
            }

        }
    }

    public boolean isTarget(ArmorStandEntity entity) {
        int i = 0;
        for (ItemStack itemStack : entity.getArmorItems()) {
            i++;
            if (i == 4) {
                if(itemStack == null) continue;
                if (itemStack != null && itemStack.getComponents() != null) {
                    if (itemStack.getComponents().toString().contains("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjk2OTIzYWQyNDczMTAwMDdmNmFlNWQzMjZkODQ3YWQ1Mzg2NGNmMTZjMzU2NWExODFkYzhlNmIyMGJlMjM4NyJ9fX0=")) {
                        if (this.valueHolder.value.getOrDefault(entity.getBlockPos().hashCode() + HypixelWay.getInstance().way(), false)) {
                            this.renderList.add(new BlockTarget(entity.getBlockPos().add(0, 2, 0), this.colorHolder1::geColor));
                        } else {
                            this.renderList.add(new BlockTarget(entity.getBlockPos().add(0, 2, 0), this.colorHolder::geColor));

                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @EventTarget
    public void onRender(WorldRenderEvent e) {
//
////        e.getMatrixStack().push();
//        RenderSystem.disableDepthTest();
//        RenderUtils.translateView(e.getMatrixStack());
        HashSet<BlockTarget> render = new HashSet<>(renderList);
        LivingEntity player = SkyMatrix.mc.player;
        for (BlockTarget blockTarget : render) {

            assert player != null;
            double v = Math.sqrt(Math.pow(blockTarget.getPos().getX() - player.getX(), 2) + Math.pow(blockTarget.getPos().getZ() - player.getZ(), 2));
            blockTarget.render(e.getMatrixStack(), e.getTickDelta());
        }
//        RenderSystem.disableBlend();
//        RenderSystem.enableDepthTest();
////        e.getMatrixStack().pop();
    }

    @Override
    public void disable() {
        this.renderList = null;
    }

    @Override
    public void enable() {
        SkyMatrix.mc.worldRenderer.reload();
        this.renderList = new HashSet<>();
    }
}
