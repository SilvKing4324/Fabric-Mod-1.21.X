package net.silvking432.silvkingsmod.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.silvking432.silvkingsmod.entity.custom.MagnaTitanEntity;

import java.util.EnumSet;

public class MagnaTitanJumpAttackGoal extends Goal {
    private final MagnaTitanEntity mob;
    private LivingEntity target;

    public MagnaTitanJumpAttackGoal(MagnaTitanEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        this.target = mob.getTarget();
        return target != null && mob.getState() == MagnaTitanEntity.BossState.ATTACKING
                && mob.distanceTo(target) > 2 && mob.distanceTo(target) < 10;
    }

    @Override
    public void start() {
        Vec3d dir = target.getPos().subtract(mob.getPos()).normalize();
        mob.setVelocity(dir.x * 0.8, 0.5, dir.z * 0.8);
        mob.velocityDirty = true;
    }

    @Override
    public void tick() {
        if (target != null && mob.distanceTo(target) <= 3.0f) {
            mob.swingHand(Hand.MAIN_HAND);
            mob.tryAttack(target);

            // Optional: Ein kleiner Sound oder Partikel hier einfügen für den "Crit"
        }
    }

    @Override
    public boolean shouldContinue() {
        // Goal läuft, bis er wieder den Boden berührt
        return !mob.isOnGround();
    }
}