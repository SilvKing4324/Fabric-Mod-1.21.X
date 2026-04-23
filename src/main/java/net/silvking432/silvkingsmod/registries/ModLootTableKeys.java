package net.silvking432.silvkingsmod.registries;

import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;

public class ModLootTableKeys {

    public static final RegistryKey<LootTable> ETERNAL_CITY_LOOT = register("chests/eternal_city_treasure");

    private static RegistryKey<LootTable> register(String path) {
        return RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of(SilvKingsMod.MOD_ID, path));
    }

    public static void registerLootTableKeys() {
        SilvKingsMod.LOGGER.info("Registering Loot Table Keys for " + SilvKingsMod.MOD_ID);
    }
}