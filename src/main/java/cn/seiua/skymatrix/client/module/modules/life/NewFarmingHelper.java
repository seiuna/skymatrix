package cn.seiua.skymatrix.client.module.modules.life;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.HypixelWay;
import cn.seiua.skymatrix.client.IToggle;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.module.ModuleManager;
import cn.seiua.skymatrix.client.module.PreCheck;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.client.module.modules.ModuleTools;
import cn.seiua.skymatrix.client.module.modules.hud.Farming;
import cn.seiua.skymatrix.client.module.modules.render.BanInfo;
import cn.seiua.skymatrix.client.rotation.Rotation;
import cn.seiua.skymatrix.client.rotation.RotationFaker;
import cn.seiua.skymatrix.client.rotation.SmoothRotation;
import cn.seiua.skymatrix.client.waypoint.WPProcess;
import cn.seiua.skymatrix.client.waypoint.Waypoint;
import cn.seiua.skymatrix.client.waypoint.WaypointEntity;
import cn.seiua.skymatrix.client.waypoint.WaypointGroupEntity;
import cn.seiua.skymatrix.config.Desc;
import cn.seiua.skymatrix.config.Hide;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.*;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.*;
import cn.seiua.skymatrix.gui.ClickGui;
import cn.seiua.skymatrix.gui.Icons;
import cn.seiua.skymatrix.hud.ClientHud;
import cn.seiua.skymatrix.hud.Hud;
import cn.seiua.skymatrix.message.Message;
import cn.seiua.skymatrix.message.MessageBuilder;
import cn.seiua.skymatrix.utils.*;
import net.minecraft.block.BlockState;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.*;

@Event
@Sign(sign = Signs.BETA)
@SModule(name = "farmingHelper", category = "life", disable = true)
public class NewFarmingHelper implements IToggle, Hud, PreCheck, BanInfo.BanWaveCallBack {

    @Use
    RotationFaker rotationFaker;
    @Use
    Waypoint waypoint;
    @Use
    SmoothRotation smoothRotation;

    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(List.of(), ReflectUtils.getModuleName(this));
    @Value(name = "farm hud")
    @Desc("将在未来删除")
    public ClientHud clientHud = new ClientHud(100, 100, false, this);

