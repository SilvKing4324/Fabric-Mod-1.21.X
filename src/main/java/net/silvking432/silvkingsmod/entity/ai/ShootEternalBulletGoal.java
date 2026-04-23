package net.silvking432.silvkingsmod.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.Difficulty;
import net.silvking432.silvkingsmod.entity.custom.EternalBulletEntity;
import net.silvking432.silvkingsmod.entity.custom.EternalShulkerEntity;

import java.util.EnumSet;

public class ShootEternalBulletGoal extends Goal {
    private final EternalShulkerEntity shulker;
    private int counter;

    public ShootEternalBulletGoal(EternalShulkerEntity shulker) {
        this.shulker = shulker;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity livingEntity = this.shulker.getTarget();
        if (livingEntity != null && livingEntity.isAlive()) {
            return this.shulker.getWorld().getDifficulty() != Difficulty.PEACEFUL;
        }
        return false;
    }

    @Override
    public void start() {
        this.counter = 10;
        this.shulker.setEternalPeekAmount(100);
    }

    @Override
    public void stop() {
        this.shulker.setEternalPeekAmount(0);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (this.shulker.getWorld().getDifficulty() != Difficulty.PEACEFUL) {
            this.counter--;
            LivingEntity livingEntity = this.shulker.getTarget();
            if (livingEntity != null) {
                this.shulker.getLookControl().lookAt(livingEntity, 180.0F, 180.0F);
                double d = this.shulker.squaredDistanceTo(livingEntity);

                if (d < 400.0) {
                    if (this.counter <= 0) {
                        this.counter = 10 + this.shulker.getRandom().nextInt(5) * 10 / 2;

                        this.shulker.getWorld().spawnEntity(new EternalBulletEntity(
                                this.shulker.getWorld(),
                                this.shulker,
                                livingEntity,
                                this.shulker.getAttachedFace().getAxis()
                        ));

                        this.shulker.playSound(
                                SoundEvents.ENTITY_SHULKER_SHOOT,
                                2.0F,
                                (this.shulker.getRandom().nextFloat() - this.shulker.getRandom().nextFloat()) * 0.2F + 1.0F
                        );
                    }
                } else {
                    this.shulker.setTarget(null);
                }
            }
        }
    }
}