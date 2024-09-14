package cn.seiua.skymatrix.client.module.modules.life;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.IToggle;
import cn.seiua.skymatrix.client.Notification;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.module.ModuleManager;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.client.module.modules.combat.AntiBot;
import cn.seiua.skymatrix.client.module.modules.render.LavaEsp;
import cn.seiua.skymatrix.client.rotation.Rotation;
import cn.seiua.skymatrix.client.rotation.RotationFaker;
import cn.seiua.skymatrix.client.rotation.SmoothRotation;
import cn.seiua.skymatrix.config.Hide;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.*;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.event.events.InteractItemEvent;
import cn.seiua.skymatrix.event.events.ServerPacketEvent;
import cn.seiua.skymatrix.event.events.WorldChangeEvent;
import cn.seiua.skymatrix.font.FontRenderer;
import cn.seiua.skymatrix.gui.ClickGui;
import cn.seiua.skymatrix.gui.Icons;
import cn.seiua.skymatrix.hud.ClientHud;
import cn.seiua.skymatrix.hud.Hud;
import cn.seiua.skymatrix.message.Message;
import cn.seiua.skymatrix.message.MessageBuilder;
import cn.seiua.skymatrix.utils.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;
import java.util.*;


@Event
@Sign(sign = Signs.FREE)
@SModule(name = "autofish", category = "life")
@SuppressWarnings("all")
public class AutoFish implements IToggle, Hud {

