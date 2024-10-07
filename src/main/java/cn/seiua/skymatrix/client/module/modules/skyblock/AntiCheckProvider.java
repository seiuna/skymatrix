package cn.seiua.skymatrix.client.module.modules.skyblock;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.Client;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.client.module.modules.combat.AntiBot;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.*;
import cn.seiua.skymatrix.utils.MathUtils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;

@Event
@Sign(sign = Signs.FREE)
@SModule(name = "AntiCheckProvider", category = "skyblock")
public class AntiCheckProvider {

    @Use
    AntiBot antiBot;
    private HashMap<String, Long> players = new HashMap<>();
    private HashMap<String, Double> players_ds = new HashMap<>();

    @EventTarget
    public void onPacket(ServerPacketEvent event) {
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            PlayerPositionLookS2CPacket packet = (PlayerPositionLookS2CPacket) event.getPacket();
            if (!packet.getFlags().contains((Object) PositionFlag.X_ROT) || (!packet.getFlags().contains((Object) PositionFlag.Y_ROT))) {
                boolean bl = packet.getFlags().contains((Object) PositionFlag.X);
                boolean bl2 = packet.getFlags().contains((Object) PositionFlag.Y);
                boolean bl3 = packet.getFlags().contains((Object) PositionFlag.Z);
                if ((!bl) || (!bl2) || (!bl3)) {
                    new ServerRotationEvent("Location").call();
//                    Client.sendMessage(Text.of("rotation"));

                }
            }
        }
    }

    @EventTarget
    public void onTick(ClientTickEvent event) {
        boolean flag = false;
        for (AbstractClientPlayerEntity entity : SkyMatrix.mc.world.getPlayers()) {
            if (entity != SkyMatrix.mc.player) {
                if (antiBot.isPlayer(entity.getUuid().toString())) {
                    String uuid = entity.getUuid().toString();
                    double distance = entity.distanceTo(SkyMatrix.mc.player);
                    if (distance < 70) {
                        if (this.players_ds.containsKey(uuid)) {
                            double cc = distance - this.players_ds.get(uuid);
                            if (cc < 0) {
                                new PlayerCloseEvent(distance, cc).call();
//                                Client.sendDebugMessage(Text.of("closing   " + entity.getName().getString() + "     " + (float) cc * 10 + "  " + (float) distance));
                            }
                            this.players_ds.put(uuid, distance);
                        } else {
                            this.players_ds.put(uuid, distance);
                        }
                        Vec3d vec3d1 = entity.getRotationVecClient();
                        Vec3d vec3d2 = new Vec3d(SkyMatrix.mc.player.getX() - entity.getX(), SkyMatrix.mc.player.getY() - entity.getY(), SkyMatrix.mc.player.getZ() - entity.getZ());
                        if (MathUtils.calculateAngle(vec3d1, vec3d2) < 10) {
                            if (players.containsKey(entity.getUuid().toString())) {
                                players.put(entity.getUuid().toString(), players.get(entity.getUuid().toString()) + 2);
                                flag = true;
                                if (players.get(entity.getUuid().toString()) > 60) {
                                    new LookLockEvent(entity, 0, entity.canSee(SkyMatrix.mc.player), false).call();
                                    Client.sendDebugMessage(Text.of("look   " + entity.getName().getString()));
                                }
                                break;
                            } else {
                                players.put(entity.getUuid().toString(), 0L);
                                flag = true;
                                break;
                            }
                        } else {
                            if (players.containsKey(entity.getUuid().toString())) {
                                if (players.get(entity.getUuid().toString()) > 0) {
                                    players.put(entity.getUuid().toString(), players.get(entity.getUuid().toString()) - 2);

                                } else {
                                    Client.sendDebugMessage(Text.of("look free " + entity.getName().getString()));
                                    players.remove(entity.getUuid().toString());
                                    new LookLockEvent(entity, 0, entity.canSee(SkyMatrix.mc.player), true).call();
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}
