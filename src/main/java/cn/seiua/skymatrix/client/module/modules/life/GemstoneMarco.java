package cn.seiua.skymatrix.client.module.modules.life;

import cn.seiua.skymatrix.client.Client;
import cn.seiua.skymatrix.client.IToggle;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.module.ModuleManager;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.client.module.modules.life.test.scripts.RootScript;
import cn.seiua.skymatrix.client.rotation.SmoothRotation;
import cn.seiua.skymatrix.client.waypoint.WaypointEntity;
import cn.seiua.skymatrix.client.waypoint.WaypointGroupEntity;
import cn.seiua.skymatrix.config.Desc;
import cn.seiua.skymatrix.config.Hide;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.*;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.*;
import cn.seiua.skymatrix.gui.Theme;
import cn.seiua.skymatrix.message.Message;
import cn.seiua.skymatrix.message.MessageBuilder;
import cn.seiua.skymatrix.render.BlockLocTarget;
import cn.seiua.skymatrix.utils.ReflectUtils;
import cn.seiua.skymatrix.utils.RenderUtilsV2;
import cn.seiua.skymatrix.utils.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static cn.seiua.skymatrix.SkyMatrix.mc;

@Event
@Sign(sign = Signs.BETA)
@SModule(name = "GemstoneMarco", category = "life", disable = true)
public class GemstoneMarco implements IToggle {
    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(Arrays.asList(), ReflectUtils.getModuleName(this));
    @Value(name = "waypoint")
    @Sign(sign = Signs.BETA)
    @Desc("一个路径点，请以FM开头")
    WaypointSelect waypointSelect = new WaypointSelect(null, "CM_");
    private int index;
    private Message message = MessageBuilder.build("CrystalMarco");
    private  RootScript rootScript;
    //    private ArrayList<BlockPos> blackList = new ArrayList<>();
    @Value(name = "aotv", desc = "你的aotv")
    @Sign(sign = Signs.BETA)
    SkyblockItemSelect aotv = new SkyblockItemSelect("", false, Selector::bestAote, Filter::aote);
    @Value(name = "tool", desc = "你的挖矿工具")
    @Sign(sign = Signs.BETA)
    SkyblockItemSelect tool = new SkyblockItemSelect("", false, Selector::bestMiningTool, Filter::miningTool);
    @Value(name = "weapon", desc = "你的武器")
    @Sign(sign = Signs.BETA)
    SkyblockItemSelect weapon = new SkyblockItemSelect("", false, null, Filter::weaponFilter);
    @Value(name = "ignore block")
    @Sign(sign = Signs.BETA)
    ToggleSwitch ignore = new ToggleSwitch(false);

    @Value(name = "escape", desc = "逃逸")
    ToggleSwitch escape = new ToggleSwitch(false);
    @Value(name = "escapeLevel", desc = "level")    @Sign(sign = Signs.BETA)
    @Hide(following = "escape")
    ValueSlider level = new ValueSlider(4, 0, 4, 1);
    @Value(name = "rotation")    @Sign(sign = Signs.BETA)
    @Hide(following = "escape")
    ToggleSwitch rotationa = new ToggleSwitch(true);
    @Value(name = "closing")    @Sign(sign = Signs.BETA)
    @Hide(following = "escape")
    ToggleSwitch closing = new ToggleSwitch(true);
    @Value(name = "look")    @Sign(sign = Signs.BETA)
    @Hide(following = "escape")
    ToggleSwitch look = new ToggleSwitch(true);
    @Value(name = "breakNode")    @Sign(sign = Signs.BETA)
    @Hide(following = "escape")
    ToggleSwitch breakNode = new ToggleSwitch(true);
    @Value(name = "entity")
    @Sign(sign = Signs.BETA)
    @Hide(following = "escape")
    ToggleSwitch entity = new ToggleSwitch(true);
    @Use
    MiningHelperV2 miningHelper;
    @Use
    SmoothRotation rotation;
    @Value(name = "already")
    public KeyBind already = new KeyBind("already",Arrays.asList(GLFW.GLFW_KEY_H),this::already);
    private boolean start = false;
    private String status;
    private BlockPos to;
    private int step;
    private String reason;

