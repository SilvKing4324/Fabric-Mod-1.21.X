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

                    }).build());

    public static final ItemGroup TITANIUM_BLOCKS_GROUP = Registry.register(Registries.ITEM_GROUP, Identifier.of(SilvKingsMod.MOD_ID, "titanium_blocks"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModBlocks.TITANIUM_BLOCK))
                    .displayName(Text.translatable("itemgroup.silvkingsmod.titanium_blocks"))
                    .entries((displayContet, entries) -> {
                        entries.add(ModBlocks.TITANIUM_BLOCK);
                        entries.add(ModBlocks.RAW_TITANIUM_BLOCK);
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
                    }).build());

    public static void registerItemGroups() {
        SilvKingsMod.LOGGER.info("Registering Item groups for " + SilvKingsMod.MOD_ID);
    }
}
