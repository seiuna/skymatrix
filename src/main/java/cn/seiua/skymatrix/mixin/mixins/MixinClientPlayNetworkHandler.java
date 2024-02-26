package cn.seiua.skymatrix.mixin.mixins;

import cn.seiua.skymatrix.event.events.ServerPacketEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler {





    @Inject(at = @At("HEAD"), method = "onEntitySpawn", cancellable = true)
    public void onEntitySpawn(EntitySpawnS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onExperienceOrbSpawn", cancellable = true)
    public void onExperienceOrbSpawn(ExperienceOrbSpawnS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onScoreboardObjectiveUpdate", cancellable = true)
    public void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }


    @Inject(at = @At("HEAD"), method = "onEntityAnimation", cancellable = true)
    public void onEntityAnimation(EntityAnimationS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onDamageTilt", cancellable = true)
    public void onDamageTilt(DamageTiltS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onStatistics", cancellable = true)
    public void onStatistics(StatisticsS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onUnlockRecipes", cancellable = true)
    public void onUnlockRecipes(UnlockRecipesS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onBlockBreakingProgress", cancellable = true)
    public void onBlockBreakingProgress(BlockBreakingProgressS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onSignEditorOpen", cancellable = true)
    public void onSignEditorOpen(SignEditorOpenS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onBlockEntityUpdate", cancellable = true)
    public void onBlockEntityUpdate(BlockEntityUpdateS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onBlockEvent", cancellable = true)
    public void onBlockEvent(BlockEventS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onBlockUpdate", cancellable = true)
    public void onBlockUpdate(BlockUpdateS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onGameMessage", cancellable = true)
    public void onGameMessage(GameMessageS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onChatMessage", cancellable = true)
    public void onChatMessage(ChatMessageS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onProfilelessChatMessage", cancellable = true)
    public void onProfilelessChatMessage(ProfilelessChatMessageS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onRemoveMessage", cancellable = true)
    public void onRemoveMessage(RemoveMessageS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onChunkDeltaUpdate", cancellable = true)
    public void onChunkDeltaUpdate(ChunkDeltaUpdateS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onMapUpdate", cancellable = true)
    public void onMapUpdate(MapUpdateS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onCloseScreen", cancellable = true)
    public void onCloseScreen(CloseScreenS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onInventory", cancellable = true)
    public void onInventory(InventoryS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onOpenHorseScreen", cancellable = true)
    public void onOpenHorseScreen(OpenHorseScreenS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onScreenHandlerPropertyUpdate", cancellable = true)
    public void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onScreenHandlerSlotUpdate", cancellable = true)
    public void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

//    @Inject(at = @At("HEAD"), method = "onCustomPayload", cancellable = true)
//    public void onCustomPayload(CustomPayloadS2CPacket var1, CallbackInfo callbackInfo) {
//
//        ServerPacketEvent event = new ServerPacketEvent(var1);
//        event.call();
//        if (event.isCancelled()) callbackInfo.cancel();
//
//
//    }
//
//    @Inject(at = @At("HEAD"), method = "onDisconnect", cancellable = true)
//    public void onDisconnect(DisconnectS2CPacket var1, CallbackInfo callbackInfo) {
//
//        ServerPacketEvent event = new ServerPacketEvent(var1);
//        event.call();
//        if (event.isCancelled()) callbackInfo.cancel();
//
//
//    }

    @Inject(at = @At("HEAD"), method = "onEntityStatus", cancellable = true)
    public void onEntityStatus(EntityStatusS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onEntityAttach", cancellable = true)
    public void onEntityAttach(EntityAttachS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onEntityPassengersSet", cancellable = true)
    public void onEntityPassengersSet(EntityPassengersSetS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onExplosion", cancellable = true)
    public void onExplosion(ExplosionS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onGameStateChange", cancellable = true)
    public void onGameStateChange(GameStateChangeS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

//    @Inject(at = @At("HEAD"), method = "onKeepAlive", cancellable = true)
//    public void onKeepAlive(KeepAliveS2CPacket var1, CallbackInfo callbackInfo) {
//
//        ServerPacketEvent event = new ServerPacketEvent(var1);
//        event.call();
//        if (event.isCancelled()) callbackInfo.cancel();
//
//
//    }

    @Inject(at = @At("HEAD"), method = "onChunkData", cancellable = true)
    public void onChunkData(ChunkDataS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onChunkBiomeData", cancellable = true)
    public void onChunkBiomeData(ChunkBiomeDataS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }
    //
    @Inject(at = @At("HEAD"), method = "onUnloadChunk", cancellable = true)
    public void onUnloadChunk(UnloadChunkS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }
//
//    public void onWorldEvent(WorldEventS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onGameJoin(GameJoinS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onEntity(EntityS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
    @Inject(at = @At("HEAD"), method = "onPlayerPositionLook", cancellable = true)
    public void onPlayerPositionLook(PlayerPositionLookS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onParticle", cancellable = true)
    public void onParticle(ParticleS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    //
//    public void onPing(PlayPingS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onPlayerAbilities(PlayerAbilitiesS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onPlayerRemove(PlayerRemoveS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onPlayerList(PlayerListS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onEntitiesDestroy(EntitiesDestroyS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onRemoveEntityStatusEffect(RemoveEntityStatusEffectS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onPlayerRespawn(PlayerRespawnS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onEntitySetHeadYaw(EntitySetHeadYawS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
    @Inject(at = @At("HEAD"), method = "onUpdateSelectedSlot", cancellable = true)
    public void onUpdateSelectedSlot(UpdateSelectedSlotS2CPacket var1, CallbackInfo callbackInfo) {
        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();
    }

    //
//    public void onScoreboardDisplay(ScoreboardDisplayS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onEntityVelocityUpdate(EntityVelocityUpdateS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onEntityEquipmentUpdate(EntityEquipmentUpdateS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onExperienceBarUpdate(ExperienceBarUpdateS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onHealthUpdate(HealthUpdateS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onTeam(TeamS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onScoreboardPlayerUpdate(ScoreboardPlayerUpdateS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onPlayerSpawnPosition(PlayerSpawnPositionS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onWorldTimeUpdate(WorldTimeUpdateS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
    @Inject(at = @At("HEAD"), method = "onPlaySound", cancellable = true)
    public void onPlaySound(PlaySoundS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }

    @Inject(at = @At("HEAD"), method = "onPlaySoundFromEntity", cancellable = true)
    public void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket var1, CallbackInfo callbackInfo) {

        ServerPacketEvent event = new ServerPacketEvent(var1);
        event.call();
        if (event.isCancelled()) callbackInfo.cancel();


    }
//
//    public void onItemPickupAnimation(ItemPickupAnimationS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onEntityPosition(EntityPositionS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onEntityAttributes(EntityAttributesS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onEntityStatusEffect(EntityStatusEffectS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onSynchronizeTags(SynchronizeTagsS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onEndCombat(EndCombatS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onEnterCombat(EnterCombatS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onDeathMessage(DeathMessageS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onDifficulty(DifficultyS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onSetCameraEntity(SetCameraEntityS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onWorldBorderInitialize(WorldBorderInitializeS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onWorldBorderInterpolateSize(WorldBorderInterpolateSizeS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onWorldBorderSizeChanged(WorldBorderSizeChangedS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onWorldBorderWarningTimeChanged(WorldBorderWarningTimeChangedS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onWorldBorderWarningBlocksChanged(WorldBorderWarningBlocksChangedS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onWorldBorderCenterChanged(WorldBorderCenterChangedS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onPlayerListHeader(PlayerListHeaderS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onResourcePackSend(ResourcePackSendS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onBossBar(BossBarS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onCooldownUpdate(CooldownUpdateS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onVehicleMove(VehicleMoveS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onAdvancements(AdvancementUpdateS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onSelectAdvancementTab(SelectAdvancementTabS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onCraftFailedResponse(CraftFailedResponseS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onCommandTree(CommandTreeS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onStopSound(StopSoundS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onCommandSuggestions(CommandSuggestionsS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onSynchronizeRecipes(SynchronizeRecipesS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onLookAt(LookAtS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onNbtQueryResponse(NbtQueryResponseS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onLightUpdate(LightUpdateS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onOpenWrittenBook(OpenWrittenBookS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onOpenScreen(OpenScreenS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onSetTradeOffers(SetTradeOffersS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onChunkLoadDistance(ChunkLoadDistanceS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onSimulationDistance(SimulationDistanceS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onChunkRenderDistanceCenter(ChunkRenderDistanceCenterS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onPlayerActionResponse(PlayerActionResponseS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onOverlayMessage(OverlayMessageS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onSubtitle(SubtitleS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onTitle(TitleS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onTitleFade(TitleFadeS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onTitleClear(ClearTitleS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onServerMetadata(ServerMetadataS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onChatSuggestions(ChatSuggestionsS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onFeatures(FeaturesS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onBundle(BundleS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//    public void onEntityDamage(EntityDamageS2CPacket var1, CallbackInfo callbackInfo){
//
//        ServerPacketEvent event=new ServerPacketEvent(var1);
//        event.call();
//        if(event.isCancelled())callbackInfo.cancel();
//
//
//    }
//
//
//


}
