package cn.seiua.skymatrix.client.module.modules.life;

import cn.seiua.skymatrix.client.Client;
import cn.seiua.skymatrix.client.IToggle;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.module.ModuleManager;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.client.rotation.ClientRotation;
import cn.seiua.skymatrix.config.Hide;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.*;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.event.events.GameMessageEvent;
import cn.seiua.skymatrix.event.events.ServerPacketEvent;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import cn.seiua.skymatrix.gui.Theme;
import cn.seiua.skymatrix.utils.MathUtils;
import cn.seiua.skymatrix.utils.ReflectUtils;
import cn.seiua.skymatrix.utils.RenderUtilsV2;
import cn.seiua.skymatrix.utils.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;

import static cn.seiua.skymatrix.SkyMatrix.mc;

@Event
@Sign(sign = Signs.BETA)
@SModule(name = "PowderMiner", category = "life", disable = true)
public class PowderMiner implements IToggle {
    @Use
    private MiningHelperV2 miningHelperV2;
    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(Arrays.asList(), ReflectUtils.getModuleName(this));
    @Value(name = "auto", desc = "")
    @Sign(sign = Signs.BETA)
    ToggleSwitch auto = new ToggleSwitch(false);
    @Value(name = "count", desc = "挖矿角度")
    @Sign(sign = Signs.BETA)
    @Hide(following = "auto")
    ValueSlider count = new ValueSlider(1, 1, 3, 1f);
    @Value(name = "angle mining", desc = "挖矿角度")
    @Sign(sign = Signs.BETA)
    ValueSlider angle = new ValueSlider(180, 0, 180, 0.1f);
    @Value(name = "angle chest", desc = "开箱角度")
    @Sign(sign = Signs.BETA)
    ValueSlider anglec = new ValueSlider(180, 0, 180, 0.1f);
    @Value(name = "instant", desc = "")
    @Sign(sign = Signs.BETA)
    ToggleSwitch instant = new ToggleSwitch(false);
    @Value(name = "tool1", desc = "你的挖矿工具")
    @Sign(sign = Signs.BETA)
    SkyblockItemSelect tool = new SkyblockItemSelect("", false, Selector::bestMiningTool, Filter::miningTool);
    @Value(name = "tool2", desc = "你的挖矿工具")
    @Sign(sign = Signs.BETA)
    SkyblockItemSelect tool2 = new SkyblockItemSelect("", false, Selector::bestMiningTool, Filter::miningTool);

    private BlockPos target = null;
    private BlockPos to = null;

    private Vec3d look = null;

    private int countc;
    @Use
    ClientRotation clientRotation;

