package net.silvking432.silvkingsmod.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.BlockPos;

public class MobScalingUtil {

    public static void applyScaling(LivingEntity entity, BlockPos pos) {
        if (entity.getCommandTags().contains("is_scaled")) {
            return;
        }

        double distance = Math.sqrt(pos.getSquaredDistance(0, 0, 0));
        double dist = distance / 200.0;

        double hpMultiplier = 0.8 + (dist * 0.2);
        double dmgMultiplier = 0.9 + (dist * 0.1);

        EntityAttributeInstance healthAttr = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if (healthAttr != null) {
            double newMax = healthAttr.getBaseValue() * hpMultiplier;
            healthAttr.setBaseValue(newMax);
            entity.setHealth((float) newMax);
        }

        EntityAttributeInstance dmgAttr = entity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (dmgAttr != null) {
            dmgAttr.setBaseValue(dmgAttr.getBaseValue() * dmgMultiplier);
        }

        entity.addCommandTag("is_scaled");
    }
}