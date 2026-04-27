package net.silvking432.silvkingsmod.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.world.ModPlacedFeatures;

public class ModTreeGeneration {
    public static void generateTrees() {
        BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.STONY_PEAKS),
                GenerationStep.Feature.VEGETAL_DECORATION, ModPlacedFeatures.DRIFTWOOD_PLACED_KEY);

        BiomeModifications.addFeature(
                BiomeSelectors.includeByKey(RegistryKey.of(RegistryKeys.BIOME, Identifier.of(SilvKingsMod.MOD_ID, "midnight_forest"))),
                GenerationStep.Feature.VEGETAL_DECORATION, ModPlacedFeatures.FALLEN_DRIFTWOOD_PLACED_KEY);
    }
}
