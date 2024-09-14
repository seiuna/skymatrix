package cn.seiua.skymatrix.client.waypoint;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalBlock;
import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.*;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.*;
import cn.seiua.skymatrix.client.rotation.Rotation;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.KeyBind;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.CommandRegisterEvent;
import cn.seiua.skymatrix.event.events.HudRenderEvent;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import cn.seiua.skymatrix.font.FontRenderer;
import cn.seiua.skymatrix.gui.ClickGui;
import cn.seiua.skymatrix.gui.Theme;
import cn.seiua.skymatrix.gui.ui.UI;
import cn.seiua.skymatrix.hud.ClientHud;
import cn.seiua.skymatrix.hud.Hud;
import cn.seiua.skymatrix.message.Message;
import cn.seiua.skymatrix.message.MessageBuilder;
import cn.seiua.skymatrix.render.BlockTarget;
import cn.seiua.skymatrix.render.BlockTextTarget;
import cn.seiua.skymatrix.utils.MathUtils;
import cn.seiua.skymatrix.utils.RenderUtils;
import cn.seiua.skymatrix.utils.RenderUtilsV2;
import cn.seiua.skymatrix.utils.RotationUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;
import java.util.*;

@Category(name = "waypoint")
@Event(register = true)
@Config(name = "waypoint")
public class Waypoint extends Screen implements Hud {
    static enum Status {
        EDIT, NONE
    }

    private static Waypoint instance;

    public static Waypoint getInstance() {
        return instance;
    }

    Message message = MessageBuilder.build("waypoint");
    private String lastMessage;

    public String getLastMessage() {
        return lastMessage;
    }

    public WaypointGroupEntity getByName(String name) {
        WaypointGroupEntity entity = null;
        if (this.waypoints.containsKey(name)) {
            entity = this.waypoints.get(name);
            if (!entity.world.equals(this.way.way())) {
                lastMessage = "client.warning.waypoint.illegal-world";
                entity = null;
            }
        } else {
            lastMessage = "client.warning.waypoint.not-found";
        }
        if (entity != null) {
            if (entity.remove) entity = null;
        }
        return entity;
    }

    @Use
    private HypixelWay way;

    @Value(name = "save")
    private KeyBind save = new KeyBind("save", Arrays.asList(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_S), this::save);
    @Value(name = "delete")
    private KeyBind delete = new KeyBind("delete", Arrays.asList(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_D), this::delete);
    @Value(name = "close")
    private KeyBind keyBind = new KeyBind("close", Arrays.asList(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_A), this::close1);
    @Value(name = "add")
    private KeyBind add = new KeyBind("add", List.of(KeyBind.getMouseKey(GLFW.GLFW_MOUSE_BUTTON_1)), this::add);
    private BlockPos current = null;

    private void add() {
        message.sendDebugMessage(Text.of("add"));
        if (this.status == Status.EDIT) {
            if (this.current != null) {
                message.sendDebugMessage(Text.of("add 1"));
                WaypointGroupEntity waypointGroupEntity = this.waypoints.get(this.name);
                waypointGroupEntity.getWaypoints().add(new WaypointEntity(current.getX(), current.getY(), current.getZ(), "#" + waypointGroupEntity.getWaypoints().size()));
            }
        }

    }

    public HashMap<String, WaypointGroupEntity> getWaypoints() {
        return waypoints;
    }

    private void close1() {
    }

