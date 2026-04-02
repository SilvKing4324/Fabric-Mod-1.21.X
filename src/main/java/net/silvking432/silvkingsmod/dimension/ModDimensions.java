package net.silvking432.silvkingsmod.dimension;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.silvking432.silvkingsmod.SilvKingsMod;

public class ModDimensions {
    // Der Key für die Dimension selbst (Level/Welt)
    public static final RegistryKey<World> DARK_WORLD_KEY = RegistryKey.of(RegistryKeys.WORLD,
            Identifier.of("silvkingsmod", "dark_world"));

    // Der Key für den Dimension-Type (die physikalischen Regeln aus deiner JSON)
    public static final RegistryKey<DimensionType> DARK_WORLD_TYPE_KEY = RegistryKey.of(RegistryKeys.DIMENSION_TYPE,
            Identifier.of("silvkingsmod", "dark_world_type"));

    public static void registerDimensions() {
        System.out.println("Registering Mod Dimensions for " + SilvKingsMod.MOD_ID);
    }
}