    private HashSet<String> blackList = new HashSet<>();
    public void updateNext(){
        to=null;
        Vec3d vec3d = mc.player.getCameraPosVec(1f).add(0,-1,0);
        Vec3d vec3d2 = Vec3d.fromPolar(0, mc.player.getYaw(1f)+40);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * 40, vec3d2.y * 40, vec3d2.z * 40);
        HitResult hitResult =    mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE,RaycastContext.FluidHandling.NONE, mc.player));
        if (hitResult.getType().equals(HitResult.Type.BLOCK)) {
            BlockPos blockPos= ((BlockHitResult) hitResult).getBlockPos();
            to=blockPos;
            Vec3d ddd=to.toCenterPos().subtract(mc.player.getPos());
            ddd=ddd.multiply(1/ddd.length());
            Vec3d ccc=to.toCenterPos().add(-ddd.x*2,0,-ddd.z*2);
            to=new BlockPos((int) ccc.x, (int) ccc.y, (int) ccc.z).add(0,-1,0);
        }


        if(target!=null){
            if(!(mc.world.getBlockState(target).getBlock() instanceof ChestBlock)||target.toCenterPos().distanceTo(mc.player.getEyePos())>3){
                target=null;
                look=null;
            }else {
                if(MathUtils.calculateAngle(mc.player.getRotationVec(1),target.toCenterPos().subtract(mc.player.getEyePos()))>=anglec.getIntValue()){
                    target=null;
                    look=null;
                }
            }

        }
    }
    @EventTarget
    public void onTick(ClientTickEvent e) {
        int range = 4;
        updateNext();
        int c = 0;
        for (int x = -range; x < range; x++) {
            for (int y = -range; y < range; y++) {
                for (int z = -range; z < range; z++) {
                    BlockPos blockPos = mc.player.getBlockPos().add(x, y, z);
                    if (this.blackList.contains(blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ())) {
                        continue;
                    }
                    BlockState blockState = mc.world.getBlockState(blockPos);
                    Block block = blockState.getBlock();
                    if (block instanceof ChestBlock) {
                        if (MathUtils.calculateAngle(mc.player.getRotationVec(1), blockPos.toCenterPos().subtract(mc.player.getEyePos())) > anglec.getIntValue() || blockPos.toCenterPos().distanceTo(mc.player.getEyePos()) > 3) {
                            continue;
                        }
                        if (target == null) {
                            target = blockPos;
                            look = target.toCenterPos();
                        }
                        c++;
                        if (mc.player.getPos().distanceTo(target.toCenterPos()) > mc.player.getPos().distanceTo(blockPos.toCenterPos())) {
                            target = blockPos;
                            if (instant.isValue()) {
                                look = target.toCenterPos();
                            }
                        }
                    }
                }
            }
        }
        countc = c;
        if(mc.options.attackKey.isPressed()||(look!=null&&auto.isValue()&&countc>=count.getIntValue())){
            if(look!=null){
                Client.instance.setKeepBlockBreaking(true);
                tool2.switchTo();
                if (this.instant.isValue()) {
                    if (mc.crosshairTarget instanceof BlockHitResult && mc.crosshairTarget.getType() == BlockHitResult.Type.BLOCK) {
                        BlockHitResult blockHitResult = (BlockHitResult) mc.crosshairTarget;
                        if (blockHitResult.getBlockPos().add(0, 0, 0).equals(target) && mc.world.getBlockState(blockHitResult.getBlockPos()).getBlock() instanceof ChestBlock) {
                            mc.options.useKey.setPressed(true);
                        }
                    } else {
                        mc.options.useKey.setPressed(false);
                    }
                }
                clientRotation.smoothRotation.smoothLook(RotationUtils.getNeededRotationsFix18(look), 2.0f, null, false);

            } else {

            }
        }else {
            tool.switchTo();
            mc.options.useKey.setPressed(false);
        }

    }
    @EventTarget
    public void onMessage(GameMessageEvent e) {
        if (e.getText().contains("CHEST LOCKPICKED")) {
            this.blackList.add(target.getX()+" "+target.getY()+" "+target.getZ());
            this.target = null;
            this.look = null;
        }
    }
    @EventTarget
    public void onRender(WorldRenderEvent e) {
        if(to!=null){
            RenderUtilsV2.renderOutlineBlock(e.getMatrixStack(),to, Theme.getInstance().THEME_UI_SELECTED.value);
        }
        if(target!=null){
            RenderUtilsV2.renderOutlineBlock(e.getMatrixStack(),target, Theme.getInstance().THEME_UI_SELECTED.value);
            if(look!=null){
                RenderUtilsV2.renderSolidBox(e.getMatrixStack(),look,look.add(0.07,0.07,0.07),new Color(255, 218, 55, 255));
            }
        }
    }
    @EventTarget
    public void onPacket(ServerPacketEvent e) {
        Packet packet = e.getPacket();
        if (packet instanceof ParticleS2CPacket) {
            ParticleS2CPacket p = (ParticleS2CPacket) packet;
            Vec3d pos = new Vec3d(p.getX(), p.getY(), p.getZ());
            if(target!=null){
                if(target.toCenterPos().distanceTo(pos)<0.7){
                    look=pos;
                }
            }

        }
    }

    @Override
    public void disable() {
        miningHelperV2.targets.clear();
        ModuleManager.instance.disable(miningHelperV2);
        miningHelperV2.clearFilter();   miningHelperV2.rclient=false;
    }
    private boolean filterP(BlockPos blockPos){


        if(mc.options.attackKey.isPressed()||(look!=null&&auto.isValue()&&countc>count.getIntValue())){
            return false;
        }
        if(MathUtils.calculateAngle(mc.player.getRotationVec(1),blockPos.toCenterPos().subtract(mc.player.getEyePos()))>=angle.getIntValue()){
            return false;
        }

        return  blockPos.getY()>mc.player.getBlockPos().getY()-1&&blockPos.getY()<mc.player.getBlockPos().getY()+3;
    }

    private boolean filterB(Block block){
        return block.getName().toString().contains("block.minecraft.stone");
    }
    @Override
    public void enable() {
        miningHelperV2.addFilter(this::filterB);
        miningHelperV2.addFilter(this::filterP);
        miningHelperV2.rclient=false;
        ModuleManager.instance.enable(miningHelperV2);
    }




}
