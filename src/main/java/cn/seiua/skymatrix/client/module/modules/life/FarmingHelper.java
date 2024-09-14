package cn.seiua.skymatrix.client.module.modules.life;

import baritone.api.BaritoneAPI;
import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.IToggle;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.module.ModuleManager;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.client.module.modules.ModuleTools;
import cn.seiua.skymatrix.client.rotation.Rotation;
import cn.seiua.skymatrix.client.rotation.RotationFaker;
import cn.seiua.skymatrix.client.rotation.SmoothRotation;
import cn.seiua.skymatrix.client.waypoint.WPProcess;
import cn.seiua.skymatrix.client.waypoint.Waypoint;
import cn.seiua.skymatrix.client.waypoint.WaypointEntity;
import cn.seiua.skymatrix.client.waypoint.WaypointGroupEntity;
import cn.seiua.skymatrix.config.Hide;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.*;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.event.events.ReachEvent;
import cn.seiua.skymatrix.event.events.ServerPacketEvent;
import cn.seiua.skymatrix.event.events.WorldChangeEvent;
import cn.seiua.skymatrix.gui.ClickGui;
import cn.seiua.skymatrix.gui.Icons;
import cn.seiua.skymatrix.hud.ClientHud;
import cn.seiua.skymatrix.hud.Hud;
import cn.seiua.skymatrix.message.Message;
import cn.seiua.skymatrix.message.MessageBuilder;
import cn.seiua.skymatrix.utils.*;
import com.google.common.collect.EvictingQueue;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.List;
import java.util.*;

@Deprecated
//@Event
//@Sign(sign = Signs.BETA)
//@SModule(name = "farmingHelper", category = "life", disable = true)
public class FarmingHelper implements IToggle, Hud {

    @Use
    RotationFaker rotationFaker;
    @Use
    Waypoint waypoint;
    @Use
    SmoothRotation smoothRotation;

    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(List.of(), ReflectUtils.getModuleName(this));
    @Value(name = "farm hud")
    public ClientHud clientHud = new ClientHud(100, 100, true, this);

