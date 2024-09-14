package cn.seiua.skymatrix.client;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.component.*;
import cn.seiua.skymatrix.client.config.Setting;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.*;
import com.google.common.collect.EvictingQueue;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.DefaultPosArgument;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import static cn.seiua.skymatrix.SkyMatrix.mc;

@Component
@Event(register = true)
@Category(name = "client")
public final class Client {

    private static final Logger logger = LogManager.getLogger();
    public static Client instance;
    @Use
    private ConfigManager configManager;
    @Use
    private EventManager eventManager;
    @Use
    private ConnectManager connectManager;
    @Use
    private Setting setting;
    public int stage;
    public static File root = new File(MinecraftClient.getInstance().runDirectory, "skymartix");
    public static HashSet<Object> blackList = new HashSet<>();
    private static final Queue<Text> priorityQueue = EvictingQueue.create(40);
    public static boolean m_ability = false;

    // block breaking progress start
    private boolean keepBlockBreaking;
    public static boolean HandleInputBlockBreaking() {
        if(!instance.keepBlockBreaking)return false;
        SkyMatrix.mc.attackCooldown= 0;
        return true;
    }
    public boolean keepRightClick;

    public static boolean isKeepRightClick() {
        return instance.keepRightClick;
    }

    public void setKeepRightClick(boolean keepRightClick) {
        this.keepRightClick = keepRightClick;
    }

    @EventTarget
    private void onHandleKeyInputBeforeEvent(HandleKeyInputBeforeEvent event) {
        if (!keepBlockBreaking && !keepRightClick) return;
        screen=MinecraftClient.getInstance().currentScreen;
        MinecraftClient.getInstance().currentScreen=null;
    }

    @EventTarget
    private void onHandleKeyInputBeforeEvent(HandleKeyInputAfterEvent event) {
        if (!keepBlockBreaking && !keepRightClick) return;
        if(screen!=null)
            MinecraftClient.getInstance().currentScreen=screen;
    }

    @EventTarget
    private void doBlackList(BlockBreakingEvent object) {
        if(SkyMatrix.mc.crosshairTarget instanceof BlockHitResult){
            if(blackList.contains(((BlockHitResult) SkyMatrix.mc.crosshairTarget).getBlockPos().add(0,0,0))||blackList.contains(SkyMatrix.mc.world.getBlockState(((BlockHitResult) SkyMatrix.mc.crosshairTarget).getBlockPos()).getBlock().getName().toString())){
                object.setCancelled(true);
            }
        }
    }

    // block breaking progress end
    @Init(level = 999)
    public void start() {
        root.mkdirs();
        instance = this;
    }
    @EventTarget
    public void registerCommand(CommandRegisterEvent e) {
        e.getDispatcher().register(
                ClientCommandManager.literal("breakingBlackList")
                        .then(
                                ClientCommandManager.literal("addName").then(
                                        ClientCommandManager.argument("name", StringArgumentType.string()).executes(this::add)
                                )
                        ).then(
                                ClientCommandManager.literal("addPos").then(
                                        ClientCommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(this::add)
                                )
                        )  .then(
                                ClientCommandManager.literal("removeName").then(
                                        ClientCommandManager.argument("name", StringArgumentType.string()).executes(this::remove)
                                )
                        ).then(
                                ClientCommandManager.literal("removePos").then(
                                        ClientCommandManager.argument("pos", BlockPosArgumentType.blockPos()).executes(this::remove)
                                )
                        ).then(
                                ClientCommandManager.literal("show").executes(this::showBlockList)
                        ).then(
                                ClientCommandManager.literal("clear").executes(this::clear)
                        )
        );
    }

    private int clear(CommandContext<FabricClientCommandSource> fabricClientCommandSourceCommandContext) {
        blackList.clear();
        return 0;
    }

