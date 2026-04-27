package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.effect.ModEffects;

public class NecroBeeEntity extends BeeEntity {

    public NecroBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.5)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 50.0);
    }

    @Override
    public boolean tryAttack(net.minecraft.entity.Entity target) {
        boolean success = super.tryAttack(target);

        if (success && target instanceof LivingEntity livingTarget) {
            livingTarget.addStatusEffect(new StatusEffectInstance(ModEffects.ANXIETY, 2400, 0));
            livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 2400, 1));
            livingTarget.addStatusEffect(new StatusEffectInstance(ModEffects.VULNERABILITY, 6000, 2));
            livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 2400, 0));
        }

        return success;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            this.setAngerTime(100);
        }
    }

    @Override
    public boolean hasHive() {
        return false;
    }

    @Override
    protected void initGoals() {
        super.initGoals();

        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }
}