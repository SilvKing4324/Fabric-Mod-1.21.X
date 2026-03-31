package net.silvking432.silvkingsmod.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.silvking432.silvkingsmod.effect.ModEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);
    @Shadow public abstract net.minecraft.entity.effect.StatusEffectInstance getStatusEffect(RegistryEntry<StatusEffect> effect);

    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float multiplyIncomingDamage(float amount, DamageSource source) {
        if (this.hasStatusEffect(ModEffects.VULNERABILITY)) {
            int amplifier = this.getStatusEffect(ModEffects.VULNERABILITY).getAmplifier() + 1;

            return amount * (1.0f + (0.15f * amplifier));
        }
        return amount;
    }
}