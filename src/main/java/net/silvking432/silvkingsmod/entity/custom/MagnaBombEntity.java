package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class MagnaBombEntity extends Entity {

    private static final TrackedData<Float> SCALE = DataTracker.registerData(MagnaBombEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public MagnaBombEntity(EntityType<?> type, World world) {
        super(type, world);
        this.noClip = true;
        this.velocityDirty = true;
        this.velocityModified = true;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(SCALE, 2.5f);
    }

    @Override
    public void tick() {
        super.tick();

        Vec3d velocity = this.getVelocity();
        this.setPosition(this.getX() + velocity.x, this.getY() + velocity.y, this.getZ() + velocity.z);

        if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld serverWorld) {
            if (this.age % 2 == 0) {
                ParticleS2CPacket packet = new ParticleS2CPacket(
                        ParticleTypes.WITCH, true,
                        this.getX(), this.getY() + 1.5, this.getZ(),
                        0.8f, 0.8f, 0.8f, 0.0f, 3
                );
                ParticleS2CPacket packet2 = new ParticleS2CPacket(
                        ParticleTypes.END_ROD, true,
                        this.getX(), this.getY() + 1.5, this.getZ(),
                        0.8f, 0.6f, 0.6f, 0.0f, 3
                );
                for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                    if (player.squaredDistanceTo(this.getPos()) < 128 * 128) {
                        player.networkHandler.sendPacket(packet);
                        player.networkHandler.sendPacket(packet2);
                    }
                }
            }

            if (this.getVelocity().y < 0 && (this.isOnGround() || this.getY() < this.getWorld().getBottomY() + 2)) {
                if (this.age > 200) this.discard();
            }

            if (this.age > 2400) this.discard();
        }
    }

    @Override
    public boolean shouldRender(double distance) {
        // Erhöht die Render-Distanz massiv (Standard ist oft zu niedrig für 45 Blöcke Höhe)
        double d = 128.0 * 128.0;
        return distance < d;
    }

    @Override
    public boolean isSilent() {
        return false;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("Scale")) {
            this.setCustomScale(nbt.getFloat("Scale"));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat("Scale", this.getCustomScale());
    }

    public void setCustomScale(float scale) {
        this.dataTracker.set(SCALE, scale);
    }

    public float getCustomScale() {
        return this.dataTracker.get(SCALE);
    }

    @Override
    public boolean isOnFire() {
        return false;
    }
}