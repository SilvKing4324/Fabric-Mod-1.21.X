package net.silvking432.silvkingsmod.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.util.math.Vec3d;
import net.silvking432.silvkingsmod.entity.custom.NecroCowEntity;

public class NecroCowAttackGoal extends MeleeAttackGoal {
    private final NecroCowEntity cow;

    public NecroCowAttackGoal(NecroCowEntity cow, double speed, boolean pauseWhenMobIdle) {
        super(cow, speed, pauseWhenMobIdle);
        this.cow = cow;
    }

    @Override
    protected void attack(LivingEntity target) {
        int ticksBefore = this.getCooldown();

        super.attack(target);

        if (ticksBefore <= 0 && this.getCooldown() > 0) {

            Vec3d awayDir = new Vec3d(this.cow.getX() - target.getX(), 0, this.cow.getZ() - target.getZ()).normalize();

            if (Double.isNaN(awayDir.x)) {
                awayDir = new Vec3d(1, 0, 0);
            }

            this.cow.setVelocity(awayDir.x * 2.2, 0.4, awayDir.z * 2.2);
            this.cow.velocityModified = true;
            this.resetCooldown();
        }
    }
}