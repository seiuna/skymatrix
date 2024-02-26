package cn.seiua.skymatrix.client.module.modules.life;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.Client;
import cn.seiua.skymatrix.client.IToggle;
import cn.seiua.skymatrix.client.RotationFaker;
import cn.seiua.skymatrix.client.SmoothRotation;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.KeyBind;
import cn.seiua.skymatrix.config.option.MultipleChoice;
import cn.seiua.skymatrix.config.option.ToggleSwitch;
import cn.seiua.skymatrix.config.option.ValueSlider;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import cn.seiua.skymatrix.gui.Icons;
import cn.seiua.skymatrix.utils.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.awt.*;
import java.util.List;
import java.util.*;

@Event
@Sign(sign = Signs.FREE)
@SModule(name = "mininghelper", category = "life")
@SuppressWarnings("all")
public class MiningHelper implements IToggle {
    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(Arrays.asList(), ReflectUtils.getModuleName(this));
    @Use
    RotationFaker rotationFaker;
    @Use
    SmoothRotation smoothRotation;
    @Value(name = "angle")
    ValueSlider angle = new ValueSlider(50, 0, 180, 0.1f);
    @Value(name = "blocks")
    MultipleChoice blocks = new MultipleChoice(SkyBlockUtils.getAllOre(), Icons.ORE);
    public boolean hold = false;
    public ArrayList<String> hold_block = new ArrayList<>();
    @Value(name = "mouse down")
    ToggleSwitch mouse = new ToggleSwitch(true);

    private List<BlockPos> blockPos;
    private BlockPos target;
    @Use
    Client client;
    private int holdTick = 0;
    private static final int RANGE = 10;
    private HashMap<String, Integer> black = new HashMap<>();
    private Vec3d holdVec;

    public boolean isTarget(BlockPos bp) {
        String name1 = (SkyMatrix.mc.world.getBlockState(bp).getBlock().getName()).getString();

        if (name1.contains("Grass") || name1.contains("Stone") || name1.contains("Leaves") || name1.contains("Wood") || name1.contains("Poppy") || name1.contains("Dandelion") || name1.contains("Azure") || name1.contains("Bluet")) {
            if (name1.equals("Grass Block")) return false;
            return true;
        }

        String name = BlockUtils.getName(bp).replace("minecraft:", "");
        name = SkyBlockUtils.getNameByMapper(name);
        if (SkyMatrix.mc.world.getBlockState(bp).isAir() || SkyMatrix.mc.world.isWater(bp) || SkyMatrix.mc.world.getFluidState(bp).getFluid().isIn(FluidTags.LAVA))
            return false;
        if (blocks.value.containsKey(name) && !hold) {
            if (blocks.value.get(name)) {
                return true;
            }
        }
        if (hold_block.contains(name) && hold) {
            return true;
        }
        return false;
    }

