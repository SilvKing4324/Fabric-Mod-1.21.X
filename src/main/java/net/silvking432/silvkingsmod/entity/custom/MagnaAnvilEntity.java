package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.entity.ModEntities;

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
        // Schwerkraft verstärken für "schweres" Gefühl
        Vec3d vel = this.getVelocity();
        this.setVelocity(vel.x, vel.y - 0.05, vel.z);

        if (this.getWorld().isClient) {
            this.getWorld().addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (!this.getWorld().isClient) {
            Entity target = entityHitResult.getEntity();
            // Immunität für Magna-Mobs
            if (!(target instanceof MagnaTitanEntity || target instanceof MagnaMinionEntity)) {
                target.damage(this.getDamageSources().fallingBlock(this), 50.0f); // 20 Herzen Schaden
                this.discard();
            }
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        if (!this.getWorld().isClient) {
            // Sound beim Aufschlag auf den Boden
            this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.HOSTILE, 1.0f, 0.5f);
            this.discard();
        }
    }
}