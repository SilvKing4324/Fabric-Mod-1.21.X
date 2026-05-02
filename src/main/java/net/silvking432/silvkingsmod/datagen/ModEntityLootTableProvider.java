package net.silvking432.silvkingsmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.AnyOfLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.condition.RandomChanceWithEnchantedBonusLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.EnchantmentsPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.predicate.item.ItemSubPredicateTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.EnchantmentTags;
import net.silvking432.silvkingsmod.entity.ModEntities;
import net.silvking432.silvkingsmod.item.ModItems;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ModEntityLootTableProvider extends SimpleFabricLootTableProvider {

    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup;

    public ModEntityLootTableProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup, LootContextTypes.ENTITY);
        this.registryLookup = registryLookup;
    }

    protected final AnyOfLootCondition.Builder createSmeltLootCondition(RegistryWrapper.WrapperLookup registries) {
        RegistryWrapper.Impl<Enchantment> impl = registries.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return AnyOfLootCondition.builder(
                EntityPropertiesLootCondition.builder(
                        LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onFire(true))
                ),
                EntityPropertiesLootCondition.builder(
                        LootContext.EntityTarget.DIRECT_ATTACKER,
                        EntityPredicate.Builder.create()
                                .equipment(
                                        EntityEquipmentPredicate.Builder.create()
                                                .mainhand(
                                                        ItemPredicate.Builder.create()
                                                                .subPredicate(
                                                                        ItemSubPredicateTypes.ENCHANTMENTS,
                                                                        EnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(impl.getOrThrow(EnchantmentTags.SMELTS_LOOT), NumberRange.IntRange.ANY))))))));
    }

    @Override
    public void accept(BiConsumer<RegistryKey<LootTable>, LootTable.Builder> exporter) {
        RegistryWrapper.WrapperLookup registries = this.registryLookup.join();

        exporter.accept(ModEntities.RHINO.getLootTableId(), LootTable.builder()
                .pool(
                        LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1.0F))
                                .with(
                                        ItemEntry.builder(Items.LEATHER)
                                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
                                                .apply(EnchantedCountIncreaseLootFunction.builder(registries, UniformLootNumberProvider.create(0.0F, 1.0F)))))
                .pool(
                        LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1.0F))
                                .with(
                                        ItemEntry.builder(ModItems.RAW_RHINO_STEAK)
                                                .apply(FurnaceSmeltLootFunction.builder().conditionally(this.createSmeltLootCondition(registries)))
                                                .apply(EnchantedCountIncreaseLootFunction.builder(registries, UniformLootNumberProvider.create(0.0F, 1.0F))))));

        exporter.accept(ModEntities.TITAN_PLAYER.getLootTableId(), LootTable.builder()
                        .pool(
                                LootPool.builder()
                                        .rolls(ConstantLootNumberProvider.create(1.0F))
                                        .with(ItemEntry.builder(ModItems.DARK_CORE_BASE))
                                        .conditionally(RandomChanceWithEnchantedBonusLootCondition.builder(registries, 0.25F, 0.05F))));

        exporter.accept(ModEntities.ETERNAL_SHULKER.getLootTableId(), LootTable.builder()
                        .pool(
                                LootPool.builder()
                                        .rolls(ConstantLootNumberProvider.create(1.0F))
                                        .with(ItemEntry.builder(Items.SHULKER_SHELL))
                                        .conditionally(RandomChanceWithEnchantedBonusLootCondition.builder(registries, 0.75F, 0.0625F)))
                        .pool(
                                LootPool.builder()
                                        .rolls(ConstantLootNumberProvider.create(1.0F))
                                        .with(ItemEntry.builder(ModItems.ETERNAL_SHELL))
                                        .conditionally(RandomChanceWithEnchantedBonusLootCondition.builder(registries, 0.05F, 0.01F))));

        exporter.accept(EntityType.ENDER_DRAGON.getLootTableId(), LootTable.builder()
                .pool(
                        LootPool.builder()
                                .rolls(ConstantLootNumberProvider.create(1))
                                .conditionally(RandomChanceLootCondition.builder(1.0f))
                                .with(ItemEntry.builder(ModItems.DRAGON_SCALE))
                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0f, 2.0f)).build())));
    }
}