package net.silvking432.silvkingsmod;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.*;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.block.entity.ModBlockEntities;
import net.silvking432.silvkingsmod.component.ModDataComponentTypes;
import net.silvking432.silvkingsmod.effect.ModEffects;
import net.silvking432.silvkingsmod.enchantment.ModEnchantmentEffects;
import net.silvking432.silvkingsmod.entity.ModEntities;
import net.silvking432.silvkingsmod.entity.custom.MantisEntity;
import net.silvking432.silvkingsmod.entity.custom.TitanPlayerEntity;
import net.silvking432.silvkingsmod.event.SmeltingTouchHandler;
import net.silvking432.silvkingsmod.item.ModItemGroups;
import net.silvking432.silvkingsmod.item.ModItems;
import net.silvking432.silvkingsmod.particle.ModParticles;
import net.silvking432.silvkingsmod.potion.ModPotions;
import net.silvking432.silvkingsmod.recipe.ModRecipes;
import net.silvking432.silvkingsmod.screen.ModScreenHandlers;
import net.silvking432.silvkingsmod.util.HammerUsageEvent;
import net.silvking432.silvkingsmod.util.ModLootTableModifiers;
import net.silvking432.silvkingsmod.villager.ModVillagerTrades;
import net.silvking432.silvkingsmod.villager.ModVillagers;
import net.silvking432.silvkingsmod.world.gen.ModWorldGeneration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SilvKingsMod implements ModInitializer {
	public static final String MOD_ID = "silvkingsmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModItemGroups.registerItemGroups();
		ModDataComponentTypes.registerDataComponentTypes();
		ModEffects.registerEffects();
		ModPotions.registerPotions();
		ModEnchantmentEffects.registerEnchantmentEffects();
		ModWorldGeneration.generateModWorldGen();
		ModEntities.registerModEntities();
		ModVillagers.registerVillagers();
		ModVillagerTrades.registerModVillagerTrades();
		ModParticles.registerParticles();
		ModLootTableModifiers.modifyLootTables();
		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerScreenHandlers();
		ModRecipes.registerRecipes();
		SmeltingTouchHandler.register();

		FuelRegistry.INSTANCE.add(ModItems.STARLIGHT_ASHES, 200000);

		PlayerBlockBreakEvents.BEFORE.register(new HammerUsageEvent());

		FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
			builder.registerPotionRecipe(Potions.AWKWARD, Items.SLIME_BALL, ModPotions.SLIMEY_POTION);
		});

		CompostingChanceRegistry.INSTANCE.add(ModItems.SUPER_FLOWER, 0.7f);
		CompostingChanceRegistry.INSTANCE.add(ModItems.SUPER_FLOWER_SEEDS, 0.35f);
		CompostingChanceRegistry.INSTANCE.add(ModItems.HONEY_BERRIES, 0.3f);

		StrippableBlockRegistry.register(ModBlocks.DRIFTWOOD_LOG, ModBlocks.STRIPPED_DRIFTWOOD_LOG);
		StrippableBlockRegistry.register(ModBlocks.DRIFTWOOD_WOOD, ModBlocks.STRIPPED_DRIFTWOOD_WOOD);

		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.DRIFTWOOD_LOG,5,5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.DRIFTWOOD_WOOD,5,5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_DRIFTWOOD_WOOD,5,5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.STRIPPED_DRIFTWOOD_LOG,5,5);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.DRIFTWOOD_PLANKS,5,20);
		FlammableBlockRegistry.getDefaultInstance().add(ModBlocks.DRIFTWOOD_LEAVES,30,60);

		FabricDefaultAttributeRegistry.register(ModEntities.MANTIS, MantisEntity.createAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.TITAN_PLAYER, TitanPlayerEntity.createAttributes());

	}
}