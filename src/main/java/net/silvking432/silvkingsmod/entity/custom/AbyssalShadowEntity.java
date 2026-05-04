package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.dark_world.DarkWorldHandler;
import org.jetbrains.annotations.Nullable;

public class AbyssalShadowEntity extends HostileEntity {

    public AbyssalShadowEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.experiencePoints = 5;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0)
                .add(EntityAttributes.GENERIC_ARMOR, 14.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.45)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 12.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.4, false));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            for (int i = 0; i < 5; i++) {
                this.getWorld().addParticle(ParticleTypes.LARGE_SMOKE,
                        this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5),
                        0, 0, 0);
                this.getWorld().addParticle(ParticleTypes.SQUID_INK,
                        this.getParticleX(0.5), this.getY() + 0.1, this.getParticleZ(0.5),
                        0, 0, 0);
                this.getWorld().addParticle(ParticleTypes.FLAME,
                        this.getParticleX(0.5), this.getY() + 0.1, this.getParticleZ(0.5),
                        0, 0, 0);
            }
        }
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean success = super.tryAttack(target);

        if (success && !this.getWorld().isClient) {
            if (target instanceof ServerPlayerEntity player) {
                DarkWorldHandler.modifyDarkFog(player, 600);
                double resistance = player.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
                double upwardVelocity = 5.0 * (1.0 - (resistance * 0.5));

                Vec3d currentVel = player.getVelocity();
                player.setVelocity(currentVel.x, upwardVelocity, currentVel.z);

                player.velocityModified = true;
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 3));
                player.getWorld().playSound(null, player.getBlockPos(),
                        SoundEvents.ENTITY_BREEZE_SHOOT, SoundCategory.HOSTILE, 1.5f, 0.5f);
            }
        }

        return success;
    }


    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_BREEZE_IDLE_GROUND;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_BREEZE_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_BREEZE_DEATH;
    }
}