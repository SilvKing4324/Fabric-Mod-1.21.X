package net.silvking432.silvkingsmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
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
        List<ItemConvertible> TITANIUM_SMELTABLES = List.of(ModBlocks.TITANIUM_ORE,ModBlocks.TITANIUM_DEEPSLATE_ORE,ModBlocks.TITANIUM_NETHER_ORE,ModBlocks.TITANIUM_END_ORE);

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
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_trapdoor_from_titanium_ingot"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.NIGHT_BRICKS, 4)
                .pattern("RR")
                .pattern("RR")
                .input('R', ModBlocks.NIGHTSLATE)
                .criterion(hasItem(ModBlocks.NIGHTSLATE), conditionsFromItem(ModBlocks.NIGHTSLATE))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"night_bricks_from_nightslate"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TRAPPED_SAND, 1)
                .pattern("R")
                .pattern("X")
                .input('R', Items.SAND)
                .input('X', Items.TRIPWIRE_HOOK)
                .criterion(hasItem(Items.SAND), conditionsFromItem(Items.SAND))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"trapped_sand_from_sand"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TITANIUM_STAIRS, 4)
                .pattern("R  ")
                .pattern("RR ")
                .pattern("RRR")
                .input('R', ModItems.TITANIUM_INGOT)
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_stairs_from_titanium_ingot"));


        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TITANIUM_FENCE,3)
                .pattern("RXR")
                .pattern("RXR")
                .input('R', ModItems.TITANIUM_INGOT)
                .input('X', Items.STICK)
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_fence_from_titanium_ingot"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TITANIUM_FENCE_GATE)
                .pattern("RXR")
                .pattern("RXR")
                .input('X', ModItems.TITANIUM_INGOT)
                .input('R', Items.STICK)
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_fence_gate_from_titanium_ingot"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TITANIUM_SLAB,6)
                .pattern("XXX")
                .input('X', ModItems.TITANIUM_INGOT)
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_slab_from_titanium_ingot"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TITANIUM_WALL,3)
                .pattern("XXX")
                .pattern("XXX")
                .input('X', ModItems.TITANIUM_INGOT)
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_wall_from_titanium_ingot"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TITANIUM_PRESSURE_PLATE)
                .pattern("XX")
                .input('X', ModItems.TITANIUM_INGOT)
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_pressure_plate_from_titanium_ingot"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TITANIUM_BUTTON, 1)
                .input(ModItems.TITANIUM_INGOT)
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_button_from_titaium_ingot"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TITANIUM_SHARD, 4)
                .input(ModBlocks.MAGIC_BLOCK)
                .criterion(hasItem(ModBlocks.MAGIC_BLOCK), conditionsFromItem(ModBlocks.RAW_TITANIUM_BLOCK))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_shard_from_magic_block"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.DRIFTWOOD_PLANKS, 4)
                .input(ModBlocks.DRIFTWOOD_LOG)
                .criterion(hasItem(ModBlocks.DRIFTWOOD_LOG), conditionsFromItem(ModBlocks.DRIFTWOOD_LOG))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"driftwood_planks_from_driftwood_log"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.DRIFTWOOD_PLANKS, 4)
                .input(ModBlocks.STRIPPED_DRIFTWOOD_LOG)
                .criterion(hasItem(ModBlocks.STRIPPED_DRIFTWOOD_LOG), conditionsFromItem(ModBlocks.STRIPPED_DRIFTWOOD_LOG))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"driftwood_planks_from_stripped_driftwood_log"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.DRIFTWOOD_PLANKS, 4)
                .input(ModBlocks.STRIPPED_DRIFTWOOD_WOOD)
                .criterion(hasItem(ModBlocks.STRIPPED_DRIFTWOOD_WOOD), conditionsFromItem(ModBlocks.STRIPPED_DRIFTWOOD_WOOD))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"driftwood_planks_from_stripped_driftwood_wood"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.DRIFTWOOD_PLANKS, 4)
                .input(ModBlocks.DRIFTWOOD_WOOD)
                .criterion(hasItem(ModBlocks.DRIFTWOOD_WOOD), conditionsFromItem(ModBlocks.DRIFTWOOD_WOOD))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"driftwood_planks_from_driftwood_wood"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.DRIFTWOOD_WOOD,1)
                .pattern("XX")
                .pattern("XX")
                .input('X', ModBlocks.DRIFTWOOD_LOG)
                .criterion(hasItem(ModBlocks.DRIFTWOOD_LOG), conditionsFromItem(ModBlocks.DRIFTWOOD_LOG))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"driftwood_wood_from_driftwood_planks"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TITAN_HEART,1)
                .pattern("NAN")
                .pattern("XSX")
                .pattern("RXR")
                .input('R', ModItems.TITANIUM_INGOT)
                .input('X', Items.NETHER_STAR)
                .input('S', ModItems.STARLIGHT_ASHES)
                .input('N', Items.NETHERITE_INGOT)
                .input('A', Items.AMETHYST_CLUSTER)

                .criterion(hasItem(Items.NETHER_STAR), conditionsFromItem(Items.NETHER_STAR))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titan_heart_from_starlight_ashes"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.DARK_WORLD_KEY,1)
                .pattern("RAR")
                .pattern("SXS")
                .pattern("RNR")
                .input('R', ModItems.TITANIUM_INGOT)
                .input('X', Items.NETHER_STAR)
                .input('S', ModItems.DRAGON_SCALE)
                .input('N', ModItems.DARK_WORLD_ORB)
                .input('A', ModItems.TITAN_HEART)

                .criterion(hasItem(ModItems.DARK_WORLD_ORB), conditionsFromItem(ModItems.DARK_WORLD_ORB))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"dark_world_key_from_dark_world_orb"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.PULSE_EMITTER,1)
                .pattern("RAR")
                .pattern("SXS")
                .pattern("RNR")
                .input('R', Items.CHISELED_STONE_BRICKS)
                .input('X', Items.COMPASS)
                .input('S', Items.REDSTONE)
                .input('N', Items.COMPARATOR)
                .input('A', Items.REPEATER)

                .criterion(hasItem(Items.REDSTONE), conditionsFromItem(Items.REDSTONE))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"pulse_emitter_from_compass"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TITANIUM_UPGRADE_TEMPLATE,1)
                .pattern("TTT")
                .pattern("TXT")
                .pattern("TTT")
                .input('T', ModItems.TITANIUM_INGOT)
                .input('X', Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)

                .criterion(hasItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), conditionsFromItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_upgrade_template_from_netherite_upgrade_smithing_template"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.UNIVERSAL_UPGRADE_TEMPLATE,2)
                .pattern(" T ")
                .pattern("TXT")
                .pattern(" T ")
                .input('T', ModItems.DARK_TITANIUM_INGOT)
                .input('X', ModItems.TITANIUM_UPGRADE_TEMPLATE)

                .criterion(hasItem(ModItems.TITANIUM_UPGRADE_TEMPLATE), conditionsFromItem(ModItems.TITANIUM_UPGRADE_TEMPLATE))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"universal_upgrade_template_from_titanium_upgrade_smithing_template"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TITANIUM_HAMMER,1)
                .pattern(" A ")
                .pattern(" XA")
                .pattern("X  ")
                .input('X', Items.STICK)
                .input('A', ModBlocks.TITANIUM_BLOCK)

                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_hammer_from_titanium_block"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.TITANIUM_TNT,1)
                .pattern("TTT")
                .pattern("XXX")
                .pattern("TTT")
                .input('T', ModItems.TITANIUM_INGOT)
                .input('X', Items.TNT)

                .criterion(hasItem(Items.TNT), conditionsFromItem(Items.TNT))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID,"titanium_tnt_from_tnt"));

        SmithingTransformRecipeJsonBuilder.create(
                        Ingredient.ofItems(ModItems.TITANIUM_UPGRADE_TEMPLATE),
                        Ingredient.ofItems(Items.NETHERITE_HELMET),
                        Ingredient.ofItems(ModItems.TITANIUM_INGOT),
                        RecipeCategory.COMBAT,
                        ModItems.TITANIUM_HELMET
                )
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID, "titanium_helmet_smithing"));

        SmithingTransformRecipeJsonBuilder.create(
                        Ingredient.ofItems(ModItems.TITANIUM_UPGRADE_TEMPLATE),
                        Ingredient.ofItems(Items.NETHERITE_CHESTPLATE),
                        Ingredient.ofItems(ModItems.TITANIUM_INGOT),
                        RecipeCategory.COMBAT,
                        ModItems.TITANIUM_CHESTPLATE
                )
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID, "titanium_chestplate_smithing"));

        SmithingTransformRecipeJsonBuilder.create(
                        Ingredient.ofItems(ModItems.TITANIUM_UPGRADE_TEMPLATE),
                        Ingredient.ofItems(Items.NETHERITE_LEGGINGS),
                        Ingredient.ofItems(ModItems.TITANIUM_INGOT),
                        RecipeCategory.COMBAT,
                        ModItems.TITANIUM_LEGGINGS
                )
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID, "titanium_leggings_smithing"));

        SmithingTransformRecipeJsonBuilder.create(
                        Ingredient.ofItems(ModItems.TITANIUM_UPGRADE_TEMPLATE),
                        Ingredient.ofItems(Items.NETHERITE_BOOTS),
                        Ingredient.ofItems(ModItems.TITANIUM_INGOT),
                        RecipeCategory.COMBAT,
                        ModItems.TITANIUM_BOOTS
                )
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID, "titanium_boots_smithing"));

        SmithingTransformRecipeJsonBuilder.create(
                        Ingredient.ofItems(ModItems.TITANIUM_UPGRADE_TEMPLATE),
                        Ingredient.ofItems(Items.NETHERITE_SWORD),
                        Ingredient.ofItems(ModItems.TITANIUM_INGOT),
                        RecipeCategory.COMBAT,
                        ModItems.TITANIUM_SWORD
                )
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID, "titanium_sword_smithing"));

        SmithingTransformRecipeJsonBuilder.create(
                        Ingredient.ofItems(ModItems.TITANIUM_UPGRADE_TEMPLATE),
                        Ingredient.ofItems(Items.NETHERITE_PICKAXE),
                        Ingredient.ofItems(ModItems.TITANIUM_INGOT),
                        RecipeCategory.COMBAT,
                        ModItems.TITANIUM_PICKAXE
                )
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID, "titanium_pickaxe_smithing"));

        SmithingTransformRecipeJsonBuilder.create(
                        Ingredient.ofItems(ModItems.TITANIUM_UPGRADE_TEMPLATE),
                        Ingredient.ofItems(Items.NETHERITE_AXE),
                        Ingredient.ofItems(ModItems.TITANIUM_INGOT),
                        RecipeCategory.COMBAT,
                        ModItems.TITANIUM_AXE
                )
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID, "titanium_axe_smithing"));

        SmithingTransformRecipeJsonBuilder.create(
                        Ingredient.ofItems(ModItems.TITANIUM_UPGRADE_TEMPLATE),
                        Ingredient.ofItems(Items.NETHERITE_SHOVEL),
                        Ingredient.ofItems(ModItems.TITANIUM_INGOT),
                        RecipeCategory.COMBAT,
                        ModItems.TITANIUM_SHOVEL
                )
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID, "titanium_shovel_smithing"));

        SmithingTransformRecipeJsonBuilder.create(
                        Ingredient.ofItems(ModItems.TITANIUM_UPGRADE_TEMPLATE),
                        Ingredient.ofItems(Items.NETHERITE_HOE),
                        Ingredient.ofItems(ModItems.TITANIUM_INGOT),
                        RecipeCategory.COMBAT,
                        ModItems.TITANIUM_HOE
                )
                .criterion(hasItem(ModItems.TITANIUM_INGOT), conditionsFromItem(ModItems.TITANIUM_INGOT))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID, "titanium_hoe_smithing"));

        record SmithingUpgrade(Item base, Item addition, Item result, String name) {}

        List<SmithingUpgrade> upgrades = List.of(
                new SmithingUpgrade(Items.DIAMOND_HELMET, Items.NETHERITE_INGOT, Items.NETHERITE_HELMET, "netherite_helmet"),
                new SmithingUpgrade(Items.DIAMOND_CHESTPLATE, Items.NETHERITE_INGOT, Items.NETHERITE_CHESTPLATE, "netherite_chestplate"),
                new SmithingUpgrade(Items.DIAMOND_LEGGINGS, Items.NETHERITE_INGOT, Items.NETHERITE_LEGGINGS, "netherite_leggings"),
                new SmithingUpgrade(Items.DIAMOND_BOOTS, Items.NETHERITE_INGOT, Items.NETHERITE_BOOTS, "netherite_boots"),
                new SmithingUpgrade(Items.DIAMOND_SWORD, Items.NETHERITE_INGOT, Items.NETHERITE_SWORD, "netherite_sword"),
                new SmithingUpgrade(Items.DIAMOND_PICKAXE, Items.NETHERITE_INGOT, Items.NETHERITE_PICKAXE, "netherite_pickaxe"),
                new SmithingUpgrade(Items.DIAMOND_AXE, Items.NETHERITE_INGOT, Items.NETHERITE_AXE, "netherite_axe"),
                new SmithingUpgrade(Items.DIAMOND_SHOVEL, Items.NETHERITE_INGOT, Items.NETHERITE_SHOVEL, "netherite_shovel"),
                new SmithingUpgrade(Items.DIAMOND_HOE, Items.NETHERITE_INGOT, Items.NETHERITE_HOE, "netherite_hoe"),

                new SmithingUpgrade(Items.NETHERITE_HELMET, ModItems.TITANIUM_INGOT, ModItems.TITANIUM_HELMET, "titanium_helmet"),
                new SmithingUpgrade(Items.NETHERITE_CHESTPLATE, ModItems.TITANIUM_INGOT, ModItems.TITANIUM_CHESTPLATE, "titanium_chestplate"),
                new SmithingUpgrade(Items.NETHERITE_LEGGINGS, ModItems.TITANIUM_INGOT, ModItems.TITANIUM_LEGGINGS, "titanium_leggings"),
                new SmithingUpgrade(Items.NETHERITE_BOOTS, ModItems.TITANIUM_INGOT, ModItems.TITANIUM_BOOTS, "titanium_boots"),
                new SmithingUpgrade(Items.NETHERITE_SWORD, ModItems.TITANIUM_INGOT, ModItems.TITANIUM_SWORD, "titanium_sword"),
                new SmithingUpgrade(Items.NETHERITE_PICKAXE, ModItems.TITANIUM_INGOT, ModItems.TITANIUM_PICKAXE, "titanium_pickaxe"),
                new SmithingUpgrade(Items.NETHERITE_AXE, ModItems.TITANIUM_INGOT, ModItems.TITANIUM_AXE, "titanium_axe"),
                new SmithingUpgrade(Items.NETHERITE_SHOVEL, ModItems.TITANIUM_INGOT, ModItems.TITANIUM_SHOVEL, "titanium_shovel"),
                new SmithingUpgrade(Items.NETHERITE_HOE, ModItems.TITANIUM_INGOT, ModItems.TITANIUM_HOE, "titanium_hoe"),

                new SmithingUpgrade(ModItems.TITANIUM_HELMET, ModItems.DARK_TITANIUM_INGOT, ModItems.DARK_TITANIUM_HELMET, "dark_titanium_helmet"),
                new SmithingUpgrade(ModItems.TITANIUM_CHESTPLATE, ModItems.DARK_TITANIUM_INGOT, ModItems.DARK_TITANIUM_CHESTPLATE, "dark_titanium_chestplate"),
                new SmithingUpgrade(ModItems.TITANIUM_LEGGINGS, ModItems.DARK_TITANIUM_INGOT, ModItems.DARK_TITANIUM_LEGGINGS, "dark_titanium_leggings"),
                new SmithingUpgrade(ModItems.TITANIUM_BOOTS, ModItems.DARK_TITANIUM_INGOT, ModItems.DARK_TITANIUM_BOOTS, "dark_titanium_boots")
        );

        upgrades.forEach(upgrade -> SmithingTransformRecipeJsonBuilder.create(
                        Ingredient.ofItems(ModItems.UNIVERSAL_UPGRADE_TEMPLATE),
                        Ingredient.ofItems(upgrade.base),
                        Ingredient.ofItems(upgrade.addition),
                        RecipeCategory.COMBAT,
                        upgrade.result
                )
                .criterion(hasItem(upgrade.addition), conditionsFromItem(upgrade.addition))
                .offerTo(recipeExporter, Identifier.of(SilvKingsMod.MOD_ID, upgrade.name + "_smithing_universal")));

        offerSmithingTrimRecipe(recipeExporter, ModItems.SILV_SMITHING_TEMPLATE, Identifier.of(SilvKingsMod.MOD_ID, "silv"));
        // TODO: Add Chisel Recipe
        // TODO: Add Titan horse Armor Drop
        // TODO: Add Titan Bow Recipe
    }
}
