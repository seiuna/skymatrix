package cn.seiua.skymatrix.client.module.modules.dungeon;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.client.IToggle;
import cn.seiua.skymatrix.client.component.Event;
import cn.seiua.skymatrix.client.component.SModule;
import cn.seiua.skymatrix.client.module.Sign;
import cn.seiua.skymatrix.client.module.Signs;
import cn.seiua.skymatrix.config.Hide;
import cn.seiua.skymatrix.config.Value;
import cn.seiua.skymatrix.config.option.Filter;
import cn.seiua.skymatrix.config.option.SkyblockItemSelect;
import cn.seiua.skymatrix.config.option.ToggleSwitch;
import cn.seiua.skymatrix.event.EventTarget;
import cn.seiua.skymatrix.event.events.ClientTickEvent;
import cn.seiua.skymatrix.event.events.WorldChangeEvent;
import cn.seiua.skymatrix.event.events.WorldRenderEvent;
import cn.seiua.skymatrix.utils.RenderUtilsV2;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.HashMap;

@Event
@Sign(sign = Signs.BETA)
@SModule(name = "ghostBlock", category = "dungeon")
public class GhostBlock implements IToggle {
    @Value(name = "render", desc = "")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch render = new ToggleSwitch(true);
    @Value(name = "with item", desc = "")
    @Sign(sign = Signs.BETA)
    public ToggleSwitch with = new ToggleSwitch(true);
    @Value(name = "item", desc = "")
    @Sign(sign = Signs.BETA)
    @Hide(following = "with item")
    SkyblockItemSelect item = new SkyblockItemSelect("", false, null, Filter::all);
    private HashMap<BlockPos, BlockState> blocks = new HashMap<>();

    @EventTarget
    public void onTick(ClientTickEvent event) {
        if (!with.isValue() || item.slot() == SkyMatrix.mc.player.getInventory().selectedSlot) {
            if (SkyMatrix.mc.options.useKey.isPressed()) {
                HitResult result = SkyMatrix.mc.crosshairTarget;
                if (result.getType() == HitResult.Type.BLOCK) {
                    Block block = SkyMatrix.mc.world.getBlockState(((BlockHitResult) result).getBlockPos()).getBlock();
                    if (block == Blocks.CHEST || block == Blocks.ENDER_CHEST || block == Blocks.TRAPPED_CHEST) {
                        return;
                    }
                    BlockHitResult blockHitResult = (BlockHitResult) result;
                    SkyMatrix.mc.world.setBlockState(blockHitResult.getBlockPos(), Blocks.AIR.getDefaultState());
                    blocks.put(blockHitResult.getBlockPos().add(0, 0, 0), SkyMatrix.mc.world.getBlockState(blockHitResult.getBlockPos()));
                }
            }
        }
    }

    @Override
    public void disable() {
        blocks.clear();
    }

    @EventTarget

    public void onWorldChange(WorldChangeEvent e) {
        blocks.clear();
    }

    @EventTarget
    public void onRender(WorldRenderEvent e) {
        if (!render.isValue()) return;
        blocks.forEach((blockPos, blockState) -> {
            RenderUtilsV2.renderOutlineBlock(e.getMatrixStack(), blockPos, new Color(255, 255, 255, 89));
        });
    }
}
