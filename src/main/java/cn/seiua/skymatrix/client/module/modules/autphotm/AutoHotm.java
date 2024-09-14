package cn.seiua.skymatrix.client.module.modules.autphotm;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.Client;
import cn.seiua.skymatrix.client.HypixelWay;
import cn.seiua.skymatrix.client.IToggle;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.Init;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.module.ModuleManager;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.client.module.modules.life.MiningHelperV2;
import cn.seiua.skymatrix.client.rotation.Rotation;
import cn.seiua.skymatrix.client.rotation.RotationFaker;
import cn.seiua.skymatrix.config.Hide;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.*;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.*;
import cn.seiua.skymatrix.gui.ClickGui;
import cn.seiua.skymatrix.gui.Theme;
import cn.seiua.skymatrix.hud.ClientHud;
import cn.seiua.skymatrix.hud.Hud;
import cn.seiua.skymatrix.message.Message;
import cn.seiua.skymatrix.message.MessageBuilder;
import cn.seiua.skymatrix.render.BlockLocTarget;
import cn.seiua.skymatrix.utils.*;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;
import java.util.*;

import static cn.seiua.skymatrix.SkyMatrix.mc;
import static cn.seiua.skymatrix.utils.PlayerListUtils.playerOrdering;

@Event
@Sign(sign = Signs.BETA)
@SModule(name = "AutoHotm", category = "skyblock")
public class AutoHotm implements IToggle, Hud {
    private final HashSet<Entity> blackList = new HashSet<Entity>();
    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(List.of(GLFW.GLFW_KEY_H), ReflectUtils.getModuleName(this));
    @Value(name = "hud")
    public ClientHud clientHud = new ClientHud(30, 30, true, this);
    @Value(name = "render nodes", desc = "显示所有节点")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch renderNodes = new ToggleSwitch(false);
    @Value(name = "use ability", desc = "自动使用技能")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch ability = new ToggleSwitch(true);
    @Value(name = "aotv tick", desc = "你的山心等级")
    @Sign(sign = Signs.BETA)
    public ValueSlider aotv_tick = new ValueSlider(20, 10, 200, 1);
    @Value(name = "mining", desc = "做挖矿任务")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch op_mining = new ToggleSwitch(true);
    @Value(name = "slayer", desc = "做冰人哥布林任务")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch op_slayer = new ToggleSwitch(true);

    @Value(name = "ice Walker", desc = "做冰人哥布林任务")
    @Hide(following = "slayer")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch op_slayer_walker = new ToggleSwitch(true);

    @Value(name = "goblin", desc = "做冰人哥布林任务")
    @Hide(following = "slayer")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch op_slayer_goblin = new ToggleSwitch(true);


    @Value(name = "your hotm level", desc = "你的山心等级")
    @Sign(sign = Signs.BETA)
    public ValueSlider level = new ValueSlider(6, 1, 7, 1);
    @Value(name = "submit count", desc = "你的山心等级")
    @Sign(sign = Signs.BETA)
    public ValueSlider count = new ValueSlider(1, 1, 4, 1);
    // 状态  pathing, doing , none
    public String status = "none";
    public String task = null;
    public String name = null;
    // 位置
    public String spot = null;
    public String status1 = "none";

    // Goblin IceWalker Mining Pathing
    @Value(name = "aotv", desc = "你的aotv")
    @Sign(sign = Signs.BETA)
    SkyblockItemSelect aotv = new SkyblockItemSelect("", false, Selector::bestAote, Filter::aote);
    @Value(name = "tool", desc = "你的挖矿工具")
    @Sign(sign = Signs.BETA)
    SkyblockItemSelect tool = new SkyblockItemSelect("", false, Selector::bestMiningTool, Filter::miningTool);
    @Value(name = "weapon", desc = "你的武器")
    @Sign(sign = Signs.BETA)
    SkyblockItemSelect weapon = new SkyblockItemSelect("", false, null, Filter::weaponFilter);
    @Value(name = "royal_pigeon", desc = "你的武器")
    @Sign(sign = Signs.BETA)
    SkyblockItemSelect royal = new SkyblockItemSelect("", false, Selector::royal_Pigeon, Filter::royal_Pigeon);
    @Use
    MiningHelperV2 miningHelper;
    TickTimer cd = TickTimer.build(20, this::tp);
    TickTimer reset = TickTimer.build(10, this::reset);
    private Message message= MessageBuilder.build("AutoHotm");
    @Use
    HypixelWay hypixelWay;
    int tick = 0;
    int cTick = 0;
    int nTick = 0;
    private Node forge;
    private int readyCount = 0;
    private boolean arrived;
    private int moveTick = 0;
    @Use
    private NodeManager nodeManager;
    @Use
    private RotationFaker rotationFaker;
    private ArrayList<Node> nodes;
    private int step = 0;
    private Node target;
    int ctt;
    @Init
    public void init() {

    }
    private boolean disable_ab;
    int cdd;
    private String focusTask;
    private boolean flag;