    private void save() {
        message.sendDebugMessage(Text.of("save"));
        ObjectMapper objectMapper = new ObjectMapper();
        if (this.status == Status.EDIT) {
            try {
                message.sendDebugMessage(Text.of("save1"));
                GLFW.glfwSetClipboardString(SkyMatrix.mc.getWindow().getHandle(), objectMapper.writeValueAsString(this.waypoints.get(name)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            this.name = null;
            this.saveWaypoints();
        }

    }

    private void delete() {
        message.sendDebugMessage(Text.of("delete"));
        if (this.status == Status.EDIT) {
            message.sendDebugMessage(Text.of("delete1"));
            WaypointGroupEntity waypointGroupEntity = this.waypoints.get(name);
            if (waypointGroupEntity.getWaypoints().size() != 0) {
                waypointGroupEntity.getWaypoints().remove(waypointGroupEntity.getWaypoints().size() - 1);
            }
        }
    }

    public Status status = Status.NONE;

    public Waypoint() {
        super(Text.of("HudManager"));
    }


    @Value(name = "waypoint")
    public ClientHud clientHud = new ClientHud(100, 100, true, this);

    @EventTarget
    public void registerCommand(CommandRegisterEvent e) {
        e.getDispatcher().register(
                ClientCommandManager.literal("wp")
                        .then(
                                ClientCommandManager.literal("edit").executes(this::editCommand)
                        ).then(
                                ClientCommandManager.literal("select").then(
                                        ClientCommandManager.argument("name", StringArgumentType.string()).executes(this::selectCommand)
                                )
                        ).then(
                                ClientCommandManager.literal("add").then(
                                        ClientCommandManager.argument("name", StringArgumentType.string()).executes(this::addCommand)
                                )
                        ).then(
                                ClientCommandManager.literal("data").then(
                                        ClientCommandManager.argument("name", StringArgumentType.string()).then(ClientCommandManager.argument("value", StringArgumentType.string()).executes(this::addData))
                                )
                        ).then(
                                ClientCommandManager.literal("process").executes(this::processCommand)
                        )
        );
    }

    private int addData(CommandContext<FabricClientCommandSource> fabricClientCommandSourceCommandContext) {
        String name = fabricClientCommandSourceCommandContext.getArgument("name", String.class);
        String value = fabricClientCommandSourceCommandContext.getArgument("value", String.class);
        if (this.status == Status.EDIT) {
            WaypointGroupEntity entity = this.getWaypoints().get(this.name);
            if (entity != null && entity.getWaypoints().size() != 0) {
                if (entity.getWaypoints().get(entity.getWaypoints().size() - 1).data == null) {
                    entity.getWaypoints().get(entity.getWaypoints().size() - 1).data = new HashMap<>();

                }
                entity.getWaypoints().get(entity.getWaypoints().size() - 1).data.put(name, value);
                this.message.sendMessage(Text.of("ok"));
            }
        }

        return 1;
    }

    private int processCommand(CommandContext<FabricClientCommandSource> fabricClientCommandSourceCommandContext) {
        if (this.waypoints.get(name) != null && this.waypoints.get(name).getWaypoints().size() != 0) {
            a = 0;
            new Thread(this::run).start();
//            System.out.println(BaritoneAPI.getSettings().assumeSafeWalk.value);
        }

        return 1;
    }

    int a = 0;

    public void run() {

        while (true) {
            if (a == -1) {
                a = 0;
                return;
            }
            if (!BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().isActive()) {
                ArrayList<WaypointEntity> wps = this.waypoints.get(name).getWaypoints();
                if (wps.size() - 1 < a) {
                    a = 0;
                    return;
                }
                WaypointEntity wp = wps.get(a);


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
                BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalBlock(wp.getX(), wp.getY() + 1, wp.getZ()));

                a++;

            } else {

            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @EventTarget
    public void onRender(WorldRenderEvent e) {
//        current = null;
        if (this.status == Status.EDIT) {
            HitResult hitResult = SkyMatrix.mc.player.raycast(100, e.getTickDelta(), false);
            if (hitResult.getType().equals(HitResult.Type.BLOCK)) {
                current=((BlockHitResult)hitResult).getBlockPos();
                RenderUtilsV2.renderOutlineBlock(e.getMatrixStack(),((BlockHitResult)hitResult).getBlockPos(),new Color(255,255,255,255));
            }
            drawCurrent(e.getMatrixStack(), e.getTickDelta(), this.waypoints.get(name));
        }
//        if (this.status == Status.NONE) {
//            for (String name : this.waypoints.keySet()) {
//
//                WaypointGroupEntity entity = this.waypoints.get(name);
//                if (entity.world.equals(this.way.way())) {
//                    drawCurrent(e.getMatrixStack(), e.getTickDelta(), entity);
//                }
//
//
//            }
//        }
    }

    public String name;


    private int addCommand(CommandContext<FabricClientCommandSource> fabricClientCommandSourceCommandContext) {
        if (way.way() == null) {
            message.sendWarningMessage(Text.of("不合法的位置你必须在SkyBlock才能执行此命令"));
            return 1;
        }
        String str = fabricClientCommandSourceCommandContext.getArgument("name", String.class);
        name = str;
        WaypointGroupEntity waypointGroupEntity = new WaypointGroupEntity(new ArrayList<WaypointEntity>(), name, way.way());
        this.waypoints.put(name, waypointGroupEntity);
        this.status = Status.EDIT;

        return 1;
    }

    private int selectCommand(CommandContext<FabricClientCommandSource> fabricClientCommandSourceCommandContext) {
        if (way.way() == null) {
            message.sendWarningMessage(Text.of("不合法的位置你必须在SkyBlock才能执行此命令"));
            return 1;
        }

        String str = fabricClientCommandSourceCommandContext.getArgument("name", String.class);
        String w = this.waypoints.get(str).world;
        if (this.waypoints.containsKey(str)) {

            if (this.way.way().equals(w)) {


                name = str;
            } else {
                message.sendWarningMessage(Text.of("你不能选择这个路径组 因为与你的当前的位置不同 " + w));
            }
        }

        return 1;
    }

    private int editCommand(CommandContext<FabricClientCommandSource> fabricClientCommandSourceCommandContext) {
        if (way.way() == null) {
            message.sendWarningMessage(Text.of("不合法的位置你必须在SkyBlock才能执行此命令"));
            return 1;
        }
        message.sendDebugMessage(Text.of("editCommand"));
        this.status = Status.EDIT;
        return 1;
    }


    private HashMap<String, WaypointGroupEntity> waypoints;

    @Init
    public void init1() {
        instance = this;
        waypoints = new HashMap<>();
        configManager.addCallBack(this::loadWaypoints);

    }

    public void loadWaypoints() {
        waypoints = configManager.loadWaypoints();
    }

    public void saveWaypoints() {
        configManager.saveWaypoints(this.waypoints);
        this.status = Status.NONE;
    }

    public boolean isInBox(ClientHud hud, int mouseX, int mouseY) {
        float x = hud.getX();
        float y = hud.getY();
        float h = hud.getTarget().getHudHeight() + y;
        float w = hud.getTarget().getHudWidth() + x;
        if ((mouseX >= x && mouseX <= w && mouseY >= y && mouseY <= h)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);

    }

    public void drawCurrent(MatrixStack matrixStack, float delta, WaypointGroupEntity entity) {
        if (!entity.show) return;
        if (entity.remove) return;
        BlockPos last = null;
        if (entity != null) {
            for (WaypointEntity waypoint : entity.getWaypoints()) {
                BlockTarget blockTarget = new BlockTextTarget(waypoint.toBlockPos(), Theme.getInstance().THEME_UI_SELECTED::geColor, waypoint.name);
                blockTarget.render(matrixStack, delta);
                if (last != null) {
                    RenderUtilsV2.renderLine(matrixStack, last, blockTarget.getPos(),Theme.getInstance().THEME_UI_SELECTED.value,2f);
                }
                last = blockTarget.getPos();
            }
            if (last != null) {
                if (current != null) {
                    RenderUtilsV2.renderLine(matrixStack, last, current,Theme.getInstance().THEME_UI_SELECTED.value,2f);
                }
            }

        }
        Vec3d vec3d = MathUtils.calculateCenter(entity.getWaypoints());
        if (vec3d != null) {
            BlockTarget blockTarget = new BlockTextTarget(new BlockPos((int) vec3d.x, (int) vec3d.y + 5, (int) vec3d.z), new Color(255, 255, 255, 0)::brighter, entity.name);
            blockTarget.render(matrixStack, delta);
        }

    }

    ArrayList<UIWaypoint> waypointArrayList = new ArrayList<>();

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float ms = UI.getS();
        mouseY = (int) (mouseY * ms);
        mouseX = (int) (mouseX * ms);
        int height = (int) (this.height * ms);
        int width = (int) (this.width * ms);
        MatrixStack matrixStack = context.getMatrices();
        matrixStack.push();
        matrixStack.scale(1.0f / ms, 1.0f / ms, 1.0f / ms);
        waypointArrayList.clear();
        float wdit = super.width * ms;

        int x = (int) (wdit / 2);
        int y = 200;
        ArrayList<WaypointGroupEntity> entityList = new ArrayList<>(this.waypoints.values());
        Comparator<WaypointGroupEntity> sortingValueComparator = Comparator.comparingInt(WaypointGroupEntity::c);
        int t = v;
        Collections.sort(entityList, sortingValueComparator);
        for (WaypointGroupEntity entity : entityList) {
            if (t != 0) {
                t--;
                continue;
            }
            if (entity.remove) continue;
            UIWaypoint waypoint = new UIWaypoint(entity);
            waypointArrayList.add(waypoint);
            waypoint.update(x, y);
            waypoint.setMouse(mouseX, mouseY);
            waypoint.render(context, mouseX, mouseY, delta);
            y += 80;
        }

        matrixStack.scale(ms, ms, ms);
        matrixStack.pop();

        super.render(context, mouseX, mouseY, delta);
    }

    int v = 0;

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {

        v += verticalAmount * -1;
        if (v < 0) v = 0;
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }


    boolean drag;
    float px;
    float py;

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float ms = UI.getS();
        mouseY = mouseY * ms;
        mouseX = mouseX * ms;
        for (UIWaypoint uiWaypoint : waypointArrayList) {
            uiWaypoint.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Use
    public ConfigManager configManager;
    @Use
    public Notification notification;

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        float ms = UI.getS();
        mouseY = mouseY * ms;
        mouseX = mouseX * ms;
        return true;
    }

    @Override
    public void close() {

        configManager.writeToProfile();
        configManager.saveProfiles();
        notification.push(new Notice("Config", "profile: " + configManager.getCurrent().getName() + " saved successfully", NoticeType.INFO));
        saveWaypoints();
        super.close();
    }

    @EventTarget
    public void onRender(HudRenderEvent event) {


    }

    @Override
    public void draw(MatrixStack matrixStack, float x, float y) {

        RenderUtils.resetCent();
        RenderUtils.setColor(new Color(0, 0, 0, 100));
        RenderUtils.drawSolidBox(new Box(x, y, 0, x + getHudWidth(), y + getHudHeight(), 0), matrixStack);
        ClickGui.fontRenderer20.resetCenteredH();
        ClickGui.fontRenderer20.setColor(Color.white);
        ClickGui.fontRenderer20.resetCenteredV();
        ClickGui.fontRenderer20.side = FontRenderer.RIGHT_TOP;
        float startX = x + 40;
        float startY = y + 40;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "Waypoint edit hud");
        startY += 30;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, SkyMatrix.mc.player.getEyePos().toString() + "   " + SkyMatrix.mc.player.getPos().toString());
//        0.54
        assert SkyMatrix.mc.world != null;
        BlockHitResult hitResult = (BlockHitResult) SkyMatrix.mc.player.raycast(100, 0, false);
        Vec3d bp = hitResult.getBlockPos().toCenterPos().add(0, 0.5, 0);

        Rotation rotation = RotationUtils.toRotation(bp.subtract(SkyMatrix.mc.player.getPos().add(0, 1.54, 0)));
        startY += 30;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "aaa: " + rotation.getPitch() + "    " + rotation.getYaw());
        startY += 30;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "Current status: " + this.status);
        startY += 30;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "Current target: " + this.name);
        startY += 30;
        int t = this.a - 1;
        int i = 0;
        if (name != null) {
            for (WaypointEntity waypoint : this.waypoints.get(name).getWaypoints()) {
                ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, waypoint.getName() + " " + (i < t ? "√" : "×"));
                startY += 30;
                i++;
            }
        }

//        startY += 30;
//        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "当前鱼竿状态 " + this.rs);
//
    }

    @Override
    public int getHudWidth() {
        return 100;
    }

    @Override
    public int getHudHeight() {
        return 100;
    }
}
