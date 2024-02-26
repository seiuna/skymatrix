package cn.seiua.skymatrix.client;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.component.Component;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.Init;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.event.events.ServerPacketEvent;
import cn.seiua.skymatrix.gui.ClickGui;
import cn.seiua.skymatrix.hud.ClientHud;
import cn.seiua.skymatrix.hud.Hud;
import cn.seiua.skymatrix.utils.PlayerListUtils;
import cn.seiua.skymatrix.utils.RenderUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.SignBlock;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.awt.*;

@Component
@Event(register = true)
public class HypixelWay implements Hud {
    public static String
            END = "The End",
            GARDEN = "The Garden";
    private final String HUB = "HUB";

    @Value(name = "waypoint")
    public ClientHud clientHud = new ClientHud(100, 100, true, this);
    private String way = "NONE";
    private String subWay = "NONE";


    private boolean skyblock;

    private static HypixelWay instance;

    public static HypixelWay getInstance() {
        return instance;
    }


    public boolean isSkyblock() {
        return skyblock;
    }

    public void setSkyblock(boolean skyblock) {
        this.skyblock = skyblock;
    }

    @Init
    public void init() {
        instance = this;
    }

    public boolean isIn(String name) {

        return this.way.equalsIgnoreCase(name);
    }

    public String subWay() {
        return subWay;
    }

    public String way() {

        return way;
    }

    int tick;

    @EventTarget
    public void onTick(ClientTickEvent e) {
        if (tick % 20 == 0) {
            if (SkyMatrix.mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = ((BlockHitResult) SkyMatrix.mc.crosshairTarget).getBlockPos();
                BlockState blockState = SkyMatrix.mc.world.getBlockState(blockPos);
                if (blockState.getBlock() instanceof SignBlock) {

                }
            }
            if (!way.equals("LIMBO")) {
                this.way = "NONE";
                this.subWay = "NONE";
            }

            boolean hypixel = false;
            boolean skyblock = false;
            for (ScoreboardObjective scoreboard : SkyMatrix.mc.world.getScoreboard().getObjectives()) {
                if (scoreboard.getDisplayName().getString().contains("SKYBLOCK")) {
                    skyblock = true;
                }
                for (Team team : SkyMatrix.mc.world.getScoreboard().getTeams()) {
                    String name = team.getPrefix().getString() + team.getSuffix().getString();
                    if (name.contains("www.hypixel.net")) {
                        this.way = "NONE";
                        this.subWay = "NONE";
                        hypixel = true;
                    }
                    String point = Character.toString(9187);
                    if (name.contains(point)) {
                        int a = name.indexOf(point);
                        this.subWay = name.substring(a + 2).trim();
                        break;
                    }
                }
            }
            String temp = PlayerListUtils.copyContainsLine("Area: ");
            if (temp != null) {
                String name = temp.replace("Area: ", "").trim();
                this.way = name;
            }


            this.skyblock = skyblock;

        }
        tick++;
    }

    @EventTarget
    public void onPacket(ServerPacketEvent event) {
        if (event.getPacket() instanceof GameMessageS2CPacket) {
            GameMessageS2CPacket eventPacket = (GameMessageS2CPacket) event.getPacket();

            if (eventPacket.content().toString().contains("You were spawned in Limbo")) {
                this.way = "LIMBO";

            }
            if (eventPacket.content().toString().contains("You are AFK. Move around to return from AFK.")) {
                this.way = "LIMBO";

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
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, String.format("当前位置: %s  %s", way(), subWay()));


    }

    @Override
    public int getHudWidth() {

        return ClickGui.fontRenderer20.getStringWidth(String.format("当前位置: %s  %s", way(), subWay())) + 32;

    }


    @Override
    public int getHudHeight() {
        return 53;
    }
}
