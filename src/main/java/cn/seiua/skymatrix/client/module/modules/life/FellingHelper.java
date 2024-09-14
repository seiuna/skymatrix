package cn.seiua.skymatrix.client.module.modules.life;

import baritone.api.BaritoneAPI;
import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.Client;
import cn.seiua.skymatrix.client.IToggle;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
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
import cn.seiua.skymatrix.event.events.*;
import cn.seiua.skymatrix.gui.ClickGui;
import cn.seiua.skymatrix.gui.Icons;
import cn.seiua.skymatrix.hud.ClientHud;
import cn.seiua.skymatrix.hud.Hud;
import cn.seiua.skymatrix.message.Message;
import cn.seiua.skymatrix.message.MessageBuilder;
import cn.seiua.skymatrix.utils.*;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.awt.*;
import java.util.List;
import java.util.*;

@Event
@Sign(sign = Signs.FREE)
@SModule(name = "fellingHelper", category = "life", disable = true)

public class FellingHelper implements IToggle, Hud {

    @Use
    RotationFaker rotationFaker;
    @Use
    Waypoint waypoint;
    @Use
    SmoothRotation smoothRotation;

    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(List.of(), ReflectUtils.getModuleName(this));
    @Value(name = "hud")
    public ClientHud clientHud = new ClientHud(100, 100, true, this);
    @Value(name = "mode")
    SingleChoice<String> mode = new SingleChoice<>(List.of("legit", "normal"), Icons.MODE);
    @Value(name = "range")
    @Hide(following = "mode", value = "normal")
    ValueSlider range = new ValueSlider(4, 0, 6, 1);
    @Value(name = "woods")
    ToggleSwitch crops = new ToggleSwitch(false);
    @Value(name = "wood")
    @Hide(following = "woods")
    MultipleChoice blocks = new MultipleChoice(SkyBlockUtils.getAllWood(), Icons.ORE);
    @Value(name = "waypoint")
    WaypointSelect waypointName = new WaypointSelect(null, "FF");
    @Value(name = "keep world")
    ToggleSwitch keep = new ToggleSwitch(false);
    @Value(name = "aote")
    @Sign(sign = Signs.PRO)
    public SkyblockItemSelect aote = new SkyblockItemSelect("none", false, Selector::bestAote, Filter::aote);

    @Value(name = "escape")
    ToggleSwitch escape = new ToggleSwitch(false);

    @Value(name = "cmd")
    @Hide(following = "escape")
    ValueInput cmd = new ValueInput("", Icons.CMD);

    @Value(name = "waiting time(m)")
    @Hide(following = "escape")
    ValueSlider wTime = new ValueSlider(0, 0, 6, 1);
    @Value(name = "escapes")
    @Hide(following = "escape")
    MultipleChoice escapeWay = new MultipleChoice(Map.of("slot", false, "fov", false, "location", false), Icons.MODE);
    private final Message message = MessageBuilder.build("felling helper");
    private BlockPos target;
    private Status status = Status.NONE;


    public static enum Status {
        NONE, DOING, ESCAPING
    }