    private void useAbility(){

    }

    public void keepWorld() {
        if (this.focusTask == null || !this.focusTask.equals("keepworld")) {
            this.focusTask = "keepworld";
            cdd = 0;
            ctt = 0;
        }
    }

    public void doKeepWorld() {
        if (this.focusTask.equals("keepworld")) {
            cdd++;
            if (ctt >= 40) {
                mc.getNetworkHandler().sendCommand("warp forge");
                this.message.sendMessage(Text.of("warp forge"));
                cdd = 0;
                this.focusTask = null;
            }
        }
    }

    public void doSwitchServer() {
        if (focusTask == null) return;
        ctt++;
        if (focusTask.equals("switch")) {
            if (cdd == 0 && ctt == 40) {
                mc.getNetworkHandler().sendCommand("warp hub");
                cdd++;
            }
            if (cdd == 1 && ctt == 80) {
                mc.getNetworkHandler().sendCommand("warp forge");
                cdd++;
            }
            if (cdd == 2 && ctt == 120) {
                focusTask = null;
                cdd = 0;
            }
        }
    }

    public void switchServer() {
        cdd = 0;
        focusTask = "switch";
    }

    @EventTarget
    public void onPacket(ServerPacketEvent event) {
        if (event.getPacket() instanceof GameMessageS2CPacket) {
            GameMessageS2CPacket eventPacket = (GameMessageS2CPacket) event.getPacket();
            if (this.task != null && this.task.equals("mining") && arrived) {
                if (eventPacket.content().getString().contains("is now available!")) {
                    mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                }
            }

        }
    }

    private void shoot() {
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
    }

    private ArrayList<String> good_mithril() {
        ArrayList<String> ores = new ArrayList<>();
        ores.add("rich_mithril");
        ores.add("medium_mithril");
        ores.add("poor_mithril");
        ores.add("low_mithril");
        return ores;
    }

    private ArrayList<String> shit_mithril() {
        ArrayList<String> ores = new ArrayList<>();
        ores.add("lack_mithril");
        ores.add("shit_mithril");
        return ores;
    }

    private ArrayList<String> titanium() {
        ArrayList<String> ores = new ArrayList<>();
        ores.add("titanium_ore");
        return ores;
    }

    public void reset() {
        mc.options.forwardKey.setPressed(false);
        mc.options.backKey.setPressed(false);
        mc.options.leftKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
    }

    public void tp() {

    }

    public void rotationReady() {
        if (mc.currentScreen == null) {
            mc.options.useKey.setPressed(true);
        }
    }

    @EventTarget
    public void registerCommand(CommandRegisterEvent e) {
        e.getDispatcher().register(
                ClientCommandManager.literal("ahotm")
                        .then(
                                ClientCommandManager.literal("to").then(
                                        ClientCommandManager.argument("tag", StringArgumentType.string()).executes(this::to)
                                )
                        )
        );
    }

    private int to(CommandContext<FabricClientCommandSource> fabricClientCommandSourceCommandContext) {
        String target = StringArgumentType.getString(fabricClientCommandSourceCommandContext, "tag");
        generatePath();
        return 0;
    }

