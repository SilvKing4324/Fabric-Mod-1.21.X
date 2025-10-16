package net.silvking432.silvkingsmod.recipe;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;

public class ModRecipes {
    public static final RecipeSerializer<GrowthChamberRecipe> GROWTH_CHAMBER_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER, Identifier.of(SilvKingsMod.MOD_ID, "growth_chamber"),
            new GrowthChamberRecipe.Serializer());

    public static final RecipeType<GrowthChamberRecipe> GROWTH_CHAMBER_TYPE = Registry.register(
            Registries.RECIPE_TYPE, Identifier.of(SilvKingsMod.MOD_ID, "growth_chamber"), new RecipeType<GrowthChamberRecipe>() {
                @Override
                public String toString() {
                    return "growth_chamber";
                }
            });
    public static void registerRecipes() {
        SilvKingsMod.LOGGER.info("Registering Mod Recipes for " + SilvKingsMod.MOD_ID);
    }
}
