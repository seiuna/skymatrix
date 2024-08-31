package cn.seiua.skymatrix.client;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.auth.UserAccount;
import cn.seiua.skymatrix.client.auth.rp.Response;
import cn.seiua.skymatrix.client.auth.rp.RpCode;
import cn.seiua.skymatrix.client.auth.rp.RpLogin;
import cn.seiua.skymatrix.client.auth.rp.RpRegister;
import cn.seiua.skymatrix.client.auth.rq.RqLogin;
import cn.seiua.skymatrix.client.component.Component;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.httpclient.HttpClient;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.CommandRegisterEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

@Component
@Event(register = true)
public class Authenticator {

    private static final String URL_REMOTE = "http://auth.seiua.cn";
    private static final String URL_LOCAL = "http://localhost:11451";
    @Use
    public HttpClient httpClient;
    private File accountFile = new File(FabricLoader.getInstance().getGameDir().toFile(), "skymatrix/account.json");
    private File cacheFile = new File(FabricLoader.getInstance().getGameDir().toFile(), "skymatrix/cache/");
    private ObjectMapper mapper = new ObjectMapper();
    private UserAccount account;

    public URL getUrl(String path) {
        String u = FabricLoader.getInstance().isDevelopmentEnvironment() ? URL_LOCAL : URL_REMOTE;
        try {
            return URI.create(u + path).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @EventTarget
    public void registerCommand(CommandRegisterEvent e) {
        e.getDispatcher().register(
                ClientCommandManager.literal("skymatrix").executes(this::root)
                        .then(
                                ClientCommandManager.literal("register").then(ClientCommandManager.argument("username", StringArgumentType.string())
                                        .then(ClientCommandManager.argument("password", StringArgumentType.string())
                                                .then(ClientCommandManager.argument("invite_code", StringArgumentType.string())
                                                        .executes(this::register)
                                                )
                                        )
                                )
                        ).then(
                                ClientCommandManager.literal("login").then(ClientCommandManager.argument("username", StringArgumentType.string())
                                        .then(ClientCommandManager.argument("password", StringArgumentType.string()).executes(this::login)
                                        )
                                )
                        ).then(
                                ClientCommandManager.literal("use").then(ClientCommandManager.argument("code", StringArgumentType.string())
                                        .executes(this::use)
                                )
                        ).then(
                                ClientCommandManager.literal("generate").then(ClientCommandManager.argument("type", StringArgumentType.string())
                                ).then(ClientCommandManager.argument("count", StringArgumentType.string())
                                        .executes(this::generate)
                                )
                        )
        );
    }

    public int root(CommandContext<FabricClientCommandSource> context) {
        System.out.println("register");
        SkyMatrix.mc.player.sendMessage(Text.of("§9§lSkymatrix §r§8>> §b§r Client Authentication System"));
        SkyMatrix.mc.player.sendMessage(Text.of("§9§lSkymatrix §r§8>> §b§r /skymatrix register <username> <password> <invite_code>"));
        SkyMatrix.mc.player.sendMessage(Text.of("§9§lSkymatrix §r§8>> §b§r /skymatrix login <username> <password>"));
        SkyMatrix.mc.player.sendMessage(Text.of("§9§lSkymatrix §r§8>> §b§r /skymatrix use <code>"));
        SkyMatrix.mc.player.sendMessage(Text.of("§9§lSkymatrix §r§8>> §b§r /skymatrix generate <type> <count>"));
        SkyMatrix.mc.player.sendMessage(Text.of("§9§lSkymatrix §r§8>> §b§r Documentation: §b§lhttps://docs.seiua.cn/auth"));


        return 0;
    }

    public int register(CommandContext<FabricClientCommandSource> context) {
        System.out.println("register");
        return 0;
    }

    public int login(CommandContext<FabricClientCommandSource> context) {
        System.out.println("login");
        String username = StringArgumentType.getString(context, "username");
        String password = StringArgumentType.getString(context, "password");
        try {
            httpClient.post(getUrl("/public/login"),
                    this::loginCallBack,
                    new TypeReference<Response<RpLogin>>() {
                    },
                    new RqLogin(password, username, true),
                    null
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    public int use(CommandContext<FabricClientCommandSource> context) {
        System.out.println("use");
        return 0;
    }

    public int generate(CommandContext<FabricClientCommandSource> context) {
        System.out.println("generate");
        return 0;
    }

    public void registerCallBack(Response<RpRegister> data, String raw) {
        System.out.println("registerCallBack");
    }

    public void loginCallBack(Response<RpLogin> data, String raw) {
        if (data.getMessage().equals("OK")) {
            SkyMatrix.mc.player.sendMessage(Text.of("§9§lSkymatrix §r§8>> §b§r Login Success"));
        }
        System.out.println("loginCallBack");
        System.out.println(raw);
    }

    public void useCallBack(Response<RpCode> data, String raw) {
        System.out.println("useCallBack");
    }

    public void generateCallBack(Response<ArrayList<String>> data, String raw) {
        System.out.println("generateCallBack");
    }

}
