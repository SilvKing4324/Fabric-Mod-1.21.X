package net.silvking432.silvkingsmod.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class GravityPullHandler {

    public enum PullType {
        LINEAR,
        QUADRATIC,
        EXPONENTIAL,
        CONSTANT
    }

    public void pullTowardsMiddle(BlockPos pos, double pullStrength, LivingEntity target) {
        if (target == null || pos == null) return;

        double targetX = pos.getX() + 0.5;
        double targetY = pos.getY() + 0.5;
        double targetZ = pos.getZ() + 0.5;

        double diffX = targetX - target.getX();
        double diffY = targetY - target.getY();
        double diffZ = targetZ - target.getZ();

        double distance = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);

        if (distance > 0.1) {
            diffX /= distance;
            diffY /= distance;
            diffZ /= distance;

            double strength = pullStrength / 100.0;

            Vec3d currentVelocity = target.getVelocity();
            target.setVelocity(
                    currentVelocity.x + (diffX * strength),
                    currentVelocity.y + (diffY * strength),
                    currentVelocity.z + (diffZ * strength)
            );

            target.velocityModified = true;
        }
    }

    public double calculatePullStrength(BlockPos center, LivingEntity target, double maxStrength, double radius, PullType type) {
        double distance = Math.sqrt(target.squaredDistanceTo(center.toCenterPos()));

        if (distance > radius) {
            return 0.0;
        }

        double baseFactor = 1.0 - (distance / radius);

        double finalFactor = switch (type) {
            case QUADRATIC -> Math.pow(baseFactor, 2);
            case EXPONENTIAL -> (Math.exp(baseFactor * 3) - 1) / (Math.exp(3) - 1);
            case CONSTANT -> 1.0;
            default -> baseFactor;
        };

        return finalFactor * maxStrength;
    }

    public void applyGravity(BlockPos center, LivingEntity target, double maxStrength, double radius, PullType type) {
        double dynamicStrength = calculatePullStrength(center, target, maxStrength, radius, type);
        if (dynamicStrength > 0) {
            pullTowardsMiddle(center, dynamicStrength, target);
        }
    }
}