    private int add(CommandContext<FabricClientCommandSource> fccsc) {
        String str = null;
        BlockPos blockPos=null;
        try {
            str = fccsc.getArgument("name", String.class);
        }catch (IllegalArgumentException ignored){
        }finally {
            DefaultPosArgument d= fccsc.getArgument("pos", DefaultPosArgument.class);
            Vec3d vec3d = fccsc.getSource().getPosition();
            blockPos =BlockPos.ofFloored(new Vec3d(d.x.toAbsoluteCoordinate(vec3d.x), d.y.toAbsoluteCoordinate(vec3d.y), d.z.toAbsoluteCoordinate(vec3d.z)));

        }
        if(str!=null)
            blackList.add(str);
        if(blockPos!=null)
            blackList.add(blockPos);
        return 0;
    }

    private int remove(CommandContext<FabricClientCommandSource> fccsc) {
        String str = null;
        BlockPos blockPos=null;
        try {
            str = fccsc.getArgument("name", String.class);
        }catch (IllegalArgumentException ignored){
        }finally {
            DefaultPosArgument d= fccsc.getArgument("pos", DefaultPosArgument.class);
            Vec3d vec3d = fccsc.getSource().getPosition();
            blockPos =BlockPos.ofFloored(new Vec3d(d.x.toAbsoluteCoordinate(vec3d.x), d.y.toAbsoluteCoordinate(vec3d.y), d.z.toAbsoluteCoordinate(vec3d.z)));
        }
        if(str!=null)
            blackList.remove(str);
        if(blockPos!=null)
            blackList.remove(blockPos);
        return 0;
    }
    private int showBlockList(CommandContext<FabricClientCommandSource> fabricClientCommandSourceCommandContext) {
        if(blackList.isEmpty()){
            Client.sendMessage(Text.of("blackList is empty"));
            return 0;
        }
        for (Object o : blackList) {
            Client.sendMessage(Text.of(o.toString()));
        }
        return 0;

    }

    public boolean isKeepBlockBreaking() {
        return keepBlockBreaking;
    }

    public void setKeepBlockBreaking(boolean keepBlockBreaking) {
        if (!keepBlockBreaking) {
            mc.interactionManager.cancelBlockBreaking();
        }
        this.keepBlockBreaking = keepBlockBreaking;
    }
    Screen screen;

    private boolean flag;

    @EventTarget
    public void ClientTickEvent(ClientTickEvent e) {
        if (!Objects.equals(setting.title.getValue(), "")) {
            SkyMatrix.mc.getWindow().setTitle(setting.title.getValue());
        }

        if (MinecraftClient.getInstance().world != null) {
            updataGuiScreen();
        }

        Text text = priorityQueue.poll();
        if (text != null) {

        }
    }

    @EventTarget
    public void onPacket(ServerPacketEvent event) {
        if (event.getPacket() instanceof GameMessageS2CPacket) {
            GameMessageS2CPacket eventPacket = (GameMessageS2CPacket) event.getPacket();
            if(eventPacket.content().getString().contains("You used your Mining Speed Boost Pickaxe Ability!")){
                m_ability=true;
            }
            if(eventPacket.content().getString().contains("Your Mining Speed Boost has expired!")){
                m_ability=false;
            }
            new GameMessageEvent(eventPacket.content()).call();
        }
    }
    @Use
    public List<Screen> guiScreens;

    private Screen targetGui;

    public void openGui(Class gui) {
        for (Screen guiScreen : guiScreens) {
            if (guiScreen.getClass() == gui) {
                logger.debug("display guiscreen: " + guiScreen);
                targetGui = guiScreen;
                return;
            }
        }
    }

    private void updataGuiScreen() {
        if (targetGui != null) {
            MinecraftClient.getInstance().setScreen(targetGui);
            targetGui = null;
        }
    }

    public static void sendMessage(Text message) {
        assert SkyMatrix.mc.player != null;
        SkyMatrix.mc.player.sendMessage(Text.of("§8[§9S§9k§9y§9M§9a§9t§9r§9i§9x§8]").copy().append(message));
    }

    public static void sendDebugMessage(Text message) {
        if (!Setting.getInstance().debug.isValue()) return;
        SkyMatrix.mc.player.sendMessage(Text.of("§3[§bDebug§3]§7: §r").copy().append(message));

    }

    public static void sendSimpleMessage(Text message) {
        assert SkyMatrix.mc.player != null;

        SkyMatrix.mc.player.sendMessage(Text.of("§c[").copy().append(message).append(Text.of("§c]")));
    }

}
