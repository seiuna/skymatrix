package cn.seiua.skymatrix.client.module.modules.autphotm;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.Client;
import cn.seiua.skymatrix.client.HypixelWay;
import cn.seiua.skymatrix.client.component.*;
import cn.seiua.skymatrix.client.config.Setting;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.KeyBind;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.CommandRegisterEvent;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import cn.seiua.skymatrix.gui.Theme;
import cn.seiua.skymatrix.hud.ClientHud;
import cn.seiua.skymatrix.hud.Hud;
import cn.seiua.skymatrix.message.Message;
import cn.seiua.skymatrix.message.MessageBuilder;
import cn.seiua.skymatrix.render.BlockLocTarget;
import cn.seiua.skymatrix.utils.MathUtils;
import cn.seiua.skymatrix.utils.RenderUtilsV2;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import org.apache.commons.io.FileUtils;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 基于A*算法的aotv寻路系统
 */
@Category(name = "nodeManager")
@Event(register = true)
@Config(name = "nodeManager")
public class NodeManager implements Hud {
    public ArrayList<Node> nodes = new ArrayList<>();
    public ArrayList<Node> targets = new ArrayList<>();
    public Node selected;
    public BlockPos blocked;
    public Node selecting;
    @Value(name = "node")
    public ClientHud clientHud = new ClientHud(100, 100, true, this);
    Message message = MessageBuilder.build("NodeManager");
    @Use
    HypixelWay hypixelWay;
    @Use
    AutoHotm autoHotm;
    @Value(name = "save")
    private KeyBind save = new KeyBind("save", Arrays.asList(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_S), this::save);
    @Value(name = "close")


    private KeyBind keyBind = new KeyBind("close", Arrays.asList(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_A), this::close1);
    @Value(name = "up")
    private KeyBind up = new KeyBind("close", Arrays.asList(GLFW.GLFW_KEY_UP), this::up);
    private String mode = "add";
    @Value(name = "delete")
    private KeyBind delete = new KeyBind("delete", Arrays.asList(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_D), this::delete);
    private List<String> modes = Arrays.asList("edit", "select", "add", "none");
    @Value(name = "down")
    private KeyBind down = new KeyBind("close", Arrays.asList(GLFW.GLFW_KEY_DOWN), this::down);
    @Value(name = "mouse2")
    private KeyBind mouse2 = new KeyBind("add", List.of(KeyBind.getMouseKey(GLFW.GLFW_MOUSE_BUTTON_2)), this::mouse2);
    private String status;
    private String status1;
    private BlockPos current = null;
    private String Atag;
    @Value(name = "mouse1")
    private KeyBind mouse1 = new KeyBind("add", List.of(KeyBind.getMouseKey(GLFW.GLFW_MOUSE_BUTTON_1)), this::mouse1);

