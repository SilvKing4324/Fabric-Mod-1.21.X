package net.silvking432.silvkingsmod.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.silvking432.silvkingsmod.entity.custom.TurretEntity;

public class DoubleLaserAttackGoal extends Goal {
    private final TurretEntity turret;
    private int cooldown = 0;

    public DoubleLaserAttackGoal(TurretEntity turret) {
        this.turret = turret;
    }

    @Override
    public boolean canStart() {
        return this.turret.getTarget() != null && this.turret.getTarget().isAlive();
    }

    @Override
    public void tick() {
        LivingEntity target = this.turret.getTarget();
        if (target == null) return;

        this.turret.getLookControl().lookAt(target, 30.0F, 30.0F);

        if (cooldown > 0) {
            cooldown--;
        } else {
            if (canSeePrecise(target)) {
                this.turret.shootLaserAt(target);
                this.cooldown = 15;
            }
        }
    }

    private boolean canSeePrecise(LivingEntity target) {
        return true;
        // return this.turret.canSee(target);
    }
}