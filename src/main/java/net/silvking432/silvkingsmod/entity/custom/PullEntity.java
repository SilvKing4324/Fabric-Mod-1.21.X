package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.util.GravityPullHandler;

import java.util.List;

import static net.silvking432.silvkingsmod.util.TraitUtil.NECRO_MOBS;

public class PullEntity extends Entity {
    private int age = 0;
    private final GravityPullHandler pullHandler = new GravityPullHandler();

    public PullEntity(EntityType<?> type, World world) {
        super(type, world);
        this.noClip = true;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            double radius = 20.0;
            double maxStrength = 50.0;
            int maxAge = 300;

            Box area = this.getBoundingBox().expand(radius);
            List<LivingEntity> targets = this.getWorld().getEntitiesByClass(LivingEntity.class, area, target -> target.isAlive() && !NECRO_MOBS.contains(target.getType()));
            for (LivingEntity target : targets) {
                pullHandler.applyGravity(this.getBlockPos(), target, maxStrength, radius, GravityPullHandler.PullType.QUADRATIC);
            }
            age++;
            if (age >= maxAge) {
                this.discard();
            }
        }
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {}

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.age = nbt.getInt("Age");
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putInt("Age", this.age);
    }

    @Override
    public boolean isInvisible() {
        return true;
    }
}