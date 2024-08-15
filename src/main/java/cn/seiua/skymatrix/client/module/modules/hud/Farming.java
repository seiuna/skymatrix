package cn.seiua.skymatrix.client.module.modules.hud;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.gui.ClickGui;
import cn.seiua.skymatrix.hud.ClientHud;
import cn.seiua.skymatrix.hud.Hud;
import cn.seiua.skymatrix.message.Message;
import cn.seiua.skymatrix.message.MessageBuilder;
import cn.seiua.skymatrix.utils.BlockUtils;
import cn.seiua.skymatrix.utils.PlayerListUtils;
import cn.seiua.skymatrix.utils.RenderUtils;
import cn.seiua.skymatrix.utils.SkyBlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Event
@Sign(sign = Signs.BETA)
@SModule(name = "farming", category = "hud")
public class Farming implements Hud {

    @Value(name = "farming")
    public ClientHud hud = new ClientHud(100, 100, true, this);
    private Map<String, Integer> price = new HashMap<>();

    private Message message = MessageBuilder.build("farming");

    public Farming() {
        price.put("melon", 3);
        price.put("pumpkin", 10);
        price.put("sugar_cane", 4);
        price.put("cactus", 3);
        price.put("nether_wart", 4); //age 3
        price.put("carrots", 3); //age 7
        price.put("potatoes", 3); //age 6
        price.put("wheat", 6); //age 7
        price.put("brown_mushroom", 10); //age 7
        price.put("red_mushroom", 10); //age 7
    }

    public long getCount() {
        assert SkyMatrix.mc.player != null;
//        ItemStack itemStack = SkyMatrix.mc.player.getInventory().getMainHandStack();
//        if (itemStack != null) {
//            NbtCompound nbtCompound = itemStack.getNbt();
//            if (nbtCompound != null) {
//                NbtCompound aa = nbtCompound.getCompound("ExtraAttributes");
//                if (aa != null) {
//                    return aa.getLong("farmed_cultivating");
//                }
//            }
//        }
        return -1;
    }

    @EventTarget
    public void onTick(ClientTickEvent event) {
        if (SkyMatrix.mc.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockPos bp = ((BlockHitResult) SkyMatrix.mc.crosshairTarget).getBlockPos();
            String name = BlockUtils.getName(bp).replace("minecraft:", "");
            name = SkyBlockUtils.getNameByMapper(name);
            assert SkyMatrix.mc.world != null;
            BlockState blockState = SkyMatrix.mc.world.getBlockState(bp);
            if (price.containsKey(name)) {
                i_price = price.get(name);
                target = name;
            }
        }

        String temp = PlayerListUtils.copyContainsLine("Queue Full!");
        if (temp != null) {
            if (!flag) {
                message.sendMessage(Text.of("Your visitor queue is full!"));
            }

            flag = true;
        } else {
            flag = false;
        }
        boolean flag1 = false;
        ArrayList<String> contexts = new ArrayList<>();
        if (SkyMatrix.mc.getNetworkHandler() != null) {
            List<PlayerListEntry> wtf = SkyMatrix.mc.player.networkHandler.getListedPlayerListEntries().stream().sorted(PlayerListHud.ENTRY_ORDERING).limit(80L).toList();

            for (PlayerListEntry info : wtf) {
                String name = SkyMatrix.mc.inGameHud.getPlayerListHud().getPlayerName(info).getString();
                if (flag1) {
                    contexts.add(name.trim().toLowerCase());
                    if (contexts.size() == 3) break;
                }
                if (name.contains("Starts in:")) {
                    if (name.contains("NOW")) {
                        active = true;
                    } else {
                        active = false;
                    }
                    flag1 = true;
                }
//                System.out.println(name);
            }
            this.contexts = contexts;
        }


    }

    public boolean isIn(String name) {
        if (active) {
            return this.contexts.contains(name);
        }
        return false;
    }

    private ArrayList<String> contexts = new ArrayList<>();
    private boolean active;
    private String target;
    private long startTime;
    private int i_price;
    private long startV;
    private boolean flag;

    public String getContext() {
        return this.target;
    }

    public void start() {
        startV = getCount();
        startTime = System.currentTimeMillis();
    }

    public void stop() {

    }

    @Override
    public void draw(MatrixStack matrixStack, float x, float y) {
        RenderUtils.resetCent();
        RenderUtils.setColor(new Color(0, 0, 0, 100));
        RenderUtils.drawSolidBox(new Box(x, y, 0, x + getHudWidth(), y + getHudHeight(), 0), matrixStack);
        ClickGui.fontRenderer20.setColor(Color.WHITE);
        ClickGui.fontRenderer20.resetCenteredH();
        ClickGui.fontRenderer20.resetCenteredV();

        int tw = 0;
        float startX = x + 15;
        float startY = y + 12;
        String v = "Context: " + this.target;
        tw = Math.max(tw, ClickGui.fontRenderer20.getStringWidth(v));
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, v);
        startY += 25;
        v = "Crops: " + (getCount() - this.startV);
        tw = Math.max(tw, ClickGui.fontRenderer20.getStringWidth(v));
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, v);
        startY += 25;

        v = "Coins: " + ((((getCount() - this.startV) * this.i_price) / 1000000f) / ((System.currentTimeMillis() - this.startTime) / 1000f) * 60 * 60) + "M/h";
        tw = Math.max(tw, ClickGui.fontRenderer20.getStringWidth(v));
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, v);
        startY += 25;
        v = "Time: " + (System.currentTimeMillis() - this.startTime) / 1000 + "s";
        tw = Math.max(tw, ClickGui.fontRenderer20.getStringWidth(v));

        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, v);
        startY += 25;
        width = tw;
    }

    int width;

    @Override
    public int getHudWidth() {
        return width + 30;
    }

    @Override
    public int getHudHeight() {
        return 130;
    }
}
