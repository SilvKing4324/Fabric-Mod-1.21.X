package net.silvking432.silvkingsmod.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.silvking432.silvkingsmod.entity.custom.NecroChickenEntity;

public class NecroChickenSpawnGoal extends Goal {
    private final NecroChickenEntity bigChicken;

    public NecroChickenSpawnGoal(NecroChickenEntity bigChicken) {
        this.bigChicken = bigChicken;
    }

    @Override
    public boolean canStart() {
        return bigChicken.getTarget() != null && bigChicken.distanceTo(bigChicken.getTarget()) < 32.0f;
    }

    @Override
    public void tick() {
        LivingEntity target = bigChicken.getTarget();
        if (target == null) return;

        double distance = bigChicken.distanceTo(target);

        if (distance < 16.0f) {
            bigChicken.getNavigation().stop();
            bigChicken.getLookControl().lookAt(target, 30.0f, 30.0f);
        } else {
            bigChicken.getNavigation().startMovingTo(target, 1.2);
        }
    }
}