package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.entity.ai.NecroPigRamGoal;
import org.jetbrains.annotations.Nullable;

public class NecroPigEntity extends HostileEntity {

    private int ramCooldown = 0;

    public NecroPigEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));

        // Hier binden wir deine neue Goal-Klasse ein
        this.goalSelector.add(1, new NecroPigRamGoal(this));

        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.ramCooldown > 0) {
            this.ramCooldown--;
        }
    }

    public boolean isRamReady() {
        return this.ramCooldown <= 0;
    }

    public void performRamAttack(LivingEntity target) {
        if (this.ramCooldown > 0) return;

        double deltaX = target.getX() - this.getX();
        double deltaZ = target.getZ() - this.getZ();
        float distance = MathHelper.sqrt((float) (deltaX * deltaX + deltaZ * deltaZ));

        if (distance > 0.0f) {
            if (target.blockedByShield(this.getDamageSources().mobAttack(this))) {
                this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);

                this.ramCooldown = 60;
            }
            double dirX = deltaX / distance;
            double dirZ = deltaZ / distance;

            float horizontalPower = 2.2f;
            float verticalPower = 0.15f;

            Vec3d newVelocity = new Vec3d(dirX * horizontalPower, verticalPower, dirZ * horizontalPower);

            target.setVelocity(newVelocity);

            target.velocityModified = true;
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 9));
            if (target instanceof PlayerEntity player) {
                int currentFood = player.getHungerManager().getFoodLevel();
                player.getHungerManager().setFoodLevel(Math.max(0, currentFood - 8));
                player.getHungerManager().setSaturationLevel(0f);
            }

            target.damage(this.getDamageSources().mobAttack(this), 20.0f);
            this.playSound(SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1.0f, 0.5f);

            this.ramCooldown = 80;
        }
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PIG_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PIG_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PIG_DEATH;
    }
}