package cn.seiua.skymatrix.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.minecraft.item.ItemStack.LORE_KEY;

public class SkyBlockUtils {
    private final static HashMap<String, String> oreMapper = new HashMap<>();

    static {
        oreMapper.put("light_blue_wool", "rich_mithril");
        oreMapper.put("prismarine", "medium_mithril");
        oreMapper.put("prismarine_bricks", "poor_mithril");
        oreMapper.put("dark_prismarine", "low_mithril");
        oreMapper.put("gray_wool", "lack_mithril");
        oreMapper.put("cyan_terracotta", "shit_mithril");
        oreMapper.put("polished_diorite", "titanium_ore");
    }

    public static String getNameByMapper(String name) {
        if (oreMapper.containsKey(name)) {
            return oreMapper.get(name);
        }
        return name;
    }

    public static Map<String, Boolean> getAllOre() {
        HashMap<String, Boolean> map = new HashMap<>();

        map.put("gold_ore", false);
        map.put("iron_ore", false);
        map.put("coal_ore", false);
        map.put("lapis_ore", false);
        map.put("redstone_ore", false);
        map.put("emerald_ore", false);
        map.put("diamond_ore", false);
        map.put("nether_quartz_ore", false);

        map.put("rich_mithril", false);
        map.put("medium_mithril", false);
        map.put("poor_mithril", false);
        map.put("low_mithril", false);
        map.put("lack_mithril", false);
        map.put("shit_mithril", false);

        map.put("stone", false);
        map.put("obsidian", false);
        map.put("end_stone", false);
        map.put("sand", false);


        return map;
    }


    public static Map<String, Boolean> getAllSeaCreatureMap() {
        HashMap<String, Boolean> map = new HashMap<>();

        map.putAll(getWaterSeaCreatureMap());
        map.putAll(getEventSeaCreatureMap());
        map.putAll(getCrimsonSeaCreatureMap());
        map.putAll(getCrystalWaterSeaCreatureMap());
        map.putAll(getCrystalLavaSeaCreatureMap());


        return map;
    }

    public static Map<String, Boolean> getWaterSeaCreatureMap() {
        HashMap<String, Boolean> map = new HashMap<>();
        // water
        map.put("Squid", true);
        map.put("Agarimoo", true);
        map.put("Sea Walker", true);
        map.put("Night Squid", true);
        map.put("Sea Guardian", true);
        map.put("Sea Witch", true);
        map.put("Sea Archer", true);
        map.put("Rider of the Deep", true);
        map.put("Catfish", true);
        map.put("Carrot King", true);
        map.put("Sea Leech", true);
        map.put("Guardian Defender", true);
        map.put("Deep Sea Protector", true);
        map.put("Water Hydra", true);
        map.put("The Sea Emperor", true);
        return map;
    }

    public static Map<String, Boolean> getEventSeaCreatureMap() {
        HashMap<String, Boolean> map = new HashMap<>();
        //event
        map.put("Scarecrow", true);
        map.put("Nightmare", true);
        map.put("Werewolf", true);
        map.put("Phantom Fisherman", true);
        map.put("Grim Reaper", true);
        map.put("Frozen Steve", true);
        map.put("Frosty The Snowman", true);
        map.put("Grinch", true);
        map.put("Yeti", true);
        map.put("Nutcracker", true);
        map.put("Reindrake", true);
        map.put("Nurse Shark", true);
        map.put("Blue Shark", true);
        map.put("Tiger Shark", true);
        map.put("Great White Shark", true);
        return map;
    }

    public static Map<String, Boolean> getCrimsonSeaCreatureMap() {
        HashMap<String, Boolean> map = new HashMap<>();
        //crimson
        map.put("Plhlegblast", true);
        map.put("Magma Slug", true);
        map.put("Moogma", true);
        map.put("Lava Leech", true);
        map.put("Pyroclastic Worm", true);
        map.put("Lava Flame", true);
        map.put("Fire Eel", true);
        map.put("Taurus", true);
        map.put("Thunder", true);
        map.put("Lord Jawbus", true);
        return map;
    }

