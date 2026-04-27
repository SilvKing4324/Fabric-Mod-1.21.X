package net.silvking432.silvkingsmod.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.component.ModDataComponentTypes;
import net.silvking432.silvkingsmod.component.custom.CoreTrait;

import java.util.List;

public class TraitEffectHandler {

    private static final Identifier HEALTH_ID = Identifier.of(SilvKingsMod.MOD_ID, "trait_health");
    private static final Identifier SPEED_ID = Identifier.of(SilvKingsMod.MOD_ID, "trait_speed");

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                applyArmorTraits(player);
            }
        });
    }

    private static void applyArmorTraits(ServerPlayerEntity player) {
        float totalExtraHealth = 0;
        float totalSpeedBonus = 0;

        for (ItemStack stack : player.getArmorItems()) {
            List<CoreTrait> traits = stack.get(ModDataComponentTypes.CORE_TRAITS);
            if (traits != null) {
                for (CoreTrait trait : traits) {
                    if (trait.traitId().equals("EXTRA_HEALTH")) {
                        totalExtraHealth += trait.value();
                    }
                    if (trait.traitId().equals("BONUS_SPEED")) {
                        totalSpeedBonus += (trait.value() / 100f);
                    }
                }
            }
        }

        updateAttribute(player, EntityAttributes.GENERIC_MAX_HEALTH, HEALTH_ID, totalExtraHealth, EntityAttributeModifier.Operation.ADD_VALUE);
        updateAttribute(player, EntityAttributes.GENERIC_MOVEMENT_SPEED, SPEED_ID, totalSpeedBonus, EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static void updateAttribute(ServerPlayerEntity player, net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.attribute.EntityAttribute> attribute, Identifier id, float value, EntityAttributeModifier.Operation op) {
        EntityAttributeInstance instance = player.getAttributeInstance(attribute);
        if (instance != null) {
            instance.removeModifier(id);
            if (value > 0) {
                instance.addTemporaryModifier(new EntityAttributeModifier(id, value, op));
            }
        }
    }
}