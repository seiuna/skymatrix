package cn.seiua.skymatrix.client.module.modules.life;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.Client;
import cn.seiua.skymatrix.client.IToggle;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.component.Use;
import cn.seiua.skymatrix.client.config.Setting;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.client.rotation.ClientRotation;
import cn.seiua.skymatrix.client.rotation.RotationFaker;
import cn.seiua.skymatrix.client.rotation.SmoothRotation;
import cn.seiua.skymatrix.config.Hide;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.KeyBind;
import cn.seiua.skymatrix.config.option.ToggleSwitch;
import cn.seiua.skymatrix.config.option.ValueInput;
import cn.seiua.skymatrix.config.option.ValueSlider;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.*;
import cn.seiua.skymatrix.gui.Icons;
import cn.seiua.skymatrix.gui.Theme;
import cn.seiua.skymatrix.render.BlockTextTarget;
import cn.seiua.skymatrix.utils.MathUtils;
import cn.seiua.skymatrix.utils.ReflectUtils;
import cn.seiua.skymatrix.utils.RenderUtilsV2;
import cn.seiua.skymatrix.utils.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static cn.seiua.skymatrix.SkyMatrix.mc;

@Event
@Sign(sign = Signs.FREE)
@SModule(name = "mininghelper", category = "life")
@SuppressWarnings("all")
public class MiningHelperV2 implements IToggle {
    @Value(name = "keyBind")
    public KeyBind keyBind = new KeyBind(Arrays.asList(), ReflectUtils.getModuleName(this));
    public boolean rclient;
    @Use
    RotationFaker rotationFaker;
    @Use
    SmoothRotation smoothRotation;
    ValueSlider angle = new ValueSlider(180, 0, 180, 0.1f);
    @Value(name = "pingless mining")
    @Sign(sign = Signs.BETA)
    ToggleSwitch pingless = new ToggleSwitch(false);
    @Value(name = "mining speed")
    @Sign(sign = Signs.BETA)
    @Hide(following = "pingless mining")
    ValueInput mininspeed = new ValueInput("-1",Icons.MODE);
    @Value(name = "mining speed%")
    @Sign(sign = Signs.BETA)
    @Hide(following = "pingless mining")
    ValueInput mininspeed1 = new ValueInput("-1",Icons.MODE);
    @Value(name = "range")
    @Sign(sign = Signs.BETA)
    @Hide(following = "pingless mining")
    ValueSlider range = new ValueSlider(-4, -10, 10, 1f);

    public ArrayList<String> targets = new ArrayList<>();

    private List<BlockPos> blockPos;
    public BlockPos target;
    @Use
    Client client;
    public boolean following = false;
    private int holdTick = 0;
    @Use
    ClientRotation clientRotation;
    private static final int RANGE = 10;
    private HashMap<String, Integer> black = new HashMap<>();
    private Vec3d holdVec;

    //Red stained glass
    public static final int RUBY=2300;
    //Orange stained glass
    public static final int AMBER=3000;
    //Light Blue stained glass

    public static final int SAPPHIRE=3000;
    //Lime stained glass
    public static final int JADE=3000;
    //Purple stained glass
    public static final int AMETHYST=3000;
    public static final int OPAL=3000;
    //Yellow stained glass
    public static final int TOPAZ=3800;
    //Magenta stained glass
    public static final int JASPER=4800;

    public void addFilter(BlockFilter filter){
       blockFilter=filter;
    }
    public void addFilter(PositionFilter filter){
       positionFilter=filter;
    }
    public void clearFilter(){
        blockFilter=null;
        positionFilter=null;
    }
    private BlockFilter blockFilter;
    private PositionFilter positionFilter;

    @EventTarget
    private void  doBlackList(BlockBreakingEvent object) {
        if(SkyMatrix.mc.crosshairTarget instanceof BlockHitResult){
                      if(positionFilter!=null&&!positionFilter.filter(((BlockHitResult) SkyMatrix.mc.crosshairTarget).getBlockPos().add(0,0,0))){
                          object.setCancelled(true);
                      }
        }
    }