    @Value(name = "mode")
    @Desc("可选 legit normal manual")
    SingleChoice<String> mode = new SingleChoice<>(List.of("legit", "normal", "manual"), Icons.MODE);
    @Value(name = "angle")
    @Hide(following = "mode", value = "normal&manual")
    @Sign(sign = Signs.BETA)
    @Desc("可以破坏的最大角度仅在normal和manual中可用")
    ValueSlider angle = new ValueSlider(50, 0, 180, 0.1f);
    @Value(name = "range")
    @Hide(following = "mode", value = "normal&manual")
    @Sign(sign = Signs.BETA)
    @Desc("可以破坏的最大距离仅在normal和manual中可用")
    ValueSlider range = new ValueSlider(4, 0, 6, 1);
    @Value(name = "lock slot")
    @Sign(sign = Signs.BETA)
    @Desc("是否锁定你的物品栏。")
    ToggleSwitch lockSlot = new ToggleSwitch(false);
    @Value(name = "crops")
    @Hide(following = "mode", value = "normal&manual")
    @Sign(sign = Signs.BETA)
    @Desc("在normal和manual模式中可以破坏的农作物.")
    ToggleSwitch crops = new ToggleSwitch(false);
    @Value(name = "blocks")
    @Hide(following = "crops")
    @Sign(sign = Signs.BETA)
    MultipleChoice blocks = new MultipleChoice(SkyBlockUtils.getAllCrop(), Icons.ORE);
    @Value(name = "waypoint")
    @Hide(following = "mode", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    @Desc("一个路径点，请以FM开头")
    WaypointSelect waypointName = new WaypointSelect(null, "FM");
    @Value(name = "anti afk")
    @Hide(following = "mode&", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    @Desc("afk")
    ToggleSwitch afk = new ToggleSwitch(false);
    @Value(name = "delay")
    @Hide(following = "mode&anti afk", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    @Desc("afk")
    ValueSlider delay = new ValueSlider(67, 0, 600, 1);
    @Value(name = "auto pause")
    @Hide(following = "mode", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    @Desc("是否启用自动暂停")
    ToggleSwitch pause = new ToggleSwitch(false);
    @Value(name = "ban wave")
    @Hide(following = "mode&auto pause", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    @Desc("是否在ban wave期间暂停farminghelper")
    ToggleSwitch banWave = new ToggleSwitch(false);
    @Value(name = "match")
    @Hide(following = "auto pause&mode", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    @Desc("是否在农业竞赛暂停farminghelper")
    ToggleSwitch match = new ToggleSwitch(false);
    @Value(name = "timer")
    @Hide(following = "auto pause&mode", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    @Desc("可以让farminghelper工作在指定的时间段。")
    ToggleSwitch timer = new ToggleSwitch(false);
    @Value(name = "alarm")
    @Hide(following = "auto pause&mode&timer", value = "legit&normal")
    @Sign(sign = Signs.PRO)
    ValueInput alarm = new ValueInput("", Icons.MODE, "00:00-12:00");
    @Value(name = "extra packet")
    @Hide(following = "mode", value = "legit")
    @Sign(sign = Signs.BETA)
    @Desc("当使用legit模式时，是否发送额外的数据包。效果上是一次挖三排")
    ToggleSwitch extra = new ToggleSwitch(false);
    @Value(name = "when over")
    @Hide(following = "mode", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    @Desc("当完成一轮循环后是否做些什么")
    ToggleSwitch over = new ToggleSwitch(false);
    //    @Value(name = "actions")
//    @Hide(following = "mode&when over", value = "legit&normal")
//    @Sign(sign = Signs.BETA)
    SingleChoice<String> actions = new SingleChoice<>(List.of("command", "do visitor", "sell to market", "warp out", "moving back"));

    @Value(name = "cmd1")
    @Hide(following = "when over&mode", value = "legit&normal")
    @Sign(sign = Signs.PRO)
    ValueInput overCmd = new ValueInput("warp garden", Icons.CMD);

    @Value(name = "keep world")
    @Hide(following = "mode", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    @Desc("保证你一直处在garden")
    ToggleSwitch keep = new ToggleSwitch(false);
    @Value(name = "lock view")
    @Hide(following = "mode", value = "legit")
    @Sign(sign = Signs.BETA)
    @Desc("锁定你的视角")
    ToggleSwitch lock = new ToggleSwitch(true);
    @Value(name = "reach")
    @Hide(following = "mode", value = "legit")
    @Sign(sign = Signs.BETA)
    @Desc("将你的最大破坏距离延长 1")
    ToggleSwitch reach = new ToggleSwitch(true);
    @Value(name = "free look")
    @Hide(following = "mode&lock view", value = "legit")
    @Sign(sign = Signs.BETA)
    @Desc("自由视角，建议一直开着否则staff ban")
    ToggleSwitch free = new ToggleSwitch(true);
    @Value(name = "waiting time of next(m)")
    @Hide(following = "mode", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    @Desc("当完成一轮循环后等待多久继续")
    ValueSlider wTimeN = new ValueSlider(4, 0, 6, 1);
    @Value(name = "cmd")
    @Hide(following = "mode", value = "legit&normal")
    @Sign(sign = Signs.PRO)
    ValueInput cmd = new ValueInput("", Icons.CMD);

    @Value(name = "escape")
    @Hide(following = "mode", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    @Desc("是否开启逃逸？")
    ToggleSwitch escape = new ToggleSwitch(false);

    @Value(name = "waiting time(m)")
    @Hide(following = "escape&mode", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    @Desc("当逃逸后等待多久继续")
    ValueSlider wTime = new ValueSlider(0, 0, 6, 1);
    @Value(name = "escapes")
    @Hide(following = "escape&mode", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    MultipleChoice escapeWay = new MultipleChoice(Map.of("slot", false, "fov", false, "location", false), Icons.MODE);
    private final Message message = MessageBuilder.build("farming helper");
    private BlockPos target;
    private Status status = Status.NONE;

    private WPProcess wpProcess = null;

    private LocalTime start;
    private LocalTime end;
    private boolean timeFlag;

    private TickTimer AfkTickTimer = TickTimer.build(this.delay::getIntValue, -1, () -> {
        ModuleTools.execute("iamactuallyhere");
        this.wpProcess.resume1();
        this.status = Status.NONE;
        this.message.sendDebugMessage(Text.of("AFK Over!"));
    });

    @EventTarget
    public void onTick(ClientTickEvent event) {
//        if (System.currentTimeMillis() > 1692026409) {
//            ModuleManager.instance.disable(this);
//        }
        this.update();
        if (!HypixelWay.getInstance().isIn("Garden")) {
            if (this.keep.isValue()) {
                if ((worldChange.getTick() < 0)) {
                    this.step = 1;
                    worldChange.reset();
                }

            }
            if (!this.mode.selectedValue().equals("manual")) {
                return;
            }

        }


        doEscape1();
        if (this.wpProcess != null && this.wpProcess.getIndex() == 1 && !this.wpProcess.isPause()) {
            BaritoneAPI.getSettings().allowSprint.value = true;
            return;
        }
        if (this.status == Status.ESCAPING || this.status == Status.WAiTING) {
            BaritoneAPI.getSettings().allowSprint.value = true;
            return;
        } else {
            SkyMatrix.mc.player
                    .getInventory().selectedSlot = slot;
        }
        if (this.status == Status.NONE) {
            BaritoneAPI.getSettings().allowSprint.value = true;
        }
        if (this.status == Status.DOING) {
            BaritoneAPI.getSettings().allowSprint.value = false;
        }
        if (!canDo()) return;
        switch (this.mode.selectedValue()) {
            case "legit" -> {
                doLegit();

            }
            case "manual", "normal" -> {
                doNormal();
            }
            default -> {

            }
        }
    }

    @EventTarget
    public void onReach(ReachEvent e) {
        if (reach.isValue())
            e.setReach(5.0f);
    }

    @EventTarget
    public void onPacket(ServerPacketEvent event) {

        if (event.getPacket() instanceof GameMessageS2CPacket) {
            GameMessageS2CPacket eventPacket = (GameMessageS2CPacket) event.getPacket();
            if (this.afk.isValue() && eventPacket.content().getString().contains("You have been put to AFK mode!")) {
                this.wpProcess.pause();
                this.AfkTickTimer.reset();
                this.status = Status.AFK;
                this.message.sendDebugMessage(Text.of("AFK!"));

            }
            if (eventPacket.content().getString().contains("Couldn't warp you! Try again later.")) {
                if (this.step == 1) {
                    this.message.sendWarningMessage("warp back failed!");
                    step = 1;
                    tempDisableEscape = true;
                    this.wpProcess.pause();
                    this.status = Status.WAiTING;
                    this.worldChange2 = TickTimer.build(80, this::__WorldChange);

                }
            }
        }


        if (this.mode.selectedValue().equals("manual")) {
            return;
        }
        if (this.status == Status.ESCAPING) return;
        if (this.status == Status.WAiTING) return;
        if (event.getPacket() instanceof UpdateSelectedSlotS2CPacket) {
            UpdateSelectedSlotS2CPacket packet = (UpdateSelectedSlotS2CPacket) event.getPacket();
            assert SkyMatrix.mc.player != null;
            if (packet.getSlot() != SkyMatrix.mc.player.getInventory().selectedSlot) {
                doEscape("slot");
                event.setCancelled(true);

                return;
            }
        }
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            PlayerPositionLookS2CPacket packet = (PlayerPositionLookS2CPacket) event.getPacket();
            if (!packet.getFlags().contains((Object) PositionFlag.X_ROT) || (!packet.getFlags().contains((Object) PositionFlag.Y_ROT))) {

                if (this.escape.isValue()) {

                    if (this.escapeWay.getValue().get("location")) {
                        boolean bl = packet.getFlags().contains((Object) PositionFlag.X);
                        boolean bl2 = packet.getFlags().contains((Object) PositionFlag.Y);
                        boolean bl3 = packet.getFlags().contains((Object) PositionFlag.Z);
                        if ((!bl) || (!bl2) || (!bl3)) {
                            assert SkyMatrix.mc.player != null;
                            if (SkyMatrix.mc.player.getBlockPos().toCenterPos().distanceTo(new Vec3d(packet.getX(), packet.getY(), packet.getZ())) > 10) {
                                this.doEscape("location");

                                return;
                            }
                        }

                    }
                    if (this.escapeWay.getValue().get("fov")) {

                        return;
                    }
                }


            }
        }
    }

    private List<String> strs = List.of("wtf?", "??", "???", "????", "wtf", "你妈死了", "操你妈", "傻逼staff你他妈的要死了啊");
    private OneTickTimer escapeTimer;
    private OneTickTimer escapeTimer1;
    //jump and send
    private OneTickTimer escapeTimer2;

    private OneTickTimer cac = TickTimer.build(20, -1, this::__doEscape);
    private String type;

    private void doEscape(String type) {
        this.type = type;
        cac.reset();
    }

    boolean escapeFlag;

    private void __doEscape() {

        if (tempDisableEscape) return;
        boolean flag = false;

        switch (type) {
            case "slot" -> {
                if (this.escapeWay.getValue().get("slot")) {
                    flag = true;
                    message.sendWarningMessage(Text.of("§bYour §4slot§b has been changed by the server,and someone may be §4watching§b you!"));
                }
            }
            case "location" -> {
                if (this.escapeWay.getValue().get("location")) {
                    flag = true;
                    message.sendWarningMessage(Text.of("§bYour §4location§b has been transformed by the server,and someone may be §4watching§b you!"));
                }
            }
            case "rotation" -> {
                if (this.escapeWay.getValue().get("rotation")) {
                    flag = true;
                    message.sendWarningMessage(Text.of("§bYour §4view§b has been rotated by the server,and someone may be §4watching§b you!"));
                }
            }
            case "block" -> {

                flag = true;
                message.sendWarningMessage(Text.of("§bA §4block§b have been added to the world by the server, and someone may be §4watching§b you!"));

            }
        }
        if (flag) {
            escapeFlag = true;
            tempDisableEscape = true;
            if (wpProcess != null) this.wpProcess.pause();
            this.status = Status.ESCAPING;
            this.message.sendMessage(Text.of("ESCAPING"));
            this.smoothRotation.smoothLook(new Rotation(SkyMatrix.mc.player.getYaw(), SkyMatrix.mc.player.getPitch()), 3, null, false);
            escapeTimer2 = TickTimer.build(60, () -> {
                if (new Random().nextInt() % 2 == 0) {
                    SkyMatrix.mc.options.jumpKey.setPressed(true);
                    escapeTimer.setTick(10);
                } else {
                    Objects.requireNonNull(SkyMatrix.mc.getNetworkHandler()).sendChatMessage(this.strs.get(new Random().nextInt() % this.strs.size()));
                    escapeTimer.setTick(120);
                }
            });

            escapeTimer = TickTimer.build(100, () -> {
                if (SkyMatrix.mc.options.jumpKey.isPressed())
                    SkyMatrix.mc.options.jumpKey.setPressed(false);

                WaypointGroupEntity entity = Waypoint.getInstance().getWaypoints().get("GARDEN_DESK");
                if (entity.getWaypoints().size() == 1) {
                    WaypointEntity entity1 = entity.getWaypoints().get(0);
                    BaritoneAPI.getSettings().allowSprint.value = true;
                    BaritoneAPI.getSettings().considerPotionEffects.value = true;
                    BaritoneAPI.getSettings().avoidance.value = false;
                    BaritoneAPI.getSettings().allowWalkOnBottomSlab.value = true;
                    BaritoneAPI.getSettings().antiCheatCompatibility.value = true;
                    BaritoneAPI.getSettings().allowParkour.value = true;
                    BaritoneAPI.getSettings().allowBreak.value = false;
                    BaritoneAPI.getSettings().allowParkourAscend.value = true;
                    BaritoneAPI.getSettings().renderGoal.value = false;
                    BaritoneAPI.getSettings().allowPlace.value = false;
                    BaritoneAPI.getSettings().renderPath.value = false;
                    BaritoneAPI.getSettings().assumeSafeWalk.value = false;
                    BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalBlock(entity1.getX(), entity1.getY(), entity1.getZ()));
                    this.message.sendMessage(Text.of("Moving to GARDEN_DESK"));
                }

                escapeTimer1 = TickTimer.build(Math.max(this.wTime.getValue().intValue() * 20 * 60, 40), () -> {
                    this.message.sendMessage(Text.of("Escape over"));
                    if (wpProcess != null) this.wpProcess.resume1();
                    this.status = Status.NONE;
                    tempDisableEscape = false;
                    escapeFlag = false;

                });
            });
        }
    }

    private void doEscape1() {
        if (this.mode.selectedValue().equals("manual")) {
            return;
        }
        if (!this.wpProcess.isPause()) {
            if (escapeFlag) {
                SkyMatrix.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2.0f, 10));
                SkyMatrix.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2.0f, 10));
                SkyMatrix.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2.0f, 10));
                SkyMatrix.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2.0f, 10));
                if (BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().isActive()) {
                    escapeTimer1.reset();
                }
            }
        }
    }

    @EventTarget
    private void onBlockChange(BlockChangeEvent event) {
        if (this.mode.selectedValue().equals("manual")) {
            return;
        }
        if (event.getOld().isAir() && !event.getNe().isAir()) {
            if (!event.getNe().canPathfindThrough( NavigationType.LAND)) {
                String name = BlockUtils.getName(event.getNe().getBlock()).replace("minecraft:", "");
                name = SkyBlockUtils.getNameByMapper(name);
                if (!this.blocks.value.containsKey(name)) {
                    this.message.sendMessage(Text.of(String.format("location X %d Y %d Z %d %s ---> %s", event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), BlockUtils.getName(event.getOld().getBlock()), BlockUtils.getName(event.getNe().getBlock()))));
                    this.doEscape("block");
                }

            }

        }

    }

    private void update() {
        if (this.worldChange != null) {
            worldChange.update();
        }
        if (this.AfkTickTimer != null) {
            AfkTickTimer.update();
        }
        if (this.escapeTimer2 != null) {
            escapeTimer2.update();
        }
        if (this.cac != null) {
            cac.update();
        }
        if (this.escapeTimer != null) {
            escapeTimer.update();
        }
        if (this.escapeTimer1 != null) {
            escapeTimer1.update();
        }
        if (this.worldChange2 != null) {
            worldChange2.update();
        }
    }


    private void doNormal() {
        if (this.mode.selectedValue().equals("manual")) {
            if (!SkyMatrix.mc.options.attackKey.isPressed()) {
                this.status = Status.NONE;
                return;
            }
        }

        List<BlockPos> blockPos = getNearbyBlocks();
        int count = 0;
        for (BlockPos bp : blockPos) {
            if (count > 10) break;
            assert SkyMatrix.mc.interactionManager != null;
            SkyMatrix.mc.interactionManager.attackBlock(bp, Direction.UP);
            assert SkyMatrix.mc.player != null;
            SkyMatrix.mc.player.swingHand(Hand.MAIN_HAND);
            count++;
        }
        if (count != 0) {
            this.status = Status.DOING;
        }
    }

    boolean flag1 = false;

    private void doLegit() {

        if (pause.isValue()) {
            if (match.isValue()) {
                if (this.farming.isIn(this.farming.getContext().replace("_", " ").toLowerCase())) {
                    if (!this.wpProcess.isPause()) {
                        flag1 = true;
                        this.wpProcess.pause();
                        this.message.sendMessage(Text.of("FarmingHelper is paused by Jacob's Farming Context.   Target: " + this.farming.getContext()));
                    }

                    return;
                } else {
                    if (flag1) {
                        this.wpProcess.resume1();
                        flag1 = false;
                    }

                }
            }
            if (this.timer.isValue()) {
                if (this.start != null && this.end != null) {

                    if (!MathUtils.isTimeInRange(start, end)) {
                        if (!this.wpProcess.isPause()) {
                            this.timeFlag = true;
                            this.wpProcess.pause();
                            this.message.sendMessage(Text.of("FarmingHelper is paused by the timer because it can only work during " + this.alarm.getValue()));
                        }
                        return;
                    } else {
                        if (timeFlag) {
                            this.wpProcess.resume1();
                            timeFlag = false;
                        }

                    }

                }
            }
        }


        boolean flag = false;
        if (SkyMatrix.mc.targetedEntity != null) {
            ModuleManager.instance.disable(this);
            this.message.sendMessage(Text.of("entity block!"));

        }
        doLockView();
        BlockPos blockPos = ModuleTools.getTarget();
        if (blockPos != null && this.isTarget(blockPos)) {
            SkyMatrix.mc.doAttack();
        }
        if (extra.isValue()) {
            ModuleTools.updataTarget();
            blockPos = ModuleTools.getTarget();
            if (blockPos != null && this.isTarget(blockPos)) {
                SkyMatrix.mc.doAttack();
            }
            ModuleTools.updataTarget();
            blockPos = ModuleTools.getTarget();
            if (blockPos != null && this.isTarget(blockPos)) {
                SkyMatrix.mc.doAttack();
            }
            ModuleTools.updataTarget();
            blockPos = ModuleTools.getTarget();
            if (blockPos != null && this.isTarget(blockPos)) {
                SkyMatrix.mc.doAttack();
            }
        }


        Vec3d vec3d = Vec3d.fromPolar(new Vec2f(rotationFaker.getServerPitch(), rotationFaker.getServerYaw()));
        SkyMatrix.mc.player.getEyePos().add(vec3d.multiply(1));
        SkyMatrix.mc.player.getEyePos().add(vec3d.multiply(2));
        SkyMatrix.mc.player.getEyePos().add(vec3d.multiply(3));
        SkyMatrix.mc.player.getEyePos().add(vec3d.multiply(4));

//        if (System.currentTimeMillis() % 120 == 0) {
//            ModuleTools.updataTarget();
//            blockPos = ModuleTools.getTarget();
//            if (blockPos != null && this.isTarget(blockPos)) {
//                SkyMatrix.mc.doAttack();
//                flag = true;
//            }
//        }

        this.status = Status.DOING;

    }

    private boolean tempDisableEscape;
    int step;

    @EventTarget
    public void onWorldChange(WorldChangeEvent e) {


        switch (this.mode.selectedValue()) {
            case "legit", "normal" -> {
                tempDisableEscape = true;
                if (this.keep.isValue()) {
                    if (this.wpProcess != null) {
                        this.wpProcess.pause();
                        this.wpProcess.setIndex(0);
                        this.status = Status.WAiTING;
                        worldChange.reset();
                    }
                } else {
                    ModuleManager.instance.disable(this);

                }
            }

        }
    }    private OneTickTimer worldChange = TickTimer.build(60, () -> {
        this.cmd.execute();
        this.message.sendDebugMessage(Text.of("keep-world step 1"));
        step = 1;
        worldChange2 = TickTimer.build(60, this::__WorldChange);
    });

    private OneTickTimer worldChange2;

    private void __WorldChange() {
        worldChange.setTick(-1);
        tempDisableEscape = false;
        this.wpProcess.resume1();
        this.status = Status.NONE;
        this.message.sendDebugMessage(Text.of("keep-world step 2"));
        step = 2;

    }

    @Override
    public void disable() {
        flag1 = false;
        timeFlag = false;
        banInfo.removeBanWaveCallBack("newfarminghelper");
        if (wpProcess != null) {
            lastIndex = wpProcess.getIndex();
            wpProcess.stopP();
            if (this.mode.selectedValue().equals("legit")) {
                assert SkyMatrix.mc.player != null;
                this.smoothRotation.smoothLook(RotationUtils.toRotation(SkyMatrix.mc.player.getRotationVecClient()), 3.5f, null, false);

            }
        }
        farming.stop();
        this.status = Status.NONE;
        this.escapeTimer = null;
        this.escapeTimer1 = null;
        this.escapeTimer2 = null;
        this.worldChange.setTick(-1);
        this.escapeFlag = false;
        this.tempDisableEscape = false;
    }

    public boolean canDo() {
        assert SkyMatrix.mc.player != null;
        ItemStack is = SkyMatrix.mc.player.getInventory().getMainHandStack();
        String type = SkyBlockUtils.getItemType(is);
        String name = is.getItem().toString().toLowerCase();
        assert SkyMatrix.mc.crosshairTarget != null;
        if (name.contains("air")) return false;
        return (name.contains("pickaxe") || name.contains("axe") || name.contains("shovel") || name.contains("hoe")) || ((type != null) && (type.equals("AXE") || type.equals("DRILL") || type.equals("PICKAXE") || type.equals("SHOVEL")));
    }

    private boolean isTarget(BlockPos bp) {

        String name = BlockUtils.getName(bp).replace("minecraft:", "");
        name = SkyBlockUtils.getNameByMapper(name);
        assert SkyMatrix.mc.world != null;
        BlockState blockState = SkyMatrix.mc.world.getBlockState(bp);
        if (blockState.isAir() || SkyMatrix.mc.world.isWater(bp) || SkyMatrix.mc.world.getFluidState(bp).getFluid().isIn(FluidTags.LAVA))
            return false;
        if (blocks.value.containsKey(name)) {
            if (this.mode.selectedValue().equals("legit")) {
                return true;
            }
            switch (name) {
                case "sugar_cane", "cactus" -> {
                    BlockPos bp1 = bp.down();
                    return SkyMatrix.mc.world.getBlockState(bp1).getBlock() == SkyMatrix.mc.world.getBlockState(bp).getBlock();
                }
                case "nether_wart" -> {
                    int age = SkyMatrix.mc.world.getBlockState(bp).get(IntProperty.of("age", 0, 3));
                    return age == 3;
                }
                case "carrots", "potatoes", "wheat" -> {
                    int age = SkyMatrix.mc.world.getBlockState(bp).get(IntProperty.of("age", 0, 7));
                    return age == 7;
                }
                default -> {
                    return true;
                }
            }
        }
        return false;
    }

    private void doLockView() {
        if (this.lock.isValue())
            this.smoothRotation.smoothLook(new Rotation(yaw, pitch), 2, null, !this.free.isValue());
    }

    private TickTimer cd;

    private void nextCircle() {
        SkyMatrix.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2.0f, 10));
        pitch = spitch;
        yaw = syaw;
        if (this.over.isValue()) {
            this.tempDisableEscape = true;
            this.overCmd.execute();
            this.message.sendMessage(Text.of("over!"));
            cd = TickTimer.build(25, () -> {
                this.tempDisableEscape = false;
            });
        }
    }

