package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.entity.ModEntities;

import java.util.List;

public class MagnaAnvilEntity extends ThrownItemEntity {
    public MagnaAnvilEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public MagnaAnvilEntity(World world, LivingEntity owner) {
        super(ModEntities.MAGNA_ANVIL, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.ANVIL;
    }

    @Override
    public void tick() {
        super.tick();
        Vec3d vel = this.getVelocity();
        this.setVelocity(vel.x, vel.y - 0.05, vel.z);

        if (this.getWorld().isClient) {
            double offsetX = (this.random.nextDouble() - 0.5) * 2.5;
            double offsetY = (this.random.nextDouble() - 0.5) * 2.0;
            double offsetZ = (this.random.nextDouble() - 0.5) * 2.5;

            this.getWorld().addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                    this.getX() + offsetX,
                    this.getY() + 1.0 + offsetY,
                    this.getZ() + offsetZ,
                    0, 0, 0);

            if (this.random.nextFloat() > 0.5f) {
                this.getWorld().addParticle(ParticleTypes.SOUL_FIRE_FLAME,
                        this.getX() + (this.random.nextGaussian() * 0.8),
                        this.getY(),
                        this.getZ() + (this.random.nextGaussian() * 0.8),
                        0, 0, 0);
            }
        }
    }

    private void applyAreaDamage() {
        if (!this.getWorld().isClient) {
            double radius = 2.5;
            List<Entity> targets = this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(radius));

            for (Entity target : targets) {
                if (target instanceof LivingEntity && !(target instanceof MagnaTitanEntity || target instanceof MagnaMinionEntity)) {

                    target.damage(this.getDamageSources().fallingBlock(this), 60.0f);

                    double dx = target.getX() - this.getX();
                    double dz = target.getZ() - this.getZ();
                    target.addVelocity(dx * 0.7, 0.3, dz * 0.7);

                    if (target instanceof LivingEntity living) {
                        living.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 100, 1));
                        living.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 100, 0));
                    }
                }
            }

            ((ServerWorld)this.getWorld()).spawnParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
            ((ServerWorld)this.getWorld()).spawnParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(), this.getZ(), 20, 1.5, 0.5, 1.5, 0.05);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        applyAreaDamage();
        this.discard();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        applyAreaDamage();
        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.HOSTILE, 1.0f, 0.5f);
        this.discard();
    }
}