    @EventTarget
    public void onPacket(ServerPacketEvent event) {

        if (this.status == Status.ESCAPING) return;
        if (event.getPacket() instanceof UpdateSelectedSlotS2CPacket) {
            UpdateSelectedSlotS2CPacket packet = (UpdateSelectedSlotS2CPacket) event.getPacket();
            assert SkyMatrix.mc.player != null;
            if (packet.getSlot() != SkyMatrix.mc.player.getInventory().selectedSlot) {
                escape("slot");
                this.status = Status.ESCAPING;
                event.setCancelled(true);
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
                                this.escape("location");
                                message.sendWarningMessage(Text.of("§4Admin Location Transfrom!"));
                                this.status = Status.ESCAPING;
                            }
                        }

                    }
                    if (this.escapeWay.getValue().get("fov")) {
                        message.sendWarningMessage(Text.of("§4Admin Rotation!"));
                        this.status = Status.ESCAPING;
                    }
                }


            }
        }
    }


    public void escape(String type) {

        this.oneTickTimer = TickTimer.build(20 * 60 * this.wTime.getValue().intValue(), this::continua);
    }

    public void continua() {

    }

    public void keepWorld() {
        Objects.requireNonNull(SkyMatrix.mc.getNetworkHandler()).sendCommand(this.cmd.getValue());
        worldChange1 = TickTimer.build(40, this::keepWorld1);

        this.oneTickTimer = null;
    }

    private void keepWorld1() {
        this.status = Status.NONE;
        this.oneTickTimer = null;
        this.wpProcess.startP();
    }

    OneTickTimer oneTickTimer;
    OneTickTimer worldChange;
    OneTickTimer worldChange1;
    OneTickTimer wait1;
    OneTickTimer wait2;

    @EventTarget
    public void onWorldChange(WorldChangeEvent e) {
        if (this.keep.isValue()) {
            this.wpProcess.pause();
            this.worldChange = TickTimer.build(60, this::keepWorld);
            this.status = Status.ESCAPING;
        }

    }

    public boolean isTarget(BlockPos bp) {

        String name = BlockUtils.getName(bp).replace("minecraft:", "");

        assert SkyMatrix.mc.world != null;
        BlockState blockState = SkyMatrix.mc.world.getBlockState(bp);
        if (blockState.isAir() || SkyMatrix.mc.world.isWater(bp) || SkyMatrix.mc.world.getFluidState(bp).getFluid().isIn(FluidTags.LAVA))
            return false;

        return blocks.value.containsKey(name);
    }


    private final HashMap<String, Integer> black = new HashMap<>();


    private boolean doing;

    public boolean next1(WaypointEntity entity) {
        Client.sendDebugMessage(Text.of("next"));
        this.wpProcess.pause();
        doing = true;
        return !flag;
    }

    int tick;

    @EventTarget
    public void onGui(OpenScreenEvent e) {
        if (wpProcess != null) {
            e.setCancelled(true);
        }

    }

    boolean flag;

    @EventTarget
    public void onTick(ClientTickEvent e) {

        assert SkyMatrix.mc.crosshairTarget != null;
        if (SkyMatrix.mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            String name = BlockUtils.getName(((BlockHitResult) SkyMatrix.mc.crosshairTarget).getBlockPos()).replace("minecraft:", "");

        }
        if (tick % 10 == 0) {
            count = 0;
        }
        BaritoneAPI.getSettings().allowSprint.value = true;
        if (worldChange != null) {
            worldChange.update();
        }
        if (worldChange1 != null) {
            worldChange1.update();
        }
        if (this.status != Status.NONE) {
            this.wpProcess.pause();
            return;
        }
        assert SkyMatrix.mc.player != null;
        ItemStack is = SkyMatrix.mc.player.getInventory().getMainHandStack();
        String type = SkyBlockUtils.getItemType(is);
        String name = is.getItem().getName().getString();
        if (name.contains("Air")) return;
        if ((name.contains("Pickaxe") || name.contains("Axe") || name.contains("Shovel")) || ((type != null) && (type.equals("AXE") || type.equals("DRILL") || type.equals("PICKAXE") || type.equals("SHOVEL")))) {
            List<BlockPos> blockPos = new ArrayList<>();
            Vec3d viewPos = Vec3d.fromPolar(rotationFaker.getServerPitch(), rotationFaker.getServerYaw());
            Vec3d viewc = SkyMatrix.mc.player.getRotationVecClient();
            Vec3d playerPos = SkyMatrix.mc.player.getEyePos();
            BlockPos blockPos1 = new BlockPos((int) playerPos.x, (int) playerPos.y, (int) playerPos.z);
            Vec3d targetVec = null;
            double lastAngle = 100000;
            double lastD = 100000;
            int i1 = 0;
            Vec3d vec3d = null;
            int r = range.getValue().intValue();
            for (int i = 0; i < r; i++) {
                for (int j = 0; j < r; j++) {
                    for (int k = 0; k < r; k++) {
                        BlockPos cp = blockPos1.add(i - r / 2, j - r / 2, k - r / 2);

                        if (!isTarget(cp)) {
                            continue;
                        }
                        vec3d = canSee(cp);


                        if (vec3d != null) {
                            Vec3d vec3d1 = cp.toCenterPos().subtract(playerPos);
                            double angle = MathUtils.calculateAngle(vec3d1, viewPos);

                            if (target == null) {
                                target = cp;
                                lastAngle = angle;
                                continue;
                            } else {
                                if (angle < lastAngle) {
                                    lastAngle = angle;
                                    targetVec = vec3d;
                                    target = cp;
                                    i1++;
                                }
                            }

                        }
                    }
                }
            }
            if (i1 != 0) {
                this.flag = true;
                if (doing) {
                    SkyMatrix.mc.options.attackKey.setPressed(true);
                    Vec3d vec3d1 = targetVec.multiply(targetVec.length());
                    smoothRotation.smoothLook(RotationUtils.toRotation(vec3d1), 3, null, true);
                    black.put(String.valueOf(Objects.hash(this.target.getX(), this.target.getY(), this.target.getZ())), 20);
                    BlockHitResult blockHitResult = (BlockHitResult) SkyMatrix.mc.crosshairTarget;
                }


            } else {
                this.flag = false;
                this.wpProcess.resume1();
                if (doing) {
                    SkyMatrix.mc.options.attackKey.setPressed(false);
                    doing = false;
                    this.wpProcess.resume1();
                }
            }

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
        Vec3d viewPos = SkyMatrix.mc.player.getRotationVecClient();
        Vec3d playerPos = SkyMatrix.mc.player.getEyePos();
        for (Vec3d vec3dv : SHIFTING) {

            Vec3d vec3d1 = blockPos.toCenterPos().subtract(playerPos);
            double angle = MathUtils.calculateAngle(vec3d1, viewPos);

            Vec3d vec3d = SkyMatrix.mc.player.getCameraPosVec(1.0f);
            float dd = 4.5F;
            Vec3d vec3d2 = new Vec3d(1, 1, 1);
            Vec3d bvec = blockPos.toCenterPos().add(vec3dv).subtract(vec3d);
            bvec = bvec.multiply(1 / bvec.length());

            Vec3d vec3d3 = vec3d.add(bvec.x * dd, bvec.y * dd, bvec.z * dd);

            HitResult hitResult = SkyMatrix.mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, SkyMatrix.mc.player));
            if (hitResult instanceof BlockHitResult) {
                BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                if (blockHitResult.getBlockPos().equals(blockPos)) {
//                    System.out.println("OK" + vec3dv.add(blockPos.getX(), vec3dv.getY(), vec3dv.getZ()));
                    return bvec;
                }
            }

        }
        return null;
    }


    public Vec3d canSee(BlockPos blockPos) {

        return traversal(blockPos);

    }


    @EventTarget
    public void onRender(WorldRenderEvent e) {

    }

    WPProcess wpProcess = null;

    public void next() {


    }

    @Override
    public void disable() {
        this.target = null;
        if (wpProcess != null) {
            wpProcess.stopP();
        }
        SkyMatrix.mc.mouse.lockCursor();
        this.status = Status.NONE;
    }

    @Override
    public void enable() {
        this.worldChange = null;
        this.worldChange1 = null;
        this.flag = false;
        this.doing = false;
        this.oneTickTimer = null;

        WaypointGroupEntity waypointGroupEntity = waypointName.waypointGroup();

        wpProcess = new WPProcess(true, waypointGroupEntity);
        wpProcess.setNextCircle(this::next);
        wpProcess.setNext(this::next1);
        wpProcess.setPriority(1);
        wpProcess.setDaemon(true);
        wpProcess.start();
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
