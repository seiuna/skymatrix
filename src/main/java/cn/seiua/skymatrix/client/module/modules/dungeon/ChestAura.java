package cn.seiua.skymatrix.client.module.modules.dungeon;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.IToggle;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.BlockRenderEvent;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import cn.seiua.skymatrix.render.BlockTarget;
import cn.seiua.skymatrix.render.RenderTarget;
import cn.seiua.skymatrix.utils.TickTimer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Event
@Sign(sign = Signs.BETA)
@SModule(name = "chestAura", category = "dungeon")
public class ChestAura implements IToggle {
    private HashSet<BlockTarget> chests;
    private TickTimer clear = TickTimer.build(2, this::clear);

    @EventTarget
    public void onBlockRender(BlockRenderEvent e) {
        System.out.println(e.getBlockState());

    }

    private void clear() {

        List<BlockTarget> temp = new ArrayList<>(chests);
        for (BlockTarget target : temp) {
            if (target == null) continue;
            assert SkyMatrix.mc.world != null;

        }

    }

    @EventTarget
    public void onTick(ClientTickEvent e) {
        clear.update();

    }

    @EventTarget
    public void onRender(WorldRenderEvent e) {
//        for (RenderTarget renderTarget :
//                chests) {
//            renderTarget.render(e.getMatrixStack(), e.getTickDelta());
//        }

    }

    @Override
    public void enable() {
        chests = new HashSet<>();

    }
}
