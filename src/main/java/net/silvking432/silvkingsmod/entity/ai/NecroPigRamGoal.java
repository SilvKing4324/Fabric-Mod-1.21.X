package net.silvking432.silvkingsmod.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.silvking432.silvkingsmod.entity.custom.NecroPigEntity;

import java.util.EnumSet;

public class NecroPigRamGoal extends Goal {
    private final NecroPigEntity pig;
    private LivingEntity target;

    public NecroPigRamGoal(NecroPigEntity pig) {
        this.pig = pig;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        this.target = this.pig.getTarget();
        return this.target != null &&
                this.pig.isRamReady() &&
                this.pig.distanceTo(this.target) < 12.0;
    }

    @Override
    public void start() {
        this.pig.getNavigation().startMovingTo(this.target, 1.6);
    }

    @Override
    public void tick() {
        if (this.target == null) return;

        this.pig.getLookControl().lookAt(this.target, 30.0F, 30.0F);
        this.pig.getNavigation().startMovingTo(this.target, 1.6);

        if (this.pig.distanceTo(this.target) < 1.8) {
            this.pig.performRamAttack(this.target);
        }
    }

    @Override
    public boolean shouldContinue() {
        return this.target != null && this.target.isAlive() && this.pig.isRamReady();
    }
}