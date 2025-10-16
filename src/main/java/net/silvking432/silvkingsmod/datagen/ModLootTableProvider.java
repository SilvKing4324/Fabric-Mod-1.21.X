package net.silvking432.silvkingsmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.block.custom.HoneyBerryBushBlock;
import net.silvking432.silvkingsmod.block.custom.SuperFlowerCropBlock;
import net.silvking432.silvkingsmod.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {

        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);

        addDrop(ModBlocks.TITANIUM_BLOCK);
        addDrop(ModBlocks.TITANIUM_TNT);
        addDrop(ModBlocks.RAW_TITANIUM_BLOCK);
        addDrop(ModBlocks.MAGIC_BLOCK);
        addDrop(ModBlocks.TITANIUM_STAIRS);
        addDrop(ModBlocks.TITANIUM_SLAB, slabDrops(ModBlocks.TITANIUM_SLAB));
        addDrop(ModBlocks.TITANIUM_BUTTON);
        addDrop(ModBlocks.TITANIUM_PRESSURE_PLATE);
        addDrop(ModBlocks.TITANIUM_WALL);
        addDrop(ModBlocks.TITANIUM_FENCE);
        addDrop(ModBlocks.TITANIUM_FENCE_GATE);
        addDrop(ModBlocks.TITANIUM_DOOR, doorDrops(ModBlocks.TITANIUM_DOOR));
        addDrop(ModBlocks.TITANIUM_TRAPDOOR);
        addDrop(ModBlocks.MATRIX_BLOCK);

        addDrop(ModBlocks.TITANIUM_ORE, oreDrops(ModBlocks.TITANIUM_ORE, ModItems.TITANIUM_SHARD));
        addDrop(ModBlocks.TITANIUM_DEEPSLATE_ORE, multipleOreDrops(ModBlocks.TITANIUM_DEEPSLATE_ORE, ModItems.TITANIUM_SHARD,1,2));
        addDrop(ModBlocks.TITANIUM_NETHER_ORE, multipleOreDrops(ModBlocks.TITANIUM_NETHER_ORE, ModItems.TITANIUM_SHARD,1,2));
        addDrop(ModBlocks.TITANIUM_END_ORE, multipleOreDrops(ModBlocks.TITANIUM_END_ORE, ModItems.TITANIUM_SHARD,1,2));

        BlockStatePropertyLootCondition.Builder builder2 = BlockStatePropertyLootCondition.builder(ModBlocks.SUPER_FLOWER_CROP)
                .properties(StatePredicate.Builder.create().exactMatch(SuperFlowerCropBlock.AGE, SuperFlowerCropBlock.MAX_AGE));
        this.addDrop(ModBlocks.SUPER_FLOWER_CROP, this.cropDrops(ModBlocks.SUPER_FLOWER_CROP, ModItems.SUPER_FLOWER, ModItems.SUPER_FLOWER_SEEDS, builder2));

        this.addDrop(
                ModBlocks.HONEY_BERRY_BUSH,
                block -> this.applyExplosionDecay(
                        block,
                        LootTable.builder()
                                .pool(
                                        LootPool.builder()
                                                .conditionally(
                                                        BlockStatePropertyLootCondition.builder(ModBlocks.HONEY_BERRY_BUSH).properties(StatePredicate.Builder.create().exactMatch(HoneyBerryBushBlock.AGE, 3)))
                                                .with(ItemEntry.builder(ModItems.HONEY_BERRIES))
                                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2.0F, 3.0F)))
                                                .apply(ApplyBonusLootFunction.uniformBonusCount(impl.getOrThrow(Enchantments.FORTUNE))))
                                .pool(LootPool.builder().conditionally(
                                                        BlockStatePropertyLootCondition.builder(ModBlocks.HONEY_BERRY_BUSH).properties(StatePredicate.Builder.create().exactMatch(HoneyBerryBushBlock.AGE, 2)))
                                                .with(ItemEntry.builder(ModItems.HONEY_BERRIES))
                                                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 2.0F)))
                                                .apply(ApplyBonusLootFunction.uniformBonusCount(impl.getOrThrow(Enchantments.FORTUNE))))));
        addDrop(ModBlocks.DRIFTWOOD_LOG);
        addDrop(ModBlocks.DRIFTWOOD_WOOD);
        addDrop(ModBlocks.STRIPPED_DRIFTWOOD_LOG);
        addDrop(ModBlocks.STRIPPED_DRIFTWOOD_WOOD);
        addDrop(ModBlocks.DRIFTWOOD_PLANKS);
        addDrop(ModBlocks.DRIFTWOOD_SAPLING);
        addDrop(ModBlocks.DRIFTWOOD_LEAVES, leavesDrops(ModBlocks.DRIFTWOOD_LEAVES, ModBlocks.DRIFTWOOD_SAPLING,0.0625f));

    }

    public LootTable.Builder multipleOreDrops(Block drop, Item item, float minDrops, float maxDrops) {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return this.dropsWithSilkTouch(drop, this.applyExplosionDecay(drop, ((LeafEntry.Builder<?>)
                ItemEntry.builder(item).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(minDrops, maxDrops))))
                .apply(ApplyBonusLootFunction.oreDrops(impl.getOrThrow(Enchantments.FORTUNE)))));
    }

}
