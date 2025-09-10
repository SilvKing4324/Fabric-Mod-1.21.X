package net.silvking432.silvkingsmod.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.block.custom.MagicBlock;

public class ModBlocks {

    public static final Block TITANIUM_BLOCK = registerBlock("titanium_block", new Block(AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL)));
    public static final Block RAW_TITANIUM_BLOCK = registerBlock("raw_titanium_block", new Block(AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.STONE)));
    public static final Block TITANIUM_ORE = registerBlock("titanium_ore", new ExperienceDroppingBlock(UniformIntProvider.create(2, 5),AbstractBlock.Settings.create().strength(4f, 3f).requiresTool().sounds(BlockSoundGroup.STONE)));
    public static final Block TITANIUM_DEEPSLATE_ORE = registerBlock("titanium_deepslate_ore", new ExperienceDroppingBlock(UniformIntProvider.create(2, 5),AbstractBlock.Settings.create().strength(5f, 3f).requiresTool().sounds(BlockSoundGroup.DEEPSLATE)));
    public static final Block MAGIC_BLOCK = registerBlock("magic_block", new MagicBlock(AbstractBlock.Settings.create().strength(10f, 4f).requiresTool().sounds(BlockSoundGroup.AMETHYST_BLOCK)));
    public static final Block TITANIUM_STAIRS = registerBlock("titanium_stairs", new StairsBlock(ModBlocks.TITANIUM_BLOCK.getDefaultState(), AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL)));
    public static final Block TITANIUM_SLAB = registerBlock("titanium_slab", new SlabBlock(AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL)));
    public static final Block TITANIUM_BUTTON = registerBlock("titanium_button", new ButtonBlock(BlockSetType.IRON, 2, AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL).noCollision()));
    public static final Block TITANIUM_PRESSURE_PLATE = registerBlock("titanium_pressure_plate", new PressurePlateBlock(BlockSetType.IRON, AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL)));
    public static final Block TITANIUM_FENCE = registerBlock("titanium_fence", new FenceBlock(AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL)));
    public static final Block TITANIUM_FENCE_GATE = registerBlock("titanium_fence_gate", new FenceGateBlock(WoodType.ACACIA, AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL)));
    public static final Block TITANIUM_WALL = registerBlock("titanium_wall", new WallBlock(AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL)));
    public static final Block TITANIUM_DOOR = registerBlock("titanium_door", new DoorBlock(BlockSetType.IRON, AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL).nonOpaque()));
    public static final Block TITANIUM_TRAPDOOR = registerBlock("titanium_trapdoor", new TrapdoorBlock(BlockSetType.IRON, AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL).nonOpaque()));


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
            entries.add(ModBlocks.TITANIUM_ORE);
            entries.add(ModBlocks.TITANIUM_DEEPSLATE_ORE);
            entries.add(ModBlocks.MAGIC_BLOCK);
        });
    }
}