    private void generatePath() {
        int min = Integer.MAX_VALUE;
        Node start = null;
        for (Node node : nodeManager.nodes) {
            BlockHitResult hitResult = mc.world.raycast(new RaycastContext(mc.player.getPos().add(0, 1.54, 0), node.toBlockPos().toCenterPos().add(0, 0.48, 0), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, mc.player));
            if (hitResult.getBlockPos().equals(node.toBlockPos())) {
                if (node.toBlockPos().getManhattanDistance(mc.player.getBlockPos()) < min) {
                    start = node;
                    min = node.toBlockPos().getManhattanDistance(mc.player.getBlockPos());
                }

            }
        }
        Node last = null;
        min = Integer.MAX_VALUE;
        for (Node node : nodeManager.nodes) {
            Node selected = null;
            if (task.equals("submit")) {
                if (node.tags.contains("submission")) {
                    selected = node;
                }
            }
            if (task.equals("mining")) {
                if (spot.equals("")) {
                    if (node.tags.contains("type:miningSpot")) {
                        selected = node;
                    }
                }
                if (node.tags.contains("Location:" + spot) && node.tags.contains("type:miningSpot")) {
                    selected = node;
                }
            }
            if (task.equals("slayer")) {
                if (spot.equals("goblin")) {
                    if (node.tags.contains("GOBLIN")) {
                        selected = node;
                    }
                }
                if (spot.equals("walker")) {
                    if (node.tags.contains("ICEWALKER")) {
                        selected = node;
                    }
                }
                if (spot.equals("treasure")) {
                    if (node.tags.contains("Location:treasure")) {
                        selected = node;
                    }
                }

            }
            if (selected != null) {
                if (selected.toBlockPos().getManhattanDistance(mc.player.getBlockPos()) < min) {
                    last = selected;
                    min = selected.toBlockPos().getManhattanDistance(mc.player.getBlockPos());
                }
            }

        }
        if (nodes == null) {
            nodes = new ArrayList<>();
        }
        if (last != null && start != null) {
            this.arrived = false;
            this.target = last;
            nodes.clear();
            nodes.addAll(Pathfinding.findPath(start, target, nodeManager.nodes));
            ArrayList<Node> temp = new ArrayList<>(Pathfinding.findPath(forge, target, nodeManager.nodes));
            if (nodes.isEmpty()) {
                if (this.tick % 80 == 0) Objects.requireNonNull(mc.getNetworkHandler()).sendCommand("warp forge");

            }
            if (temp.size() < nodes.size()) {
                nodes.clear();
                nodes.addAll(temp);
                Objects.requireNonNull(mc.getNetworkHandler()).sendCommand("warp forge");
            }

        } else {
            nodes.clear();
            this.target = null;
            // 待添加到设置 由用户决定
        }
    }

    private void switchStatus(String status) {
        this.status = status;
    }

    public void updateTask() {

        if ((mc.currentScreen instanceof HandledScreen<?>)) return;
        if (mc.getNetworkHandler() == null) return;
        String task1 = null;
        String spot = null;
        readyCount = 0;
        List<PlayerListEntry> players = playerOrdering.sortedCopy(mc.getNetworkHandler().getPlayerList());
        boolean flag = false;
//        System.out.println("---");

        for (PlayerListEntry info : players) {
            String name = mc.inGameHud.getPlayerListHud().getPlayerName(info).getString();
            if (name.contains("Commission")) {
                flag = true;
                continue;
            }
            if (flag) {

                if (name.isEmpty()) {
                    break;
                }

                String target = "";
                boolean isReady = name.contains("DONE");
                if(name.contains("Powder Collector"))continue;
                // 优先级 slayer 带位置的 mining 不带位置的 mining
                if (!isReady) {
                    if (this.op_mining.isValue()) {
                        if ("slayer".equals(task1)) {
                            continue;
                        }
                        if (name.contains("Titanium") || name.contains("Mithril")) {
                            target = (name.contains("Titanium") ? "Titanium" : "Mithril").trim();
                            int index = name.indexOf(target);
                            String old = spot;
                            spot = name.substring(0, index).trim();
//                            System.out.println(name);
                            if (spot.isBlank() && "mining".equals(task1)) {
                                spot = old;
                                continue;
                            }

                            task1 = "mining";
                            miningHelper.targets.clear();
                            this.miningHelper.targets.add("Prismarine");
                            this.miningHelper.targets.add("Light Blue Wool");
                            this.miningHelper.targets.add("Polished Diorite");
                            this.miningHelper.targets.add("Cyan Terracotta");

                        }
                    }
                    if (this.op_slayer.isValue()) {
                        if ("slayer".equals(task1)) {
                            return;
                        }
                        String t1 = null;
                        String t2 = null;
                        if (name.contains("Goblin Slayer") && !name.contains("Golden")&&op_slayer_goblin.isValue()) {
                            t1 = "slayer";
                            t2 = "goblin";
                        }
                        if (name.contains("Glacite Walker") && op_slayer_walker.isValue()) {
                            t1 = "slayer";
                            t2 = "walker";
                        }
                        if (name.contains("Treasure Hoarder Slayer")) {
                            t1 = "slayer";
                            t2 = "treasure";
                        }
                        if (t1 != null) {
                            task1 = t1;
                            spot = t2;
                        }

                    }
                } else {
                }
                if (isReady) {
                    readyCount++;
                }

            }

        }
        if (readyCount >= count.getIntValue()) {
            task1 = "submit";
        }
        if (task1 != null) {
            if (!task1.equals(this.task) || (spot != null && !spot.equals(this.spot))) {
                message.sendMessage(Text.of("下一个任务:"+task1+" 位置:"+spot));
                mc.options.forwardKey.setPressed(false);
                mc.options.backKey.setPressed(false);
                mc.options.leftKey.setPressed(false);
                mc.options.rightKey.setPressed(false);
                status = "none";
            }
            this.task = task1;
            this.spot = spot;
        }


    }

