package net.silvking432.silvkingsmod.util;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.entity.ModEntities;
import net.silvking432.silvkingsmod.item.ModItems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TraitUtil {
    private static List<Block> BASIC_BLOCKS;
    private static Map<Item, Float> ORE_CHANCES;
    private static List<Block> BONUS_DROPS_BLOCKS;
    private static List<TagKey<Block>> BONUS_DROPS_TAGS;

    public static List<Block> getBasicBlocks() {
        if (BASIC_BLOCKS == null) {
            BASIC_BLOCKS = List.of(
                    Blocks.STONE, Blocks.DIRT, Blocks.NETHERRACK,
                    Blocks.DEEPSLATE, Blocks.ANDESITE, Blocks.DIORITE,
                    Blocks.GRANITE, Blocks.GRAVEL, Blocks.TUFF,
                    ModBlocks.NIGHTSLATE, ModBlocks.DARK_SOIL
            );
        }
        return BASIC_BLOCKS;
    }

    public static Map<Item, Float> getOreChances() {
        if (ORE_CHANCES == null) {
            ORE_CHANCES = new HashMap<>();
            ORE_CHANCES.put(Items.COAL, 0.05f);
            ORE_CHANCES.put(Items.QUARTZ, 0.05f);
            ORE_CHANCES.put(Items.RAW_IRON, 0.03f);
            ORE_CHANCES.put(Items.RAW_COPPER, 0.03f);
            ORE_CHANCES.put(Items.RAW_GOLD, 0.015f);
            ORE_CHANCES.put(Items.REDSTONE, 0.02f);
            ORE_CHANCES.put(Items.LAPIS_LAZULI, 0.01f);
            ORE_CHANCES.put(Items.DIAMOND, 0.004f);
            ORE_CHANCES.put(Items.EMERALD, 0.002f);
            ORE_CHANCES.put(Blocks.ANCIENT_DEBRIS.asItem(), 0.002f);
            ORE_CHANCES.put(ModItems.TITANIUM_SHARD, 0.001f);
        }
        return ORE_CHANCES;
    }

    public static List<Block> getBonusDropsBlocks() {
        if (BONUS_DROPS_BLOCKS == null) {
            BONUS_DROPS_BLOCKS = List.of(
                    Blocks.AMETHYST_CLUSTER,
                    Blocks.NETHER_QUARTZ_ORE,
                    Blocks.NETHER_GOLD_ORE,
                    ModBlocks.TITANIUM_ORE,
                    ModBlocks.TITANIUM_NETHER_ORE,
                    ModBlocks.TITANIUM_END_ORE,
                    ModBlocks.TITANIUM_DEEPSLATE_ORE
            );
        }
        return BONUS_DROPS_BLOCKS;
    }

    public static List<TagKey<Block>> getBonusDropsTags() {
        if (BONUS_DROPS_TAGS == null) {
            BONUS_DROPS_TAGS = List.of(
                    BlockTags.COAL_ORES,
                    BlockTags.IRON_ORES,
                    BlockTags.GOLD_ORES,
                    BlockTags.DIAMOND_ORES,
                    BlockTags.REDSTONE_ORES,
                    BlockTags.LAPIS_ORES,
                    BlockTags.EMERALD_ORES,
                    BlockTags.COPPER_ORES
            );
        }
        return BONUS_DROPS_TAGS;
    }

    public static final Set<EntityType<?>> NECRO_MOBS = Set.of(
            ModEntities.NECRO_BEE,
            ModEntities.NECRO_CHICKEN,
            ModEntities.NECRO_WOLF,
            ModEntities.NECRO_COW,
            ModEntities.NECRO_PIG,
            ModEntities.NECRO_MINI_CHICKEN,
            ModEntities.DARK_SHADOW,
            ModEntities.ABYSSAL_SHADOW,
            ModEntities.NECRO_LLAMA,
            ModEntities.NECRO_GECKO,
            ModEntities.NECRO_SHEEP
    );
}