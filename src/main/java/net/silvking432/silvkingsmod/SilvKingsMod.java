package net.silvking432.silvkingsmod;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.component.ModDataComponentTypes;
import net.silvking432.silvkingsmod.item.ModItemGroups;
import net.silvking432.silvkingsmod.item.ModItems;
import net.silvking432.silvkingsmod.util.HammerUsageEvent;
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

		FuelRegistry.INSTANCE.add(ModItems.STARLIGHT_ASHES, 200000);

		PlayerBlockBreakEvents.BEFORE.register(new HammerUsageEvent());
	}
}