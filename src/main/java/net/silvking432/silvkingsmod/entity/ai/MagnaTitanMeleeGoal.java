package net.silvking432.silvkingsmod.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.silvking432.silvkingsmod.entity.custom.MagnaTitanEntity;

public class MagnaTitanMeleeGoal extends MeleeAttackGoal {
    private final MagnaTitanEntity entity;
    private int ticksUntilNextAttack = 0;
    private boolean shouldCountTillNextAttack = false;

    public MagnaTitanMeleeGoal(MagnaTitanEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
        this.entity = mob;
    }

    @Override
    public boolean canStart() {
        return super.canStart() && this.entity.getState() == MagnaTitanEntity.BossState.ATTACKING;
    }

    @Override
    public boolean shouldContinue() {
        return super.shouldContinue() && this.entity.getState() == MagnaTitanEntity.BossState.ATTACKING;
    }

    @Override
    public void start() {
        super.start();
        ticksUntilNextAttack = 0;
    }

    @Override
    protected void attack(LivingEntity pEnemy) {
        if (isEnemyWithinAttackDistance(pEnemy)) {
            shouldCountTillNextAttack = true;

            if (isTimeToAttack()) {
                this.mob.getLookControl().lookAt(pEnemy.getX(), pEnemy.getEyeY(), pEnemy.getZ());
                performMagnaAttack(pEnemy);
            }
        } else {
            shouldCountTillNextAttack = false;
            this.ticksUntilNextAttack = 0;
        }
    }

    private boolean isEnemyWithinAttackDistance(LivingEntity pEnemy) {
        return this.entity.distanceTo(pEnemy) <= 3.0f;
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected void performMagnaAttack(LivingEntity pEnemy) {
        this.ticksUntilNextAttack = 20;

        if (this.mob.isOnGround()) {
            this.mob.addVelocity(0, 0.3, 0);
            this.mob.velocityDirty = true;
        }

        this.mob.swingHand(Hand.MAIN_HAND);
        this.mob.tryAttack(pEnemy);

        double deltaX = this.mob.getX() - pEnemy.getX();
        double deltaZ = this.mob.getZ() - pEnemy.getZ();
        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        if (distance > 0) {
            deltaX /= distance;
            deltaZ /= distance;

            float pushBackStrength = 0.6f;
            Vec3d currentVel = this.mob.getVelocity();
            this.mob.setVelocity(currentVel.x + (deltaX * pushBackStrength), 0.1, currentVel.z + (deltaZ * pushBackStrength));
            this.mob.velocityDirty = true;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (shouldCountTillNextAttack) {
            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        }
    }

    @Override
    public void stop() {
        super.stop();
    }
}