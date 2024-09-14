package cn.seiua.skymatrix.client.module.modules.life;

import baritone.api.BaritoneAPI;
import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.HypixelWay;
import cn.seiua.skymatrix.client.IToggle;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.module.modules.ModuleTools;
import cn.seiua.skymatrix.client.rotation.SmoothRotation;
import cn.seiua.skymatrix.client.waypoint.Waypoint;
import cn.seiua.skymatrix.client.waypoint.WaypointEntity;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.gui.ClickGui;
import cn.seiua.skymatrix.hud.ClientHud;
import cn.seiua.skymatrix.hud.Hud;
import cn.seiua.skymatrix.message.Message;
import cn.seiua.skymatrix.message.MessageBuilder;
import cn.seiua.skymatrix.utils.OneTickTimer;
import cn.seiua.skymatrix.utils.RenderUtils;
import cn.seiua.skymatrix.utils.RotationUtils;
import cn.seiua.skymatrix.utils.TickTimer;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;

//@Event
//@Sign(sign = Signs.BETA)
//@SModule(name = "doVisitor", category = "life", disable = true)
public class DoVisitor implements Hud, IToggle {


    private enum Status {
        WAITTiNG_GUI, DO_VISITOR, WAITING_BZ, PATHING, NONE
    }

    @Value(name = "visitor")
    public ClientHud clientHud = new ClientHud(100, 100, true, this);
    private OneTickTimer warp = TickTimer.build(20, -1, () -> {
        ModuleTools.execute("warp garden");
    });
    @Use
    SmoothRotation smoothRotation;
    private OneTickTimer attack = TickTimer.build(18, -1, () -> {
        SkyMatrix.mc.interactionManager.attackEntity(SkyMatrix.mc.player, this.target.get(0));
        SkyMatrix.mc.interactionManager.interactEntity(SkyMatrix.mc.player, this.target.get(0), Hand.OFF_HAND);
    });
    private OneTickTimer oneTickTimer2;
    private OneTickTimer oneTickTimer3;
    private OneTickTimer oneTickTimer4;

    private Status status = Status.NONE;
    private Message message = MessageBuilder.build("dovisitor");
    ArrayList<LivingEntity> target = new ArrayList<>();


    @EventTarget
    public void onTick(ClientTickEvent event) {
        update();
        target.clear();
        ArrayList<ArmorStandEntity> entities = new ArrayList<>();
        ArmorStandEntity log = null;
        for (Entity entity : SkyMatrix.mc.world.getEntities()) {
            if (entity instanceof ArmorStandEntity) {
                ArmorStandEntity e = (ArmorStandEntity) entity;
                if (e.getDisplayName().getString().contains("CLICK")) {
                    entities.add(e);
                    continue;
                }
                if (e.getDisplayName().getString().contains("Visitor's Logbook")) {
                    log = e;

                }
            }
        }
        if (!HypixelWay.getInstance().isIn("Garden") && warp.getTick() < 0) {
            message.sendMessage(Text.of("当前不处于Garden run command /warp garden"));
            this.status = Status.PATHING;
            warp.reset();
            return;
        }
        if (warp.getTick() >= 0) {
            if (this.status != Status.DO_VISITOR) {
                BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
            }

            return;
        }
        if (log == null) {
            if (!BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().isActive()) {
                message.sendMessage(Text.of("未找到 Visitor's Logbook moving to VISITOR_LOGBACK"));
                this.status = Status.PATHING;
                ModuleTools.pathing(Waypoint.getInstance().getByName("VISITOR_LOGBACK").getWaypoints().get(0));
            }
            BlockPos entity = Waypoint.getInstance().getByName("VISITOR_LOGBACK").getWaypoints().get(0).toBlockPos();
            smoothRotation.smoothLook(RotationUtils.toRotation(new Vec3d(entity.getX(), entity.getY() + 2, entity.getZ()).subtract(SkyMatrix.mc.player.getEyePos())), 2.6f, null, false);


            return;
        } else {
            if (this.status != Status.DO_VISITOR) {
                BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
            }
        }

        for (ArmorStandEntity entity : entities) {
            if (log.getPos().distanceTo(entity.getPos()) < 16) {
                for (Entity entity1 : SkyMatrix.mc.world.getEntities()) {
                    if (entity1 instanceof LivingEntity) {
                        LivingEntity entity2 = (LivingEntity) entity1;
                        if (entity2.getPos().multiply(1, 0, 1).distanceTo(entity.getPos().multiply(1, 0, 1)) < 0.4) {
                            if (entity2 != SkyMatrix.mc.player && !entity2.isInvisible()) {
                                target.add(entity2);

                            }

                        }
                    }
                }
            }
        }


        doVisitor();

    }

    private void doVisitor() {
        this.status = Status.DO_VISITOR;


        if (!this.target.isEmpty()) {

            BlockPos blockPos = this.target.get(0).getBlockPos();
            smoothRotation.smoothLook(RotationUtils.toRotation(this.target.get(0)), 3, null, false);

            if (SkyMatrix.mc.player.getEyePos().distanceTo(this.target.get(0).getPos()) < 3) {
                BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
                processGui();
                return;
            }
            ModuleTools.pathing(new WaypointEntity(blockPos.getX(), blockPos.getY(), blockPos.getZ(), "a"));


        }


    }

    public void processGui() {

        if (!(SkyMatrix.mc.currentScreen instanceof HandledScreen<?>)) {
            if (this.attack.getTick() <= 0) {
                this.attack.setTick(20);
                this.status = Status.WAITTiNG_GUI;
            }
        } else {
            if (isTargetGUI()) {

            }
        }
    }

    public boolean isTargetGUI() {
        HandledScreen<?> screen = (HandledScreen<?>) SkyMatrix.mc.currentScreen;

        return false;
    }

    public void update() {
        if (warp != null) {
            warp.update();
        }
        if (attack != null) {
            attack.update();
        }

    }


    private boolean flag1;
    private boolean flag2;
    private boolean flag3;

    @Override
    public void enable() {
        if (Waypoint.getInstance().getByName("VISITOR_LOGBACK") == null) {
            throw new RuntimeException("路径点 VISITOR_LOGBACK 未设置");
        } else {
            if (Waypoint.getInstance().getByName("VISITOR_LOGBACK").getWaypoints().size() == 0) {
                throw new RuntimeException("VISITOR_LOGBACK 不包含任何路径点");
            }
        }

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
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "当前状态: " + this.status);
        startY += 25;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "visitors:");
        startY += 25;
        for (LivingEntity entity : this.target) {
            ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "    " + entity.getDisplayName());
            startY += 25;
        }

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
