package net.silvking432.silvkingsmod.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;

public class DarkWorldHandler {

    private static final Identifier DARK_WORLD_ID = Identifier.of(SilvKingsMod.MOD_ID, "dark_world");

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(DarkWorldHandler::handleDarkWorldTick);
    }

    private static void handleDarkWorldTick(ServerWorld world) {
        if (world.getRegistryKey().getValue().equals(DARK_WORLD_ID)) {
            for (ServerPlayerEntity player : world.getPlayers()) {
                handleElytraRestriction(player);
            }
        }
    }

    private static void handleElytraRestriction(ServerPlayerEntity player) {
        if (player.isFallFlying()) {
            player.stopFallFlying();
        }

        ItemStack chestStack = player.getEquippedStack(EquipmentSlot.CHEST);
        if (chestStack.isOf(Items.ELYTRA)) {
            ItemStack elytraCopy = chestStack.copy();
            player.getInventory().offerOrDrop(elytraCopy);
            chestStack.setCount(0);

            player.sendMessage(Text.literal("§7The Gravity here is too strong for the Elytra!"), true);
        }
    }
}