    public interface BlockFilter{
        boolean filter(Block block);
    }
    public interface PositionFilter{
        boolean filter(BlockPos blockPos);
    }
    private ArrayList<BlockInfo> pos = new ArrayList<>();
    int tick;

    public boolean isTarget(BlockPos bp) {
        if(Client.instance.blackList.contains(Objects.hash(bp.getX(), bp.getY(), bp.getZ())))return false;
        if(black.containsKey(Objects.hash(bp.getX(), bp.getY(), bp.getZ())))return false;
        if(   mc.world.isAir(bp))return false;
        if(   mc.world.isWater(bp))return false;
        String name = (mc.world.getBlockState(bp).getBlock().getName()).toString();
        for (String s: targets) {
            if(name.contains(s)){
                return true;
            }
        }
        if(blockFilter!=null&&blockFilter.filter(mc.world.getBlockState(bp).getBlock())){
            if(positionFilter!=null&&positionFilter.filter(bp))return true;
        }
        return false;
    }

    @EventTarget
    public void onBlock(ServerPacketEvent e) {
    }

    @EventTarget
    public void onMessage(GameMessageEvent e) {

    }

    public int caculateTick(Block block){
        if(!pingless.isValue())return -1;
        int rtv=-1;
        int m=1;
        int ms=Integer.parseInt(this.mininspeed.getValue());

        if(Client.m_ability){
            ms=Integer.parseInt(this.mininspeed1.getValue());
        }
        String blockName = block.getName().toString();
        if(blockName.contains("red_stained_glass"))rtv= Math.round(RUBY*30/(ms*m));
        if(blockName.contains("orange_stained_glass"))rtv= Math.round(AMBER*30/(ms*m));
        if(blockName.contains("light_blue_stained_glass"))rtv= Math.round(SAPPHIRE*30/(ms*m));
        if(blockName.contains("lime_stained_glass"))rtv= Math.round(JADE*30/(ms*m));
        if(blockName.contains("purple_stained_glass"))rtv= Math.round(AMETHYST*30/(ms*m));
        if(blockName.contains("yellow_stained_glass"))rtv= Math.round(TOPAZ*30/(ms*m));
        if(blockName.contains("magenta_stained_glass"))rtv= Math.round(JASPER*30/(ms*m));
        if (blockName.contains("coal")) rtv = Math.round(30 * 30 / (ms * m));
        if (blockName.contains("iron")) rtv = Math.round(30 * 30 / (ms * m));
        if (blockName.contains("diamond")) rtv = Math.round(30 * 30 / (ms * m));
        if (blockName.contains("iron")) rtv = Math.round(30 * 30 / (ms * m));
        if (blockName.contains("lapis")) rtv = Math.round(30 * 30 / (ms * m));
        if (blockName.contains("gold")) rtv = Math.round(30 * 30 / (ms * m));
        if (blockName.contains("emerald")) rtv = Math.round(30 * 30 / (ms * m));
        if (blockName.contains("redstone")) rtv = Math.round(1800 * 30 / (ms * m));
        if(blockName.contains("prismarine"))rtv= Math.round(800*30/(ms*m));
        if(blockName.contains("light_blue_wool"))rtv= Math.round(1500*30/(ms*m));
        if(blockName.contains("cyan_terracotta")||blockName.contains("Gray Wool"))rtv= Math.round(500*30/(ms*m));
        if(blockName.contains("polished_diorite"))rtv= Math.round(2000*30/(ms*m));
        if(blockName.contains("stone"))rtv= Math.round(10*30/(ms*m));
        if(rtv!=-1) {
                rtv=rtv+range.getIntValue();
        }
        return rtv;
    }

    public List<BlockPos> getpos() {
        return pos.stream().map(blockInfo -> blockInfo.blockPos).toList();
    }

