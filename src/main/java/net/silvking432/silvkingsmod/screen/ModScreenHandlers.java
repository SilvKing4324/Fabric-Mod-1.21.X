package net.silvking432.silvkingsmod.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.screen.custom.GrowthChamberScreenHandler;
import net.silvking432.silvkingsmod.screen.custom.PedestalScreenHandler;

public class ModScreenHandlers {

    public static final ScreenHandlerType<PedestalScreenHandler> PEDESTAL_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(SilvKingsMod.MOD_ID, "pedestal_screen_handler"),
                    new ExtendedScreenHandlerType<>(PedestalScreenHandler::new, BlockPos.PACKET_CODEC));

    public static final ScreenHandlerType<GrowthChamberScreenHandler> GROWTH_CHAMBER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, Identifier.of(SilvKingsMod.MOD_ID, "growth_chamber_screen_handler"),
                    new ExtendedScreenHandlerType<>(GrowthChamberScreenHandler::new, BlockPos.PACKET_CODEC));

    public static void registerScreenHandlers() {
        SilvKingsMod.LOGGER.info("Registering Screen handlers for " + SilvKingsMod.MOD_ID);
    }
}
