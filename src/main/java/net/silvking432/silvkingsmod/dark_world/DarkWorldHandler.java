package net.silvking432.silvkingsmod.dark_world;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.component.ModDataComponentTypes;
import net.silvking432.silvkingsmod.component.custom.CoreTrait;
import net.silvking432.silvkingsmod.effect.ModEffects;
import net.silvking432.silvkingsmod.item.ModItems;
import net.silvking432.silvkingsmod.network.DarkFogNetworking;

import java.util.List;

public class DarkWorldHandler {
    private static final Identifier DARK_WORLD_ID = Identifier.of(SilvKingsMod.MOD_ID, "dark_world");

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(DarkWorldHandler::handleDarkWorldTick);
    }

    private static void handleDarkWorldTick(ServerWorld world) {
        boolean isDarkWorld = world.getRegistryKey().getValue().equals(DARK_WORLD_ID);
        long time = world.getTime();
        boolean isSecondTick = time % 20 == 0;
        boolean isFiveSecondTick = time % 100 == 0;

        for (ServerPlayerEntity player : world.getPlayers()) {
            if (isDarkWorld) {
                handleElytraRestriction(player);
                updateDarkFog(player);
                if (isSecondTick) {
                    syncTraitsImmediately(player);
                }
                if (isFiveSecondTick) {
                    checkDistanceAdvancements(player);
                }
            } else {
                resetDarkFog(player);
            }
        }
    }

    private static void checkDistanceAdvancements(ServerPlayerEntity player) {
        double x = player.getX();
        double z = player.getZ();
        double distanceSquared = x * x + z * z;
        double radius1 = 2000.0;
        double radius2 = 5000.0;
        double radius3 = 50000.0;

        if (distanceSquared >= radius1 * radius1) {
            grantAdvancement(player, "dark_world/beyond_borders");
        }
        if (distanceSquared >= radius2 * radius2) {
            grantAdvancement(player, "dark_world/world_edge");
        }
        if (distanceSquared >= radius3 * radius3) {
            grantAdvancement(player, "dark_world/max_world_edge");
        }
    }

    private static void grantAdvancement(ServerPlayerEntity player, String path) {
        var advancementEntry = player.getServer().getAdvancementLoader()
                .get(Identifier.of(SilvKingsMod.MOD_ID, path));

        if (advancementEntry != null) {
            var progress = player.getAdvancementTracker().getProgress(advancementEntry);
            if (!progress.isDone()) {
                for (String criterion : progress.getUnobtainedCriteria()) {
                    player.getAdvancementTracker().grantCriterion(advancementEntry, criterion);
                }
            }
        }
    }

    private static void updateDarkFog(ServerPlayerEntity player) {
        Scoreboard scoreboard = player.getServer().getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective("dark_fog");

        if (objective == null) {
            objective = scoreboard.addObjective("dark_fog",
                    ScoreboardCriterion.DUMMY, Text.literal("Dark Fog Timer"),
                    ScoreboardCriterion.RenderType.INTEGER, true, null);
        }

        ScoreAccess scoreAccess = scoreboard.getOrCreateScore(player, objective);
        int currentTimer = scoreAccess.getScore();

        float resistance = getDarknessResistance(player);

        if (currentTimer < 24000 && resistance < 1.0f) {
            if (player.getRandom().nextFloat() >= resistance) {

                int baseTick = 1;
                if (player.hasStatusEffect(ModEffects.ANXIETY)) {
                    StatusEffectInstance effect = player.getStatusEffect(ModEffects.ANXIETY);
                    if (effect != null) {
                        float multiplier = 1.0f + (0.25f * (effect.getAmplifier() + 1));
                        if (player.getRandom().nextFloat() < (multiplier - 1.0f)) {
                            baseTick = 2;
                        }
                    }
                }
                int nextValue = Math.min(currentTimer + baseTick, 24000);
                scoreAccess.setScore(nextValue);
                float extraVision = getExtraVisionBonus(player);
                DarkFogNetworking.sendSyncPacket(player, nextValue, extraVision);
            }
        }
    }

    private static void handleElytraRestriction(ServerPlayerEntity player) {
        if (player.isFallFlying()) {
            player.stopFallFlying();
        }

        ItemStack chestStack = player.getEquippedStack(EquipmentSlot.CHEST);
        if (chestStack.isOf(Items.ELYTRA) || chestStack.isOf(ModItems.ETERNAL_ELYTRA)) {
            ItemStack elytraCopy = chestStack.copy();
            player.getInventory().offerOrDrop(elytraCopy);
            chestStack.setCount(0);

            player.sendMessage(Text.literal("§7The Gravity here is too strong for the Elytra!"), true);
        }
    }

    private static float getDarknessResistance(ServerPlayerEntity player) {
        float resistance = 0.0f;

        for (ItemStack armorItem : player.getArmorItems()) {
            if (isDarkTitanArmor(armorItem)) {
                resistance += 0.1f;
            }

            List<CoreTrait> traits = armorItem.get(ModDataComponentTypes.CORE_TRAITS);
            if (traits != null) {
                for (CoreTrait trait : traits) {
                    if (trait.traitId().equals("DARK_FOG_RES")) {
                        resistance += (trait.value() / 100f);
                    }
                }
            }
        }
        return Math.min(resistance, 1.0f);
    }

    private static boolean isDarkTitanArmor(ItemStack stack) {
        return stack.isOf(ModItems.DARK_TITANIUM_HELMET) ||
                stack.isOf(ModItems.DARK_TITANIUM_CHESTPLATE) ||
                stack.isOf(ModItems.DARK_TITANIUM_LEGGINGS) ||
                stack.isOf(ModItems.DARK_TITANIUM_BOOTS);
    }

    private static void resetDarkFog(ServerPlayerEntity player) {
        Scoreboard scoreboard = player.getServer().getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective("dark_fog");

        if (objective != null) {
            ScoreAccess scoreAccess = scoreboard.getOrCreateScore(player, objective);
            if (scoreAccess.getScore() > 0) {
                scoreAccess.setScore(0);
                float extraVision = getExtraVisionBonus(player);
                DarkFogNetworking.sendSyncPacket(player, 0, extraVision);
            }
        }
    }

    public static void modifyDarkFog(ServerPlayerEntity player, int amount) {
        Scoreboard scoreboard = player.getServer().getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective("dark_fog");

        if (objective == null) {
            objective = scoreboard.addObjective("dark_fog",
                    ScoreboardCriterion.DUMMY, Text.literal("Dark Fog Timer"),
                    ScoreboardCriterion.RenderType.INTEGER, true, null);
        }

        ScoreAccess scoreAccess = scoreboard.getOrCreateScore(player, objective);
        int current = scoreAccess.getScore();

        int nextValue = Math.clamp(current + amount, 0, 24000);
        scoreAccess.setScore(nextValue);
        float extraVision = getExtraVisionBonus(player);
        DarkFogNetworking.sendSyncPacket(player, nextValue, extraVision);
    }

    public static int getDarkFogValue(ServerPlayerEntity player) {
        Scoreboard scoreboard = player.getServer().getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective("dark_fog");
        if (objective == null) return 0;
        return scoreboard.getOrCreateScore(player, objective).getScore();
    }

    private static float getExtraVisionBonus(ServerPlayerEntity player) {
        float extraVision = 0.0f;

        for (ItemStack armorItem : player.getArmorItems()) {
            List<CoreTrait> traits = armorItem.get(ModDataComponentTypes.CORE_TRAITS);
            if (traits != null) {
                for (CoreTrait trait : traits) {
                    if (trait.traitId().equals("EXTRA_VISION")) {
                        extraVision += trait.value(); // Hier werden die Blöcke addiert
                    }
                }
            }
        }
        return extraVision;
    }

    private static void syncTraitsImmediately(ServerPlayerEntity player) {
        int currentTimer = getDarkFogValue(player);
        float extraVision = getExtraVisionBonus(player);
        // Wir senden die aktuellen Werte.
        // Der Client überschreibt seine alten Werte sofort.
        DarkFogNetworking.sendSyncPacket(player, currentTimer, extraVision);
    }
}