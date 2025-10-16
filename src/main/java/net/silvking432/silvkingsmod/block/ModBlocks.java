package net.silvking432.silvkingsmod.block;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.block.custom.*;
import net.silvking432.silvkingsmod.sound.ModSounds;
import net.silvking432.silvkingsmod.world.tree.ModSaplingGenerators;

public class ModBlocks {

    public static final Block TITANIUM_BLOCK = registerBlock("titanium_block", new Block(AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL)));
    public static final Block RAW_TITANIUM_BLOCK = registerBlock("raw_titanium_block", new Block(AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.STONE)));
    public static final Block TITANIUM_ORE = registerBlock("titanium_ore", new ExperienceDroppingBlock(UniformIntProvider.create(2, 5),AbstractBlock.Settings.create().strength(4f, 3f).requiresTool().sounds(BlockSoundGroup.STONE)));
    public static final Block TITANIUM_DEEPSLATE_ORE = registerBlock("titanium_deepslate_ore", new ExperienceDroppingBlock(UniformIntProvider.create(2, 5),AbstractBlock.Settings.create().strength(5f, 3f).requiresTool().sounds(BlockSoundGroup.DEEPSLATE)));
    public static final Block MAGIC_BLOCK = registerBlock("magic_block", new MagicBlock(AbstractBlock.Settings.create().strength(10f, 4f).requiresTool().sounds(ModSounds.MAGIC_BLOCK_SOUNDS)));
    public static final Block TITANIUM_STAIRS = registerBlock("titanium_stairs", new StairsBlock(ModBlocks.TITANIUM_BLOCK.getDefaultState(), AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL)));
    public static final Block TITANIUM_SLAB = registerBlock("titanium_slab", new SlabBlock(AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL)));
    public static final Block TITANIUM_BUTTON = registerBlock("titanium_button", new ButtonBlock(BlockSetType.IRON, 2, AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL).noCollision()));
    public static final Block TITANIUM_PRESSURE_PLATE = registerBlock("titanium_pressure_plate", new PressurePlateBlock(BlockSetType.IRON, AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL)));
    public static final Block TITANIUM_FENCE = registerBlock("titanium_fence", new FenceBlock(AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL)));
    public static final Block TITANIUM_FENCE_GATE = registerBlock("titanium_fence_gate", new FenceGateBlock(WoodType.ACACIA, AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL)));
    public static final Block TITANIUM_WALL = registerBlock("titanium_wall", new WallBlock(AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL)));
    public static final Block TITANIUM_DOOR = registerBlock("titanium_door", new DoorBlock(BlockSetType.IRON, AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL).nonOpaque()));
    public static final Block TITANIUM_TRAPDOOR = registerBlock("titanium_trapdoor", new TrapdoorBlock(BlockSetType.IRON, AbstractBlock.Settings.create().strength(8f, 4f).requiresTool().sounds(BlockSoundGroup.METAL).nonOpaque()));
    public static final Block TITANIUM_LAMP = registerBlock("titanium_lamp", new TitaniumLampBlock(AbstractBlock.Settings.create().strength(10f, 4f).requiresTool().luminance(state -> state.get(TitaniumLampBlock.CLICKED) ? 15 : 0)));
    public static final Block SUPER_FLOWER_CROP = registerBlockWithoutBlockItem("super_flower_crop", new SuperFlowerCropBlock(AbstractBlock.Settings.create().noCollision().ticksRandomly().breakInstantly().sounds(BlockSoundGroup.CROP).pistonBehavior(PistonBehavior.DESTROY).mapColor(MapColor.DARK_GREEN)));
    public static final Block HONEY_BERRY_BUSH = registerBlockWithoutBlockItem("honey_berry_bush", new HoneyBerryBushBlock(AbstractBlock.Settings.copy(Blocks.SWEET_BERRY_BUSH)));
    public static final Block TITANIUM_NETHER_ORE = registerBlock("titanium_nether_ore", new ExperienceDroppingBlock(UniformIntProvider.create(2, 5),AbstractBlock.Settings.create().strength(4f, 3f).requiresTool().sounds(BlockSoundGroup.NETHER_ORE)));
    public static final Block TITANIUM_END_ORE = registerBlock("titanium_end_ore", new ExperienceDroppingBlock(UniformIntProvider.create(2, 5),AbstractBlock.Settings.create().strength(4f, 3f).requiresTool().sounds(BlockSoundGroup.STONE)));
    public static final Block DRIFTWOOD_LOG = registerBlock("driftwood_log", new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_LOG)));
    public static final Block DRIFTWOOD_WOOD = registerBlock("driftwood_wood", new PillarBlock(AbstractBlock.Settings.copy(Blocks.OAK_WOOD)));
    public static final Block STRIPPED_DRIFTWOOD_LOG = registerBlock("stripped_driftwood_log", new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_LOG)));
    public static final Block STRIPPED_DRIFTWOOD_WOOD = registerBlock("stripped_driftwood_wood", new PillarBlock(AbstractBlock.Settings.copy(Blocks.STRIPPED_OAK_WOOD)));
    public static final Block DRIFTWOOD_PLANKS = registerBlock("driftwood_planks", new Block(AbstractBlock.Settings.copy(Blocks.OAK_PLANKS)));
    public static final Block DRIFTWOOD_LEAVES = registerBlock("driftwood_leaves", new LeavesBlock(AbstractBlock.Settings.copy(Blocks.OAK_LEAVES)));
    public static final Block DRIFTWOOD_SAPLING = registerBlock("driftwood_sapling", new ModSaplingBlock(ModSaplingGenerators.DRIFTWOOD, AbstractBlock.Settings.copy(Blocks.OAK_SAPLING), Blocks.STONE));
    public static final Block CHAIR = registerBlock("chair", new ChairBlock(AbstractBlock.Settings.create().nonOpaque()));
    public static final Block PEDESTAL = registerBlock("pedestal", new PedestalBlock(AbstractBlock.Settings.create().nonOpaque()));
    public static final Block GROWTH_CHAMBER = registerBlock("growth_chamber", new GrowthChamberBlock(AbstractBlock.Settings.create()));
    public static final Block TITANIUM_TNT = registerBlock("titanium_tnt", new TitaniumTntBlock(AbstractBlock.Settings.create().mapColor(MapColor.BLUE).breakInstantly().sounds(BlockSoundGroup.GRASS).burnable().solidBlock(Blocks::never)));
    public static final Block MATRIX_BLOCK = registerBlock("matrix_block", new MatrixBlock(AbstractBlock.Settings.create().strength(100f, 100f).requiresTool().sounds(BlockSoundGroup.METAL)));



    public static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(SilvKingsMod.MOD_ID, name), block);
    }

    public static Block registerBlockWithoutBlockItem(String name, Block block) {
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
