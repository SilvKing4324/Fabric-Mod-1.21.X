package net.silvking432.silvkingsmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.item.ModItems;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ModEntityLootTableProvider extends SimpleFabricLootTableProvider {
    public ModEntityLootTableProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registries) {
        super(output, registries, LootContextTypes.ENTITY);
    }

    @Override
    public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> biConsumer) {
        addVanillaEntityDrop(biConsumer, "ender_dragon", ModItems.DRAGON_SCALE, 2.0f);

    }

    /**
     * Helper-Methode um Vanilla-Entity Drops zu überschreiben
     * @param entityName Der Name der Entity (z.B. "ender_dragon")
     * @param drop Das Item, das gedroppt werden soll
     * @param count Die Anzahl der Items
     */
    private void addVanillaEntityDrop(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> biConsumer,
                                      String entityName, Item drop, float count) {

        RegistryKey<LootTable> key = RegistryKey.of(
                RegistryKeys.LOOT_TABLE,
                Identifier.of("minecraft", "entities/" + entityName)
        );

        biConsumer.accept(key, LootTable.builder().pool(LootPool.builder()
                .rolls(ConstantLootNumberProvider.create(1.0f))
                .with(ItemEntry.builder(drop))
                .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(count)))
        ));
    }
}