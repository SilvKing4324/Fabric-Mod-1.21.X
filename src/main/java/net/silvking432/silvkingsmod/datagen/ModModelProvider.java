package net.silvking432.silvkingsmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.block.custom.HoneyBerryBushBlock;
import net.silvking432.silvkingsmod.block.custom.SuperFlowerCropBlock;
import net.silvking432.silvkingsmod.block.custom.TitaniumLampBlock;
import net.silvking432.silvkingsmod.item.ModItems;

import java.util.Optional;

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
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.TITANIUM_NETHER_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.TITANIUM_END_ORE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MAGIC_BLOCK);
        blockStateModelGenerator.registerDoor(ModBlocks.TITANIUM_DOOR);
        blockStateModelGenerator.registerTrapdoor(ModBlocks.TITANIUM_TRAPDOOR);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.TITANIUM_TNT);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.MATRIX_BLOCK);


        titanium_pool.stairs(ModBlocks.TITANIUM_STAIRS);
        titanium_pool.slab(ModBlocks.TITANIUM_SLAB);
        titanium_pool.button(ModBlocks.TITANIUM_BUTTON);
        titanium_pool.pressurePlate(ModBlocks.TITANIUM_PRESSURE_PLATE);
        titanium_pool.fence(ModBlocks.TITANIUM_FENCE);
        titanium_pool.fenceGate(ModBlocks.TITANIUM_FENCE_GATE);
        titanium_pool.wall(ModBlocks.TITANIUM_WALL);

        Identifier lampOffIdentifier = TexturedModel.CUBE_ALL.upload(ModBlocks.TITANIUM_LAMP, blockStateModelGenerator.modelCollector);
        Identifier lampOnIdentifier = blockStateModelGenerator.createSubModel(ModBlocks.TITANIUM_LAMP, "_on", Models.CUBE_ALL, TextureMap::all);
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(ModBlocks.TITANIUM_LAMP)
                .coordinate(BlockStateModelGenerator.createBooleanModelMap(TitaniumLampBlock.CLICKED, lampOnIdentifier, lampOffIdentifier)));

        blockStateModelGenerator.registerCrop(ModBlocks.SUPER_FLOWER_CROP, SuperFlowerCropBlock.AGE,0,1,2,3,4,5,6);
        blockStateModelGenerator.registerTintableCrossBlockStateWithStages(ModBlocks.HONEY_BERRY_BUSH, BlockStateModelGenerator.TintType.NOT_TINTED, HoneyBerryBushBlock.AGE,0,1,2,3);

        blockStateModelGenerator.registerLog(ModBlocks.DRIFTWOOD_LOG).log(ModBlocks.DRIFTWOOD_LOG).wood(ModBlocks.DRIFTWOOD_WOOD);
        blockStateModelGenerator.registerLog(ModBlocks.STRIPPED_DRIFTWOOD_LOG).log(ModBlocks.STRIPPED_DRIFTWOOD_LOG).wood(ModBlocks.STRIPPED_DRIFTWOOD_WOOD);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DRIFTWOOD_PLANKS);
        blockStateModelGenerator.registerSingleton(ModBlocks.DRIFTWOOD_LEAVES, TexturedModel.LEAVES);
        blockStateModelGenerator.registerTintableCrossBlockState(ModBlocks.DRIFTWOOD_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotation(ModBlocks.CHAIR);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.GROWTH_CHAMBER);

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.TITANIUM_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.SUPER_FLOWER, Models.GENERATED);
        itemModelGenerator.register(ModItems.TITANIUM_SHARD, Models.GENERATED);
        // itemModelGenerator.register(ModItems.CHISEL, Models.GENERATED);
        itemModelGenerator.register(ModItems.STARLIGHT_ASHES, Models.GENERATED);
        itemModelGenerator.register(ModItems.TITANIUM_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.TITANIUM_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.TITANIUM_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.TITANIUM_HOE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.TITANIUM_SHOVEL, Models.HANDHELD);
        itemModelGenerator.register(ModItems.TITANIUM_HAMMER, Models.HANDHELD);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.TITANIUM_HELMET);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.TITANIUM_CHESTPLATE);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.TITANIUM_LEGGINGS);
        itemModelGenerator.registerArmor((ArmorItem) ModItems.TITANIUM_BOOTS);
        itemModelGenerator.register(ModItems.TITANIUM_HORSE_ARMOR, Models.GENERATED);
        itemModelGenerator.register(ModItems.SILV_SMITHING_TEMPLATE, Models.GENERATED);
        itemModelGenerator.register(ModItems.NECRON_DOOM_MUSIC_DISC, Models.GENERATED);
        itemModelGenerator.register(ModItems.MANTIS_SPAWN_EGG, new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));
        itemModelGenerator.register(ModItems.TITAN_PLAYER_SPAWN_EGG, new Model(Optional.of(Identifier.of("item/template_spawn_egg")), Optional.empty()));

    }
}
