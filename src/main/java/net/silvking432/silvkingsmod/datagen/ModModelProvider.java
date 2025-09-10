package net.silvking432.silvkingsmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.item.ModItems;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        BlockStateModelGenerator.BlockTexturePool titanium_pool = blockStateModelGenerator.registerCubeAllModelTexturePool(ModBlocks.TITANIUM_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_TITANIUM_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.TITANIUM_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.TITANIUM_DEEPSLATE_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MAGIC_BLOCK);
        blockStateModelGenerator.registerDoor(ModBlocks.TITANIUM_DOOR);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.TITANIUM_TRAPDOOR);

        titanium_pool.stairs(ModBlocks.TITANIUM_STAIRS);
        titanium_pool.slab(ModBlocks.TITANIUM_SLAB);
        titanium_pool.button(ModBlocks.TITANIUM_BUTTON);
        titanium_pool.pressurePlate(ModBlocks.TITANIUM_PRESSURE_PLATE);
        titanium_pool.fence(ModBlocks.TITANIUM_FENCE);
        titanium_pool.fenceGate(ModBlocks.TITANIUM_FENCE_GATE);
        titanium_pool.wall(ModBlocks.TITANIUM_WALL);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.TITANIUM_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.SUPER_FLOWER, Models.GENERATED);
        itemModelGenerator.register(ModItems.TITANIUM_SHARD, Models.GENERATED);
        itemModelGenerator.register(ModItems.CHISEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.STARLIGHT_ASHES, Models.GENERATED);
    }
}
