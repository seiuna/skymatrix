package cn.seiua.skymatrix.mixin.mixins;

import cn.seiua.skymatrix.event.events.ClientChunkEvent;
import cn.seiua.skymatrix.event.events.ClientChunkUnLoadEvent;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ClientChunkManager.class)
public abstract class MixinClientChunkManager {
    @Final
    @Shadow
    ClientWorld world;

    @Inject(method = "loadChunkFromPacket", at = @At("TAIL"))
    private void onChunkLoad(int x, int z, PacketByteBuf packetByteBuf, NbtCompound
            nbtCompound, Consumer<ChunkData.BlockEntityVisitor> consumer, CallbackInfoReturnable<WorldChunk> info) {
        new ClientChunkEvent(this.world, info.getReturnValue()).call();

    }

    @Inject(method = "unload", at = @At("TAIL"))
    public void unload(ChunkPos pos, CallbackInfo ci) {
        new ClientChunkUnLoadEvent(pos.x, pos.z).call();
    }
}
