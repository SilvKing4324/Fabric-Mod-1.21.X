package net.silvking432.silvkingsmod.entity.custom;


import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.entity.ModEntities;
import net.silvking432.silvkingsmod.item.ModItems;
import org.jetbrains.annotations.Nullable;

public class GeckoEntity extends AnimalEntity {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState swimAnimationState = new AnimationState();
    public final AnimationState sleepAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public GeckoEntity(EntityType<? extends AnimalEntity> entityType, World level) {
        super(entityType, level);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));

        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.0));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(3, new TemptGoal(this, 1.25, stack -> stack.isOf(ModItems.SUPER_FLOWER), false));

        this.goalSelector.add(4, new FollowParentGoal(this, 1.25));

        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return AnimalEntity.createLivingAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.isOf(ModItems.SUPER_FLOWER);
    }


    @Override
    public @Nullable PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return ModEntities.GECKO.create(world);
    }

    private void setupAnimationStates() {
        if (this.getWorld().isClient) {
            if (this.isSwimming()) {
                this.idleAnimationState.stop();
                this.swimAnimationState.startIfNotRunning(this.age);
            }
            else if (this.isSleeping()) {
                this.idleAnimationState.stop();
                this.swimAnimationState.stop();
                this.sleepAnimationState.startIfNotRunning(this.age);
            }
            else {
                this.swimAnimationState.stop();
                this.sleepAnimationState.stop();

                if (this.idleAnimationTimeout <= 0) {
                    this.idleAnimationTimeout = 80; // 4 Sekunden (20 Ticks * 4)
                    this.idleAnimationState.start(this.age);
                } else {
                    --this.idleAnimationTimeout;
                }
            }
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) {
            this.setupAnimationStates();
        }
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_FOX_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_FOX_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_FOX_DEATH;
    }
}
