package net.silvking432.silvkingsmod.villager;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.block.ModBlocks;

public class ModVillagers {
    public static final RegistryKey<PointOfInterestType> TITANIUM_TRADER_POI_KEY = registerPoiKey("titanium_trader_poi");
    public static final PointOfInterestType TITANIUM_TRADER_POI = registerPOI("titanium_trader_poi", ModBlocks.CHAIR);

    public static final VillagerProfession TITANIUM_TRADER = registerProfession("titanium_trader", TITANIUM_TRADER_POI_KEY);


    private static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> type) {
        return Registry.register(Registries.VILLAGER_PROFESSION, Identifier.of(SilvKingsMod.MOD_ID, name),
                new VillagerProfession(name, entry -> entry.matchesKey(type), entry -> entry.matchesKey(type),
                        ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN));
    }

    private static PointOfInterestType registerPOI(String name, Block block) {
        return PointOfInterestHelper.register(Identifier.of(SilvKingsMod.MOD_ID, name),
                1, 1, block);
    }

    private static RegistryKey<PointOfInterestType> registerPoiKey(String name) {
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Identifier.of(SilvKingsMod.MOD_ID, name));
    }

    public static void registerVillagers() {
        SilvKingsMod.LOGGER.info("Registering Villagers for " + SilvKingsMod.MOD_ID);
    }
}