    private void save() {
        if (!Setting.getInstance().debug.isValue()) return;
        try {
            FileUtils.writeStringToFile(new File(Client.root, "nodes.json"), new Gson().toJson(nodes), "utf-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        if (mode.equals("edit") && selected != null && selecting != null) {
//            targets.addAll(Pathfinding.findPath(selected, selecting, nodes));
//
//        }

    }

    private void delete() {
        if (!Setting.getInstance().debug.isValue()) return;
        if (mode.equals("select")) {
            if (selecting != null) {
                nodes.remove(selecting);
                for (Node node : selecting.getRounds()) {
                    node.getRounds().remove(selecting);
                }
                selected = null;
                selecting = null;
            }
        }
    }

    private void up() {
        if (!Setting.getInstance().debug.isValue()) return;
        for (Node node : nodes) {

            node.roundssav.clear();
            node.rounds.clear();
            for (Node nn : nodes) {
                if (nn.toBlockPos().toCenterPos().distanceTo(node.toBlockPos().toCenterPos()) > 60) {
                    continue;
                }
                BlockHitResult hitResult = SkyMatrix.mc.world.raycast(new RaycastContext(node.toBlockPos().toCenterPos().add(0, 2.04, 0), nn.toBlockPos().toCenterPos().add(0, 0.48, 0), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, SkyMatrix.mc.player));
//                BlockHitResult hitResult = SkyMatrix.mc.world.raycast(new RaycastContext(node.toBlockPos().toCenterPos().add(0, 2, 0), nn.toBlockPos().toCenterPos().add(0, 0.48, 0), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, SkyMatrix.mc.player));
                if (hitResult.getBlockPos().equals(nn.toBlockPos())) {
                    node.rounds.add(nn);
                    node.roundssav.add(nn.id);
                    Client.sendDebugMessage(Text.of("§r添加成功" + nn.toBlockPos().toString()));
                }
            }
        }

    }

    private void down() {
        if (!Setting.getInstance().debug.isValue()) return;
        mode = modes.get((modes.indexOf(mode) + modes.size() - 1) % modes.size());
        update();
    }

    private void update() {
        if (!Setting.getInstance().debug.isValue()) return;
        switch (mode) {
            case "edit" ->
                    message.sendDebugMessage(Text.of("§r当前模式为: §b编辑 §r使用鼠标左键添加节点 右键将周围节点作为周边节点进行连接"));
            case "select" ->
                    message.sendDebugMessage(Text.of("§r当前模式为: §b选择 §r使用鼠标左键选择节点 使用Ctrl+D§s删除节点"));
            case "add" -> message.sendDebugMessage(Text.of("§r当前模式为: §b添加 §r使用鼠标左键添加节点"));
        }
    }

    private void close1() {
        if (!Setting.getInstance().debug.isValue()) return;


    }

    @Init(level = 99999)
    public void init() {
        try {
            ArrayList<LinkedTreeMap> arr = new Gson().fromJson(FileUtils.readFileToString(new File(Client.root, "nodes.json"), "utf-8"), ArrayList.class);
            System.out.println(arr);
            for (LinkedTreeMap o : arr) {
                Node node = new Node(((Double) o.get("x")).intValue(), ((Double) o.get("y")).intValue(), ((Double) o.get("z")).intValue(), new ArrayList<>(), new ArrayList<>());
                node.tags.addAll((Collection<? extends String>) o.get("tags"));
                node.roundssav.addAll((Collection<? extends String>) o.get("roundssav"));
                nodes.add(node);
                node.id = (String) o.get("id");

            }
            for (Node node : nodes) {
                for (String s : node.roundssav) {
                    System.out.println(s);
                    for (Node node1 : nodes) {
//                        System.out.println(s.equals(node1.id)+" "+s+" "+node1.id);
                        if (s.equals(node1.id)) {
                            node.rounds.add(node1);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mouse1() {
        if (!Setting.getInstance().debug.isValue()) return;
        if (mode.equals("add")) {
            if (current != null) {
                if (!isValid(current)) {
                    message.sendWarningMessage("§r当前位置不合法 原因：高度不够");
                    return;
                }
                Node c = new Node(current.getX(), current.getY(), current.getZ(), new ArrayList<>(), new ArrayList<>());
                c.id = String.valueOf(UUID.randomUUID().toString());
                this.nodes.add(c);
                c.tags.add("Location:" + hypixelWay.subWay());
                if (Atag != null) {
                    c.tags.add(Atag);
                }
            }
        }
        if (mode.equals("select")) {
            if (selecting != null) {
                selected = selecting;
                mode = "edit";
            }
        }
        if (mode.equals("edit")) {
            if (!isValid(null)) {
                message.sendWarningMessage("§r当前位置不合法 原因：路径不可达");
                return;
            }
            if (selecting != null) {
                if (selected != null) {
                    if (!selected.equals(selecting)) {
                        if (!selected.getRounds().contains(selecting)) {
                            selected.getRounds().add(selecting);
                            selected.roundssav.add(selecting.id);
                            selecting.getRounds().add(selected);
                            selecting.roundssav.add(selected.id);
                        }
                    }
                }
            }
        }
        if (mode.equals("none")) {
            for (Node node : nodes) {
                node.roundssav.clear();
                node.rounds.clear();
                node.id = String.valueOf(UUID.randomUUID().toString());
            }
            nodes.clear();


//            assert SkyMatrix.mc.world != null;
//            BlockHitResult hitResult = (BlockHitResult) SkyMatrix.mc.player.raycast(58, 0, false);
//            Vec3d bp = hitResult.getBlockPos().toCenterPos().add(0, 0.5, 0);
//            Rotation rotation= RotationUtils.toRotation(bp.subtract(SkyMatrix.mc.player.getPos().add(0,1.54,0)));
//            SkyMatrix.mc.player.setPitch(rotation.getPitch());
        }

    }

    private void mouse2() {
        if (!Setting.getInstance().debug.isValue()) return;
        if (!SkyMatrix.mc.options.sneakKey.isPressed()) {
            if (mode.equals("add")) {
                this.mode = "select";
                update();
                return;
            }
            if (mode.equals("edit")) {
                this.mode = "select";
                update();
                return;

            }
            if (mode.equals("select")) {
                this.mode = "add";
                update();

            }
        }


    }

    @EventTarget
    public void onRender(WorldRenderEvent e) {
        if (!Setting.getInstance().debug.isValue()) return;
        HitResult hitResult = SkyMatrix.mc.player.raycast(100, e.getTickDelta(), false);
        current = ((BlockHitResult) hitResult).getBlockPos();
        if (current != null) {
            if (SkyMatrix.mc.world.isAir(current)) {
                current = null;
            }
        }
        if (blocked != null) {
            RenderSystem.disableDepthTest();
            BlockLocTarget blockLocTarget = new BlockLocTarget(blocked, Theme.getInstance().THEME_UI_ERROR::geColor);
            blockLocTarget.render(e.getMatrixStack(), e.getTickDelta());
            RenderSystem.enableDepthTest();
        }
        if (current != null) {
            RenderSystem.disableDepthTest();
            BlockLocTarget blockLocTarget = new BlockLocTarget(((BlockHitResult) hitResult).getBlockPos(), Theme.getInstance().THEME::geColor);
            blockLocTarget.render(e.getMatrixStack(), e.getTickDelta());
            RenderSystem.enableDepthTest();
        }
        // 计算夹角最小的节点
        double min = Double.MAX_VALUE;
        Node minNode = null;
        for (Node node : nodes) {
            double tv = MathUtils.calculateAngle(SkyMatrix.mc.player.getRotationVec(e.getTickDelta()), node.toBlockPos().toCenterPos().subtract(SkyMatrix.mc.player.getCameraPosVec(e.getTickDelta())));
            if (tv < min) {
                min = tv;
                minNode = node;
            }
        }
        if (!mode.equals("edit")) {
            selected = minNode;

        }
        selecting = minNode;


        drawSelected(e, selected);
        drawSelected(e, selecting);
        if (mode.equals("select")) {
            if (selecting != null) {
                for (Node round : selecting.getRounds()) {
                    RenderSystem.disableDepthTest();
                    RenderUtilsV2.renderLine(e.getMatrixStack(), selecting.toBlockPos(), round.toBlockPos(), Theme.getInstance().THEME_UI_SELECTED.value, 1);
                }
            }

        }
        if (autoHotm.renderNodes.isValue()) {
            Node last = null;
            for (Node node : targets) {
                RenderSystem.disableDepthTest();
                BlockLocTarget blockLocTarget = new BlockLocTarget(node.toBlockPos(), Theme.getInstance().THEME_UI_SELECTED::geColor);
                blockLocTarget.render(e.getMatrixStack(), e.getTickDelta());
                if (last != null)
                    RenderUtilsV2.renderLine(e.getMatrixStack(), blockLocTarget.getPos(), last.toBlockPos(), Theme.getInstance().THEME_UI_SELECTED.value, 1);
                RenderSystem.enableDepthTest();
                last = node;
            }
            for (Node node : nodes) {
                RenderSystem.disableDepthTest();
                BlockLocTarget blockLocTarget = new BlockLocTarget(node.toBlockPos(), Theme.getInstance().UNSELECTED::geColor);

                if (!node.equals(selected) && !node.equals(selecting) && !targets.contains(node)) {
                    blockLocTarget.render(e.getMatrixStack(), e.getTickDelta());
                    if (mode.equals("edit")) {
                        for (Node round : node.getRounds()) {
                            RenderUtilsV2.renderLine(e.getMatrixStack(), blockLocTarget.getPos(), round.toBlockPos(), Theme.getInstance().THEME_UI_SELECTED.value, 1);

                        }
                    }
                }
                RenderSystem.enableDepthTest();
            }
        }


    }

    private void drawSelected(WorldRenderEvent e, Node selecting) {
        if (selecting != null) {
            RenderSystem.disableDepthTest();
            BlockLocTarget blockLocTarget = new BlockLocTarget(selecting.toBlockPos(), Theme.getInstance().THEME_UI_SELECTED::geColor);
            RenderUtilsV2.renderLine(e.getMatrixStack(), SkyMatrix.mc.player.getClientCameraPosVec(e.getTickDelta()).add(SkyMatrix.mc.player.getRotationVec(e.getTickDelta()).multiply(0.2, 0.2, 0.2)), selecting.toBlockPos().toCenterPos(), Theme.getInstance().THEME_UI_SELECTED.value, 1);
            RenderUtilsV2.renderLine(e.getMatrixStack(), selected.toBlockPos().toCenterPos().add(0, 1.54, 0), SkyMatrix.mc.player.getClientCameraPosVec(e.getTickDelta()).add(SkyMatrix.mc.player.getRotationVec(e.getTickDelta()).multiply(0.2, 0.2, 0.2)), Theme.getInstance().THEME_UI_ERROR.value, 1);
            RenderUtilsV2.renderLine(e.getMatrixStack(), this.selecting.toBlockPos().toCenterPos().add(0, 0.5, 0), SkyMatrix.mc.player.getClientCameraPosVec(e.getTickDelta()).add(SkyMatrix.mc.player.getRotationVec(e.getTickDelta()).multiply(0.2, 0.2, 0.2)), Theme.getInstance().THEME_UI_ERROR.value, 1);
            blockLocTarget.render(e.getMatrixStack(), e.getTickDelta());
            RenderSystem.enableDepthTest();
        }
    }

    @EventTarget
    public void registerCommand(CommandRegisterEvent e) {
        e.getDispatcher().register(
                ClientCommandManager.literal("node")
                        .then(
                                ClientCommandManager.literal("addTag").then(
                                        ClientCommandManager.argument("tag", StringArgumentType.string()).executes(this::addTag)
                                )
                        )
                        .then(
                                ClientCommandManager.literal("setAllTag").then(
                                        ClientCommandManager.argument("tag", StringArgumentType.string()).executes(this::setTag)
                                )
                        )
                        .then(
                                ClientCommandManager.literal("removeAllTag").then(
                                        ClientCommandManager.argument("tag", StringArgumentType.string()).executes(this::remTag)
                                )
                        )
        );
    }

    private int setTag(CommandContext<FabricClientCommandSource> fabricClientCommandSourceCommandContext) {
        String str = fabricClientCommandSourceCommandContext.getArgument("tag", String.class);
        this.Atag = str.replace("-", ":");
        message.sendMessage(Text.of("§r添加标签成功"));
        return 0;
    }

    private int remTag(CommandContext<FabricClientCommandSource> fabricClientCommandSourceCommandContext) {
        Atag = null;
        message.sendMessage(Text.of("§r删除成功"));
        return 0;
    }

    private int addTag(CommandContext<FabricClientCommandSource> fabricClientCommandSourceCommandContext) {
        String str = fabricClientCommandSourceCommandContext.getArgument("tag", String.class);
        this.selected.tags.add(str.replace("-", ":"));
        message.sendMessage(Text.of("§r添加标签成功"));
        return 0;
    }

    private boolean isValid(BlockPos blockPos) {
        if (blockPos != null) {
            assert SkyMatrix.mc.world != null;
            return SkyMatrix.mc.world.isAir(blockPos.add(0, 2, 0)) && SkyMatrix.mc.world.isAir(blockPos.add(0, 1, 0));
        }

//        BlockHitResult hitResult = SkyMatrix.mc.world.raycast(new RaycastContext(node.toBlockPos().toCenterPos().add(0, 2, 0), nn.toBlockPos().toCenterPos().add(0, 0.48, 0), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, SkyMatrix.mc.player));
//        if (hitResult.getBlockPos().equals(nn.toBlockPos())) {
//            node.rounds.add(nn);
//            node.roundssav.add(nn.id);
//        }

        assert SkyMatrix.mc.world != null;
        BlockHitResult hitResult = SkyMatrix.mc.world.raycast(new RaycastContext(selected.toBlockPos().toCenterPos().add(0, 2.04, 0), selecting.toBlockPos().toCenterPos().add(0, 0.48, 0), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, SkyMatrix.mc.player));
        message.sendDebugMessage(Text.of(selected.toBlockPos().toCenterPos().add(0, 1.54, 0).toString()));
        if (mode.equals("edit")) {
            blocked = hitResult.getBlockPos();
            blocked = blocked.add(0, 1, 0);
            message.sendMessage(Text.of(blocked.toString()));
        } else {
            blocked = null;
        }
        message.sendDebugMessage(Text.of(hitResult.getBlockPos() + "  |  " + selecting.toBlockPos()));

        return hitResult.getBlockPos().equals(selecting.toBlockPos());
    }

    @Override
    public void draw(MatrixStack matrixStack, float x, float y) {

    }

    @Override
    public int getHudWidth() {
        return 0;
    }

    @Override
    public int getHudHeight() {
        return 0;
    }
}