    public static Map<String, Boolean> getCrystalWaterSeaCreatureMap() {
        HashMap<String, Boolean> map = new HashMap<>();

        //crystal water
        map.put("Water Worm", true);
        map.put("Poisoned Water Worm", true);
        map.put("Zombie Miner", true);
        return map;
    }

    public static Map<String, Boolean> getCrystalLavaSeaCreatureMap() {
        HashMap<String, Boolean> map = new HashMap<>();
        //crystal lava
        map.put("Flaming Worm", true);
        map.put("Lava Blaze", true);
        map.put("Lava Pigman", true);

        return map;
    }

    public static String getItemUuid(ItemStack itemStack) {
        return get(itemStack, "uuid");
    }

    public static String getItemRarity(ItemStack itemStack) {
        return get(itemStack, "rarity_upgrades");
    }

    public static String getItemId(ItemStack itemStack) {
        return get(itemStack, "id");
    }

    public static String getItemModifier(ItemStack itemStack) {

        return get(itemStack, "modifier");
    }

    private static String get(ItemStack itemStack, String key) {
        if (itemStack.getNbt() == null) return "none";
        if (itemStack.getNbt().getCompound("ExtraAttributes") == null) return "none";
        if (itemStack.getNbt().getCompound("ExtraAttributes").get(key) == null) return "none";
        return Objects.requireNonNull(itemStack.getNbt().getCompound("ExtraAttributes").get(key)).asString();
    }

    public static String getRodType(ItemStack itemStack) {
        if (itemStack.getNbt() == null) return null;
        if (itemStack.getNbt().toString().contains("Lava Rod")) {
            return "LAVA";

        } else {
            return "WATER";
        }
    }


    public static String getItemType(ItemStack itemStack) {
        if (itemStack.getNbt() == null) return null;
        NbtList nbtList = itemStack.getNbt().getCompound("display").getList(LORE_KEY, NbtElement.STRING_TYPE);
        String target = nbtList.getString(nbtList.size() - 1);
        MutableText mutableText2 = Text.Serialization.fromJson(target);
        if (mutableText2 == null) return null;
        target = mutableText2.getString();
        if (target.contains("BOW")) {
            return "BOW";
        }
        if (target.contains("SWORD")) {
            return "SWORD";
        }
        if (target.contains(" AXE")) {
            return "AXE";
        }
        if (target.contains("PICKAXE")) {
            return "PICKAXE";
        }
        if (target.contains("DRILL")) {
            return "DRILL";
        }
        if (target.contains("SHOVEL")) {
            return "SHOVEL";
        }
        if (target.contains("WAND")) {
            return "WAND";
        }
        if (target.contains("SHEARS")) {
            return "SHEARS";
        }

        return null;
    }

    public static Map getAllCrop() {

        HashMap<String, Boolean> map = new HashMap<>();
        map.put("melon", false);
        map.put("cocoa", false);
        map.put("carved_pumpkin", false);
        map.put("sugar_cane", false);
        map.put("cactus", false);
        map.put("nether_wart", false); //age 3
        map.put("carrots", false); //age 7
        map.put("potatoes", false); //age 6
        map.put("wheat", false); //age 7
        map.put("brown_mushroom", false); //age 7
        map.put("red_mushroom", false); //age 7
        return map;
    }

    public static Map getAllWood() {
        HashMap<String, Boolean> map = new HashMap<>();
        map.put("oak_log", false);
        map.put("oak_wood", false);
        map.put("birch_log", false);
        map.put("birch_wood", false);
        map.put("jungle_log", false);
        map.put("jungle_wood", false);
        map.put("dark_oak_log", false);
        map.put("dark_oak_wood", false);
        map.put("acacia_log", false);
        map.put("acacia_wood", false);
        return map;
    }
}
