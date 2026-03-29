package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.entity.ModEntities;

public class MagnaFireballEntity extends SmallFireballEntity {

    public MagnaFireballEntity(EntityType<? extends SmallFireballEntity> entityType, World world) {
        super(entityType, world);
    }

    public MagnaFireballEntity(World world, LivingEntity owner, Vec3d velocity) {
        super(ModEntities.MAGNA_FIREBALL, world);
        this.setOwner(owner);
        this.setVelocity(velocity);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (!this.getWorld().isClient) {
            Entity target = entityHitResult.getEntity();


            boolean isAlly = target instanceof MagnaTitanEntity ||
                    target instanceof MagnaMinionEntity ||
                    target instanceof LavaGolemEntity ||
                    target instanceof MagnaWitchEntity;

            if (isAlly) {
                return;
            }

            float damage = 30.0f;
            target.damage(this.getDamageSources().magic(), damage);
            target.setOnFireFor(15);

            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient) {
            for (int i = 0; i < 3; i++) {
                this.getWorld().addParticle(
                        net.minecraft.particle.ParticleTypes.SOUL_FIRE_FLAME,
                        this.getParticleX(0.5),
                        this.getRandomBodyY(),
                        this.getParticleZ(0.5),
                        0.0, 0.0, 0.0
                );
            }

            if (this.age % 2 == 0) {
                this.getWorld().addParticle(
                        net.minecraft.particle.ParticleTypes.SOUL,
                        this.getX(), this.getY(), this.getZ(),
                        0.0, 0.02, 0.0
                );
            }
        }
    }

    @Override
    public boolean isOnFire() {
        return false;
    }
}