    @Use
    public Notification notification;
    @Use
    public AntiBot antiBot;
    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(Arrays.asList(GLFW.GLFW_KEY_X), ReflectUtils.getModuleName(this));
    @Value(name = "hud")
    public ClientHud clientHud = new ClientHud(30, 30, true, this);
    @Value(name = "auto disable LavaESP")
    private ToggleSwitch autoDiableESP = new ToggleSwitch(false);
    @Value(name = "rethrow")
    @Sign(sign = Signs.PRO)
    public ToggleSwitch autoThrow = new ToggleSwitch(false);
    @Value(name = "lock rod")
    @Sign(sign = Signs.PRO)
    public ToggleSwitch lockRod = new ToggleSwitch(false);
    @Value(name = "rod")
    @Sign(sign = Signs.PRO)
    @Hide(following = "lock rod")
    public SkyblockItemSelect rod = new SkyblockItemSelect("none", false, Selector::bestRodSelector, Filter::rodFilter);
    @Value(name = "escape")
    @Sign(sign = Signs.PRO)
    public ToggleSwitch escape = new ToggleSwitch(false);
    @Value(name = "escape cmd")
    @Hide(following = "escape")
    @Sign(sign = Signs.PRO)
    public ValueInput command = new ValueInput("lobby", Icons.CMD);
    @Value(name = "escape way")
    @Hide(following = "escape")
    @Sign(sign = Signs.FREE)
    public MultipleChoice escapeWay = new MultipleChoice(Map.of("admin rotated", false, "admin hook moved", false, "admin location transformed", false, "someone nearing", false), Icons.WARNING);
    @Value(name = "escape range")
    @Hide(following = "escape")
    @Sign(sign = Signs.PRO)
    public ValueSlider range = new ValueSlider(15d, 0d, 30d, 0.1);
    @Value(name = "sneaking")
    @Sign(sign = Signs.PRO)
    public ToggleSwitch sneaking = new ToggleSwitch(false);
    @Value(name = "sneakingMode")
    @Hide(following = "sneaking")
    @Sign(sign = Signs.FREE)
    public SingleChoice<String> sneakingMode = new SingleChoice(List.of("always", "smart"), SingleChoice.MODE);
    @Value(name = "random tick")
    @Sign(sign = Signs.FREE)
    public ToggleSwitch random = new ToggleSwitch(true);
    @Value(name = "catch tick")
    @Hide(following = "random tick")
    @Sign(sign = Signs.FREE)
    public DoubleValueSlider randomCatchTick = new DoubleValueSlider(5, 2, 0, 10, 1);
    @Value(name = "throw tick")
    @Hide(following = "random tick")
    @Sign(sign = Signs.FREE)
    public DoubleValueSlider randomThrowTick = new DoubleValueSlider(6, 1, 0, 20, 1);
    @Value(name = "auto sell")
    @Sign(sign = Signs.PRO)
    public ToggleSwitch autoSell = new ToggleSwitch(false);
    @Value(name = "adjust position")
    @Sign(sign = Signs.FREE)
    public ToggleSwitch adjust = new ToggleSwitch(false);
    @Value(name = "position")
    @Hide(following = "adjust position")
    @Sign(sign = Signs.FREE)
    public SingleChoice<String> position = new SingleChoice(List.of("right", "left", "forward", "backward"), SingleChoice.MODE);
    @Value(name = "antiAFK")
    @Sign(sign = Signs.FREE)
    public ToggleSwitch antiAfk = new ToggleSwitch(false);
    @Value(name = "antiAFKMode")
    @Hide(following = "antiAFK")
    @Sign(sign = Signs.FREE)
    public SingleChoice<String> afkMode = new SingleChoice(List.of("when throw", "when catch", "smart"), SingleChoice.MODE);
    @Value(name = "antiAFKWay")
    @Hide(following = "antiAFK")
    @Sign(sign = Signs.FREE)
    public SingleChoice<String> afkWay = new SingleChoice(List.of("AD", "WS"), SingleChoice.MODE);
    @Value(name = "disable")
    @Sign(sign = Signs.FREE)
    public ToggleSwitch disable = new ToggleSwitch(false);
    @Value(name = "6")
    @Hide(following = "disable")
    @Sign(sign = Signs.FREE)
    public MultipleChoice disables = new MultipleChoice(Map.of("world change", false, "throw failed", false), Icons.WARNING);
    @Value(name = "auto kill")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch autoKill = new ToggleSwitch(true);
    @Value(name = "weapon")
    @Sign(sign = Signs.PRO)
    @Hide(following = "auto kill")
    public SkyblockItemSelect weapon = new SkyblockItemSelect("none", false, null, Filter::weaponFilter);
    @Value(name = "server rotation")
    @Hide(following = "auto kill")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch serverRotation = new ToggleSwitch(true);
    @Value(name = "kill range")
    @Hide(following = "auto kill")
    @Sign(sign = Signs.BETA)
    public DoubleValueSlider killRange = new DoubleValueSlider(0d, 4d, 0d, 20d, 0.5d);
    @Value(name = "kill size")
    @Hide(following = "auto kill")
    @Sign(sign = Signs.BETA)
    public ValueSlider killSize = new ValueSlider(15, 0, 60, 1);
    @Value(name = "kill mode")
    @Hide(following = "auto kill")
    @Sign(sign = Signs.BETA)
    public SingleChoice<String> killMode = new SingleChoice<>(List.of("Right Click", "Left Click"), SingleChoice.MODE);
    @Value(name = "Crystal Water")
    @Sign(sign = Signs.FREE)
    public ToggleSwitch ML = new ToggleSwitch(false);
    @Value(name = "q")
    @Hide(following = "Crystal Water")
    @Sign(sign = Signs.FREE)
    public MultipleChoice crystalWater = new MultipleChoice(SkyBlockUtils.getCrystalWaterSeaCreatureMap(), Icons.ATTACK);
    @Value(name = "Crystal Lava")
    @Sign(sign = Signs.FREE)
    public ToggleSwitch ML1 = new ToggleSwitch(false);
    @Value(name = "w")
    @Hide(following = "Crystal Lava")
    @Sign(sign = Signs.FREE)
    public MultipleChoice crystalLava = new MultipleChoice(SkyBlockUtils.getCrystalLavaSeaCreatureMap(), Icons.ATTACK);
    @Value(name = "Event")
    @Sign(sign = Signs.FREE)
    public ToggleSwitch ML2 = new ToggleSwitch(false);
    @Value(name = "e")
    @Hide(following = "Event")
    @Sign(sign = Signs.FREE)
    public MultipleChoice eventSC = new MultipleChoice(SkyBlockUtils.getEventSeaCreatureMap(), Icons.ATTACK);
    @Value(name = "Water")
    @Sign(sign = Signs.FREE)
    public ToggleSwitch ML3 = new ToggleSwitch(false);
    @Value(name = "r")
    @Hide(following = "Water")
    @Sign(sign = Signs.FREE)
    public MultipleChoice water = new MultipleChoice(SkyBlockUtils.getWaterSeaCreatureMap(), Icons.ATTACK);
    @Value(name = "Crimson")
    @Sign(sign = Signs.FREE)
    public ToggleSwitch ML4 = new ToggleSwitch(false);
    @Value(name = "t")
    @Hide(following = "Crimson")
    @Sign(sign = Signs.FREE)
    public MultipleChoice crimson = new MultipleChoice(SkyBlockUtils.getCrimsonSeaCreatureMap(), Icons.ATTACK);
    @Value(name = "misc")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch misc = new ToggleSwitch(true);
    @Value(name = "angle")
    @Hide(following = "misc")
    @Sign(sign = Signs.BETA)
    public ValueSlider angle = new ValueSlider(4f, 0f, 40f, 0.1f);
    @Value(name = "distance")
    @Hide(following = "misc")
    @Sign(sign = Signs.BETA)
    public ValueSlider distance = new ValueSlider(0.4f, 0f, 20f, 0.1f);


