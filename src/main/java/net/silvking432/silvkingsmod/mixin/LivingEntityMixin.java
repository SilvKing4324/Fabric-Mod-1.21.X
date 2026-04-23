package net.silvking432.silvkingsmod.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;
import net.silvking432.silvkingsmod.effect.ModEffects;
import net.silvking432.silvkingsmod.item.ModItems; // Achte auf deinen Item-Pfad
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract boolean hasStatusEffect(RegistryEntry<StatusEffect> effect);
    @Shadow public abstract net.minecraft.entity.effect.StatusEffectInstance getStatusEffect(RegistryEntry<StatusEffect> effect);

    // Dein bestehender Code für Vulnerability
    @ModifyVariable(method = "damage", at = @At("HEAD"), argsOnly = true)
    private float multiplyIncomingDamage(float amount, DamageSource source) {
        if (this.hasStatusEffect(ModEffects.VULNERABILITY)) {
            int amplifier = this.getStatusEffect(ModEffects.VULNERABILITY).getAmplifier() + 1;
            return amount * (1.0f + (0.15f * amplifier));
        }
        return amount;
    }
    @Redirect(
            method = "tickFallFlying",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z")
    )
    private boolean allowCustomElytra(ItemStack instance, net.minecraft.item.Item item) {
        // Falls Minecraft fragt "Ist das eine Elytra?", sagen wir ja,
        // wenn es die Vanilla ODER deine Mod-Elytra ist.
        return instance.isOf(net.minecraft.item.Items.ELYTRA) || instance.isOf(ModItems.ETERNAL_ELYTRA);
    }

    @Inject(method = "travel", at = @At("RETURN"))
    private void applyEternalElytraPhysics(Vec3d movementInput, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity.isFallFlying()) {
            ItemStack chestStack = entity.getEquippedStack(EquipmentSlot.CHEST);

            // 2. Prüfen, ob es deine Eternal Elytra ist
            if (chestStack.isOf(ModItems.ETERNAL_ELYTRA)) {
                Vec3d velocity = entity.getVelocity();

            /* Erklärung der Mathematik:
               Minecraft hat die Geschwindigkeit gerade mit 0.99 multipliziert.
               Um den Effekt von 0.99 auf 0.999 zu heben, müssen wir
               die Geschwindigkeit wieder mit ~1.0091 multiplizieren.
               (0.99 * 1.0091 ≈ 0.999)
            */
                double boost = 1.0091;
                entity.setVelocity(velocity.x * boost, velocity.y, velocity.z * boost);
            }
        }
    }
}