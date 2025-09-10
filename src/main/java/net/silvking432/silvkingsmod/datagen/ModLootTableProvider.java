package net.silvking432.silvkingsmod.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {
    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.TITANIUM_BLOCK);
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

        addDrop(ModBlocks.TITANIUM_ORE, oreDrops(ModBlocks.TITANIUM_ORE, ModItems.TITANIUM_SHARD));
        addDrop(ModBlocks.TITANIUM_DEEPSLATE_ORE, multipleOreDrops(ModBlocks.TITANIUM_ORE, ModItems.TITANIUM_SHARD,1,2));
    }

    public LootTable.Builder multipleOreDrops(Block drop, Item item, float minDrops, float maxDrops) {
        RegistryWrapper.Impl<Enchantment> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT);
        return this.dropsWithSilkTouch(drop, this.applyExplosionDecay(drop, ((LeafEntry.Builder<?>)
                ItemEntry.builder(item).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(minDrops, maxDrops))))
                .apply(ApplyBonusLootFunction.oreDrops(impl.getOrThrow(Enchantments.FORTUNE)))));
    }

}