    public void already() {
        if(ModuleManager.instance.isEnable("life.GemstoneMarco")){


        if(!status.equals("stop")){
            start=false;
            status="stop";
            message.sendMessage(Text.of("Stop!"));
            mc.options.sneakKey.setPressed(false);
            ModuleManager.instance.disable(this);
            return;
        }
        if(block&&!ignore.isValue()){
            if(t!=null){
                rotation.smoothLook(RotationUtils.getNeededRotationsFix18(t.toCenterPos()),3.4f,null,true);
            }
            message.sendMessage(Text.of("After starting the marco,please clear the block that stands in the way!"));
        }else {
            lock=false;
            message.sendMessage(Text.of("Start!"));
            index=0;
            status="start";
            to=waypointSelect.waypointGroup().getWaypoints().get(0).toBlockPos();
            status="next";
            step=0;
            canUseAbility=true;
            this.aotv.switchTo();
            reason=null;
        }

    }   }
    public boolean escape(String reason){
        if( this.status.equals("stop")){
            return false;
        }
        message.sendMessage(Text.of("Press the key [H] to start again!"));
        this.reason=reason;
        this.status="stop";
        this.message.sendMessage(Text.of(reason));
        ModuleManager.instance.disable(this);

        return true;
    }
    @EventTarget
    public void onPacket(ServerPacketEvent event) {
        if (event.getPacket() instanceof GameMessageS2CPacket) {
            GameMessageS2CPacket eventPacket = (GameMessageS2CPacket) event.getPacket();
                if (eventPacket.content().getString().contains("is now available!")) {
                    this.canUseAbility=true;
                }
        }
    }
    @EventTarget
    public void onAntiCheck(ServerRotationEvent event) {
        if(escape.isValue()&&level.getValue().intValue()>0&&rotationa.isValue()){
            if(status.equals("mining")){
                escape("You are rotated by the server! ");
            }
        }
    }
    @EventTarget
    public void onAntiCheck(LookLockEvent event) {
        if(escape.isValue()&&level.getValue().intValue()>3&&look.isValue()){
            escape("Someone always look at you! "+event.getPlayer().getDisplayName().getString());
        }
    }
    public void doEscape(){
        for (WaypointEntity entity: waypointSelect.waypointGroup().getWaypoints()) {
            if(escape.isValue()&&level.getValue().intValue()>0&&breakNode.isValue()){
                if(mc.world.getBlockState(entity.toBlockPos()).isAir()){
                    BlockPos blockPos = entity.toBlockPos();
                   if(mc.player.getPos().distanceTo(blockPos.toCenterPos())<10.5){
                    escape("Your node["+blockPos.getX()+" "+blockPos.getY()+" "+blockPos.getZ()+"] has been ruined! ");
                    return;
                   }
                }

            }
        }
        if(escape.isValue()&&level.getValue().intValue()>2&&entity.isValue()){
            if(mc.crosshairTarget.getType()==HitResult.Type.ENTITY){
                assert mc.targetedEntity != null;
                escape("You are facing an entity! "+(mc.targetedEntity).getDisplayName().getString());
            }
        }
    }
    @EventTarget
    public void onPacket(ClientPacketEvent event){
    }
    private boolean canUseAbility;
    private WaypointGroupEntity last;
    @EventTarget
    public void onTick(ClientTickEvent e) {
        if( this.last!=null&& this.last!= this.waypointSelect.waypointGroup()){
            for (WaypointEntity entity:  this.last.getWaypoints()) {
                Client.blackList.remove(entity.toBlockPos());
            }
            for (WaypointEntity entity:  this.waypointSelect.waypointGroup().getWaypoints()) {
                Client.blackList.add(entity.toBlockPos());
            }
        }
        this.last=this.waypointSelect.waypointGroup();
        doEscape();
//        Client.sendDebugMessage(Text.of(status));
        if(!status.equals("stop")){
            mc.options.sneakKey.setPressed(true);
            if(status.equals("mining")){
                if(canUseAbility){
                    this.tool.switchTo();
                    mc.interactionManager.interactItem(mc.player, mc.player.getActiveHand());
                    this.canUseAbility=false;
                }
                this.tool.switchTo();
                ModuleManager.instance.enable(this.miningHelper);
                if(!miningHelper.hasTarget(null)){
                    this.status="next";
                    this.step=0;
                    this.to=this.waypointSelect.waypointGroup().getWaypoints().get((this.index+1)%this.waypointSelect.waypointGroup().getWaypoints().size()).toBlockPos();
                    this.index++;
                    this.aotv.switchTo();
                }
            }
            if(status.equals("next")){
                ModuleManager.instance.disable(this.miningHelper);
                BlockPos blockPos1=mc.player.getBlockPos().add(0,-1,0);
                if(step==0){
                    this.rotation.smoothLook(RotationUtils.getNeededRotationsFix18(this.to.toCenterPos().add(0, 0.48, 0)),3.4f,this::rotationReady,false);
                    this.step++;
                    return;
                }
                if(this.step>=2&&this.step<=43){
                    if(blockPos1.equals(this.to)){
                        this.status="mining";
                        this.miningHelper.target=new BlockPos(0,0,0);
                    }else {
                        this.step++;
                    }
                }else if (this.step>43){
                    // 40 tick后还没到达目标点，重新尝试。
                    this.step=0;
                    this.status="next";
                }
            }
        }
    }
    private boolean rotationReady;
    private boolean lock;
    private void rotationReady(){
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        step++;
    }
    private boolean canNext(){
        if(lock){
            return true;
        }
        if(miningHelper.target==null&&start){
            return true;
        }
        return false;
    }
    ArrayList<BlockPos> targets = new ArrayList<>();