    @EventTarget
    public void onTick(ClientTickEvent e) {
        if (SkyMatrix.mc.crosshairTarget.getType().equals(HitResult.Type.BLOCK)) {


        }

        client.setKeepBlockBreaking(!mouse.isValue());

        if ((!mouse.isValue() || (SkyMatrix.mc.currentScreen == null && SkyMatrix.mc.options.attackKey.isPressed())) || hold) {
            ItemStack is = SkyMatrix.mc.player.getInventory().getMainHandStack();
            String type = SkyBlockUtils.getItemType(is);
            String name = is.getItem().toString().toLowerCase();
            if (name.contains("Air")) return;
            if ((name.contains("pickaxe") || name.contains("hoe") || name.contains("Axe") || name.contains("shovel")) || ((type != null) && (type.equals("AXE") || type.equals("DRILL") || type.equals("PICKAXE") || type.equals("SHOVEL") || type.equals("HOE")))) {
                blockPos = new ArrayList<>();
                Vec3d viewPos = Vec3d.fromPolar(rotationFaker.getServerPitch(), rotationFaker.getServerYaw());
                Vec3d viewc = SkyMatrix.mc.player.getRotationVecClient();
                Vec3d playerPos = SkyMatrix.mc.player.getEyePos();
                BlockPos blockPos1 = new BlockPos((int) playerPos.x, (int) playerPos.y, (int) playerPos.z);
                Vec3d targetVec = null;
                double lastAngle = 100000;
                double lastD = 100000;
                int flag = 0;
                Vec3d vec3d = null;
                for (int i = 0; i < RANGE; i++) {
                    for (int j = 0; j < RANGE; j++) {
                        for (int k = 0; k < RANGE; k++) {
                            BlockPos cp = blockPos1.add(i - RANGE / 2, j - RANGE / 2, k - RANGE / 2);
                            if (black.getOrDefault(String.valueOf(Objects.hash(cp.getX(), cp.getY(), cp.getZ())), 0) > 0) {
                                continue;
                            }
                            if (MathUtils.calculateAngle(viewc, cp.toCenterPos().subtract(playerPos)) > angle.getValue().doubleValue())
                                continue;
                            if (!isTarget(cp)) {
                                continue;
                            }
                            vec3d = canSee(cp);


                            if (vec3d != null) {
                                Vec3d vec3d1 = cp.toCenterPos().subtract(playerPos);
                                double angle = MathUtils.calculateAngle(vec3d1, viewPos);

                                if (target == null) {
                                    target = cp;
                                    lastAngle = angle;
                                    continue;
                                } else {
                                    if (angle < lastAngle) {
                                        lastAngle = angle;
                                        targetVec = vec3d;
                                        target = cp;
                                        flag++;
                                    }
                                }

                            }
                        }
                    }
                }
                if (flag != 0) {
                    Vec3d vec3d1 = targetVec.multiply(targetVec.length());
                    if (!vec3d1.equals(holdVec)) {
                        holdTick = 0;
                        holdVec = vec3d1;
                    } else {
                        holdTick++;
                        if (holdTick > 60) {
                            black.put(String.valueOf(Objects.hash(this.target.getX(), this.target.getY(), this.target.getZ())), 20);
                        }
                    }
                    smoothRotation.smoothLook(RotationUtils.toRotation(vec3d1), 1.4f, null, true);
                } else {

                }

            }
        }
    }
    private static final ArrayList<Vec3d> SHIFTING = new ArrayList<>();

    static {

        SHIFTING.add(new Vec3d(0.5, 0, 0));
        SHIFTING.add(new Vec3d(0, 0.5, 0));
        SHIFTING.add(new Vec3d(0, 0, 0.5));
        SHIFTING.add(new Vec3d(-0.5, 0, 0));
        SHIFTING.add(new Vec3d(0, -0.5, 0));
        SHIFTING.add(new Vec3d(0, 0, -0.5));


    }

    public Vec3d traversal(BlockPos blockPos) {
        Vec3d viewPos = SkyMatrix.mc.player.getRotationVecClient();
        Vec3d playerPos = SkyMatrix.mc.player.getEyePos();
        for (Vec3d vec3dv : SHIFTING) {

            Vec3d vec3d1 = blockPos.toCenterPos().subtract(playerPos);
            double angle = MathUtils.calculateAngle(vec3d1, viewPos);

            Vec3d vec3d = SkyMatrix.mc.player.getCameraPosVec(1.0f);
            float dd = 4.5F;
            Vec3d vec3d2 = new Vec3d(1, 1, 1);
            Vec3d bvec = blockPos.toCenterPos().add(vec3dv).subtract(vec3d);
            bvec = bvec.multiply(1 / bvec.length());

            Vec3d vec3d3 = vec3d.add(bvec.x * dd, bvec.y * dd, bvec.z * dd);

            HitResult hitResult = SkyMatrix.mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, SkyMatrix.mc.player));
            if (hitResult instanceof BlockHitResult) {
                BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                if (blockHitResult.getBlockPos().equals(blockPos)) {
//                    System.out.println("OK" + vec3dv.add(blockPos.getX(), vec3dv.getY(), vec3dv.getZ()));
                    return bvec;
                }
            }

        }
        return null;
    }

    public Vec3d canSee(BlockPos blockPos) {

        return traversal(blockPos);

    }

    @EventTarget
    public void onRender(WorldRenderEvent e) {
        if (target == null) return;
        HitResult hitResult = SkyMatrix.mc.crosshairTarget;
        if (hitResult instanceof BlockHitResult) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            if (!blockHitResult.getBlockPos().equals(target)) {
                return;
            }
        }
        RenderSystem.disableDepthTest();
        RenderUtils.translateView(e.getMatrixStack());
        RenderUtils.setColor(new Color(0, 255, 220, 20));
        LivingEntity player = SkyMatrix.mc.player;
        RenderUtils.translatePos(e.getMatrixStack(), target);
        RenderUtils.drawSolidBox(new Box(0, 0, 0, 1, 1, 1), e.getMatrixStack());
        e.getMatrixStack().pop();
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();

    }

    @Override
    public void disable() {
        this.target = null;
        client.setKeepBlockBreaking(false);

    }

    @Override
    public void enable() {

    }
}
