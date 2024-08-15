package cn.seiua.skymatrix.client.module.modules.render;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.ConfigManager;
import cn.seiua.skymatrix.client.IToggle;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.Init;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.config.Hide;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.ColorHolder;
import cn.seiua.skymatrix.config.option.DoubleValueSlider;
import cn.seiua.skymatrix.config.option.KeyBind;
import cn.seiua.skymatrix.config.option.SingleChoice;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.*;
import cn.seiua.skymatrix.gui.Icons;
import cn.seiua.skymatrix.render.BlockTarget;
import cn.seiua.skymatrix.utils.ReflectUtils;
import cn.seiua.skymatrix.utils.RenderUtils;
import cn.seiua.skymatrix.utils.TickTimer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.network.packet.Packet;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.spi.CopyOnWrite;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Event
@Sign(sign = Signs.FREE)
@SModule(name = "LavaESP", category = "render")
public class LavaEsp implements IToggle {

//Objects.hash(chunkX, chunkZ);

    public boolean tempDisable;

    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(Arrays.asList(), ReflectUtils.getModuleName(this));
    @Value(name = "Color")
    private ColorHolder colorHolder = new ColorHolder(new Color(255, 184, 0, 84));
    @Value(name = "range")
    private DoubleValueSlider range = new DoubleValueSlider(6, 50, 0, 120, 0.5);
    @Value(name = "mode")
    private SingleChoice<String> mode = new SingleChoice(List.of("worm", "custom"), Icons.MODE);
    @Value(name = "y")
    @Hide(following = "mode", value = "custom")
    private DoubleValueSlider y = new DoubleValueSlider(0, 12, -30, 255, 1.0d);
    private TickTimer tickTimer = TickTimer.build(2, this::clear);
    @Use
    private ConfigManager configManager;

    @Init
    public void init() {
        configManager.addReloadCallbacks(this::onConfig);
    }


    private CopyOnWriteArrayList<BlockPos> renderList = new CopyOnWriteArrayList<>();

    public void onConfig() {
        if (SkyMatrix.mc.world != null) {
            SkyMatrix.mc.worldRenderer.reload();
        }
    }

    @EventTarget
    public void onBlock(FluidRenderEvent e) {
        if (e.getFluidState().getFluid() instanceof LavaFluid) {
            FluidState fluidState = e.getFluidState();
                BlockPos blockPos = e.getBlockPos();
                if(!isTarget(blockPos)) return;
                if (mode.selectedValue().equals("worm")) {
                    if (blockPos.getY() >= 64 && blockPos.getX() >= 513 && blockPos.getZ() >= 513) {
                        if(renderList.contains(blockPos.add(0,0,0))) return;
                        renderList.add(blockPos.add(0,0,0));
                        return;
                    }
                }
                if (mode.selectedValue().equals("custom")) {
                    if (blockPos.getY() >= this.y.minValue().intValue() && blockPos.getY() <= this.y.maxValue().intValue()) {
                        if(renderList.contains(blockPos.add(0,0,0))) return;
                        renderList.add(blockPos.add(0,0,0));
                        return;
                    }
                }
        }
        tickTimer.reset();

    }

    private void clear() {
        for (BlockPos target : renderList) {
            if (target == null) continue;
            assert SkyMatrix.mc.world != null;
            if (!(SkyMatrix.mc.world.getFluidState(target).getFluid() instanceof LavaFluid)) {
                renderList.remove(target);
            }
        }

    }

    @EventTarget
    public void onTick(ClientTickEvent e) {
        if (tickTimer != null) {
            tickTimer.update();
        }

    }

    @EventTarget
    public void onWorldChange(WorldChangeEvent e) {
        this.renderList.clear();
    }

    public boolean isTarget(BlockPos blockPos) {
        assert SkyMatrix.mc.world != null;
        return SkyMatrix.mc.world.getBlockState(blockPos).toString().contains(":lava");
    }

    @EventTarget
    public void onRender(WorldRenderEvent e) {
        if (tempDisable) return;
        LivingEntity player = SkyMatrix.mc.player;
        for (BlockPos blockPos : renderList) {
            assert player != null;
            double v = Math.sqrt(Math.pow(blockPos.getX() - player.getX(), 2) + Math.pow(blockPos.getZ() - player.getZ(), 2));
            if (!(v <= range.maxValue().doubleValue() && v >= range.minValue().doubleValue())) {
                continue;
            }
            new BlockTarget(blockPos, colorHolder::geColor).render(e.getMatrixStack(), e.getTickDelta());
        }

//        e.getMatrixStack().pop();
    }

    @Override
    public void disable() {
        this.renderList = null;
    }

    @Override
    public void enable() {
        SkyMatrix.mc.worldRenderer.reload();
        this.renderList.clear();
    }
}