    private Boolean nextNode(WaypointEntity entity) {
        if (entity.data != null) {
            if (entity.data.getOrDefault("rotation", "none").equals("switch")) {
                this.yaw += 180;
                this.message.sendDebugMessage(Text.of("switch"));
            }
        }
        return true;
    }

    private int slot;
    private static final int RANGE = 12;

    public List<BlockPos> getNearbyBlocks() {
        List<BlockPos> blockPos = new ArrayList<>();
        assert SkyMatrix.mc.player != null;
        Vec3d playerPos = SkyMatrix.mc.player.getEyePos();
        BlockPos playerBlockpos = new BlockPos((int) playerPos.x, (int) playerPos.y, (int) playerPos.z);
        for (int i = 0; i < RANGE; i++) {
            for (int j = 0; j < RANGE; j++) {
                for (int k = 0; k < RANGE; k++) {
                    BlockPos tp = playerBlockpos.add(i - RANGE / 2, j - RANGE / 2, k - RANGE / 2);
                    if (this.isTarget(tp) && playerPos.distanceTo(tp.toCenterPos()) < this.range.getValue().doubleValue()) {
                        blockPos.add(tp);
                    }
                }
            }
        }
        blockPos.sort((o1, o2) -> (int) (playerPos.distanceTo(o2.toCenterPos()) - playerPos.distanceTo(o1.toCenterPos())));
        return blockPos;
    }


