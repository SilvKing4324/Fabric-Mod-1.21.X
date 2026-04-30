package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.effect.ModEffects;
import net.silvking432.silvkingsmod.entity.ModEntities; // Pfad anpassen

import java.util.List;

public class TornadoEntity extends Entity {
    private int age = 0;
    private PullEntity childPullEntity;

    public TornadoEntity(EntityType<?> type, World world) {
        super(type, world);
        this.noClip = true;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient && childPullEntity == null) {
            childPullEntity = new PullEntity(ModEntities.PULL, this.getWorld());
            childPullEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), 0, 0);
            this.getWorld().spawnEntity(childPullEntity);
        }

        // 15 Sekunden
        int maxAge = 300;
        double progress = (double) age / maxAge;
        double currentHeight = progress * 10.0;
        double rotationSpeed = 0.5;
        double angle = age * rotationSpeed;
        double currentRadius = progress * 5.0;

        double offsetX = Math.cos(angle) * currentRadius;
        double offsetZ = Math.sin(angle) * currentRadius;

        Vec3d pullPos = new Vec3d(this.getX() + offsetX, this.getY() + currentHeight, this.getZ() + offsetZ);

        if (childPullEntity != null && childPullEntity.isAlive()) {
            childPullEntity.refreshPositionAndAngles(pullPos.x, pullPos.y, pullPos.z, 0, 0);
        }

        if (!this.getWorld().isClient) {
            if (age % 20 == 0 && age < 101) {
                this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ITEM_ELYTRA_FLYING, SoundCategory.WEATHER, 1.5f, 0.5f);
            }

            ((ServerWorld)this.getWorld()).spawnParticles(ParticleTypes.ANGRY_VILLAGER, pullPos.x, pullPos.y, pullPos.z, 1, 0, 0, 0, 0);

            double maxHeight = 10.0;
            double maxRadiusWithPuffer = currentRadius + 2.0;

            Box checkArea = this.getBoundingBox().expand(maxRadiusWithPuffer, maxHeight, maxRadiusWithPuffer);
            List<LivingEntity> targets = this.getWorld().getEntitiesByClass(LivingEntity.class, checkArea, LivingEntity::isAlive);

            for (LivingEntity target : targets) {
                double relativeY = target.getY() - this.getY();

                if (relativeY >= 0 && relativeY <= maxHeight) {
                    double radiusAtHeight = (relativeY / maxHeight) * currentRadius + 1.5;
                    double distSq = target.squaredDistanceTo(this.getX(), target.getY(), this.getZ());

                    if (distSq <= radiusAtHeight * radiusAtHeight) {
                        target.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                                StatusEffects.NAUSEA, 100, 0, false, false, true));
                        target.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                                ModEffects.VULNERABILITY, 100, 9, false, false, false));
                    }
                }
            }
        } else {
            spawnTornadoParticles(currentHeight, currentRadius);
        }

        age++;
        if (age >= maxAge) {
            if (childPullEntity != null) childPullEntity.discard();
            this.discard();
        }
    }

    private void spawnTornadoParticles(double maxHeight, double maxRadius) {
        for (int i = 0; i < 5; i++) {
            double h = this.getWorld().random.nextDouble() * maxHeight;
            double r = (h / maxHeight) * (maxRadius + 2.0);
            double a = this.getWorld().random.nextDouble() * Math.PI * 2;

            double px = this.getX() + Math.cos(a) * r;
            double pz = this.getZ() + Math.sin(a) * r;

            this.getWorld().addParticle(ParticleTypes.CLOUD, px, this.getY() + h, pz, -Math.sin(a) * 0.2, 0.1, Math.cos(a) * 0.2);
            this.getWorld().addParticle(ParticleTypes.POOF, px, this.getY() + h, pz, 0, 0.05, 0);
        }
    }

    @Override protected void initDataTracker(DataTracker.Builder builder) {}
    @Override protected void readCustomDataFromNbt(NbtCompound nbt) { this.age = nbt.getInt("Age"); }
    @Override protected void writeCustomDataToNbt(NbtCompound nbt) { nbt.putInt("Age", this.age); }
}