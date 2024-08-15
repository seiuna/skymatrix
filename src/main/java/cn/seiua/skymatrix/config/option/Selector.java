package cn.seiua.skymatrix.config.option;

import cn.seiua.skymatrix.SkyMatrix;
import cn.seiua.skymatrix.utils.SkyBlockUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface Selector {

    static String bestRodSelector() {
        String v = null;
        BlockPos blockPos = new BlockPos(SkyMatrix.mc.player.getBlockPos());

        Vec3d vec3d = SkyMatrix.mc.player.getRotationVector();
        t:
        for (int i = 0; i < 14; i += 1) {
            blockPos = blockPos.add((int) Math.round(vec3d.x), (int) Math.round(vec3d.y), (int) Math.round(vec3d.z));
            BlockPos blockPosc = new BlockPos(blockPos);
            for (int j = 0; j < 18; j += 1) {

                BlockPos blockPos1 = blockPosc.add(0, 9 - j, 0);
                if (SkyMatrix.mc.world.getBlockState(blockPos1).getBlock().getDefaultState().toString().contains("minecraft:water")) {

                    v = "WATER";
                    break t;
                }

                if (SkyMatrix.mc.world.getBlockState(blockPos1).getBlock().getDefaultState().toString().contains("minecraft:lava")) {
                    v = "LAVA";
                    break t;
                }
            }
        }
        if (v == null) {
            return null;
        }

        String best = null;
        int i = 0;
        for (ItemStack itemStack : SkyMatrix.mc.player.getInventory().main) {
            if (i == 9) {
                break;
            }
            String uuid = SkyBlockUtils.getItemUuid(itemStack);
            String id = SkyBlockUtils.getItemId(itemStack);
            if (id.contains("ROD")) {
                String type = SkyBlockUtils.getRodType(itemStack);

                if (v.equals(type)) {
                    return uuid;
                }
            }
            i++;
        }
        return null;
    }

    String select();
    static String royal_Pigeon() {
        int i = 0;
        for (ItemStack itemStack : SkyMatrix.mc.player.getInventory().main) {
            if (i == 9) {
                break;
            }
            if (itemStack.getName().getString().contains("Royal Pigeon")) {
//            if (itemStack.getName().getString().contains("Jungle")) {
                if (Filter.royal_Pigeon(itemStack)) {
                    String uuid = SkyBlockUtils.getItemUuid(itemStack);
                    return uuid;
                }
            }
            i++;
        }
        return null;

    }
    static String bestAote() {
        int i = 0;
        for (ItemStack itemStack : SkyMatrix.mc.player.getInventory().main) {
            if (i == 9) {
                break;
            }
            if (Filter.aote(itemStack)) {
                String uuid = SkyBlockUtils.getItemUuid(itemStack);
                return uuid;
            }
            i++;
        }
        return null;
    }

    static String bestMiningTool() {
        int i = 0;
        for (ItemStack itemStack : SkyMatrix.mc.player.getInventory().main) {
            if (i == 9) {
                break;
            }
            if (Filter.miningTool(itemStack)) {
                String uuid = SkyBlockUtils.getItemUuid(itemStack);
                return uuid;
            }
            i++;
        }
        return null;
    }
}