    private boolean block;
    private BlockPos t;
    @EventTarget
    public void onRender(WorldRenderEvent e) {

        BlockPos tt = null;
        if(waypointSelect.waypointGroup()!=null){
            block=false;
            WaypointEntity waypointEntity = null;
            boolean isfirst=true;
            for (WaypointEntity entity : waypointSelect.waypointGroup().getWaypoints()) {
                BlockLocTarget blockLocTarget = new BlockLocTarget(entity.toBlockPos(), Theme.getInstance().THEME_UI_SELECTED::geColor);
                if(isfirst){
                    isfirst=false;
                    blockLocTarget = new BlockLocTarget(entity.toBlockPos(), new Color(123, 255, 0, 255)::brighter);
                }
                if(mc.world.getBlockState(entity.toBlockPos()).isAir()){
                    blockLocTarget=new BlockLocTarget(entity.toBlockPos(), Theme.getInstance().THEME_UI_ERROR::geColor);
                }
                blockLocTarget.render(e.getMatrixStack(), e.getTickDelta());
                if(waypointEntity!=null){
                    RenderUtilsV2.renderLine(e.getMatrixStack(),waypointEntity.toBlockPos().toCenterPos(),waypointEntity.toBlockPos().toCenterPos().add(0,2.4,0),Theme.getInstance().THEME_UI_SELECTED.value,2f);
////                    RenderUtils.drawLine(e.getMatrixStack(),waypointEntity.toBlockPos().toCenterPos(),waypointEntity.toBlockPos().toCenterPos().add(0,2.4,0));
                    RenderUtilsV2.renderLine(e.getMatrixStack(),waypointEntity.toBlockPos().toCenterPos().add(0,2.4,0),entity.toBlockPos().toCenterPos().add(0, 0.48, 0),Theme.getInstance().THEME_UI_SELECTED.value,2f);
                    BlockHitResult hitResult = mc.world.raycast(new RaycastContext(
                            waypointEntity.toBlockPos().toCenterPos().add(0,2.4,0),
                            entity.toBlockPos().toCenterPos().add(0, 0.48, 0),
                            RaycastContext.ShapeType.OUTLINE,
                            RaycastContext.FluidHandling.ANY,
                            mc.player));
                    if(hitResult.getType()== HitResult.Type.BLOCK&&!hitResult.getBlockPos().equals(entity.toBlockPos())){
                        if(tt==null){
                            tt=hitResult.getBlockPos();
                        }
                        block=true;
                        BlockLocTarget b = new BlockLocTarget(hitResult.getBlockPos(), Theme.getInstance().THEME_UI_ERROR::geColor);
                        b.render(e.getMatrixStack(), e.getTickDelta());
                    }

                }
                waypointEntity=entity;
            }
            t=tt;
        }

    }

    @Override
    public void enable() {
        for (WaypointEntity entity: waypointSelect.waypointGroup().getWaypoints()) {
            Client.blackList.add(entity.toBlockPos());
        }       miningHelper.rclient=true;
        miningHelper.targets.add("x1x11");
        miningHelper.addFilter(this::filterB);
        miningHelper.addFilter(this::filterP);
        status="stop";
        reason=null;
        message.sendMessage(Text.of("If you are already to go,press the key [H]!"));
        this.index=0;
    }
    private boolean filterP(BlockPos blockPos){

     return true;
    }

    private boolean filterB(Block block){
        String name = block.getName().toString();
        if(name.contains("glass")||name.contains("polished_diorite")||name.contains("light_blue_wool")||name.contains("cyan_terracotta")||name.contains("prismarine'")||name.contains("prismarine_bricks"))
        return true;else return false;
    }
    @Override
    public void disable() {
        for (WaypointEntity entity: waypointSelect.waypointGroup().getWaypoints()) {
            Client.blackList.remove(entity.toBlockPos());
        }
        miningHelper.targets.clear();
        miningHelper.clearFilter();
        ModuleManager.instance.disable(miningHelper);
        mc.options.sneakKey.setPressed(false);
        miningHelper.rclient=false;
        if(reason!=null){
            message.sendMessage(Text.of(reason));
        }
    }
}
