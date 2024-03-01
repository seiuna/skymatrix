package cn.seiua.skymatrix.client.module.modules.skyblock;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.client.module.modules.combat.AntiBot;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.AntiCheckEvent;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.event.events.LookLockEvent;
import cn.seiua.skymatrix.event.events.ServerPacketEvent;
import cn.seiua.skymatrix.utils.MathUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;

@Event
@Sign(sign = Signs.FREE)
@SModule(name = "AntiCheckProvider", category = "skyblock")
public class AntiCheckProvider {

    @Use
    AntiBot antiBot;
    private HashMap<String, Long> players = new HashMap<>();

    @EventTarget
    public void onPacket(ServerPacketEvent event) {
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            PlayerPositionLookS2CPacket packet = (PlayerPositionLookS2CPacket) event.getPacket();
            if (!packet.getFlags().contains((Object) PositionFlag.X_ROT) || (!packet.getFlags().contains((Object) PositionFlag.Y_ROT))) {
                boolean bl = packet.getFlags().contains((Object) PositionFlag.X);
                boolean bl2 = packet.getFlags().contains((Object) PositionFlag.Y);
                boolean bl3 = packet.getFlags().contains((Object) PositionFlag.Z);
                if ((!bl) || (!bl2) || (!bl3)) {
                    new AntiCheckEvent("Location").call();
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
                    if (entity.getBlockPos().isWithinDistance(SkyMatrix.mc.player.getBlockPos(), 40)) {
                        flag = true;
                        break;
                    } else if (entity.canSee(SkyMatrix.mc.player)) {
                        Vec3d vec3d1 = entity.getRotationVecClient();
                        Vec3d vec3d2 = new Vec3d(SkyMatrix.mc.player.getX() - entity.getX(), SkyMatrix.mc.player.getY() - entity.getY(), SkyMatrix.mc.player.getZ() - entity.getZ());
                        if (MathUtils.calculateAngle(vec3d1, vec3d2) < 8) {
                            if (players.containsKey(entity.getUuid().toString())) {
                                players.put(entity.getUuid().toString(), players.get(entity.getUuid().toString()) + 2);
                                flag = true;
                                if (players.get(entity.getUuid().toString()) > 60) {
                                    new LookLockEvent(entity, 0, entity.canSee(SkyMatrix.mc.player), false).call();
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
                                    players.put(entity.getUuid().toString(), players.get(entity.getUuid().toString()) - 1);

                                } else {
                                    new LookLockEvent(entity, 0, entity.canSee(SkyMatrix.mc.player), true).call();
                                }
                            }
                        }
                    }
                }
            }
        }
        if (flag) {
            MinecraftClient.getInstance().options.sneakKey.setPressed(true);
        } else {
            MinecraftClient.getInstance().options.sneakKey.setPressed(false);
        }
    }
}
