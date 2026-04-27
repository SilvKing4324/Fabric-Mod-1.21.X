package net.silvking432.silvkingsmod.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.block.ModBlocks;


public class ModItemGroups {

    public static final ItemGroup TITANIUM_ITEMS_GROUP = Registry.register(Registries.ITEM_GROUP, Identifier.of(SilvKingsMod.MOD_ID, "titanium_items"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModItems.TITANIUM_INGOT))
                    .displayName(Text.translatable("itemgroup.silvkingsmod.titanium_items"))
                    .entries((displayContet, entries) -> {
                        entries.add(ModItems.TITANMOD_GUIDE);
                        entries.add(ModItems.TITANIUM_SHARD);
                        entries.add(ModItems.TITANIUM_INGOT);
                        entries.add(ModItems.DARK_TITANIUM_INGOT);
                        entries.add(ModItems.CHISEL);
                        entries.add(ModItems.SUPER_FLOWER);
                        entries.add(ModItems.STARLIGHT_ASHES);
                        entries.add(ModItems.TITANIUM_SWORD);
                        entries.add(ModItems.TITANIUM_AXE);
                        entries.add(ModItems.TITANIUM_PICKAXE);
                        entries.add(ModItems.TITANIUM_SHOVEL);
                        entries.add(ModItems.TITANIUM_HOE);
                        entries.add(ModItems.TITANIUM_HAMMER);
                        entries.add(ModItems.TITANIUM_HELMET);
                        entries.add(ModItems.TITANIUM_CHESTPLATE);
                        entries.add(ModItems.TITANIUM_LEGGINGS);
                        entries.add(ModItems.TITANIUM_BOOTS);
                        entries.add(ModItems.DARK_TITANIUM_HELMET);
                        entries.add(ModItems.DARK_TITANIUM_CHESTPLATE);
                        entries.add(ModItems.DARK_TITANIUM_LEGGINGS);
                        entries.add(ModItems.DARK_TITANIUM_BOOTS);
                        entries.add(ModItems.TITANIUM_HORSE_ARMOR);
                        entries.add(ModItems.SILV_SMITHING_TEMPLATE);
                        entries.add(ModItems.TITANIUM_BOW);
                        entries.add(ModItems.NECRON_DOOM_MUSIC_DISC);
                        entries.add(ModItems.SUPER_FLOWER_SEEDS);
                        entries.add(ModItems.HONEY_BERRIES);
                        entries.add(ModItems.TOMAHAWK);
                        entries.add(ModItems.HYPERION);
                        entries.add(ModItems.SPECTRE_STAFF);
                        entries.add(ModItems.TITAN_HEART);
                        entries.add(ModItems.DRAGON_SCALE);
                        entries.add(ModItems.DARK_WORLD_ORB);
                        entries.add(ModItems.DARK_WORLD_KEY);
                        entries.add(ModItems.TITANIUM_UPGRADE_TEMPLATE);
                        entries.add(ModItems.UNIVERSAL_UPGRADE_TEMPLATE);
                        entries.add(ModItems.MANTIS_SPAWN_EGG);
                        entries.add(ModItems.TITAN_PLAYER_SPAWN_EGG);
                        entries.add(ModItems.ETERNAL_SHULKER_SPAWN_EGG);
                        entries.add(ModItems.DARK_SHARD_TIER1);
                        entries.add(ModItems.DARK_SHARD_TIER2);
                        entries.add(ModItems.DARK_SHARD_TIER3);
                        entries.add(ModItems.DARK_SHARD_TIER4);
                        entries.add(ModItems.DARK_SHARD_TIER5);
                        entries.add(ModItems.DARK_CORE_BASE);
                        entries.add(ModItems.DARK_CORE_TIER1);
                        entries.add(ModItems.DARK_CORE_TIER2);
                        entries.add(ModItems.DARK_CORE_TIER3);
                        entries.add(ModItems.DARK_CORE_TIER4);
                        entries.add(ModItems.DARK_CORE_TIER5);
                        entries.add(ModItems.REFINED_ARMOR_CORE);
                        entries.add(ModItems.REFINED_TOOL_CORE);
                        entries.add(ModItems.REFINED_WEAPON_CORE);
                        entries.add(ModItems.DARK_TITANIUM_SWORD);
                        entries.add(ModItems.DARK_TITANIUM_PICKAXE);
                        entries.add(ModItems.DARK_TITANIUM_AXE);
                        entries.add(ModItems.DARK_TITANIUM_SHOVEL);
                        entries.add(ModItems.DARK_TITANIUM_HOE);

                    }).build());

    public static final ItemGroup TITANIUM_BLOCKS_GROUP = Registry.register(Registries.ITEM_GROUP, Identifier.of(SilvKingsMod.MOD_ID, "titanium_blocks"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModBlocks.TITANIUM_BLOCK))
                    .displayName(Text.translatable("itemgroup.silvkingsmod.titanium_blocks"))
                    .entries((displayContet, entries) -> {
                        entries.add(ModBlocks.TITANIUM_BLOCK);
                        entries.add(ModBlocks.RAW_TITANIUM_BLOCK);
                        entries.add(ModBlocks.MAGIC_BLOCK);
                        entries.add(ModBlocks.TITANIUM_ORE);
                        entries.add(ModBlocks.TITANIUM_DEEPSLATE_ORE);
                        entries.add(ModBlocks.TITANIUM_NETHER_ORE);
                        entries.add(ModBlocks.TITANIUM_END_ORE);
                        entries.add(ModBlocks.HARDSTONE);
                        entries.add(ModBlocks.NIGHTSLATE);
                        entries.add(ModBlocks.NIGHT_BRICKS);
                        entries.add(ModBlocks.NIGHT_BRICK_STAIRS);
                        entries.add(ModBlocks.NIGHT_BRICK_SLAB);
                        entries.add(ModBlocks.TITANIUM_STAIRS);
                        entries.add(ModBlocks.TITANIUM_SLAB);
                        entries.add(ModBlocks.TITANIUM_BUTTON);
                        entries.add(ModBlocks.TITANIUM_PRESSURE_PLATE);
                        entries.add(ModBlocks.TITANIUM_FENCE);
                        entries.add(ModBlocks.TITANIUM_FENCE_GATE);
                        entries.add(ModBlocks.TITANIUM_WALL);
                        entries.add(ModBlocks.TITANIUM_DOOR);
                        entries.add(ModBlocks.TITANIUM_TRAPDOOR);
                        entries.add(ModBlocks.TITANIUM_LAMP);
                        entries.add(ModBlocks.DRIFTWOOD_LOG);
                        entries.add(ModBlocks.DRIFTWOOD_WOOD);
                        entries.add(ModBlocks.STRIPPED_DRIFTWOOD_LOG);
                        entries.add(ModBlocks.STRIPPED_DRIFTWOOD_WOOD);
                        entries.add(ModBlocks.DRIFTWOOD_LEAVES);
                        entries.add(ModBlocks.DRIFTWOOD_PLANKS);
                        entries.add(ModBlocks.DRIFTWOOD_STAIRS);
                        entries.add(ModBlocks.DRIFTWOOD_SLAB);
                        entries.add(ModBlocks.DRIFTWOOD_SAPLING);
                        entries.add(ModBlocks.CHAIR);
                        entries.add(ModBlocks.PEDESTAL);
                        entries.add(ModBlocks.GROWTH_CHAMBER);
                        entries.add(ModBlocks.TITANIUM_TNT);
                        entries.add(ModBlocks.MATRIX_BLOCK);
                        entries.add(ModBlocks.TITANIUM_BEACON);
                        entries.add(ModBlocks.TRAPPED_SAND);
                        entries.add(ModBlocks.DARK_WORLD_PORTAL);
                        entries.add(ModBlocks.DARK_SOIL);
                        entries.add(ModBlocks.PULSE_EMITTER);
                        entries.add(ModBlocks.DARK_TITANIUM_BLOCK);
                        entries.add(ModBlocks.ETERNAL_BRICKS);
                        entries.add(ModBlocks.GOLDEN_GLOWING_GLASS);
                        entries.add(ModBlocks.GOLDEN_GLOWING_GLASS_PANE);
                        entries.add(ModBlocks.RED_PURPUR_BLOCK);
                        entries.add(ModBlocks.RED_PURPUR_STAIRS);
                        entries.add(ModBlocks.RED_PURPUR_SLAB);
                        entries.add(ModBlocks.RED_PURPUR_PILLAR);
                        entries.add(ModItems.ETERNAL_SHULKER_BOX);
                        entries.add(ModBlocks.CORE_REFINERY);
                        entries.add(ModBlocks.DARK_ANVIL);
                    }).build());

    public static void registerItemGroups() {
        SilvKingsMod.LOGGER.info("Registering Item groups for " + SilvKingsMod.MOD_ID);
    }
}
