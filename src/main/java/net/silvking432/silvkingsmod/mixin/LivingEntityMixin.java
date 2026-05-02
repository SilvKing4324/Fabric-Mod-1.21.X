package net.silvking432.silvkingsmod.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.silvking432.silvkingsmod.component.ModDataComponentTypes;
import net.silvking432.silvkingsmod.component.custom.CoreTrait;
import net.silvking432.silvkingsmod.effect.ModEffects;
import net.silvking432.silvkingsmod.item.ModItems; // Achte auf deinen Item-Pfad
import net.silvking432.silvkingsmod.util.TraitUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.silvking432.silvkingsmod.util.TraitUtil.NECRO_MOBS;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);
    @Shadow public abstract StatusEffectInstance getStatusEffect(RegistryEntry<StatusEffect> effect);


    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float silvkingsmod$handleAllDamageModifications(float amount, DamageSource source) {
        float finalAmount = amount;

        if (source.getAttacker() instanceof PlayerEntity attacker) {
            ItemStack weapon = attacker.getMainHandStack();

            if (!weapon.isEmpty()) {
                List<CoreTrait> traits = weapon.get(ModDataComponentTypes.CORE_TRAITS);
                if (traits != null) {
                    float extraDamage = 0.0f;
                    float critBonus = 0.0f;
                    boolean hasNecroSlayer = false;

                    for (CoreTrait trait : traits) {
                        if (trait.traitId().equals("EXTRA_DAMAGE")) extraDamage = trait.value();
                        if (trait.traitId().equals("EXTRA_CRIT_BONUS")) critBonus = trait.value();
                        if (trait.traitId().equals("SKILL_NECRO_SLAYER")) {hasNecroSlayer = true;}
                    }

                    if (extraDamage > 0) {
                        finalAmount *= (1.0f + (extraDamage / 100.0f));
                    }

                    if (critBonus > 0) {
                        if( attacker.fallDistance > 0.0F
                                && !attacker.isOnGround()
                                && !attacker.isClimbing()
                                && !attacker.isTouchingWater()
                                && !attacker.hasStatusEffect(StatusEffects.BLINDNESS)
                                && !attacker.hasVehicle()
                                && !attacker.isSprinting())
                        {
                            finalAmount *= (1.0f + (critBonus / 100.0f));
                        }
                    }
                    if (hasNecroSlayer) {
                        LivingEntity target = (LivingEntity) (Object) this;
                        if (NECRO_MOBS.contains(target.getType())) {
                            finalAmount *= 2.0f;
                        }
                    }
                }
            }
        }

        if (this.hasStatusEffect(ModEffects.VULNERABILITY)) {
            int amplifier = this.getStatusEffect(ModEffects.VULNERABILITY).getAmplifier() + 1;
            finalAmount *= (1.0f + (0.15f * amplifier));
        }

        if ((Object) this instanceof PlayerEntity targetPlayer) {
            float totalReductionPercentage = 0.0f;
            for (ItemStack armorItem : targetPlayer.getArmorItems()) {
                List<CoreTrait> traits = armorItem.get(ModDataComponentTypes.CORE_TRAITS);
                if (traits != null) {
                    for (CoreTrait trait : traits) {
                        if (trait.traitId().equals("DAMAGE_REDUCTION")) {
                            totalReductionPercentage += trait.value();
                        }
                    }
                }
            }

            if (totalReductionPercentage > 0) {
                float cap = 80.0f;
                float actualReduction = Math.min(totalReductionPercentage, cap);
                finalAmount *= (1.0f - (actualReduction / 100.0f));
            }
        }

        return finalAmount;
    }

    @Inject(method = "applyArmorToDamage", at = @At("HEAD"), cancellable = true)
    private void silvkingsmod$bypassArmorIfVoid(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        if (source.getAttacker() instanceof PlayerEntity player) {
            ItemStack weapon = player.getMainHandStack();
            List<CoreTrait> traits = weapon.get(ModDataComponentTypes.CORE_TRAITS);

            if (traits != null) {
                for (CoreTrait trait : traits) {
                    if (trait.traitId().equals("SKILL_VOID_DAMAGE")) {
                        cir.setReturnValue(amount);
                    }
                }
            }
        }
    }


    @Redirect(
            method = "tickFallFlying",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z")
    )
    private boolean allowCustomElytra(ItemStack instance, net.minecraft.item.Item item) {
        return instance.isOf(Items.ELYTRA) || instance.isOf(ModItems.ETERNAL_ELYTRA);
    }

    @Inject(method = "travel", at = @At("RETURN"))
    private void applyEternalElytraPhysics(Vec3d movementInput, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.isFallFlying()) {
            ItemStack chestStack = entity.getEquippedStack(EquipmentSlot.CHEST);

            if (chestStack.isOf(ModItems.ETERNAL_ELYTRA)) {
                Vec3d velocity = entity.getVelocity();
                double boost = 1.0091;
                entity.setVelocity(velocity.x * boost, velocity.y, velocity.z * boost);
            }
        }
    }

    @Inject(method = "dropLoot", at = @At("TAIL"))
    private void silvkingsmod$applyScalingLoot(DamageSource source, boolean causedByPlayer, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.getWorld().isClient || !causedByPlayer) return;

        ServerWorld world = (ServerWorld) entity.getWorld();
        if (!world.getRegistryKey().getValue().getPath().equals("dark_world")) return;

        EntityType<?> type = entity.getType();
        if (!TraitUtil.getNecroMobTiers().containsKey(type)) return;

        int baseTier = TraitUtil.getNecroMobTiers().get(type);
        BlockPos pos = entity.getBlockPos();
        double distance = Math.min(50000.0, Math.sqrt(pos.getSquaredDistance(0, pos.getY(), 0)));

        if (baseTier == 2 && distance < 2000) return;
        if (baseTier == 3 && distance < 5000) return;

        int lootingLevel = 0;
        float bonusShardsTraitValue = 0.0f;

        if (source.getAttacker() instanceof LivingEntity attacker) {
            ItemStack weapon = attacker.getMainHandStack();
            lootingLevel = EnchantmentHelper.getLevel(world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.LOOTING).get(), weapon);

            List<CoreTrait> traits = weapon.get(ModDataComponentTypes.CORE_TRAITS);
            if (traits != null) {
                for (CoreTrait trait : traits) {
                    if (trait.traitId().equals("BONUS_SHARDS")) {
                        bonusShardsTraitValue += trait.value();
                    }
                }
            }
        }

        double chance = calculateChance(baseTier, distance, lootingLevel, bonusShardsTraitValue);

        int dropAmount = (int) (chance / 100.0);
        double remainingChance = chance % 100.0;

        if (entity.getRandom().nextFloat() * 100.0 < remainingChance) {
            dropAmount++;
        }

        if (dropAmount > 0) {
            for (int i = 0; i < dropAmount; i++) {
                int finalTier = baseTier;
                if (entity.getRandom().nextFloat() < 0.01f) {
                    finalTier = baseTier + 1;
                }

                entity.dropStack(new ItemStack(getShardItem(finalTier), 1));
            }
        }
    }

    @Unique
    private double calculateChance(int tier, double distance, int lootingLevel, float bonusShardsTrait) {
        double chance = 0;

        switch (tier) {
            case 1 -> chance = 50.0 + (distance / 200.0) * 5.0;
            case 2 -> {
                // Tier 2: 20% Base + 3% all 200 Blocks
                if (distance >= 2000) {
                    chance = 20.0 + ((distance - 2000) / 200.0) * 3.0;
                }
            }
            case 3 -> {
                // Tier 3: 10% Base + 0.5% all 200 Blocks
                if (distance >= 5000) {
                    chance = 10.0 + ((distance - 5000) / 200.0) * 0.5;
                }
            }
        }

        double lootingMultiplier = 1.0 + (lootingLevel * 0.05);
        double traitMultiplier = 1.0 + (bonusShardsTrait / 100.0);

        return chance * lootingMultiplier * traitMultiplier;
    }

    @Unique
    private Item getShardItem(int tier) {
        return switch (tier) {
            case 2 -> ModItems.DARK_SHARD_TIER2;
            case 3 -> ModItems.DARK_SHARD_TIER3;
            default -> ModItems.DARK_SHARD_TIER1; // and case 1
        };
    }

    @Unique
    private boolean silvkingsmod$isExtending = false;

    @Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;)Z", at = @At("HEAD"), cancellable = true)
    private void silvkingsmod$extendPositiveEffects(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        if (silvkingsmod$isExtending) return;

        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof PlayerEntity player && !player.getWorld().isClient()) {
            if (effect.getEffectType().value().getCategory() == StatusEffectCategory.BENEFICIAL) {

                float extraSeconds = 0.0f;
                for (ItemStack stack : player.getArmorItems()) {
                    List<CoreTrait> traits = stack.get(ModDataComponentTypes.CORE_TRAITS);
                    if (traits != null) {
                        for (CoreTrait trait : traits) {
                            if (trait.traitId().equals("EFFECT_DURATION")) {
                                extraSeconds += trait.value();
                            }
                        }
                    }
                }

                if (extraSeconds > 0) {
                    int extraTicks = (int) (extraSeconds * 20);

                    silvkingsmod$isExtending = true;

                    StatusEffectInstance extendedEffect = new StatusEffectInstance(
                            effect.getEffectType(),
                            effect.getDuration() + extraTicks,
                            effect.getAmplifier(),
                            effect.isAmbient(),
                            effect.shouldShowParticles(),
                            effect.shouldShowIcon()
                    );

                    player.addStatusEffect(extendedEffect);
                    silvkingsmod$isExtending = false;
                    cir.setReturnValue(true);
                }
            }
        }
    }
}