    float yaw;
    float pitch;
    float syaw;
    float spitch;
    private int lastIndex;

    @Override
    public void enable() {

        banInfo.addBanWaveCallBack("newfarminghelper", this);
        switch (this.mode.selectedValue()) {
            case "legit", "normal" -> {
                yaw = this.rotationFaker.getServerYaw();
                pitch = this.rotationFaker.getServerPitch();
                syaw = yaw;
                spitch = pitch;

                slot = SkyMatrix.mc.player
                        .getInventory().selectedSlot;

                wpProcess = new WPProcess(true, this.waypointName.waypointGroup());
                wpProcess.setNext(this::nextNode);
                wpProcess.setNextCircle(this::nextCircle);
//                if (lastIndex >= 0) {
//                    wpProcess.setIndex(lastIndex);
//                }

                wpProcess.start();
                farming.start();

            }
            default -> {

            }
        }
//        if (!ModuleManager.instance.isEnable("render.banInfo")) {
//            this.message.sendMessage(Text.of("如果你不开启 Baninfo 将无法使用banwave暂停等功能"));
//        }
    }

    @Use
    private Farming farming;

    public enum Status {
        NONE, DOING, ESCAPING, WAiTING, AFK
    }

    @Override
    public void draw(MatrixStack matrixStack, float x, float y) {
        ClickGui.fontRenderer20.setColor(Color.WHITE);
        ClickGui.fontRenderer20.resetCenteredH();
        ClickGui.fontRenderer20.resetCenteredV();
        float startX = x + 15;
        float startY = y + 12;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, String.format("状态 %s", this.status));

    }

    @Override
    public int getHudWidth() {
        return 300;
    }

    @Override
    public int getHudHeight() {
        return 300;
    }

    @Use
    BanInfo banInfo;

    @Override
    public void check() throws RuntimeException {
//        if (banInfo.ban > 10) {
//            throw new RuntimeException("ban wave");
//        }
        if (this.pause.isValue()) {
            if (this.timer.isValue()) {
                boolean flag = false;
                if (this.alarm.getValue() != null && this.alarm.getValue().contains("-")) {
                    String[] sp = this.alarm.getValue().split("-");
                    if (sp.length == 2) {
                        this.start = LocalTime.parse(sp[0]);
                        this.end = LocalTime.parse(sp[1]);
                    } else {
                        flag = true;
                    }
                } else {
                    flag = true;
                }
                if (flag) {
                    throw new RuntimeException("不合法的时间段 " + this.alarm.getValue());
                }
            }
        }
    }

    @Override
    public void callBack(int v) {
        if (!this.wpProcess.isPause()) {
            if (this.banWave.isValue()) {
                this.message.sendWarningMessage("Ban wave!");
                ModuleManager.instance.disable(this);
            }
        }

    }




}
