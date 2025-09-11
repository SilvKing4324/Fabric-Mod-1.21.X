package net.silvking432.silvkingsmod.world.tree;

import net.minecraft.block.SaplingGenerator;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.world.ModConfiguredFeatures;

import java.util.Optional;

public class ModSaplingGenerators {
    public static final SaplingGenerator DRIFTWOOD = new SaplingGenerator(SilvKingsMod.MOD_ID + ":driftwood",
            Optional.empty(), Optional.of(ModConfiguredFeatures.DRIFTWOOD_KEY),Optional.empty());
}
