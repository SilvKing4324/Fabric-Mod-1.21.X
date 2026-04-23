package net.silvking432.silvkingsmod.entity.ai;

import net.minecraft.entity.ai.goal.Goal;
import net.silvking432.silvkingsmod.entity.custom.EternalShulkerEntity;

public class EternalPeekGoal extends Goal {
    private final EternalShulkerEntity shulker;
    private int counter;

    public EternalPeekGoal(EternalShulkerEntity shulker) {
        this.shulker = shulker;
    }

    @Override
    public boolean canStart() {

        return this.shulker.getTarget() == null
                && this.shulker.getRandom().nextInt(toGoalTicks(40)) == 0
                && this.shulker.canEternallyStay(this.shulker.getBlockPos(), this.shulker.getAttachedFace());
    }

    @Override
    public boolean shouldContinue() {
        return this.shulker.getTarget() == null && this.counter > 0;
    }

    @Override
    public void start() {
        this.counter = this.getTickCount(20 * (1 + this.shulker.getRandom().nextInt(3)));
        this.shulker.setEternalPeekAmount(30);
    }

    @Override
    public void stop() {
        if (this.shulker.getTarget() == null) {
            this.shulker.setEternalPeekAmount(0);
        }
    }

    @Override
    public void tick() {
        this.counter--;
    }
}