    @Value(name = "mode")
    SingleChoice<String> mode = new SingleChoice<>(List.of("legit", "normal", "manual"), Icons.MODE);
    @Value(name = "angle")
    @Hide(following = "mode", value = "normal&manual")
    @Sign(sign = Signs.BETA)
    ValueSlider angle = new ValueSlider(50, 0, 180, 0.1f);
    @Value(name = "range")
    @Hide(following = "mode", value = "normal&manual")
    @Sign(sign = Signs.BETA)
    ValueSlider range = new ValueSlider(4, 0, 6, 1);
    @Value(name = "lock slot")
    @Sign(sign = Signs.BETA)
    ToggleSwitch lockSlot = new ToggleSwitch(false);
    @Value(name = "crops")
    @Hide(following = "mode", value = "normal&manual")
    @Sign(sign = Signs.BETA)
    ToggleSwitch crops = new ToggleSwitch(false);
    @Value(name = "blocks")
    @Hide(following = "crops")
    @Sign(sign = Signs.BETA)
    MultipleChoice blocks = new MultipleChoice(SkyBlockUtils.getAllCrop(), Icons.ORE);
    @Value(name = "waypoint")
    @Hide(following = "mode", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    WaypointSelect waypointName = new WaypointSelect(null, "FM");
    @Value(name = "when over")
    @Hide(following = "mode", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    ToggleSwitch over = new ToggleSwitch(false);
    @Value(name = "cmd1")
    @Hide(following = "when over&mode", value = "legit&normal")
    @Sign(sign = Signs.PRO)
    ValueInput overCmd = new ValueInput("warp garden", Icons.CMD);
    @Value(name = "keep world")
    @Hide(following = "mode", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    ToggleSwitch keep = new ToggleSwitch(false);
    @Value(name = "lock view")
    @Hide(following = "mode", value = "legit")
    @Sign(sign = Signs.BETA)
    ToggleSwitch lock = new ToggleSwitch(true);
    @Value(name = "reach")
    @Hide(following = "mode", value = "legit")
    @Sign(sign = Signs.BETA)
    ToggleSwitch reach = new ToggleSwitch(true);
    @Value(name = "free look")
    @Hide(following = "mode&lock view", value = "legit")
    @Sign(sign = Signs.BETA)
    ToggleSwitch server = new ToggleSwitch(true);
    @Value(name = "waiting time of next(m)")
    @Hide(following = "mode", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    ValueSlider wTimeN = new ValueSlider(4, 0, 6, 1);
    @Value(name = "cmd")
    @Hide(following = "mode", value = "legit&normal")
    @Sign(sign = Signs.PRO)
    ValueInput cmd = new ValueInput("", Icons.CMD);

    @Value(name = "escape")
    @Hide(following = "mode", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    ToggleSwitch escape = new ToggleSwitch(false);

    @Value(name = "waiting time(m)")
    @Hide(following = "escape&mode", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    ValueSlider wTime = new ValueSlider(0, 0, 6, 1);
    @Value(name = "escapes")
    @Hide(following = "escape&mode", value = "legit&normal")
    @Sign(sign = Signs.BETA)
    MultipleChoice escapeWay = new MultipleChoice(Map.of("slot", false, "fov", false, "location", false), Icons.MODE);
    private final Message message = MessageBuilder.build("farming helper");
    private BlockPos target;
    private Status status = Status.NONE;

    public enum Status {
        NONE, DOING, ESCAPING, WAiTING
    }

    @EventTarget
    public void onReach(ReachEvent e) {
        if (reach.isValue())
            e.setReach(5.0f);
    }

    @EventTarget
    public void onPacket(ServerPacketEvent event) {
        if (this.mode.selectedValue().equals("manual")) {
            return;
        }

        if (this.status == Status.ESCAPING) return;
        if (this.status == Status.WAiTING) return;
        if (event.getPacket() instanceof UpdateSelectedSlotS2CPacket) {
            UpdateSelectedSlotS2CPacket packet = (UpdateSelectedSlotS2CPacket) event.getPacket();
            if (packet.getSlot() != SkyMatrix.mc.player.getInventory().selectedSlot) {
                escape("slot");
                this.status = Status.ESCAPING;
                event.setCancelled(true);
                message.sendWarningMessage(Text.of("§4slot switch!"));
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
                            if (SkyMatrix.mc.player.getBlockPos().toCenterPos().distanceTo(new Vec3d(packet.getX(), packet.getY(), packet.getZ())) > 10) {
                                this.escape("location");
                                message.sendWarningMessage(Text.of("§4Admin Location Transfrom!"));
                                return;
                            }
                        }

                    }
                    if (this.escapeWay.getValue().get("fov")) {
                        message.sendWarningMessage(Text.of("§4Admin Rotation!"));
                        return;
                    }
                }


            }
        }
    }

    private float yaw;
    private float pitch;

    private float sYaw;
    private float sPitch;
    ;

    public void escape(String type) {
        yaw = sYaw;
        pitch = sPitch;
        this.wpProcess.pause();
        this.status = Status.ESCAPING;
        this.wait2 = TickTimer.build(80, () -> {

            SkyMatrix.mc.getNetworkHandler().sendCommand(this.cmd.getValue());
            this.oneTickTimer = TickTimer.build(20 * 60 * this.wTime.getValue().intValue(), this::continua);
        });

    }

    public void continua() {
        yaw = sYaw;
        pitch = sPitch;
        this.status = Status.NONE;
        wpProcess.resume1();
    }

    int slot;

    public void keepWorld() {
        yaw = sYaw;
        pitch = sPitch;
        this.wait2 = null;
        SkyMatrix.mc.getNetworkHandler().sendCommand(this.cmd.getValue());
        worldChange1 = TickTimer.build(40, this::keepWorld1);
        this.status = Status.WAiTING;
        this.oneTickTimer = null;
    }

    private void keepWorld1() {
        this.wait2 = null;
        this.status = Status.NONE;
        this.oneTickTimer = null;
        this.wpProcess.resume1();
    }

    OneTickTimer oneTickTimer;
    OneTickTimer worldChange;
    OneTickTimer worldChange1;
    OneTickTimer wait1;
    OneTickTimer wait2;

    @EventTarget
    public void onWorldChange(WorldChangeEvent e) {
        if (this.mode.selectedValue().equals("manual")) {
            return;
        }
        if (this.keep.isValue()) {
            this.wpProcess.pause();
            this.worldChange = TickTimer.build(60, this::keepWorld);
            this.oneTickTimer = null;
            this.wait2 = null;
            this.wait1 = null;
        } else {
            ModuleManager.instance.disable(this);
        }

    }

    public boolean isTarget(BlockPos bp) {

        String name = BlockUtils.getName(bp).replace("minecraft:", "");
        name = SkyBlockUtils.getNameByMapper(name);
        assert SkyMatrix.mc.world != null;
        BlockState blockState = SkyMatrix.mc.world.getBlockState(bp);
        if (blockState.isAir() || SkyMatrix.mc.world.isWater(bp) || SkyMatrix.mc.world.getFluidState(bp).getFluid().isIn(FluidTags.LAVA))
            return false;

        if (blocks.value.containsKey(name)) {
            if (this.mode.selectedValue().equals("legit")) return true;
            if (blocks.value.get(name)) {
//                switch (name) {
//                    case "sugar_cane", "cactus" -> {
//                        BlockPos bp1 = bp.down();
//                        return SkyMatrix.mc.world.getBlockState(bp1).getBlock() == SkyMatrix.mc.world.getBlockState(bp).getBlock();
//                    }
//                    case "nether_wart" -> {
//                        int age = SkyMatrix.mc.world.getBlockState(bp).get(IntProperty.of("age", 0, 3));
//                        return age == 3;
//                    }
//                    case "carrots", "potatoes", "wheat" -> {
//                        int age = SkyMatrix.mc.world.getBlockState(bp).get(IntProperty.of("age", 0, 7));
//                        return age == 7;
//                    }
//                    default -> {
//                        return true;
//                    }
//                }
                return true;

            }
        }
        return false;
    }


    private final HashMap<String, Integer> black = new HashMap<>();


    int tick;
    private final EvictingQueue<BlockInfo> evictingQueue = EvictingQueue.create(50);

    private static class BlockInfo {
        BlockPos blockPos;
        String old;

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public String getOld() {
            return old;
        }

        public BlockInfo(BlockPos blockPos, String old) {
            this.blockPos = blockPos;
            this.old = old;
        }

        public BlockInfo(BlockPos blockPos) {
            this.blockPos = blockPos;
            BlockState state = SkyMatrix.mc.world.getBlockState(blockPos);
            this.old = state.toString();

        }

        public boolean isChanged() {
            BlockState state = SkyMatrix.mc.world.getBlockState(blockPos);
            return !state.toString().equals(this.old);
        }
    }

    @EventTarget
    public void onTick(ClientTickEvent e) {
        if (lockSlot.isValue()) {
            SkyMatrix.mc.player.getInventory().selectedSlot = this.slot;
        }

        if (tick % 10 == 0) {
            BaritoneAPI.getSettings().allowSprint.value = count != 10;
            if (this.evictingQueue.size() == 50) {
                int a = 0;
                float v = 50f;
                for (BlockInfo info : this.evictingQueue.stream().toList()) {
                    if (!info.old.contains("air")) {
                        if (!info.isChanged()) {
                            a++;
                        }
                    } else {
                        v--;
                    }
                }
                float f = a / v;
                if (f > 0.95) {
                    SkyMatrix.mc.getNetworkHandler().sendCommand("is");
                    this.evictingQueue.clear();
                }
            }

            count = 0;
        }
        tick++;
        if (this.oneTickTimer != null) {
            oneTickTimer.update();
        }
        if (this.worldChange != null) {
            worldChange.update();
        }
        if (this.worldChange1 != null) {
            worldChange1.update();
        }
        if (this.wait1 != null) {
            wait1.update();
        }
        if (this.wait2 != null) {
            wait2.update();
        }
        if (this.mode.selectedValue().equals("legit")) {
            if (!(this.status == Status.ESCAPING || this.status == Status.WAiTING)) {
                if (this.lock.isValue()) {
                    if (this.wpProcess.getCurrentIndex() > 1) {
                        smoothRotation.smoothLook(new Rotation(yaw, pitch), 3, null, !this.server.isValue());
                        BaritoneAPI.getSettings().allowSprint.value = false;
                    } else {
                        this.yaw = this.sYaw;
                        this.pitch = this.sPitch;
                        BaritoneAPI.getSettings().allowSprint.value = true;
                    }

                }
            }


        }

        ArrayList<String> s = new ArrayList<>(this.black.keySet());
        for (String hash : s) {
            this.black.put(hash, this.black.get(hash) - 1);
            if (this.black.get(hash) <= 0) {
                this.black.remove(hash);
            }
        }
        if (!this.mode.selectedValue().equals("manual")) {
            if (this.status == Status.ESCAPING) {
                BaritoneAPI.getSettings().allowSprint.value = true;
                this.wpProcess.pause();
                return;
            } else {
                BaritoneAPI.getSettings().allowSprint.value = false;
            }
        }

        if (this.status.equals(Status.ESCAPING)) return;
        if (!(SkyMatrix.mc.currentScreen instanceof HandledScreen<?>)) {
            if (this.wpProcess != null) {
                if (this.wpProcess.getCurrentIndex() <= 1) {
                    return;
                }
            }
            if (this.mode.selectedValue().equals("manual") && !SkyMatrix.mc.options.attackKey.isPressed()) {
                return;
            }
            assert SkyMatrix.mc.player != null;
            ItemStack is = SkyMatrix.mc.player.getInventory().getMainHandStack();
            String type = SkyBlockUtils.getItemType(is);
            String name = is.getItem().toString().toLowerCase();
            assert SkyMatrix.mc.crosshairTarget != null;
            if (name.contains("air")) return;
            if ((name.contains("pickaxe") || name.contains("axe") || name.contains("shovel") || name.contains("hoe")) || ((type != null) && (type.equals("AXE") || type.equals("DRILL") || type.equals("PICKAXE") || type.equals("SHOVEL")))) {
                if (this.mode.selectedValue().equals("legit")) {
                    if (SkyMatrix.mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                        BlockPos blockPos = ((BlockHitResult) SkyMatrix.mc.crosshairTarget).getBlockPos();
                        if (isTarget(blockPos)) {
                            evictingQueue.add(new BlockInfo(blockPos));
                            count++;
                            SkyMatrix.mc.doAttack();

                            ModuleTools.updataTarget();
                            if (SkyMatrix.mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                                if (isTarget(((BlockHitResult) SkyMatrix.mc.crosshairTarget).getBlockPos())) {
                                    SkyMatrix.mc.doAttack();
                                    evictingQueue.add(new BlockInfo(((BlockHitResult) SkyMatrix.mc.crosshairTarget).getBlockPos()));
                                }

                            }
                            ModuleTools.updataTarget();
                            if (this.reach.isValue() && System.currentTimeMillis() % 3 == 0) {

                                ModuleTools.updataTarget();
                                if (SkyMatrix.mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                                    if (isTarget(((BlockHitResult) SkyMatrix.mc.crosshairTarget).getBlockPos())) {
                                        SkyMatrix.mc.doAttack();
                                        evictingQueue.add(new BlockInfo(((BlockHitResult) SkyMatrix.mc.crosshairTarget).getBlockPos()));
                                    }

                                }
                            }
                            this.status = Status.DOING;
                            BaritoneAPI.getSettings().allowSprint.value = false;
                        } else {
                            this.status = Status.NONE;
                            BaritoneAPI.getSettings().allowSprint.value = true;
                        }


                    }
                    return;
                }
                Vec3d viewPos = Vec3d.fromPolar(rotationFaker.getServerPitch(), rotationFaker.getServerYaw());
                Vec3d viewc = SkyMatrix.mc.player.getRotationVecClient();
                Vec3d playerPos = SkyMatrix.mc.player.getEyePos();
                BlockPos blockPos1 = new BlockPos((int) playerPos.x, (int) playerPos.y, (int) playerPos.z);
                Vec3d targetVec = null;
                double lastD = 100000;
                int flag = 0;
                Vec3d vec3d = null;
                int RANGE = range.getValue().intValue() * 2;
                for (int i = 0; i < RANGE; i++) {
                    for (int j = 0; j < RANGE; j++) {
                        for (int k = 0; k < RANGE; k++) {
                            BlockPos cp = blockPos1.add(i - RANGE / 2, j - RANGE / 2, k - RANGE / 2);
                            if (SkyMatrix.mc.player.getEyePos().distanceTo(cp.toCenterPos()) > range.getValue().doubleValue()) {
                                continue;
                            }
                            if (MathUtils.calculateAngle(viewc, cp.toCenterPos().subtract(playerPos)) > angle.getValue().doubleValue())
                                continue;
                            if (!isTarget(cp)) {
                                continue;
                            }
                            vec3d = canSee(cp);


                            if (vec3d != null) {
                                Vec3d vec3d1 = cp.toCenterPos().subtract(playerPos);
                                double angle = MathUtils.calculateAngle(vec3d1, viewPos);
                                double d = vec3d1.length();

                                if (target == null) {
                                    target = cp;
                                    continue;
                                } else {

                                    if (d < lastD) {
                                        lastD = d;
                                        targetVec = vec3d;
                                        target = cp;
                                        flag++;
                                    }
                                }

                            }
                        }
                    }
                }
                if (flag != 0) {
                    status = Status.DOING;
                    Vec3d vec3d1 = targetVec.multiply(targetVec.length());
                    rotationFaker.keepServer();
                    smoothRotation.smoothLook(RotationUtils.toRotation(vec3d1), 2f, null, false);
                    SkyMatrix.mc.interactionManager.attackBlock(target, Direction.UP);
                    SkyMatrix.mc.player.swingHand(Hand.MAIN_HAND);
                    count++;
                    black.put(String.valueOf(Objects.hash(this.target.getX(), this.target.getY(), this.target.getZ())), 20);
                }
            } else {
                this.evictingQueue.clear();
            }
            rotationFaker.release();
        }
    }

    int count = 0;
    private static final ArrayList<Vec3d> SHIFTING = new ArrayList<>();

    static {
        SHIFTING.add(new Vec3d(0.5, 0, 0));
        SHIFTING.add(new Vec3d(0, 0, 0.5));
        SHIFTING.add(new Vec3d(-0.5, 0, 0));
        SHIFTING.add(new Vec3d(0, -0.5, 0));
        SHIFTING.add(new Vec3d(0, 0, -0.5));
    }

    public Vec3d traversal(BlockPos blockPos) {
        assert SkyMatrix.mc.player != null;
        for (Vec3d vec3dv : SHIFTING) {
            Vec3d vec3d = SkyMatrix.mc.player.getEyePos();
            Vec3d bvec = blockPos.toCenterPos().add(vec3dv).subtract(vec3d);
            bvec = bvec.multiply(1 / bvec.length());
            return bvec;
        }
        return null;
    }

    public Vec3d canSee(BlockPos blockPos) {
        return traversal(blockPos);
    }

    private WPProcess wpProcess = null;

    public void next() {
        this.status = Status.WAiTING;
        if (over.isValue()) {

            overCmd.execute();
        }
        this.wait2 = null;
        this.oneTickTimer = null;

        yaw = sYaw;
        pitch = sPitch;
        wpProcess.pause();
        this.wait1 = TickTimer.build(Math.max(this.wTimeN.getValue().intValue() * 20 * 60, 20), () -> {
            this.wait2 = null;
            this.oneTickTimer = null;
            wpProcess.resume1();
            this.status = Status.NONE;
        });
    }

    long a;
    long b;


    @Override
    public void disable() {
        this.target = null;
        if (wpProcess != null) {
            wpProcess.stopP();
        }
        wpProcess = null;
    }

    @Override
    public void enable() {
        a = System.currentTimeMillis();
        this.slot = SkyMatrix.mc.player.getInventory().selectedSlot;
        this.evictingQueue.clear();
        ItemStack itemStack = SkyMatrix.mc.player.getInventory().getMainHandStack();
        if (itemStack != null) {
            NbtCompound nbtCompound = null;
            if (nbtCompound != null) {
                NbtCompound aa = nbtCompound.getCompound("ExtraAttributes");
                if (aa != null) {
                    int bb = aa.getInt("farmed_cultivating");
                    b = bb;
                }
            }
        }
        this.wait2 = null;
        this.wait1 = null;
        this.worldChange1 = null;
        this.worldChange = null;
        this.oneTickTimer = null;
        yaw = SkyMatrix.mc.player.getYaw();
        pitch = SkyMatrix.mc.player.getPitch();
        sYaw = SkyMatrix.mc.player.getYaw();
        sPitch = SkyMatrix.mc.player.getPitch();
        this.status = Status.NONE;
        this.oneTickTimer = null;
        if (this.mode.selectedValue().equals("manual")) {

        } else {
            WaypointGroupEntity waypointGroupEntity = waypointName.waypointGroup();
            wpProcess = new WPProcess(true, waypointGroupEntity);
            wpProcess.setNextCircle(this::next);
            wpProcess.setNext(this::next1);
            wpProcess.setPriority(1);
            wpProcess.setDaemon(true);
            wpProcess.start();
        }
    }

    private Boolean next1(WaypointEntity entity) {
        if (entity.data != null) {
            if (entity.data.getOrDefault("rotation", "none").equals("switch")) {
                this.yaw += 180;
                this.message.sendDebugMessage(Text.of("switch"));
            }
        }
        return true;
    }

    @Override
    public void draw(MatrixStack matrixStack, float x, float y) {
        RenderUtils.resetCent();
        RenderUtils.setColor(new Color(0, 0, 0, 100));
        RenderUtils.drawSolidBox(new Box(x, y, 0, x + getHudWidth(), y + getHudHeight(), 0), matrixStack);
        ClickGui.fontRenderer20.setColor(Color.WHITE);
        ClickGui.fontRenderer20.resetCenteredH();
        ClickGui.fontRenderer20.resetCenteredV();
        float startX = x + 15;
        float startY = y + 12;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, getV());
        ItemStack itemStack = SkyMatrix.mc.player.getInventory().getMainHandStack();
        if (itemStack != null) {
            NbtCompound nbtCompound = null;
            if (nbtCompound != null) {
                NbtCompound aa = nbtCompound.getCompound("ExtraAttributes");
                long cc = 0;
                if (aa != null) {
                    int bb = aa.getInt("farmed_cultivating");
                    cc = bb - b;
                }
                startY += 25;
                if (System.currentTimeMillis() - a == 0) return;
                float t = (int) (cc / ((System.currentTimeMillis() - a) / 1000f) * 4 * 60 * 60);
                ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "平均每小时收益: " + t / 1000000 + "M/h 当前总收益: " + cc * 4 / 1000000f + "M 当前总时长: " + ((System.currentTimeMillis() - a) / 1000) / 60 + "m");
            }
        }

    }

    @Override
    public int getHudWidth() {

        return ClickGui.fontRenderer20.getStringWidth(getV()) + 32;

    }

    public String getV() {
        var v1 = "0%";
        var v2 = "0";
        if (this.wpProcess != null) {
            v1 = ((int) (this.wpProcess.getCDegree() * 100)) + "%";
            v2 = String.valueOf(this.wpProcess.getCount() + 1);
        }
        return String.format("当前路径点完成度: %s 第%s个循环 每 10tick 挖掘次数 %d 状态 %s %dm", v1, v2, this.count, this.status, this.oneTickTimer != null ? this.oneTickTimer.getTick() / 1200 : 0);
    }

    @Override
    public int getHudHeight() {
        return 53;
    }
}
