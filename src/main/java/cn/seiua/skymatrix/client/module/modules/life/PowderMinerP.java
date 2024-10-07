package cn.seiua.skymatrix.client.module.modules.life;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.calc.IPath;
import baritone.api.pathing.goals.GoalBlock;
import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.ChatOverride;
import cn.seiua.skymatrix.client.Client;
import cn.seiua.skymatrix.client.IToggle;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.Pro;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.config.Setting;
import cn.seiua.skymatrix.client.module.ModuleManager;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.client.rotation.ClientRotation;
import cn.seiua.skymatrix.client.rotation.Rotation;
import cn.seiua.skymatrix.client.rotation.RotationFaker;
import cn.seiua.skymatrix.config.Hide;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.*;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.*;
import cn.seiua.skymatrix.gui.Theme;
import cn.seiua.skymatrix.message.Message;
import cn.seiua.skymatrix.message.MessageBuilder;
import cn.seiua.skymatrix.utils.MathUtils;
import cn.seiua.skymatrix.utils.ReflectUtils;
import cn.seiua.skymatrix.utils.RenderUtilsV2;
import cn.seiua.skymatrix.utils.RotationUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static cn.seiua.skymatrix.SkyMatrix.mc;

@Event
@Sign(sign = Signs.PRO)
@SModule(name = "PowderMiner", category = "life", disable = true)
@Pro
@SuppressWarnings("all")
public class PowderMinerP extends PowderMiner implements IToggle {
    private final List<BlockPos> record = new ArrayList<>();
    private final HashSet<BlockPos> chests = new HashSet<>();
    private final HashSet<String> blackList = new HashSet<>();
    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(List.of(), ReflectUtils.getModuleName(this));
    //    @Value(name = "auto", desc = "")
//    @Sign(sign = Signs.BETA)
    @Deprecated
    ToggleSwitch auto = new ToggleSwitch(true);
    @Value(name = "count", desc = "挖矿角度")
    @Sign(sign = Signs.BETA)
    ValueSlider count = new ValueSlider(1, 1, 10, 1f);
    @Value(name = "angle mining", desc = "挖矿角度")
    @Sign(sign = Signs.BETA)
    ValueSlider angle = new ValueSlider(57, 0, 180, 0.1f);
    @Value(name = "angle chest", desc = "开箱角度")
    @Sign(sign = Signs.BETA)
    ValueSlider anglec = new ValueSlider(180, 0, 180, 0.1f);
    @Value(name = "range", desc = "开箱角度")
    @Sign(sign = Signs.BETA)
    ValueSlider range = new ValueSlider(4, 2, 12, 0.1f);
    //    @Value(name = "instant", desc = "")
//    @Sign(sign = Signs.BETA)
    @Deprecated
    ToggleSwitch instant = new ToggleSwitch(false);
    @Value(name = "render", desc = "")
    @Sign(sign = Signs.BETA)
    ToggleSwitch render = new ToggleSwitch(false);
    @Value(name = "back to record", desc = "")
    @Sign(sign = Signs.BETA)
    ToggleSwitch backTo = new ToggleSwitch(true);
    @Value(name = "clear gemstones", desc = "")
    @Sign(sign = Signs.BETA)
    ToggleSwitch gemstone = new ToggleSwitch(true);
    @Value(name = "clear mobs", desc = "")
    @Sign(sign = Signs.BETA)
    ToggleSwitch mobs = new ToggleSwitch(true);
    @Value(name = "tool1", desc = "你的挖矿工具")
    @Sign(sign = Signs.BETA)
    SkyblockItemSelect tool = new SkyblockItemSelect("", false, Selector::bestMiningTool, Filter::miningTool);
    @Value(name = "tool2", desc = "你的挖矿工具")
    @Sign(sign = Signs.BETA)
    SkyblockItemSelect tool2 = new SkyblockItemSelect("", false, Selector::bestMiningTool, Filter::miningTool);
    @Value(name = "weapon", desc = "你的武器")
    @Sign(sign = Signs.BETA)
    SkyblockItemSelect weapon = new SkyblockItemSelect("", false, null, Filter::weaponFilter);
    @Value(name = "weapon", desc = "你的武器")
    @Sign(sign = Signs.BETA)
    SkyblockItemSelect aotv = new SkyblockItemSelect("", false, Selector::bestAote, Filter::aote);
    @Value(name = "escape", desc = "逃逸")
    ToggleSwitch escape = new ToggleSwitch(false);
    @Value(name = "only warning", desc = "逃逸")
    @Hide(following = "escape")
    ToggleSwitch warning = new ToggleSwitch(true);
    @Use
    ClientRotation clientRotation;
    @Use
    Client client;
    @Use
    ChatOverride chatOverride;
    Message message = MessageBuilder.build("PowderMiner");
    int tickTimer;
    @Use
    private MiningHelperV2 miningHelperV2;
    private int powder_count1 = 0;
    private int powder_count2 = 0;
    private BlockPos target = null;
    private BlockPos to = null;
    private String status = "default";
    private Vec3d look = null;
    private Vec3d look1 = null;
    private int countc;
    private int tick;
    private int move_tick;
    private int clear_tick;
    private int failed_time;
    private BlockPos target_chest_node;
    private BlockPos target_chest;
    private int chest_failed;
    private int pathing_failed;
    private Entity targetMob;
    private int flash;

