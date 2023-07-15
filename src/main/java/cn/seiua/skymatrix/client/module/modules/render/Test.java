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
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.ColorHolder;
import cn.seiua.skymatrix.config.option.DoubleValueSlider;
import cn.seiua.skymatrix.config.option.KeyBind;
import cn.seiua.skymatrix.config.option.SingleChoice;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.event.events.FluidRenderEvent;
import cn.seiua.skymatrix.event.events.WorldChangeEvent;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import cn.seiua.skymatrix.gui.Icons;
import cn.seiua.skymatrix.render.BlockLocTarget;
import cn.seiua.skymatrix.render.BlockTarget;
import cn.seiua.skymatrix.utils.ReflectUtils;
import cn.seiua.skymatrix.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Event
@Sign(sign = Signs.FREE)
@SModule(name = "测试", category = "render")
public class Test implements IToggle {

//Objects.hash(chunkX, chunkZ);

    public boolean tempDisable;

    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(Arrays.asList(), ReflectUtils.getModuleName(this));
    @Value(name = "你好")
    private ColorHolder colorHolder = new ColorHolder(new Color(255, 184, 0, 84));
    @Value(name = "------")
    private DoubleValueSlider range = new DoubleValueSlider(6, 50, 0, 120, 0.5);
    @Value(name = "你好你好")
    private SingleChoice<String> mode = new SingleChoice(List.of("worm", "custom"), Icons.MODE);

    @Use
    private ConfigManager configManager;

    @Init
    public void init() {
        configManager.addReloadCallbacks(this::onConfig);
    }

    private HashSet<BlockTarget> renderList = new HashSet<>();

    public void onConfig() {

    }

    @EventTarget
    public void onBlock(FluidRenderEvent e) {


    }

    private void clear() {

    }

    @EventTarget
    public void onTick(ClientTickEvent e) {

    }

    @EventTarget
    public void onWorldChange(WorldChangeEvent e) {
        this.renderList = new HashSet<>();
    }

    public boolean isTarget(BlockPos blockPos) {

        return false;
    }

    @EventTarget
    public void onRender(WorldRenderEvent e) {
        RenderUtils.translateView(e.getMatrixStack());
        RenderSystem.disableDepthTest();
        e.getMatrixStack().push();
        BlockPos blockPos1 = new BlockPos(-14, -50, 22);
        BlockPos blockPos2 = new BlockPos(-10, -50, 32);
        BlockLocTarget blockLocTarget = new BlockLocTarget(blockPos1, this.colorHolder::geColor);
        blockLocTarget.render(e.getMatrixStack(), e.getTickDelta());
        blockLocTarget = new BlockLocTarget(blockPos2, this.colorHolder::geColor);
        blockLocTarget.render(e.getMatrixStack(), e.getTickDelta());


        RenderUtils.drawLine(e.getMatrixStack(), blockPos2, blockPos1);
        e.getMatrixStack().pop();

        RenderSystem.enableDepthTest();

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