    public int getSize() {
        return pos.size();
    }

    @EventTarget
    public void onTick(ClientTickEvent e) {
        for (String ke : black.keySet()) {
            black.put(ke, black.get(ke) - 1);
        }


        pos.clear();
        int range = 12;
        BlockPos blockPos1 = mc.player.getBlockPos();
        CopyOnWriteArrayList<BlockInfo> blockPos = new CopyOnWriteArrayList<>();
        for (int i = 0; i < range; i++) {
            for (int j = 0; j < range; j++) {
                for (int k = 0; k < range; k++) {
                    BlockPos cp = blockPos1.add(i - range / 2, j - range / 2, k - range / 2);
                    if (black.getOrDefault(String.valueOf(Objects.hash(cp.getX(), cp.getY(), cp.getZ())), 0) > 0) {
                        continue;
                    }

                    if (isTarget(cp)) {
                        blockPos.add(new BlockInfo(cp, null));
                    }
                }
            }
        }
        Vec3d start = Vec3d.fromPolar(rotationFaker.getServerPitch(), rotationFaker.getServerYaw());
        while (blockPos.size() > 0) {
            double lastAngle = Double.MAX_VALUE;
            BlockInfo blockPos2 = null;
            Vec3d vc = null;
            for (BlockInfo b : blockPos) {
                if (blockPos2 == null) {
                    blockPos2 = b;
                }
                Vec3d v = traversal(b.blockPos, start);
                if (v != null) {
                    Vec3d vv = v.subtract(mc.player.getEyePos());
                    double angle = MathUtils.calculateAngle(vv, start);
                    if (angle < lastAngle) {
                        lastAngle = angle;
                        blockPos2 = b;
                        b.lookPos = v;
                        vc = v;
                    }
                } else {
                    blockPos.remove(b);
                }
            }
            if (vc != null) {
                start = vc;
                blockPos.remove(blockPos2);
                pos.add(blockPos2);
            }
        }
        if(pos.size()>0){
            target = pos.get(0).blockPos;
            HitResult hitResult1=mc.crosshairTarget;
            if(hitResult1 instanceof BlockHitResult){
                if(((BlockHitResult) hitResult1).getBlockPos().add(0,0,0).equals(target)&&((BlockHitResult) hitResult1).getType() == HitResult.Type.BLOCK){
                    client.setKeepBlockBreaking(true);
                    int t=caculateTick(mc.world.getBlockState(target).getBlock());
                    Client.sendDebugMessage(Text.of(tick+"/"+t));
                    if(t!=-1){
                        tick++;
                        if(tick>t)
                        {
                            tick=0;
                            black.put(Objects.hash(target.getX(), target.getY(), target.getZ()) + "", t + 4);
                            Client.sendDebugMessage(Text.of("next"));
                        }
                    }
            }else {
                    tick=0;
//                    client.setKeepBlockBreaking(false);
                }
            }

            if (this.following && rclient == false) {
                clientRotation.clientLook(RotationUtils.toRotation(pos.get(0).lookPos.subtract(mc.player.getEyePos())), 420, null);
            } else {
                clientRotation.cancelClientLook();
            }
            this.smoothRotation.smoothLook(RotationUtils.toRotation(pos.get(0).lookPos.subtract(mc.player.getEyePos())), 1.2f, null, rclient);
        }else {
            tick=0;
            client.setKeepBlockBreaking(false);
            target=null;
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
        SHIFTING.add(new Vec3d(0.5, 0.5, 0));
        SHIFTING.add(new Vec3d(0, 0.5, 0.5));
        SHIFTING.add(new Vec3d(0.5, 0, 0.5));
        SHIFTING.add(new Vec3d(-0.5, 0.5, 0));
        SHIFTING.add(new Vec3d(0, -0.5, 0.5));
        SHIFTING.add(new Vec3d(0.5, 0, -0.5));
        SHIFTING.add(new Vec3d(-0.5, 0, 0.5));
        SHIFTING.add(new Vec3d(0, 0.5, -0.5));
        SHIFTING.add(new Vec3d(-0.5, 0, -0.5));
        SHIFTING.add(new Vec3d(0, -0.5, -0.5));
        SHIFTING.add(new Vec3d(-0.5, -0.5, 0));
        SHIFTING.add(new Vec3d(0, -0.5, -0.5));
        SHIFTING.add(new Vec3d(-0.5, 0, -0.5));
        SHIFTING.add(new Vec3d(-0.5, -0.5, -0.5));
        SHIFTING.add(new Vec3d(0.5, 0.5, 0.5));
        SHIFTING.add(new Vec3d(0.5, 0.5, -0.5));
        SHIFTING.add(new Vec3d(0.5, -0.5, 0.5));
        SHIFTING.add(new Vec3d(-0.5, 0.5, 0.5));
        SHIFTING.add(new Vec3d(-0.5, -0.5, 0.5));
        SHIFTING.add(new Vec3d(0,0,0));

    }
    public boolean hasTarget(BlockPos blockPos) {
        return target != null;
    }
    public Vec3d traversal(BlockPos blockPos, Vec3d viewVec1) {
        Vec3d viewVec =  Vec3d.fromPolar(rotationFaker.getServerPitch(), rotationFaker.getServerYaw());
        Vec3d playerPos = mc.player.getCameraPosVec(1.0f);
        if(viewVec1!=null){
            viewVec=viewVec1;
        }
        double lastAngle = Double.MAX_VALUE;
        Vec3d rtv=null;
        for (Vec3d vec3dv : SHIFTING) {
            Vec3d cpos=blockPos.toCenterPos().add(vec3dv);
            if(cpos.distanceTo(playerPos)>4.5)continue;
            HitResult hitResult = mc.world.raycast(new RaycastContext(playerPos, cpos, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE,mc.player));
            if (hitResult instanceof BlockHitResult) {
                BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                if (blockHitResult.getBlockPos().equals(blockPos)&&blockHitResult.getType() == HitResult.Type.BLOCK){
                    if (rtv == null) {
                        rtv = cpos;
                        lastAngle = MathUtils.calculateAngle(cpos.subtract(playerPos), viewVec);
                    } else {
                        Vec3d vec3d = cpos.subtract(playerPos);
                        double angle = MathUtils.calculateAngle(vec3d, viewVec);
                        if (angle < lastAngle) {
                            lastAngle = angle;
                            rtv = cpos;
                        }
                    }
                }
            }
        }
        return rtv;
    }

    public Vec3d canSee(BlockPos blockPos) {
        HitResult hitResult = mc.crosshairTarget;
        return traversal(blockPos,null);
    }
    private class BlockInfo {
        public BlockPos blockPos;
        public Vec3d lookPos;

        public BlockInfo(BlockPos blockPos, Vec3d lookPos) {
            this.blockPos = blockPos;
            this.lookPos = lookPos;
        }
    }
    @EventTarget
    public void onRender(WorldRenderEvent e) {
        if(Setting.getInstance().debug.isValue()){
          LivingEntity player = mc.player;
          int i = 1;
          Vec3d last = null;
          for (BlockInfo ps : pos) {
              if(i>6)break;
              new BlockTextTarget(ps.blockPos, Theme.getInstance().THEME_UI_SELECTED::geColor, i++ + "").render(e.getMatrixStack(), e.getTickDelta());
              if(last!=null){
                  RenderUtilsV2.renderLine(e.getMatrixStack(), last, ps.lookPos,new Color(0, 255, 220, 190),2f);
                  last = ps.lookPos;
              }else {
                  last = ps.lookPos;
              }}


    }  }

    @Override
    public void disable() {
        client.setKeepBlockBreaking(false);
        black.clear();
        clientRotation.cancelClientLook();
    }

    @Override
    public void enable() {

    }
}
