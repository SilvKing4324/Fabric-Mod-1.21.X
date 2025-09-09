package net.silvking432.silvkingsmod.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;

public class ModBlocks {

    public static final Block TITANIUM_BLOCK = registerBlock("titanium_block", new Block(AbstractBlock.Settings.create().strength(4f, 3f).requiresTool().sounds(BlockSoundGroup.METAL)));
    public static final Block RAW_TITANIUM_BLOCK = registerBlock("raw_titanium_block", new Block(AbstractBlock.Settings.create().strength(4f, 3f).requiresTool().sounds(BlockSoundGroup.STONE)));


    public static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(SilvKingsMod.MOD_ID, name), block);
    }

    public static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(SilvKingsMod.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        SilvKingsMod.LOGGER.info("Registering Mod Blocks for " + SilvKingsMod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
           entries.add(ModBlocks.TITANIUM_BLOCK);
           entries.add(ModBlocks.RAW_TITANIUM_BLOCK);

        });
    }
}