    @EventTarget
    public void onRender(WorldRenderEvent e) {
        if (nodes == null) {
            return;
        }
        Node last = null;
        for (Node node : nodes) {
            BlockLocTarget blockLocTarget = new BlockLocTarget(node.toBlockPos(), Theme.getInstance().THEME_UI_SELECTED::geColor);
            blockLocTarget.render(e.getMatrixStack(), e.getTickDelta());
            if (last != null) {
                RenderUtilsV2.renderLine(e.getMatrixStack(), last.toBlockPos(), node.toBlockPos(),Theme.getInstance().THEME_UI_SELECTED.geColor(),2f);
            }
            last = node;
        }
    }

    private void doPathing() {

        if(task.equals("submit")&& !Objects.equals(royal.getUuid(), "")&&royal.getUuid()!=null){
            arrived=true;
            status="doing";
            return;
        }
        if (step < nodes.size()) {
            this.aotv.switchTo();
            Vec3d bp = nodes.get(step).toBlockPos().toCenterPos().add(0, 0.5, 0);
            assert mc.player != null;
//            Rotation rotation = RotationUtils.getNeededRotations(bp.subtract(mc.player.getPos().add(0, 1.54, 0)));

            Rotation rotation = RotationUtils.getNeededRotationsFix18(bp);
                rotationFaker.smoothRotation.smoothLook(rotation, 2.4f, () -> {
//                    if (((OneTickTimer) cd).getTick() <= 1) {
//                        assert mc.interactionManager != null;
//                        mc.interactionManager.interactItem(mc.player, mc.player.getActiveHand());
//                        ((OneTickTimer) cd).setTick(aotv_tick.getValue().intValue());
//                    }
                }, true);


            MinecraftClient.getInstance().options.sneakKey.setPressed(true);
            BlockHitResult hitResult = SkyMatrix.mc.world.raycast(new RaycastContext(mc.player.getPos().add(0, 1.54, 0),nodes.get(step).toBlockPos().toCenterPos().add(0, 0.5, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, SkyMatrix.mc.player));
//            System.out.println(hitResult.getBlockPos());
//            System.out.println(nodes.get(step).toBlockPos());
//            System.out.println("--");
            //待修复
            BlockPos bp2=nodes.get(step).toBlockPos().withY(0);
            BlockPos bp1= hitResult.getBlockPos().withY(0);
            moveTick++;
            if(bp2.equals(bp1)){
                cd.update();

                if (((OneTickTimer) cd).getTick() <= 1) {
                    assert mc.interactionManager != null;
                    mc.interactionManager.interactItem(mc.player, mc.player.getActiveHand());
                    ((OneTickTimer) cd).setTick(aotv_tick.getValue().intValue());
                    moveTick=0;
                }
            }else {
                if(moveTick>20*6){
                        mc.getNetworkHandler().sendCommand("warp forge");
                        this.status = "none";
                         moveTick=0;
                         this.nodes.clear();
                            this.step=0;
                }
            }

            MinecraftClient.getInstance().options.sneakKey.setPressed(true);
            arrived = false;
        } else {
            status = "doing";
            Client.sendDebugMessage(Text.of("over"));
            arrived = true;
            nodes.clear();
            step = 0;
            return;
        }
        if (nodes.get(step).toBlockPos().toCenterPos().distanceTo(mc.player.getPos()) < 2.4) {
            step++;
            Client.sendDebugMessage(Text.of("rotating to " + nodes.get(step - 1).toBlockPos().toCenterPos().add(0, 0.5, 0)));
            Client.sendDebugMessage(Text.of("step " + step + " of " + nodes.size()));
        }


    }

    @EventTarget
    public void onWorldChange(WorldChangeEvent event) {
        flag = true;
    }

    @EventTarget
    public void onTick(ClientTickEvent e) {
        if (tick++ % 20 == 0) {
        }
//        if(this.flag&&this.focusTask==null&&!this.hypixelWay.way().equals("Dwarven Mines")){
//            this.flag=false;
//            this.focusTask="keepworld";
//        }
        if (this.hypixelWay.way().equals("Dwarven Mines")) {
            this.flag = false;
//            if(this.focusTask!=null&&this.focusTask.equals("keepworld")){
//                this.focusTask=null;
//            }
        } else {
            keepWorld();
        }
        if (focusTask != null) {
            doKeepWorld();
            doSwitchServer();
            status = "none";
            step = 0;
            arrived = false;
            this.tick = 0;
            this.moveTick = 0;
            this.cTick = 0;
            this.nTick = 0;
            this.blackList.clear();
            this.target = null;
            this.task = null;
            this.spot = null;
            this.name = null;
            this.readyCount = 0;
            return;
        }

        if (!hypixelWay.way().equals("Dwarven Mines")) {
            // warp back or disable this module
            return;
        }
        mc.mouse.unlockCursor();
        if (status.equals("pathing") && !this.arrived) {
            doPathing();
        }
        updateTask();

        if (status.equals("none")) {
            this.generatePath();
            if (!nodes.isEmpty()) {
                status = "pathing";
            }
        }

        if (status.equals("doing")) {

            update();
        } else {

                ModuleManager.instance.disable(miningHelper);

        }

    }

    @Override
    public void disable() {
        SkyMatrix.mc.mouse.lockCursor();
        reset();
        miningHelper.targets.clear();
        miningHelper.clearFilter();
        ModuleManager.instance.disable(miningHelper);
        miningHelper.rclient=false;

    }

    private void update() {
        if (this.task.equals("submit")) {
            if (mc.currentScreen instanceof HandledScreen<?>) {
                HandledScreen<?> screen = (HandledScreen<?>) mc.currentScreen;
                Client.sendDebugMessage(Text.of(screen.getTitle().getString()));
                if (screen.getTitle().getString().contains("Commission")) {
                    assert mc.interactionManager != null;
                    nTick++;
                    if (nTick == 5)
                        mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, 11, 0, SlotActionType.CLONE, mc.player);
                    if (nTick == 10)
                        mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, 12, 0, SlotActionType.CLONE, mc.player);
                    if (nTick == 15)
                        mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, 13, 0, SlotActionType.CLONE, mc.player);
                    if (nTick == 20)
                        mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, 14, 0, SlotActionType.CLONE, mc.player);
                    if (nTick == 25)
                        mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, 15, 0, SlotActionType.CLONE, mc.player);
                    if (nTick > 30) mc.setScreen(null);
                }
            } else {
                nTick = 0;
            }
            if (arrived) {
                cTick++;

                if(!Objects.equals(royal.getUuid(), "")&&royal.getUuid()!=null){
                    this.royal.switchTo();
                    if (cTick % 20 == 0) {
                        this.rotationReady();
                    }
                    return;
                }else {
                    this.tool.switchTo();
                }
                if (!rotationFaker.smoothRotation.running) {
                    if (mc.currentScreen == null) {
                        if (cTick % 20 == 0) {
                            rotationFaker.smoothRotation.smoothLook(RotationUtils.toRotation(new BlockPos(129, 196, 196)), 3f, this::rotationReady, true);
                        }
                    }
                }
            }
        } else {
            nTick = 0;
        }

        if (this.task.equals("mining")) {
            mc.options.useKey.setPressed(false);
            this.tool.switchTo();
                ModuleManager.instance.enable(miningHelper);
            if (this.ability.isValue() && moveTick % (20 * 30) == 0) {
                assert mc.interactionManager != null;
                assert mc.player != null;
                mc.interactionManager.interactItem(mc.player, mc.player.getActiveHand());
            }
        }
        if (this.task.equals("slayer")) {
            if (tick % 60 == 0) this.blackList.clear();
            ArrayList<Entity> targets = new ArrayList<>();
            this.weapon.switchTo();
            for (Entity e : mc.world.getEntities()) {
                if (e instanceof ArmorStandEntity) {
                    if (e.getPos().distanceTo(mc.player.getPos()) < 100) {
                        String name = e.getDisplayName().getString();
                        if ((name.contains("Glacite Walker") && spot.equals("walker")) || (name.contains("Goblin") || name.contains("Knife") && spot.equals("goblin")) || (name.contains("Treasure Hoarder") && spot.equals("treasure"))) {
                            if (!blackList.contains(e)) {
                                if (mc.player.canSee(e)) {
                                    targets.add(e);
                                }
                            }
                        }
                    }
                }
            }
            if (!targets.isEmpty()) {
                Entity target = targets.get(0);
                if (!rotationFaker.smoothRotation.running) {
                    rotationFaker.smoothRotation.smoothLook(RotationUtils.toRotation(target.getEyePos().subtract(0, 1.5, 0).subtract(SkyMatrix.mc.player.getEyePos())), 3.9f, this::shoot, true);
                    this.blackList.add(target);
                }

            }

        }
        if ((this.task.equals("mining") || this.task.equals("slayer")) && arrived) {
            mc.options.sneakKey.setPressed(true);
            moveTick++;
            this.reset.update();
            if (moveTick % 200 == 0) {
                if (new Random().nextBoolean()) mc.options.forwardKey.setPressed(true);
                if (new Random().nextBoolean()&&new Random().nextBoolean()&&new Random().nextBoolean()) mc.options.backKey.setPressed(true);
                if (new Random().nextBoolean()) mc.options.leftKey.setPressed(true);
                if (new Random().nextBoolean()) mc.options.rightKey.setPressed(true);
                this.reset.reset();
            }
        }

    }
    private boolean filterP(BlockPos blockPos){

        return true;
    }

    private boolean filterB(Block block){
        String name = block.getName().toString();
        if (name.contains("polished_diorite") || name.contains("light_blue_wool") || name.contains("cyan_terracotta") || name.contains("prismarine'") || name.contains("prismarine_bricks") || name.contains("_wool"))
            return true;else return false;
    }

    @Override
    public void enable() {
        if (nodeManager.nodes == null || nodeManager.nodes.isEmpty()) {
            throw new RuntimeException("未加载 nodes.json 文件 请检查配置文件是否存在或者配置文件是否正确或在爱发电上购买 放到.minecraft/skymatrix/nodes.json 后重启游戏");
        }
        if (nodes == null) {
            nodes = new ArrayList<>();
        }
        miningHelper.targets.add("x1x11x1");
        miningHelper.addFilter(this::filterB);
        miningHelper.rclient=true;
        miningHelper.addFilter(this::filterP);
        status = "none";
        step = 0;
        arrived = false;
        this.tick = 0;
        this.moveTick = 0;
        this.cTick = 0;
        this.nTick = 0;
        this.blackList.clear();
        this.target = null;
        this.task = null;
        this.spot = null;
        this.name = null;
        this.readyCount = 0;
        if (forge == null) {
            for (Node node : nodeManager.nodes) {
                if (node.tags.contains("FORGE")) {
                    forge = node;
                }
            }
        }
        if (forge == null) {
            throw new RuntimeException("未找到Forge节点 请检查nodes.json文件");
        }

    }

    @Override
    public void draw(MatrixStack matrixStack, float x, float y) {
        if (this.nodeManager.nodes == null || this.nodeManager.nodes.isEmpty()) {
            return;
        }
        RenderUtils.resetCent();
        RenderUtils.setColor(new Color(0, 0, 0, 100));
        ClickGui.fontRenderer20.centeredH();
        ClickGui.fontRenderer20.centeredV();
        float startX = x + 40;
        float startY = y + 40;
        if(nodes==null)return;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "状态: " + status + " 位置: " + spot + " 当前任务: " + task + " 目标: " + target + " 是否到达: " + arrived + "  nodes: " + step + "/" + nodes.size());

    }


    @Override
    public int getHudWidth() {
        return 500;
    }

    @Override
    public int getHudHeight() {
        return 53;
    }
}
