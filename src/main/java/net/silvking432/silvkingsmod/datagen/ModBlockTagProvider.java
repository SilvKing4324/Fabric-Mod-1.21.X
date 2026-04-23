package net.silvking432.silvkingsmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.util.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.TITANIUM_BLOCK)
                .add(ModBlocks.TITANIUM_SLAB)
                .add(ModBlocks.TITANIUM_WALL)
                .add(ModBlocks.TITANIUM_FENCE_GATE)
                .add(ModBlocks.TITANIUM_PRESSURE_PLATE)
                .add(ModBlocks.TITANIUM_FENCE)
                .add(ModBlocks.TITANIUM_STAIRS)
                .add(ModBlocks.RAW_TITANIUM_BLOCK)
                .add(ModBlocks.TITANIUM_DEEPSLATE_ORE)
                .add(ModBlocks.TITANIUM_ORE)
                .add(ModBlocks.TITANIUM_NETHER_ORE)
                .add(ModBlocks.TITANIUM_END_ORE)
                .add(ModBlocks.MAGIC_BLOCK)
                .add(ModBlocks.MATRIX_BLOCK)
                .add(ModBlocks.NIGHTSLATE)
                .add(ModBlocks.PULSE_EMITTER)
                .add(ModBlocks.HARDSTONE)
                .add(ModBlocks.TITANIUM_DOOR)
                .add(ModBlocks.TITANIUM_BUTTON)
                .add(ModBlocks.TITANIUM_LAMP)
                .add(ModBlocks.GROWTH_CHAMBER)
                .add(ModBlocks.NIGHT_BRICKS)
                .add(ModBlocks.RED_PURPUR_BLOCK)
                .add(ModBlocks.RED_PURPUR_PILLAR)
                .add(ModBlocks.RED_PURPUR_SLAB)
                .add(ModBlocks.RED_PURPUR_STAIRS)
                .add(ModBlocks.ETERNAL_BRICKS)
                .add(ModBlocks.TITANIUM_TRAPDOOR);

        getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE)
                .add(ModBlocks.TRAPPED_SAND);

        getOrCreateTagBuilder(BlockTags.HOE_MINEABLE)
                .add(ModBlocks.DRIFTWOOD_LEAVES);

        getOrCreateTagBuilder(BlockTags.AXE_MINEABLE)
                .add(ModBlocks.DRIFTWOOD_PLANKS)
                .add(ModBlocks.CHAIR);

        getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.TITANIUM_BLOCK)
                .add(ModBlocks.RAW_TITANIUM_BLOCK)
                .add(ModBlocks.TITANIUM_DEEPSLATE_ORE)
                .add(ModBlocks.TITANIUM_ORE);

        getOrCreateTagBuilder(BlockTags.INCORRECT_FOR_NETHERITE_TOOL)
                .add(ModBlocks.HARDSTONE);

        getOrCreateTagBuilder(BlockTags.FENCES).add(ModBlocks.TITANIUM_FENCE);
        getOrCreateTagBuilder(BlockTags.WALLS).add(ModBlocks.TITANIUM_WALL);

        getOrCreateTagBuilder(ModTags.Blocks.NEEDS_TITANIUM_TOOL)
                .addTag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.HARDSTONE);

        getOrCreateTagBuilder(BlockTags.LOGS_THAT_BURN)
                .add(ModBlocks.DRIFTWOOD_LOG)
                .add(ModBlocks.DRIFTWOOD_WOOD)
                .add(ModBlocks.STRIPPED_DRIFTWOOD_LOG)
                .add(ModBlocks.STRIPPED_DRIFTWOOD_WOOD);

        getOrCreateTagBuilder(BlockTags.PLANKS)
                .add(ModBlocks.DRIFTWOOD_PLANKS);

        getOrCreateTagBuilder(BlockTags.LEAVES)
                .add(ModBlocks.DRIFTWOOD_LEAVES);

        getOrCreateTagBuilder(BlockTags.SAPLINGS)
                .add(ModBlocks.DRIFTWOOD_SAPLING);
    }
}
