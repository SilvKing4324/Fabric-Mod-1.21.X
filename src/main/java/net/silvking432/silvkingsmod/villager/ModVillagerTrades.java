package net.silvking432.silvkingsmod.villager;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.item.ModItems;

public class ModVillagerTrades {
    public static void registerModVillagerTrades() {

        SilvKingsMod.LOGGER.info("Registering Villager Trades for " + SilvKingsMod.MOD_ID);

        TradeOfferHelper.registerVillagerOffers(ModVillagers.TITANIUM_TRADER,1, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD,3),
                    new ItemStack(ModItems.SUPER_FLOWER,1),8,2,0.04f));

            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(ModItems.TITANIUM_SHARD,8),
                    new ItemStack(ModItems.SUPER_FLOWER_SEEDS,1),1,6,0.04f));

        });

        TradeOfferHelper.registerVillagerOffers(ModVillagers.TITANIUM_TRADER,2,factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD,32),
                    new ItemStack(ModBlocks.MAGIC_BLOCK,1),8,9,0.04f));

            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(ModItems.TITANIUM_SHARD,4),
                    new ItemStack(ModItems.CHISEL,1),4,15,0.04f));

        });

        TradeOfferHelper.registerVillagerOffers(ModVillagers.TITANIUM_TRADER,3,factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.IRON_INGOT,5),
                    new ItemStack(ModItems.TOMAHAWK,16),8,4,0.09f));

            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(ModItems.TITANIUM_SHARD,4),
                    new ItemStack(ModItems.CHISEL,1),4,20,0.04f));

        });

        TradeOfferHelper.registerWanderingTraderOffers(1,factories -> {
            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD,3),
                    new ItemStack(ModBlocks.CHAIR,16),8,4,0.09f));

            factories.add((entity, random) -> new TradeOffer(
                    new TradedItem(Items.EMERALD,16),
                    new ItemStack(ModItems.TITANIUM_SHARD,1),1,20,0.04f));

        });
    }
}
