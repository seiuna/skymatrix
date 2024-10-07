package cn.seiua.skymatrix;


import cn.seiua.skymatrix.client.component.ComponentHandler;
import cn.seiua.skymatrix.event.events.CommandRegisterEvent;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static moe.seiua.skymatrix.loader.InjectionKt.setupInjection;


public class SkyMatrix implements ModInitializer, ClientTickEvents.StartTick {
    public static final String MODID = "skymatrix";
    public static final Logger LOGGER = LoggerFactory.getLogger("skymatrix");
    public static MinecraftClient mc;

    public static void registerCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        new CommandRegisterEvent(dispatcher, registryAccess).call();

    }

    @Override
    public void onInitialize() {
        setupInjection();
        ClientCommandRegistrationCallback.EVENT.register(SkyMatrix::registerCommands);
        ClientTickEvents.START_CLIENT_TICK.register(this);

    }

    private boolean flag;

    @Override
    public void onStartTick(MinecraftClient client) {
        if (flag == false) {
            mc = MinecraftClient.getInstance();
            LOGGER.info("client loaded!");
            ComponentHandler.loadAllClasesName();
            ComponentHandler.setup();
            flag = true;
        }
    }
}
