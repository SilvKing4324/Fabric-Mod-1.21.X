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
                        entries.add(ModItems.TITANIUM_SHARD);
                        entries.add(ModItems.TITANIUM_INGOT);
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
                        entries.add(ModItems.TITANIUM_HORSE_ARMOR);
                        entries.add(ModItems.SILV_SMITHING_TEMPLATE);
                        entries.add(ModItems.TITANIUM_BOW);
                        entries.add(ModItems.NECRON_DOOM_MUSIC_DISC);
                        entries.add(ModItems.SUPER_FLOWER_SEEDS);
                        entries.add(ModItems.HONEY_BERRIES);
                        entries.add(ModItems.MANTIS_SPAWN_EGG);
                        entries.add(ModItems.TOMAHAWK);
                        entries.add(ModItems.HYPERION);
                        entries.add(ModItems.SPECTRE_STAFF);
                        entries.add(ModItems.TITAN_PLAYER_SPAWN_EGG);

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
                        entries.add(ModBlocks.DRIFTWOOD_SAPLING);
                        entries.add(ModBlocks.CHAIR);
                        entries.add(ModBlocks.PEDESTAL);
                        entries.add(ModBlocks.GROWTH_CHAMBER);
                        entries.add(ModBlocks.TITANIUM_TNT);
                        entries.add(ModBlocks.MATRIX_BLOCK);



                    }).build());

    public static void registerItemGroups() {
        SilvKingsMod.LOGGER.info("Registering Item groups for " + SilvKingsMod.MOD_ID);
    }
}