    private Map<Object, Boolean> fullMap = new HashMap<>();

    public AutoFish() {

    }

    @Use
    private LavaEsp wormLavaEsp;
    @Use
    private SmoothRotation smoothRotation;
    @Use
    private RotationFaker rotationFaker;

    private Message message = MessageBuilder.build("autofish");

    enum Status {
        CAUGHT, THROWN, NONE, THROWING, WAITING, KILLING, CHECKING, IMMEDIATE

    }

    /**
     * autokill
     */
    private Status aks = Status.NONE;
    /**
     * rod
     */
    private Status rs = Status.NONE;
    private Status rts = Status.NONE;
    private Status afks = Status.NONE;
    private boolean catc;
    private Vec2f start;

    @EventTarget
    public void onPacket(ServerPacketEvent event) {
        if (event.getPacket() instanceof PlaySoundS2CPacket) {
            PlaySoundS2CPacket packet = (PlaySoundS2CPacket) event.getPacket();
            Optional<SoundEvent> optional = packet.getSound().getKeyOrValue().right();
            String name = null;
            if (optional.isPresent()) {
                name = optional.get().getId().getPath();
            } else {
                name = packet.getSound().getKey().get().getValue().getPath();
            }
            if (packet.getVolume() == 0.25) {
                if (name.contains("entity.fishing_bobber.splash") || name.contains("entity.generic.splash") || name.contains("entity.player.splash")) {
                    if (canCatch(packet.getX(), packet.getY(), packet.getZ())) {
                        catc = true;
                    }
                }
            }
        }
        //do server rotation and location transfrom
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            PlayerPositionLookS2CPacket packet = (PlayerPositionLookS2CPacket) event.getPacket();
            if (!packet.getFlags().contains((Object) PositionFlag.X_ROT) || (!packet.getFlags().contains((Object) PositionFlag.Y_ROT))) {
                if (SkyMatrix.mc.player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof FishingRodItem) {
                    if (this.escape.isValue()) {
                        if (this.escapeWay.getValue().get("admin location transformed")) {
                            boolean bl = packet.getFlags().contains((Object) PositionFlag.X);
                            boolean bl2 = packet.getFlags().contains((Object) PositionFlag.Y);
                            boolean bl3 = packet.getFlags().contains((Object) PositionFlag.Z);
                            if ((!bl) || (!bl2) || (!bl3)) {
                                if (!isEscape) {
                                    message.sendWarningMessage(Text.of("§4Admin Location Transfrom!"));
                                    isEscape = true;
                                    escapeTimer = TickTimer.build(50, this::disableE);
                                    this.aks = Status.IMMEDIATE;
                                    this.dokill();
                                }
                            }
                        }
                        if (this.escapeWay.getValue().get("admin rotated")) {
                            if (!isEscape) {
                                message.sendWarningMessage(Text.of("§4Admin Rotation!"));
                                isEscape = true;
                                escapeTimer = TickTimer.build(50, this::disableE);
                                this.dokill();
                            }
                        }
                    }

                }
            }
        }
    }

    @EventTarget
    public void onUse(InteractItemEvent event) {
        if (event.getItemStack().getItem() instanceof FishingRodItem) {
            start = new Vec2f((float) SkyMatrix.mc.player.getX(), (float) SkyMatrix.mc.player.getZ());
            this.rts = Status.NONE;
        }
    }

    int oldSlot;
    private Vec3d targetVec = null;

    private boolean doAutoKill() {
        boolean over = false;
        if (this.aks == Status.KILLING || this.aks == Status.IMMEDIATE) {
            int slot = this.weapon.slot();
            if (slot == -1) {
                disableE();
                message.sendWarningMessage(Text.of("没有设置武器"));
                return false;
            }
            t11 = false;
            if (this.targetSet == null || this.targetSet.size() == 0) {
                over = true;
                this.targetSet = new HashSet<>();
            }
            ArrayList<Entity> entities = new ArrayList<>();
            entities.addAll(this.targetSet);

            boolean flag = false;
            for (Entity entity : entities) {
                if (entity.isAlive()) {
                    Vec3d vec3d = entity.getPos();
                    String name = entity.getName().getString();
                    if (name.contains("Flaming Worm") || name.contains("Sea Leech") || name.contains("Rider of the Deep") || name.contains("Catfish") || name.contains("Squid")) {
                        vec3d = vec3d.add(0, -0.1, 0);
                    } else {
                        if (name.contains("Guardian Defender") || name.contains("Sea Guardian")) {
                            vec3d = vec3d.add(0, -0.7, 0);

                        } else {
                            vec3d = vec3d.add(0, -2, 0);
                        }
                    }
                    if (serverRotation.isValue()) {
                        smoothRotation.smoothLook(RotationUtils.getNeededRotations(vec3d), 2, () -> {
                        }, false);
                    }
                    flag = true;
                    targetVec = vec3d;
                    break;
                } else {
                    this.targetSet.remove(entity);
                }
            }
            if (entities.size() == 0) {
                over = true;
            }
            if (flag) {

                SkyMatrix.mc.player.getInventory().selectedSlot = slot;
                if (SkyMatrix.mc.player.getInventory().selectedSlot == slot) {
                    if (this.clickDelay == null || this.clickDelay.getTick() < 0) {
                        this.clickDelay = TickTimer.build(7, this::doAutoKillCallBack);
                    }
                }


            } else {
                over = true;
            }
        }

        if (over) {
            targetVec = null;
            t11 = false;
            resetStatus();
            SkyMatrix.mc.player.getInventory().selectedSlot = oldSlot;
            smoothRotation.smoothLook(new Rotation(lastYaw, lastPitch), 4, null, false);
            killDelay = TickTimer.build(8, () -> {
                SkyMatrix.mc.player.getInventory().selectedSlot = oldSlot;
                t11 = true;
            });

            return true;
        }
        return false;
    }

    boolean t11;
    boolean t21;
    boolean t31;
    int fishCount;
    int t51;

    private void setupAutokill() {
        lastPitch = rotationFaker.getServerPitch();
        lastYaw = rotationFaker.getServerYaw();
    }

    private void doAutoKillCallBack() {
        float lastY = SkyMatrix.mc.player.getYaw();
        float lastP = SkyMatrix.mc.player.getPitch();
        if (!this.serverRotation.isValue()) {
            if (targetVec != null) {
                Rotation rotation = RotationUtils.toRotation(targetVec.subtract(SkyMatrix.mc.player.getEyePos()));
                SkyMatrix.mc.player.setPitch(rotation.getPitch());
                SkyMatrix.mc.player.setYaw(rotation.getYaw());
            }

        }
        SkyMatrix.mc.interactionManager.interactItem(SkyMatrix.mc.player, Hand.MAIN_HAND);
        if (!this.serverRotation.isValue()) {
            SkyMatrix.mc.player.setPitch(lastP);
            SkyMatrix.mc.player.setYaw(lastY);
        }

    }

    public void update() {
        if (this.escapeTimer != null)
            this.escapeTimer.update();
        if ((this.aks == Status.IMMEDIATE) || (this.aks == Status.KILLING)) {
            if (this.clickDelay != null)
                this.clickDelay.update();

        } else {
            if (this.OneTickTimer_throw != null)
                this.OneTickTimer_throw.update();
            if (this.OneTickTimer_catch != null)
                this.OneTickTimer_catch.update();
            if (this.afkBackTimer != null)
                this.afkBackTimer.update();
            if (this.throwCheck != null)
                this.throwCheck.update();


        }


        if (SkyMatrix.mc.player.fishHook != null) {
            this.rs = Status.WAITING;

        } else {
            this.rs = Status.NONE;
        }
        if (failure == 5) {
            if (this.disables.getValue().get("throw failed")) {
                message.sendWarningMessage(Text.of("你§4抛竿失败§r了太多次，所以Autofish被自动关闭了!"));

                this.disableE();
                failure = 0;
            }

        }
    }

    private void doLockRod() {
        if (this.aks != Status.IMMEDIATE || this.aks != Status.KILLING) {
            if (t11) {
                if (lockRod.isValue() && this.rod.slot() != -1)
                    SkyMatrix.mc.player.getInventory().selectedSlot = this.rod.slot();
            }

        }

    }

    @EventTarget
    public void onWorld(WorldChangeEvent event) {

        if (disable.isValue()) {
            if (this.disables.getValue().get("world change")) {
                this.disableE();
            }
        }
    }

    private void resetStatus() {
        this.afks = Status.NONE;
        this.aks = Status.NONE;
        this.rts = Status.NONE;
        this.rs = Status.NONE;
    }

    @EventTarget
    public void onTick(ClientTickEvent event) {
        if (SkyMatrix.mc.player.fishHook == null) {


            //不知道为什么 反正是抛竿与拉杆间隔太短导致的
            for (Entity entity : SkyMatrix.mc.world.getEntities()) {
                if (entity instanceof FishingBobberEntity) {
                    FishingBobberEntity fe = (FishingBobberEntity) entity;
                    if (fe.getPlayerOwner().equals(SkyMatrix.mc.player)) {
                        this.rts = Status.WAITING;
                        this.rts = Status.CHECKING;
//                    System.out.println(((FishingBobberEntity) fe).getHookedEntity());
                        SkyMatrix.mc.player.fishHook = fe;
                        break;
                    }

                }
            }
        }
        if (killDelay != null) {
            killDelay.update();
        }
        update();
        if (doAutoKill()) {
            return;
        }

        doLockRod();

        doEscape();
        if (killDelay != null && killDelay.getTick() > 0) {
            return;
        }
        if (this.autoDiableESP.isValue()) {

            if (fishCount > 2) {
                this.wormLavaEsp.tempDisable = true;
            } else {
                this.wormLavaEsp.tempDisable = false;
            }
        } else {
            this.wormLavaEsp.tempDisable = false;
        }
        if (!(SkyMatrix.mc.player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof FishingRodItem)) {
            this.rs = Status.NONE;
            this.afks = Status.NONE;
            this.rts = Status.NONE;
            this.catc = false;
            fishCount = 0;
            return;
        }
        if (SkyMatrix.mc.options.useKey.isPressed()) {

            this.rts = Status.NONE;

        }

        doSneaking();
        doCatch();
        doRethrow();
        doAntiAfk();
        doAutoKillCheck();
    }

    private void doRethrow() {
        if (this.autoThrow.isValue()) {
            if (this.rs == Status.NONE && this.rts == Status.NONE) {
                throwCheck = TickTimer.build(30 + this.randomThrowTick.maxValue().intValue(), null);
                this.OneTickTimer_throw = TickTimer.build((int) this.randomThrowTick.getRandomValue(), this::throwRod);
                this.rts = Status.CHECKING;

            }
        }
        if (this.rts == Status.CHECKING) {
            checkHook();
        }
    }

    public void checkHook() {
        FishingBobberEntity fishHook = SkyMatrix.mc.player.fishHook;
        if (fishHook != null) {
            BlockPos blockPos = fishHook.getBlockPos();
            if (fishHook.isOnGround() && !(fishHook.isWet() || fishHook.isOnFire())) {
                SkyMatrix.mc.interactionManager.interactItem(SkyMatrix.mc.player, SkyMatrix.mc.player.getActiveHand());
                SkyMatrix.mc.player.fishHook = null;
                SkyMatrix.mc.player.swingHand(SkyMatrix.mc.player.getActiveHand());
                failure++;
                this.rts = Status.NONE;
            }
            if (SkyMatrix.mc.player.fishHook != null) {
                if (SkyMatrix.mc.player.fishHook.getHookedEntity() != null && !(SkyMatrix.mc.player.fishHook.getHookedEntity() instanceof ArmorStandEntity)) {
                    SkyMatrix.mc.interactionManager.interactItem(SkyMatrix.mc.player, SkyMatrix.mc.player.getActiveHand());
                    SkyMatrix.mc.player.fishHook = null;
                    SkyMatrix.mc.player.swingHand(SkyMatrix.mc.player.getActiveHand());
                    failure++;
                    this.rts = Status.NONE;
                }
            }

        }

        if (throwCheck.getTick() <= 0 && fishHook == null && this.rts == Status.CHECKING) {
            this.rts = Status.NONE;
            this.rs = Status.NONE;
            this.aks = Status.NONE;
            this.afks = Status.NONE;
        }
    }

    private void doCatch() {
        if (catc && this.rs == rs.WAITING && SkyMatrix.mc.player.fishHook != null) {
            catc = false;
            OneTickTimer_catch = TickTimer.build(random.isValue() ? (int) this.randomCatchTick.getRandomValue() : 3, this::catchFish);
            if (this.afkMode.selectedValue().equals("when catch")) {
                this.afks = Status.CAUGHT;
            }
            if (this.afkMode.selectedValue().equals("smart")) {
                antiAFKRate.append();
                if (this.antiAFKRate.tryTrigger()) {
                    this.afks = Status.IMMEDIATE;
                }
            }
        }
    }

    private HashSet<Entity> targetSet;

    private void doAutoKillCheck() {


        fullMap.putAll(this.crystalLava.value);
        fullMap.putAll(this.water.value);
        fullMap.putAll(this.crystalWater.value);
        fullMap.putAll(this.eventSC.value);
        fullMap.putAll(this.water.value);
        targetSet = new HashSet<>();
        int i = 0;
        for (Entity entity : SkyMatrix.mc.world.getEntities()) {
            if (entity instanceof ArmorStandEntity) {
                ArmorStandEntity entity1 = (ArmorStandEntity) entity;
                String name = entity1.getName().getString();
                if (name.contains("[Lv")) {
                    int t = name.indexOf(' ');
                    int t2 = name.lastIndexOf(' ');
                    String target = name.substring(t, t2);
                    if (fullMap.containsKey(target.trim())) {
                        if (fullMap.get(target.trim())) {
                            double d = entity.getPos().distanceTo(SkyMatrix.mc.player.getPos());
                            if (d >= this.killRange.minValue().doubleValue() && d <= this.killRange.maxValue().doubleValue()) {
                                targetSet.add(entity);
                                if (this.autoKill.isValue() && this.aks != Status.KILLING && this.aks != Status.IMMEDIATE) {
                                    i++;
                                }
                            }
                        }

                    }

                }
            }
            seacreatire = i;
            if (this.killSize.getValue().intValue() <= i) {
                this.aks = Status.KILLING;
                setupAutokill();

                oldSlot = SkyMatrix.mc.player.getInventory().selectedSlot;
            }
        }
    }

    private List<String> playerlist = new ArrayList<>();

    private void doEscape() {
        if (escape.isValue()) {
            playerlist = new ArrayList<>();
            boolean flag = false;
            for (AbstractClientPlayerEntity player : SkyMatrix.mc.world.getPlayers()) {
                if (antiBot.isPlayer(player.getUuid().toString()) && player != SkyMatrix.mc.player) {
                    if (player.getName().getString().contains("TankMan8964_L")) {
                        continue;
                    }
                    if (player.getName().getString().contains("Techniqueikun")) {
                        continue;
                    }
                    if (player.getName().getString().contains("Technoikun")) {
                        continue;
                    }
                    double d = (SkyMatrix.mc.player.getPos().distanceTo(player.getPos()));
                    playerlist.add(player.getName().getString() + ": " + (int) d);
                    if (range.getValue().doubleValue() > d) {
                        if (escapeWay.getValue().get("someone nearing")) {
                            if (!isEscape) {
                                message.sendWarningMessage(Text.of("有人接近! id: " + player.getName().getString()));
                                this.dokill();
                                flag = true;
                                break;
                            }
                        }
                    }
                }
            }
            if (flag && !isEscape) {
                isEscape = true;
                escapeTimer = TickTimer.build(80, this::disableE);
            }
        }
    }

    //改成 逃逸
    private void disableE() {
        if (isEscape) {
            SkyMatrix.mc.getNetworkHandler().sendCommand(this.command.getValue());
            isEscape = false;
        }
        ModuleManager.instance.disable(this.getClass());
    }


    private OneTickTimer OneTickTimer_catch;
    private OneTickTimer OneTickTimer_throw;


    public void dokill() {
        this.aks = Status.IMMEDIATE;
        setupAutokill();
    }

    private void catchFish() {
        if (SkyMatrix.mc.player.fishHook != null) {
            SkyMatrix.mc.player.swingHand(SkyMatrix.mc.player.getActiveHand());
            SkyMatrix.mc.interactionManager.interactItem(SkyMatrix.mc.player, SkyMatrix.mc.player.getActiveHand());
            SkyMatrix.mc.player.fishHook = null;
            SkyMatrix.mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2.0f, 10));
            this.rs = Status.NONE;
            this.rts = Status.NONE;
            fishCount++;
            failure = 0;

        }
    }

    public void doSneaking() {
        if (sneaking.isValue()) {
            if (sneakingMode.selectedValue().equals("always")) {
                MinecraftClient.getInstance().options.sneakKey.setPressed(true);
                return;
            }
            if (sneakingMode.selectedValue().equals("smart")) {
                boolean flag = false;
                for (AbstractClientPlayerEntity entity : SkyMatrix.mc.world.getPlayers()) {
                    if (entity != SkyMatrix.mc.player) {
                        if (antiBot.isPlayer(entity.getUuid().toString())) {
                            if (entity.getBlockPos().isWithinDistance(SkyMatrix.mc.player.getBlockPos(), 20)) {
                                flag = true;
                                break;
                            } else if (entity.canSee(SkyMatrix.mc.player)) {
                                Vec3d vec3d1 = entity.getRotationVecClient();
                                Vec3d vec3d2 = new Vec3d(SkyMatrix.mc.player.getX() - entity.getX(), SkyMatrix.mc.player.getY() - entity.getY(), SkyMatrix.mc.player.getZ() - entity.getZ());

                                if (MathUtils.calculateAngle(vec3d1, vec3d2) < 10) {
                                    flag = true;
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
    }

    private RateStacker antiAFKRate = new RateStacker(10);

    private Rotation lock;

    boolean afk;
    private OneTickTimer afkBackTimer;
    private OneTickTimer escapeTimer;
    OneTickTimer throwCheck;
    OneTickTimer killDelay;
    OneTickTimer clickDelay;

    public void doAntiAfk() {
        if (this.antiAfk.isValue()) {
            if (((this.afks == Status.THROWN && this.afkMode.selectedValue().equals("when throw"))) || (this.afks == Status.CAUGHT && this.afkMode.selectedValue().equals("when catch")) || this.afks == Status.IMMEDIATE) {

                MinecraftClient.getInstance().options.sneakKey.setPressed(true);
                if (afkWay.selectedValue().equals("AD")) {
                    if (afk) {
                        SkyMatrix.mc.options.leftKey.setPressed(true);
                    } else {
                        SkyMatrix.mc.options.rightKey.setPressed(true);
                    }
                }
                if (afkWay.selectedValue().equals("WS")) {
                    if (afk) {
                        SkyMatrix.mc.options.forwardKey.setPressed(true);
                    } else {
                        SkyMatrix.mc.options.backKey.setPressed(true);
                    }
                }
                if (adjust.isValue()) {
                    if (position.selectedValue().equals("left")) {
                        SkyMatrix.mc.options.leftKey.setPressed(true);
                    }
                    if (position.selectedValue().equals("right")) {
                        SkyMatrix.mc.options.rightKey.setPressed(true);
                    }
                    if (position.selectedValue().equals("forward")) {
                        SkyMatrix.mc.options.forwardKey.setPressed(true);
                    }
                    if (position.selectedValue().equals("backward")) {
                        SkyMatrix.mc.options.backKey.setPressed(true);
                    }
                }


                afkBackTimer = TickTimer.build(4, this::afkCallBack);
                this.afks = Status.NONE;
                this.antiAFKRate.reset();
            }
        }


    }

    //change
    private boolean isEscape;
    private boolean t2;

    //change
    private int failure;
    private int seacreatire;

    public void afkCallBack() {
        SkyMatrix.mc.options.sneakKey.setPressed(false);


        SkyMatrix.mc.options.leftKey.setPressed(false);
        SkyMatrix.mc.options.rightKey.setPressed(false);
        SkyMatrix.mc.options.forwardKey.setPressed(false);
        SkyMatrix.mc.options.backKey.setPressed(false);


        afk = !afk;
    }

    private boolean canCatch(double x, double y, double z) {

        FishingBobberEntity entity1 = SkyMatrix.mc.player.fishHook;
        if (entity1 == null) return false;
        //夹角
        Vec2f vec2f0 = new Vec2f((float) (entity1.getX() - start.x), (float) (entity1.getZ() - start.y));
        Vec2f vec2f1 = new Vec2f((float) (x - start.x), (float) (z - start.y));

        double a = MathUtils.calculateAngle(vec2f0, vec2f1);
        double d = new Vec3d(x, 0, z).distanceTo(new Vec3d(entity1.getX(), 0, entity1.getZ()));
        message.sendDebugMessage(Text.of("angle: " + a + " distance: " + d));
        if ((a < angle.getValue().doubleValue()) || d < distance.getValue().doubleValue()) {
            return true;
        }

        return false;
    }

    private float lastPitch;
    private float lastYaw;

    //antiafk
    private float x;
    private float y;

    @Override
    public void disable() {
        this.wormLavaEsp.tempDisable = false;
        fishCount = 0;
        MinecraftClient.getInstance().options.sneakKey.setPressed(false);
        this.rts = Status.NONE;
        this.rs = Status.NONE;
        this.aks = Status.NONE;
        this.afks = Status.NONE;
        this.isEscape = false;
        t11 = true;
    }

    private void throwRod() {

        SkyMatrix.mc.player.swingHand(SkyMatrix.mc.player.getActiveHand());
        SkyMatrix.mc.interactionManager.interactItem(SkyMatrix.mc.player, SkyMatrix.mc.player.getActiveHand());
        this.rts = Status.CHECKING;

        if (this.afkMode.selectedValue().equals("when throw")) {
            this.afks = Status.THROWN;
        }


        if (antiAfk.isValue()) {
            Random random1 = new Random();
            int rv1 = Math.abs(random1.nextInt()) % 16 - 8;
            int rv2 = Math.abs(random1.nextInt()) % 16 - 8;
            if (this.t31) {
                this.t31 = false;
                smoothRotation.smoothLook(new Rotation((float) (SkyMatrix.mc.player.getYaw() + x), (float) (SkyMatrix.mc.player.getPitch() + y)), 6, null, true);
            } else {
                this.x = rv1 * -1;
                this.y = rv2 * -1;
                this.t31 = true;
                smoothRotation.smoothLook(lock, 6, null, true);

            }
        } else {
            this.t31 = true;
        }


    }

    @Override
    public void enable() {
        lock = RotationUtils.toRotation(SkyMatrix.mc.player.getRotationVecClient());
        this.wormLavaEsp.tempDisable = false;
        fishCount = 0;
        t11 = true;
        this.rts = Status.NONE;
        this.rs = Status.NONE;
        this.aks = Status.NONE;
        this.afks = Status.NONE;
        this.isEscape = false;
        assert SkyMatrix.mc.player != null;
    }

    @Override
    public void draw(MatrixStack matrixStack, float x, float y) {
        RenderUtils.resetCent();
        RenderUtils.setColor(new Color(0, 0, 0, 100));
        RenderUtils.drawSolidBox(new Box(x, y, 0, x + getHudWidth(), y + getHudHeight(), 0), matrixStack);
        ClickGui.fontRenderer20.resetCenteredH();
        ClickGui.fontRenderer20.resetCenteredV();
        ClickGui.fontRenderer20.side = FontRenderer.RIGHT_TOP;
        float startX = x + 40;
        float startY = y + 40;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "Autofish Debug ");
        startY += 30;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "当前浮漂是否存在 " + SkyMatrix.mc.player.fishHook);
        startY += 30;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "当前鱼竿状态 " + this.rs);
        startY += 30;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "当前ReThrow状态 " + this.rts);
        startY += 30;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "当前AntiAFK状态 " + this.afks);
        startY += 30;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "当前自动击杀状态 " + this.aks);
        startY += 30;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "是否开始逃逸 " + (this.isEscape ? "是" : "否"));
        startY += 30;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "当前抛竿失败次数 " + this.failure);
        startY += 30;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "周围已选择的海洋生物数量 " + this.seacreatire + " ->> " + this.killSize.getValue().intValue());
        startY += 30;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "当前概率累加器状态 " + this.antiAFKRate.getCurrent() + "/" + antiAFKRate.getTarget());
        startY += 30;
        ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, "周围的玩家");
        for (String s : playerlist) {
            startY += 30;
            ClickGui.fontRenderer20.drawString(matrixStack, startX, startY, s);
        }
    }

    @Override
    public int getHudWidth() {
        return 500;
    }

    @Override
    public int getHudHeight() {
        return 700;
    }
}
