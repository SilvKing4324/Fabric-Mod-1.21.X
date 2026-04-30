package net.silvking432.silvkingsmod.dark_world;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.component.ModDataComponentTypes;
import net.silvking432.silvkingsmod.component.custom.CoreTrait;
import net.silvking432.silvkingsmod.entity.ModEntities;
import net.silvking432.silvkingsmod.entity.custom.*;
import net.silvking432.silvkingsmod.util.MobScalingUtil;

import java.util.List;

public class MobSpawnHandler {

    private static final Identifier DARK_WORLD_ID = Identifier.of(SilvKingsMod.MOD_ID, "dark_world");

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (world.getRegistryKey().getValue().equals(DARK_WORLD_ID) && entity instanceof HostileEntity hostile) {

                if (hostile.getCommandTags().contains("is_scaled")) {
                    return;
                }

                double x = hostile.getX();
                double z = hostile.getZ();
                double distance = Math.sqrt(x * x + z * z);

                if (distance > 2000) {
                    if (tryReplaceWithOther(hostile, world)) {
                        return;
                    }
                }

                MobScalingUtil.applyScaling(hostile, hostile.getBlockPos());
            }
        });
    }

    private static boolean tryReplaceWithOther(HostileEntity entity, ServerWorld world) {
        if (entity.getCommandTags().contains("is_scaled")) return false;
        PlayerEntity closestPlayer = world.getClosestPlayer(entity.getX(), entity.getY(), entity.getZ(), 16.0, false);
        float multiplier = 1.0f;
        if (closestPlayer != null) {
            multiplier += getRareFinderMultiplier(closestPlayer);
        }

        float finalMultiplier = multiplier;
        return switch (entity) {
            case DarkShadowEntity darkShadowEntity when world.random.nextFloat() < (0.5f * finalMultiplier) -> convertToTierTwo(entity, ModEntities.ABYSSAL_SHADOW, world);
            case NecroPigEntity necroPigEntity when world.random.nextFloat() < (0.5f * finalMultiplier) -> convertToTierTwo(entity, ModEntities.NECRO_WOLF, world);
            case NecroCowEntity necroCowEntity when world.random.nextFloat() < (0.5f * finalMultiplier) -> convertToTierTwo(entity, ModEntities.NECRO_LLAMA, world);
            case NecroSheepEntity necroSheepEntity when world.random.nextFloat() < (0.5f * finalMultiplier) -> convertToTierTwo(entity, ModEntities.NECRO_GECKO, world);
            default -> false;
        };

    }


    private static <T extends HostileEntity> boolean convertToTierTwo(HostileEntity original, EntityType<T> targetType, ServerWorld world) {
        T newEntity = targetType.create(world);
        if (newEntity != null) {
            newEntity.refreshPositionAndAngles(original.getX(), original.getY(), original.getZ(), original.getYaw(), original.getPitch());
            MobScalingUtil.applyScaling(newEntity, newEntity.getBlockPos());
            newEntity.addCommandTag("is_scaled");

            world.spawnEntity(newEntity);
            original.discard();
            return true;
        }
        return false;
    }

    private static float getRareFinderMultiplier(PlayerEntity player) {
        float totalMultiplierBonus = 0.0f;
        for (ItemStack stack : player.getArmorItems()) {
            List<CoreTrait> traits = stack.get(ModDataComponentTypes.CORE_TRAITS);
            if (traits != null) {
                for (CoreTrait trait : traits) {
                    if (trait.traitId().equals("RARE_FINDER")) {
                        totalMultiplierBonus += (trait.value() / 100.0f);
                    }
                }
            }
        }
        return totalMultiplierBonus;
    }
}