    private void doChest() {
        if (this.chests.isEmpty()) {
            this.status = "default";
            target_chest_node = null;
            target_chest = null;
            mc.options.sneakKey.setPressed(false);
            String lastnode = record.getLast().getX() + " " + record.getLast().getY() + " " + record.getLast().getZ();
            lastnode = "回到上一个记录点§6 [" + lastnode + "] ";
            if (backTo.isValue()) {
                this.to = this.record.getLast();
            } else {
                lastnode = "";
                this.to = target_chest_node;
            }
            client.setKeepRightClick(false);
            look = null;
            target_chest = null;
            target_chest_node = null;
            message.sendMessage(Text.of("§a箱子都开完了呢，我要" + lastnode + "§a继续去挖\"硬\"石了喵!"));
            return;
        }

        if (this.target_chest_node != null) {
            assert mc.player != null;
            double distance = (mc.player.getBlockPos().toCenterPos().distanceTo(target_chest_node.toCenterPos()));
            if (!BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) {
                if (distance > 1.5) {
                    this.chest_failed++;
//                    message.sendMessage(Text.of(""+chest_failed));
                    if (this.chest_failed >= 20) {
                        if (chest_failed >= 70) {
                            this.blackList.add(target_chest.getX() + " " + target_chest.getY() + " " + target_chest.getZ());
                            chest_failed = 0;
                            return;
                        }
                        if (move_tick == -1) {
                            move_tick = angle.getIntValue();
                            this.angle.setValue(179);
                        }
                    } else {
                        if (move_tick != -1) {
                            angle.setValue(move_tick);
                            move_tick = -1;
                        }
                    }
                }

            }

            if (distance > 1.75 && mc.player.getEyePos().distanceTo(target_chest.toCenterPos()) > 2) {
                if (BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) {
                    chest_failed = 0;
                    Optional<IPath> optional = BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().getPath();
                    if (optional.isPresent()) {
                        pathing_failed = 0;
                        if (optional.get().length() > 6) {
                            BaritoneAPI.getSettings().allowSprint.value = true;
                        } else {
                            BaritoneAPI.getSettings().allowSprint.value = false;
                        }
                    } else {
                        blackList.add(target_chest.getX() + " " + target_chest.getY() + " " + target_chest.getZ());
                        pathing_failed++;
                    }
                } else {
                    if (target_chest_node != null) {
                        clientRotation.clientLook(RotationUtils.toRotation(target_chest_node), 100, null);
                        clientRotation.serverLook(RotationUtils.toRotation(target_chest_node), 2, null);
                    } else {
                        clientRotation.clientLook(RotationUtils.toRotation(target_chest), 100, null);
                        clientRotation.serverLook(RotationUtils.toRotation(target_chest), 2, null);
                    }
                    pathing_failed++;
                    BaritoneAPI.getSettings().allowSprint.value = false;
                    BaritoneAPI.getSettings().avoidance.value = false;
                    BaritoneAPI.getSettings().allowWalkOnBottomSlab.value = true;
                    BaritoneAPI.getSettings().antiCheatCompatibility.value = true;
                    BaritoneAPI.getSettings().allowParkour.value = false;
                    BaritoneAPI.getSettings().allowBreak.value = false;
                    BaritoneAPI.getSettings().allowPlace.value = false;
                    BaritoneAPI.getSettings().allowParkourAscend.value = true;
                    BaritoneAPI.getSettings().renderGoal.value = false;
                    BaritoneAPI.getSettings().freeLook.value = true;
                    BaritoneAPI.getSettings().assumeSafeWalk.value = false;
                    BaritoneAPI.getSettings().renderPath.value = false;
                    BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalBlock(target_chest_node.getX(), target_chest_node.getY() + 1, target_chest_node.getZ()));
                }
            } else {


                if (mc.crosshairTarget.getType().equals(HitResult.Type.BLOCK)) {
                    BlockHitResult hitResult = (BlockHitResult) mc.crosshairTarget;
                    if (mc.world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof ChestBlock) {
                        client.setKeepRightClick(true);
                        Client.instance.setKeepBlockBreaking(false);

                    } else {
                        Client.instance.setKeepBlockBreaking(true);
                        client.setKeepRightClick(false);
                    }
                }


                tool2.switchTo();
                Vec3d lk = look;
                if (look == null) {
                    lk = target_chest.toCenterPos();
                }
                clientRotation.serverLook(RotationUtils.getNeededRotationsFix18(lk), 2, null);
                clientRotation.clientLook(RotationUtils.getNeededRotations(lk), 100, null);
            }

        }
    }

    private void findChest() {
        BlockPos r = null;
        for (BlockPos bp : chests) {
            if (r == null) {
                r = bp;
            } else {
                if (mc.player.getPos().distanceTo(r.toCenterPos()) > mc.player.getPos().distanceTo(bp.toCenterPos())) {
                    r = bp;
                }
            }
        }
        if (r == null) {
            return;
        }
        int range = 1;
        BlockPos t = null;
        ArrayList<BlockPos> remove = new ArrayList<>();
        boolean flag = true;
        for (int dx = -range; dx <= range; dx++) {
            for (int dz = -range; dz <= range; dz++) {
                for (int dy = -range - 3; dy <= range; dy++) {
                    if (dx == 0 && dz == 0 && dy == 0) continue;
                    BlockPos blockPos = r.add(dx, dy, dz);
                    if (!this.filterB(mc.world.getBlockState(blockPos).getBlock())) continue;
                    if (mc.world.getBlockState(blockPos).isAir()) continue;
                    if (!mc.world.getBlockState(blockPos.up()).isAir()) continue;
                    if (!mc.world.getBlockState(blockPos.up().up()).isAir()) continue;
//                    if(mc.world.getBlockState(blockPos.down()).isAir())continue;
//                    if(mc.world.getBlockState(blockPos.down().down()).isAir())continue;
                    BlockPos c = blockPos.up().up();
                    if (t == null) {
                        t = blockPos;
                        flag = false;
                    } else {
                        if (mc.player.getPos().distanceTo(t.toCenterPos()) > mc.player.getPos().distanceTo(blockPos.toCenterPos())) {
                            t = blockPos;
                            flag = false;
                        }
                    }
                }
            }
        }
        if (flag) {
            remove.add(r);
            blackList.add(r.getX() + " " + r.getY() + " " + r.getZ());
        }
        chests.removeAll(remove);
        if (t != null) {
            target_chest_node = t;
            target_chest = r;
        } else {
            target_chest_node = null;
            target_chest = null;
        }
    }

    public void updateChests() {
        int range = 10;
//        System.out.println(this.blackList);
        for (int x = -range; x < range; x++) {
            for (int y = -range; y < range; y++) {
                for (int z = -range; z < range; z++) {
                    BlockPos blockPos = mc.player.getBlockPos().add(x, y, z);
                    if (this.blackList.contains(blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ())) {
                        chests.remove(blockPos);
                        continue;
                    }
                    if (blockPos.getY() - 3 > mc.player.getBlockPos().getY()) continue;
                    BlockState blockState = mc.world.getBlockState(blockPos);
                    Block block = blockState.getBlock();
                    if (block instanceof ChestBlock) {
                        chests.add(blockPos.add(0, 0, 0));
                    }
                }
            }
        }
        ArrayList<BlockPos> remove = new ArrayList<>();
        for (BlockPos b : chests) {
            if (mc.world.getBlockState(b).getBlock() instanceof ChestBlock) {
//                if(mc.player.getPos().distanceTo(b.toCenterPos())>3){
//
//                }
                if (b.getY() - 3 > mc.player.getBlockPos().getY()) {
//                    this.blackList.add(b.getX()+" "+b.getY()+" "+b.getZ());
                    remove.add(b);
                }
            } else {
//                this.blackList.add(b.getX()+" "+b.getY()+" "+b.getZ());
                remove.add(b);
            }
        }
        chests.removeAll(remove);
        findChest();
        if (chests.size() >= this.count.getIntValue()) {
            if (this.status.equals("default")) {
                record.add(mc.player.getBlockPos().add(0, -1, 0));
                this.status = "chest";
            }
        }
    }

    private boolean isInCorrectArea(BlockPos blockPos) {
        if (blockPos.getX() >= 463 && blockPos.getX() <= 554 && blockPos.getY() >= 64 && blockPos.getY() <= 250 && blockPos.getZ() >= 460 && blockPos.getZ() <= 562) {
            return false;
        }
        return true;
    }

    public void doMob() {
        if (targetMob != null && mc.player.getPos().distanceTo(targetMob.getPos()) < 8 && mc.player.canSee(targetMob)) {
            this.status = "doMob";
            mc.options.sneakKey.setPressed(false);
            weapon.switchTo();
            if (!clientRotation.smoothRotation.running) {
                clientRotation.cancelClientLook();
                clientRotation.smoothRotation.smoothLook(RotationUtils.toRotation(targetMob.getEyePos().subtract(0, -4, 0).subtract(mc.player.getEyePos())), 3.3f, this::mobRTReady, true);
            }
            if (tickTimer > 2) {
                client.setKeepRightClick(true);
                tickTimer--;
            } else {
                client.setKeepRightClick(false);
            }
        } else {
            if (this.status.equals("doMob")) {
                this.status = "default";
                message.sendMessage(Text.of("§a恶徒清理完毕,继续去挖\"硬\"石了喵~"));
                if (tickTimer == 1) {
                    tickTimer = 0;
                }
            }
        }
    }

    public void updateMob() {
        if (!mobs.isValue()) return;
        targetMob = null;
        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player) continue;
            if (entity instanceof ArmorStandEntity && entity.isAlive() && entity.getDisplayName().getString().contains("[") && !entity.getDisplayName().getString().contains("Golden") && !entity.getDisplayName().getString().contains("Member")) {
                if (mc.player.distanceTo(entity) > 6) continue;
                if (entity.getDisplayName().getString().contains(" 0/")) continue;
                if (targetMob == null) {
                    targetMob = entity;
                } else {
                    if (mc.player.getPos().distanceTo(entity.getPos()) < mc.player.getPos().distanceTo(targetMob.getPos())) {
                        targetMob = entity;
                    }
                }
            }
        }
    }

    private void mobRTReady() {
        tickTimer++;
    }

    public void updateNext() {
        if (BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().isActive()) return;
        to = null;
        BlockPos temp = null;
        int count = 0;
        //循环三次
        int t = 0;
        int downflag = 0;
        for (int i = 0; i <= 8; i++) {
            Vec3d vec3d = mc.player.getCameraPosVec(1f).add(0, -1, 0);
            Vec3d vec3d2 = Vec3d.fromPolar(0, mc.player.headYaw + t);
            Vec3d vec3d3 = vec3d.add(vec3d2.x * 12, vec3d2.y * 12, vec3d2.z * 12);
            if (i == 1) t = 45;
            if (i == 2) t = -45;
            if (i == 5) t = 135;
            if (i == 6) t = -135;
            if (i == 7) t = -180;
            if (i == 8) {
                if (record.size() >= 2) {
                    temp = record.get(record.size() - 2);
                    break;
                }
            }
            HitResult hitResult = mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
            if (hitResult.getType().equals(HitResult.Type.BLOCK)) {
                if (hitResult instanceof BlockHitResult) {
                    if (filterB(mc.world.getBlockState(((BlockHitResult) hitResult).getBlockPos()).getBlock()) && isInCorrectArea(((BlockHitResult) hitResult).getBlockPos())) {
                        BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos().add(0, -1, 0);
                        ;
                        if (countBlock(blockPos) > 10) {
                            to = blockPos;
                            to = findForTo();
                            if (to == null || mc.world.getBlockState(to).isAir()) {
                                to = null;
                                continue;
                            }
                            if (to != null) {
                                break;
                            }
                        } else {
                            downflag++;
                        }
                    } else {
                        downflag++;
                    }
                }
            } else {
                downflag++;
            }
        }
//        message.sendMessage(Text.of(to==null?"null":to.toString()));
        if (to == null) {
            failed_time++;
            if (failed_time >= 2 || record.isEmpty()) {
                to = mc.player.getBlockPos();
                this.status = "down";
                failed_time = 0;
            } else if (failed_time == 1) {
                if (record.size() >= 2) {
                    to = record.get(record.size() - 2);
                    String lastnode = to.getX() + " " + to.getY() + " " + to.getZ();
                    lastnode = "回到上一个记录点§6 [" + lastnode + "] ";
                    message.sendMessage(Text.of("§a我找不到下一个点了呢~ 尝试回到" + lastnode + " 失败次数: " + failed_time));
                }
            } else if (failed_time == 2) {
                if (record.size() >= 3) {
                    to = record.get(record.size() - 3);
                    String lastnode = to.getX() + " " + to.getY() + " " + to.getZ();
                    lastnode = "回到上一个记录点§6 [" + lastnode + "] ";
                    message.sendMessage(Text.of("§a我找不到下一个点了呢~ 尝试回到" + lastnode + " 失败次数: " + failed_time));
                } else {
                    to = mc.player.getBlockPos();
                    this.status = "down";
                    failed_time = 0;
                }
            }
        } else {
            failed_time = 0;
        }

    }

    public int countBlock(BlockPos blockPos) {
        int c = 0;
        for (int x = -4; x < 8; x++) {
            for (int y = 1; y < 3; y++) {
                for (int z = -1; z < 8; z++) {
                    if (x == 0 && z == 0) continue;
                    BlockPos blockPos1 = blockPos.add(x, y, z);
                    if (filterB(mc.world.getBlockState(blockPos1).getBlock())) {
                        c++;
                    }
                }
            }
        }

        return c;
    }

    public BlockPos findForTo() {
        if (to == null) return null;
        BlockPos rtv = null;
        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                if (x == 0 && z == 0) continue;
                BlockPos blockPos = to.add(x, 0, z);
                if (mc.world.getBlockState(blockPos.add(0, 1, 0)).isAir() && mc.world.getBlockState(blockPos.add(0, 2, 0)).isAir()) {
                    BlockHitResult hitResult = mc.world.raycast(new RaycastContext(
                            mc.player.getEyePos(),
                            blockPos.toCenterPos().add(0, 0.4, 0),
                            RaycastContext.ShapeType.OUTLINE,
                            RaycastContext.FluidHandling.ANY,
                            mc.player));
                    if (hitResult instanceof BlockHitResult) {
                        if (!mc.world.getBlockState(blockPos.add(0, 1, 0)).isAir()) continue;
                        if (!mc.world.getBlockState(blockPos.add(0, 2, 0)).isAir()) continue;
                        if (mc.world.getBlockState(blockPos).getBlock().getName().toString().contains("glass"))
                            continue;

                        BlockPos tp = blockPos.add(0, 2, 0);
                        int flag = 0;
                        //前后左右
                        if (mc.world.getBlockState(tp.add(1, 0, 0)).isAir()) {
                            flag++;
                        }
                        if (mc.world.getBlockState(tp.add(-1, 0, 0)).isAir()) {
                            flag++;
                        }
                        if (mc.world.getBlockState(tp.add(0, 0, 1)).isAir()) {
                            flag++;
                        }
                        if (mc.world.getBlockState(tp.add(0, 0, -1)).isAir()) {
                            flag++;
                        }
                        if (flag > 1) {
                            if (hitResult.getBlockPos().add(0, 0, 0).equals(blockPos)) {
                                if (rtv == null) {
                                    rtv = blockPos;
                                } else {
                                    if (mc.player.getPos().distanceTo(rtv.toCenterPos()) > mc.player.getPos().distanceTo(blockPos.toCenterPos())) {
                                        rtv = blockPos;
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
        return rtv;
    }

    public BlockPos findForChest() {
        return target;
    }

    public boolean canAotvTo() {
        BlockPos t = this.status.equals("chest") ? target_chest_node : to;
        Vec3d vec3d = new Vec3d(mc.player.getX(), mc.player.getEyeY(), mc.player.getZ());
        Vec3d vec3d2 = t.toCenterPos().add(0, 0.4, 0);
        HitResult hitResult1 = mc.world.raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
        if (hitResult1 instanceof BlockHitResult) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult1;
            if (blockHitResult.getType() == HitResult.Type.BLOCK && blockHitResult.getBlockPos().add(0, 0, 0).equals(t)) {
                return true;
            }
        }
        return false;
    }

    public void processAotvTo() {
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        pathing_failed = 0;
    }

    @EventTarget
    public void onTick(ClientTickEvent e) {
//        if(mc.currentScreen!=null){
//            ModuleManager.instance.disable(this);
//        }
        if (Setting.getInstance().debug.isValue()) {
            chatOverride.setOverride("§aSTATUS: §r" + status + " §aBLOCKS: §r" + miningHelperV2.getSize() + " §aGP: §r§d" + powder_count2 + " §aMP: §r§2" + powder_count1 + " §aFailed: " + pathing_failed + " §aBlackList: " + blackList.size() + " §aChests: " + chests.size() + " §aRecord: " + record.size() + " §aTarget: " + (target == null ? "null" : target.toString()) + " §aTo: " + (to == null ? "null" : to.toString()) + " §aTargetChest: " + (target_chest == null ? "null" : target_chest.toString()) + " §aTargetChestNode: " + (target_chest_node == null ? "null" : target_chest_node.toString()));

        } else {
            chatOverride.setOverride("§aSTATUS: §r" + status + " §aBLOCKS: §r" + miningHelperV2.getSize() + " §aGP: §r§d" + powder_count2 + " §aMP: §r§2" + powder_count1 + " §aFailed: " + pathing_failed + " §alook: " + this.move_tick);

        }


//        flash++;
//        if(flash==20){
//            client.setKeepRightClick(false);
//            client.setKeepBlockBreaking(false);
//            return;
//        }

//        if ((this.pathing_failed >= 60 || this.pathing_failed == -1) && this.aotv.slot() != -1) {
//            mc.options.sneakKey.setPressed(true);
//            if (canAotvTo()) {
//                this.aotv.switchTo();
//                BlockPos t = this.status.equals("chest") ? target_chest_node : to;
//                if (pathing_failed != -1) {
//                    BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
//                    clientRotation.cancelServerLook();
//                    clientRotation.cancelClientLook();
//                    pathing_failed = -1;
//                    this.clientRotation.smoothRotation.smoothLook(RotationUtils.getNeededRotationsFix18(t.toCenterPos().add(0, 0.4, 0)), 2.4f, this::processAotvTo, true);
//                }
//                return;
//            } else {
//                if (this.status.equals("chest")) {
//                    this.blackList.add(target_chest.getX() + " " + target_chest.getY() + " " + target_chest.getZ());
//                    pathing_failed = -0;
//                }
//                if (this.status.equals("default")) {
//                    this.blackList.add(to.getX() + " " + to.getY() + " " + to.getZ());
//                    pathing_failed = -0;
//                }
//                return;
//            }
//        }
//        message.sendMessage(Text.of(miningHelperV2.getSize()+""));
        if (mc.crosshairTarget instanceof BlockHitResult) {
            BlockHitResult hitResult = (BlockHitResult) mc.crosshairTarget;
            if (mc.world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof ChestBlock) {
            }
        }
        if (look != null) {
            if (look1 != look) {
                look1 = look;
            } else {
                clear_tick++;
                if (clear_tick > 120) {
                    look = null;
                    clear_tick = 0;
                }
            }
        }
        if (this.status.equals("chest")) {
//            mc.options.useKey.setPressed(true);
        } else {
//            mc.options.useKey.setPressed(false);
        }
        int range = 4;
        updateMob();
        doMob();
        doEscape();
        updateChests();
        if (status.equals("chest")) {
            doChest();
        }
        if (status.equals("default")) {
            updateNext();
        }
        if (status.equals("doMob")) {
            return;
        }

        if (target != null) {
            if (!(mc.world.getBlockState(target).getBlock() instanceof ChestBlock) || target.toCenterPos().distanceTo(mc.player.getEyePos()) > 3) {
                target = null;
                look = null;
            } else {
                if (MathUtils.calculateAngle(mc.player.getRotationVec(1), target.toCenterPos().subtract(mc.player.getEyePos())) >= anglec.getIntValue()) {
                    target = null;
                    look = null;
                }
            }
        }

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

        if (false) {
        } else {
            ModuleManager.instance.enable(miningHelperV2);
            if (status.equals("default")) {
                tool.switchTo();

                if (to != null) {
                    if (mc.world.getBlockState(to).isAir()) {
                        to = null;
                        return;
                    }
                    if (mc.player.getPos().distanceTo(to.toCenterPos()) > 2.5) {
                        if (BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().isActive()) {
                            clientRotation.clientLook(RotationUtils.toRotation(to.add(0, 1, 0)), 50, null);
                            RotationFaker.instance.smoothRotation.smoothLook(RotationUtils.toRotation(to.add(0, 1, 0)), 2.0f, null, false);
                            miningHelperV2.following = false;
                            mc.options.sneakKey.setPressed(true);
                            Optional<IPath> optional = BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().getPath();
                            if (move_tick != -1) {
                                angle.setValue(move_tick);
                                move_tick = -1;
                            }
                            if (optional.isPresent()) {
                                this.pathing_failed = 0;
                                if (optional.get().length() > 9) {
                                    BaritoneAPI.getSettings().allowSprint.value = true;
                                } else {
                                    BaritoneAPI.getSettings().allowSprint.value = false;
                                }
                                if (optional.get().length() > 20 && this.status.equals("default")) {
                                    BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
                                    this.to = null;
                                }
                            } else {
                                if (move_tick == -1) {
                                    move_tick = angle.getIntValue();
                                    angle.setValue(179);
                                }

                                this.pathing_failed++;


                            }
                            return;
                        }
                        if (miningHelperV2.getSize() < 6) {


                            BaritoneAPI.getSettings().allowSprint.value = false;
                            BaritoneAPI.getSettings().avoidance.value = false;
                            BaritoneAPI.getSettings().allowWalkOnBottomSlab.value = true;
                            BaritoneAPI.getSettings().antiCheatCompatibility.value = true;
                            BaritoneAPI.getSettings().allowBreak.value = false;
                            BaritoneAPI.getSettings().allowParkour.value = false;
                            BaritoneAPI.getSettings().allowPlace.value = false;
                            BaritoneAPI.getSettings().allowParkourAscend.value = true;
                            BaritoneAPI.getSettings().renderGoal.value = false;
                            BaritoneAPI.getSettings().freeLook.value = true;
                            BaritoneAPI.getSettings().assumeSafeWalk.value = false;
                            BaritoneAPI.getSettings().renderPath.value = false;
                            record.add(to);
                            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalBlock(to.getX(), to.getY() + 1, to.getZ()));
                            countc = 0;
                        }
                    } else {
                        if (countc >= 80) {
                            if (move_tick != -1) {
                                angle.setValue(move_tick);
                                move_tick = -1;
                            }


                        } else {
                            countc++;
                        }

                        miningHelperV2.following = true;
                    }
                }
            }

            if (status.equals("down")) {
                if (mc.player.getBlockPos().getY() - to.getY() <= -3) {
                    status = "default";
                    clientRotation.clientLook(new Rotation(mc.player.headYaw, 0), 150, null);
                    return;
                }
                clientRotation.clientLook(new Rotation(mc.player.headYaw, 89), 50, null);
            }

        }

    }

    @EventTarget
    public void onMessage(GameMessageEvent e) {
        if (e.getText().contains("CHEST LOCKPICKED")) {
            this.blackList.add(target_chest.getX() + " " + target_chest.getY() + " " + target_chest.getZ());
            this.chests.remove(target_chest);
            this.look = null;
            this.target_chest_node = null;
            tick = 0;
        }
        if (e.getText().contains("Mithril Powder x")) {
            this.powder_count1 += Integer.parseInt(e.getText().replace("    Mithril Powder x", "").replace(",", ""));
        }
        if (e.getText().contains("Gemstone Powder x")) {
            this.powder_count2 += Integer.parseInt(e.getText().replace("    Gemstone Powder x", "").replace(",", ""));
        }
    }

    @EventTarget
    public void onRender(WorldRenderEvent e) {
        if (!render.isValue()) return;
        if (to != null) {
            RenderUtilsV2.renderOutlineBlock(e.getMatrixStack(), to, Theme.getInstance().THEME_UI_SELECTED.value);
        }

        if (look != null) {
            RenderUtilsV2.renderSolidBox(e.getMatrixStack(), new Box(look.x, look.y, look.z, look.x + 0.1, look.y + 0.1, look.z + 0.1), new Color(255, 218, 55, 255));
        }
        if (target_chest_node != null) {
            RenderUtilsV2.renderOutlineBlock(e.getMatrixStack(), target_chest_node, new Color(187, 50, 255, 255));
        }
        if (target_chest != null) {
            RenderUtilsV2.renderOutlineBlock(e.getMatrixStack(), target_chest, new Color(64, 50, 255, 255));
        }
        BlockPos last = null;
        for (BlockPos b : record) {
            if (last != null) {
                RenderUtilsV2.renderLine(e.getMatrixStack(), last.toCenterPos().add(0, 0.51, 0), b.toCenterPos().add(0, 0.51, 0), new Color(255, 218, 55, 255), 2f);
            }
//            RenderUtilsV2.renderOutlineBlock(e.getMatrixStack(),b, Theme.getInstance().THEME_UI_SELECTED.value);
            last = b;
        }
        for (BlockPos b : chests) {

            RenderUtilsV2.renderOutlineBlock(e.getMatrixStack(), b, Theme.getInstance().THEME_UI_SELECTED.value);
        }
    }

    @EventTarget
    public void onPacket(ServerPacketEvent e) {
        Packet packet = e.getPacket();
        if (packet instanceof ParticleS2CPacket p) {
            Vec3d pos = new Vec3d(p.getX(), p.getY(), p.getZ());
            if (target_chest != null) {
                if (target_chest.toCenterPos().distanceTo(pos) < 0.7) {
                    look = pos;
                }
            }

        }
        if (packet instanceof PlayerPositionLookS2CPacket) {
            PlayerPositionLookS2CPacket p = (PlayerPositionLookS2CPacket) packet;
            if (!p.getFlags().contains((Object) PositionFlag.X_ROT) || (!p.getFlags().contains((Object) PositionFlag.Y_ROT))) {
                boolean bl = p.getFlags().contains((Object) PositionFlag.X);
                boolean bl2 = p.getFlags().contains((Object) PositionFlag.Y);
                boolean bl3 = p.getFlags().contains((Object) PositionFlag.Z);
                if ((!bl) || (!bl2) || (!bl3)) {
                    if (new Vec3d(p.getX(), p.getY(), p.getZ()).distanceTo(mc.player.getPos()) > 1.4) {
                        if (this.aotv.slot() != mc.player.getInventory().selectedSlot) {
                            escape("被未知的东西旋转");
                        }
                    }


                }
            }
        }

    }

    public boolean escape(String reason) {
        if (!escape.isValue()) return false;
        if (this.status.equals("stop")) {
            return false;
        }

        String c = "警告";
        if (!warning.isValue()) {
            this.status = "stop";
            ModuleManager.instance.disable(this);
            c = "逃逸";
        }
        SkyMatrix.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2.0f, 10));
        SkyMatrix.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2.0f, 10));
        SkyMatrix.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2.0f, 10));
        SkyMatrix.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2.0f, 10));
        SkyMatrix.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2.0f, 10));
        SkyMatrix.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2.0f, 10));
        SkyMatrix.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2.0f, 10));
        SkyMatrix.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2.0f, 10));
        this.message.sendMessage(Text.of("§a看来你触发了" + c + "呢~ §b原因是: " + reason + "§a?"));


        return true;
    }

    public void doEscape() {
        if (mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            assert mc.targetedEntity != null;
            escape("你正在看着  " + (mc.targetedEntity).getDisplayName().getString());
        }
    }

    @EventTarget
    public void onAntiCheck(ServerRotationEvent event) {
//        escape("被未知的东西旋转");
    }

    @EventTarget
    public void onAntiCheck(LookLockEvent event) {
        escape("有人一直看着你");
    }


    private boolean filterP(BlockPos blockPos) {
        if (status.equals("doMob")) return false;
        if (status.equals("down")) {
            if (blockPos.getY() < mc.player.getBlockPos().getY() && blockPos.getY() > to.getY() - 4) {
                BlockPos c = blockPos.withY(0);
                BlockPos d = mc.player.getBlockPos().withY(0);
                if (c.getSquaredDistance(d) <= 2) {
                    return true;
                }
            }
            return false;
        }
        if (!isInCorrectArea(blockPos)) {
            return false;
        }
        if (BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().isActive()) {
            Optional<IPath> optional = BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().getPath();
            if (optional.isPresent()) {
                if (optional.get().length() > 9) {
                    return false;
                }
            }
        }
        if (MathUtils.calculateAngle(mc.player.getRotationVec(1).multiply(1, 0, 1), blockPos.toCenterPos().multiply(1, 0, 1).subtract(mc.player.getEyePos().multiply(1, 0, 1))) >= angle.getIntValue()) {
            return false;
        }
        return blockPos.getY() > mc.player.getBlockPos().getY() - 1 && blockPos.getY() < mc.player.getBlockPos().getY() + 3;
    }

    private boolean filterB(Block block) {
        if (this.status.equals("down") && block != Blocks.CHEST) return true;
        double count = 0;
        for (BlockPos blockPos : miningHelperV2.getpos()) {
            Block block1 = mc.world.getBlockState(blockPos).getBlock();
            if (block1 == Blocks.PRISMARINE || block1 == Blocks.PRISMARINE_BRICKS || block1 == Blocks.DARK_PRISMARINE) {
                count++;
            }
        }
        if (count / miningHelperV2.getpos().size() > 0.5f) {
            if (block == Blocks.PRISMARINE || block == Blocks.PRISMARINE_BRICKS || block == Blocks.DARK_PRISMARINE) {
                return false;
            } else {
                return true;
            }
        }
        if (gemstone.isValue()) {
            if (block.getName().toString().contains("glass")) return true;
        }

        return block.getName().toString().contains("block.minecraft.stone") || block.getName().toString().contains("block.minecraft.cobblestone") || block.getName().toString().contains("ore") || block.getName().toString().contains("prismarine");
    }

    @Override
    public void enable() {
        if (!FabricLoader.getInstance().isModLoaded("baritone")) {
            throw new RuntimeException("§c你没有安装Baritone");
        }
        message.sendMessage(Text.of("§a建议主人在开始前关闭§b跳跃提升§a喵~"));
        chests.clear();
        blackList.clear();
        target_chest = null;
        target_chest_node = null;
        look = null;
        status = "default";
        record.clear();
        miningHelperV2.addFilter(this::filterB);
        miningHelperV2.addFilter(this::filterP);
        miningHelperV2.rclient = false;
        miningHelperV2.following = true;
        ModuleManager.instance.enable(miningHelperV2);
        mc.mouse.unlockCursor();

    }

    @EventTarget
    public void onUpdateHitResult(UpdateTargetedEntityEvent.Post event) {
        if (chest_failed > 56) {
            if (mc.player.getEyePos().distanceTo(target_chest.toCenterPos()) < 3.5) {
                mc.crosshairTarget = new BlockHitResult(target_chest.toCenterPos(), Direction.UP, target_chest, false);

            }
        }
    }

    @Override
    public void disable() {
        move_tick = -1;
        powder_count1 = 0;
        powder_count2 = 0;
        client.setKeepRightClick(false);
        miningHelperV2.targets.clear();
        ModuleManager.instance.disable(miningHelperV2);
        miningHelperV2.clearFilter();
        miningHelperV2.rclient = false;
        miningHelperV2.following = false;
        BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
        mc.options.sneakKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
        mc.options.leftKey.setPressed(false);
        mc.mouse.lockCursor();
        failed_time = 0;
        pathing_failed = 0;
        chatOverride.clearOverride();
    }


}
