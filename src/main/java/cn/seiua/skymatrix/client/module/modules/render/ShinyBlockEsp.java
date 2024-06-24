package cn.seiua.skymatrix.client.module.modules.render;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.ConfigManager;
import cn.seiua.skymatrix.client.HypixelWay;
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Event
@Sign(sign = Signs.FREE)
@SModule(name = "ShinyBlockEsp", category = "render")
public class ShinyBlockEsp implements IToggle {

//Objects.hash(chunkX, chunkZ);

    public boolean tempDisable;

    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(Arrays.asList(), ReflectUtils.getModuleName(this));
    @Value(name = "Color")
    private ColorHolder colorHolder = new ColorHolder(new Color(255, 184, 0, 84));
    @Value(name = "range")
    private DoubleValueSlider range = new DoubleValueSlider(6, 50, 0, 120, 0.5);
    private TickTimer tickTimer = TickTimer.build(2, this::clear);
    @Use
    private ConfigManager configManager;
    @Use
    private HypixelWay way;

    @Init
    public void init() {
        configManager.addReloadCallbacks(this::onConfig);
    }

    private HashSet<BlockTarget> renderList = new HashSet<>();
    private HashSet<BlockPos> temp = new HashSet<>();
    private HashSet<BlockPos> temp1 = new HashSet<>();

    public void onConfig() {
        if (SkyMatrix.mc.world != null) {
            this.renderList = new HashSet<>();
            SkyMatrix.mc.worldRenderer.reload();

        }
    }

    @EventTarget
    public void onBlock(ServerPacketEvent e) {
        if (!this.way.isIn("The End")) return;
        Packet packet = e.getPacket();
        if (packet instanceof ParticleS2CPacket) {
            ParticleS2CPacket p = (ParticleS2CPacket) packet;
            String name = p.getParameters().asString();

            if (name.contains("minecraft:witch")) {

                temp.add(new BlockPos((int) Math.round(p.getX()), (int) Math.round(p.getY()), (int) Math.round(p.getZ())));


            }
            if (name.contains("minecraft:portal")) {

                temp1.add(new BlockPos((int) Math.round(p.getX()), (int) Math.round(p.getY()), (int) Math.round(p.getZ())));


            }
        }


    }

    private void clear() {
        List<BlockTarget> temp = new ArrayList<>(renderList);
        for (BlockTarget target : temp) {
            if (target == null) continue;
            assert SkyMatrix.mc.world != null;
            if (!(SkyMatrix.mc.world.getFluidState(target.getPos()).getFluid() instanceof LavaFluid)) {
                renderList.remove(target);
            }
        }

    }

    int tick;

    @EventTarget
    public void onTick(ClientTickEvent e) {
        if (!this.way.isIn("The End")) return;
        tick++;
        if (tick % 20 == 0) {
            this.renderList = new HashSet<>();

            for (BlockPos blockPos : temp) {
                if (temp1.contains(blockPos)) {

                    renderList.add(new BlockTarget(blockPos, this.colorHolder::geColor));
                }
            }
            temp1.clear();
            temp.clear();
        }
    }

    @EventTarget
    public void onWorldChange(WorldChangeEvent e) {
        this.renderList = new HashSet<>();
    }

    public boolean isTarget(BlockPos blockPos) {

        assert SkyMatrix.mc.world != null;
        return SkyMatrix.mc.world.getBlockState(blockPos).toString().contains(":lava");
    }

    @EventTarget
    public void onRender(WorldRenderEvent e) {
        if (!this.way.isIn("The End")) return;
//        e.getMatrixStack().push();
        RenderSystem.disableDepthTest();
        RenderUtils.translateView(e.getMatrixStack());
        HashSet<BlockTarget> render = new HashSet<>(renderList);
        LivingEntity player = SkyMatrix.mc.player;
        for (BlockTarget blockTarget : render) {

            assert player != null;
            double v = Math.sqrt(Math.pow(blockTarget.getPos().getX() - player.getX(), 2) + Math.pow(blockTarget.getPos().getZ() - player.getZ(), 2));
            if (!(v <= range.maxValue().doubleValue() && v >= range.minValue().doubleValue())) {
                continue;
            }
            blockTarget.render(e.getMatrixStack(), e.getTickDelta());
        }
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
//        e.getMatrixStack().pop();
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
