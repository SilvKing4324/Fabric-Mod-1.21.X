package net.silvking432.silvkingsmod.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.silvking432.silvkingsmod.entity.custom.NecroJumperEntity;

import java.util.EnumSet;

public class WindChargeMaceGoal extends Goal {
    private final NecroJumperEntity mob;
    private int cooldown = 0;

    public WindChargeMaceGoal(NecroJumperEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Control.MOVE, Control.JUMP));
    }

    @Override
    public boolean canStart() {
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }

        LivingEntity target = this.mob.getTarget();
        return target != null && target.isAlive() && this.mob.isOnGround() && this.mob.distanceTo(target) < 6.0;
    }

    @Override
    public void start() {
        if (this.mob.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    ParticleTypes.GUST,
                    this.mob.getX(),
                    this.mob.getY(),
                    this.mob.getZ(),
                    10,
                    0.5, 0.5, 0.5,
                    1.0
            );
        }
        this.mob.playSound(SoundEvents.ENTITY_WIND_CHARGE_THROW, 1.0F, 1.0F);
        this.mob.playSound(SoundEvents.ENTITY_BREEZE_WIND_BURST.value(), 1.0F, 1.0F);

        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            Vec3d dir = target.getPos().subtract(this.mob.getPos()).normalize().multiply(0.5);
            this.mob.setVelocity(dir.x, 1.5, dir.z);
        } else {
            this.mob.setVelocity(0, 1.5, 0);
        }

        this.mob.velocityDirty = true;
        this.cooldown = 100;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            this.mob.getLookControl().lookAt(target, 30.0F, 30.0F);

            if (!this.mob.isOnGround()) {
                Vec3d moveDir = target.getPos().subtract(this.mob.getPos()).normalize().multiply(0.02);
                this.mob.addVelocity(moveDir.x, 0, moveDir.z);
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public boolean shouldContinue() {
        return !this.mob.isOnGround() && this.mob.getVelocity().y > 0;
    }
}