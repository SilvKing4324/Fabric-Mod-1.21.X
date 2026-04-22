package net.silvking432.silvkingsmod.util;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.item.ModItems;

public class ModLootTableModifiers {
    private static final Identifier GRASS_BLOCK_ID
            = Identifier.of("minecraft", "blocks/short_grass");
    private static final Identifier CREEPER_ID
            = Identifier.of("minecraft", "entities/creeper");
    private static final Identifier ENDERDRAGON_ID
            = Identifier.of("minecraft", "entities/ender_dragon");
    private static final Identifier TITANPLAYER_ID
            = Identifier.of("silvkingsmod", "entities/titan_player");

    public static void modifyLootTables() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registry) -> {
            if(GRASS_BLOCK_ID.equals(key.getValue())) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.01f)) // Drops 1% of the time
                        .with(ItemEntry.builder(ModItems.SUPER_FLOWER_SEEDS))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());

                tableBuilder.pool(poolBuilder.build());
            }

            if(LootTables.IGLOO_CHEST_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(1.0f))
                        .with(ItemEntry.builder(ModItems.CHISEL))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());

                tableBuilder.pool(poolBuilder.build());
            }

            if(CREEPER_ID.equals(key.getValue())) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.05f))
                        .with(ItemEntry.builder(ModItems.NECRON_DOOM_MUSIC_DISC))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 2.0f)).build());

                tableBuilder.pool(poolBuilder.build());
            }
            if(ENDERDRAGON_ID.equals(key.getValue())) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(1.0f))
                        .with(ItemEntry.builder(ModItems.DRAGON_SCALE))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0f, 2.0f)).build());

                tableBuilder.pool(poolBuilder.build());
            }

            if(TITANPLAYER_ID.equals(key.getValue())) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .conditionally(RandomChanceLootCondition.builder(0.05f))
                        .with(ItemEntry.builder(ModItems.STARLIGHT_ASHES))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0f, 1.0f)).build());

                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.REPLACE.register((key,original,source,registries) -> {

            return original;
        });
    }
}