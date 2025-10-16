package net.silvking432.silvkingsmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.item.ModItems;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter recipeExporter) {
        List<ItemConvertible> TITANIUM_SMELTABLES = List.of(ModBlocks.TITANIUM_ORE,ModBlocks.TITANIUM_DEEPSLATE_ORE);

        offerSmelting(recipeExporter, TITANIUM_SMELTABLES, RecipeCategory.MISC, ModItems.TITANIUM_INGOT, 0.25f, 200,"titanium_ingot");
        offerBlasting(recipeExporter, TITANIUM_SMELTABLES, RecipeCategory.MISC, ModItems.TITANIUM_INGOT, 0.25f, 100,"titanium_ingot");

        offerReversibleCompactingRecipes(recipeExporter, RecipeCategory.BUILDING_BLOCKS, ModItems.TITANIUM_INGOT, RecipeCategory.MISC, ModBlocks.TITANIUM_BLOCK);
        offerReversibleCompactingRecipes(recipeExporter, RecipeCategory.BUILDING_BLOCKS, ModItems.TITANIUM_SHARD, RecipeCategory.MISC, ModBlocks.RAW_TITANIUM_BLOCK);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TITANIUM_DOOR, 2)
                .pattern("RR")
                .pattern("RR")
                .pattern("RR")
                .input('R', ModItems.TITANIUM_INGOT)
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT)) // Unlocks Recipe
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_door_from_titanium_ingot"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TITANIUM_TRAPDOOR, 1)
                .pattern("RR")
                .pattern("RR")
                .input('R', ModItems.TITANIUM_INGOT)
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT)) // Unlocks Recipe
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_trapdoor_from_titanium_ingot"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TITANIUM_STAIRS, 4)
                .pattern("R  ")
                .pattern("RR ")
                .pattern("RRR")
                .input('R', ModItems.TITANIUM_INGOT)
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT)) // Unlocks Recipe
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_stairs_from_titanium_ingot"));


        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TITANIUM_FENCE,3)
                .pattern("RXR")
                .pattern("RXR")
                .input('R', ModItems.TITANIUM_INGOT)
                .input('X', Items.STICK)
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT)) // Unlocks Recipe
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_fence_from_titanium_ingot"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TITANIUM_FENCE_GATE)
                .pattern("RXR")
                .pattern("RXR")
                .input('X', ModItems.TITANIUM_INGOT)
                .input('R', Items.STICK)
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT)) // Unlocks Recipe
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_fence_gate_from_titanium_ingot"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TITANIUM_SLAB,6)
                .pattern("XXX")
                .input('X', ModItems.TITANIUM_INGOT)
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT)) // Unlocks Recipe
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_slab_from_titanium_ingot"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TITANIUM_WALL,3)
                .pattern("XXX")
                .pattern("XXX")
                .input('X', ModItems.TITANIUM_INGOT)
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT)) // Unlocks Recipe
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_wall_from_titanium_ingot"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TITANIUM_PRESSURE_PLATE)
                .pattern("XX")
                .input('X', ModItems.TITANIUM_INGOT)
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT)) // Unlocks Recipe
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_pressure_plate_from_titanium_ingot"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TITANIUM_BUTTON, 1)
                .input(ModItems.TITANIUM_INGOT)
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_button_from_titaium_ingot"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TITANIUM_SHARD, 32)
                .input(ModBlocks.MAGIC_BLOCK)
                .criterion(hasItem(ModBlocks.MAGIC_BLOCK), conditionsFromItem(ModBlocks.RAW_TITANIUM_BLOCK))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_shard_from_magic_block"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.DRIFTWOOD_PLANKS, 4)
                .input(ModBlocks.DRIFTWOOD_LOG)
                .criterion(hasItem(ModBlocks.DRIFTWOOD_LOG), conditionsFromItem(ModBlocks.DRIFTWOOD_LOG))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"driftwood_planks_from_driftwood_log"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.DRIFTWOOD_WOOD,1)
                .pattern("XX")
                .pattern("XX")
                .input('X', ModBlocks.DRIFTWOOD_LOG)
                .criterion(hasItem(ModBlocks.DRIFTWOOD_LOG), conditionsFromItem(ModBlocks.DRIFTWOOD_LOG)) // Unlocks Recipe
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"driftwood_wood_from_driftwood_planks"));




        offerSmithingTrimRecipe(recipeExporter, ModItems.SILV_SMITHING_TEMPLATE, Identifier.of(SilvKingsMod.MOD_ID, "silv"));
    }
}
