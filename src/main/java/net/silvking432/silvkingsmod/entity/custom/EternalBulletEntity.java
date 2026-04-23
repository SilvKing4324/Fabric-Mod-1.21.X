package net.silvking432.silvkingsmod.entity.custom;

import com.google.common.base.MoreObjects;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.effect.ModEffects;

public class EternalBulletEntity extends ShulkerBulletEntity {
    public EternalBulletEntity(EntityType<? extends ShulkerBulletEntity> type, World world) {
        super(type, world);
    }

    public EternalBulletEntity(World world, LivingEntity owner, Entity target, Direction.Axis axis) {
        super(world, owner, target, axis);
    }



    @Override
    public void tick() {
        super.tick();
        this.setVelocity(this.getVelocity().multiply(1.07));
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        Entity entity2 = this.getOwner();
        LivingEntity livingEntity = entity2 instanceof LivingEntity ? (LivingEntity)entity2 : null;
        DamageSource damageSource = this.getDamageSources().mobProjectile(this, livingEntity);
        boolean bl = entity.damage(damageSource, 8.0F);
        if (bl) {
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                EnchantmentHelper.onTargetDamaged(serverWorld, entity, damageSource);
            }

            if (entity instanceof LivingEntity livingEntity2) {
                livingEntity2.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 200,1), MoreObjects.firstNonNull(entity2, this));
                livingEntity2.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200), MoreObjects.firstNonNull(entity2, this));
                livingEntity2.addStatusEffect(new StatusEffectInstance(ModEffects.VULNERABILITY, 300,4), MoreObjects.firstNonNull(entity2, this));
            }
        }
    }
}
