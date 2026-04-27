package net.silvking432.silvkingsmod.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.component.ModDataComponentTypes;
import net.silvking432.silvkingsmod.component.custom.CoreTrait;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlMixin extends ThrownItemEntity {

    public EnderPearlMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"))
    private boolean silvkingsmod$cancelPearlDamage(Entity instance, DamageSource source, float amount) {
        if (instance instanceof ServerPlayerEntity player) {
            for (ItemStack stack : player.getArmorItems()) {
                List<CoreTrait> traits = stack.get(ModDataComponentTypes.CORE_TRAITS);
                if (traits != null) {
                    for (CoreTrait trait : traits) {
                        if (trait.traitId().equals("SKILL_PEARL_MAYHEM")) {
                            return false;
                        }
                    }
                }
            }
        }
        return instance.damage(source, amount);
    }
}