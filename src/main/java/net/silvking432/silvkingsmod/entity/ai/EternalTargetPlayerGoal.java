package net.silvking432.silvkingsmod.entity.ai;

import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Difficulty;
import net.silvking432.silvkingsmod.entity.custom.EternalShulkerEntity;

public class EternalTargetPlayerGoal extends ActiveTargetGoal<PlayerEntity> {
    private final EternalShulkerEntity shulker;

    public EternalTargetPlayerGoal(EternalShulkerEntity shulker) {
        // 'true' steht für 'checkVisibility' (Sichtlinie prüfen)
        super(shulker, PlayerEntity.class, true);
        this.shulker = shulker;
    }

    @Override
    public boolean canStart() {
        // Greift nicht an, wenn der Schwierigkeitsgrad auf Friedlich steht
        if (this.shulker.getWorld().getDifficulty() == Difficulty.PEACEFUL) {
            return false;
        }
        return super.canStart();
    }

    @Override
    protected Box getSearchBox(double distance) {
        Direction direction = this.shulker.getAttachedFace();

        if (direction.getAxis() == Direction.Axis.X) {
            return this.shulker.getBoundingBox().expand(4.0, distance, distance);
        } else {
            return direction.getAxis() == Direction.Axis.Z
                    ? this.shulker.getBoundingBox().expand(distance, distance, 4.0)
                    : this.shulker.getBoundingBox().expand(distance, 4.0, distance);
        }
    }
}