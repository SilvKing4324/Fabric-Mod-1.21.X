package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.effect.ModEffects;
import net.silvking432.silvkingsmod.item.ModItems;

import java.util.ArrayList;
import java.util.List;

public class NecroPiglinEntity extends PiglinEntity {

    public NecroPiglinEntity(EntityType<? extends PiglinEntity> entityType, World world) {
        super(entityType, world);
        this.setImmuneToZombification(true);
        this.experiencePoints = 20;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0)
                .add(EntityAttributes.GENERIC_ARMOR, 8.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.2, false) {
            @Override
            protected void attack(LivingEntity pEnemy) {
                if (isEnemyWithinAttackDistance(pEnemy)) {
                    this.mob.getLookControl().lookAt(pEnemy.getX(), pEnemy.getEyeY(), pEnemy.getZ());
                    this.mob.swingHand(Hand.MAIN_HAND);
                    this.mob.tryAttack(pEnemy);
                }
            }

            private boolean isEnemyWithinAttackDistance(LivingEntity pEnemy) {
                return this.mob.distanceTo(pEnemy) <= 3f;
            }
        });
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }


    @Override
    public boolean canGather(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public boolean shouldZombify() {
        return false;
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean success = super.tryAttack(target);

        if (success && target instanceof LivingEntity livingTarget) {
            applyRandomNegativeEffect(livingTarget);
        }

        return success;
    }

    @Override
    protected void initEquipment(net.minecraft.util.math.random.Random random, LocalDifficulty localDifficulty) {
        ItemStack sword = new ItemStack(Items.NETHERITE_SWORD);
        this.equipStack(EquipmentSlot.MAINHAND, sword);

        this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0f);
    }

    @Override
    protected void dropEquipment(ServerWorld world, DamageSource source, boolean causedByPlayer) {
        ItemStack mainHand = this.getEquippedStack(EquipmentSlot.MAINHAND);
        if (mainHand.isOf(ModItems.TITANIUM_HOE)) {
            this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }

        super.dropEquipment(world, source, causedByPlayer);
    }

    @Override
    public boolean isAdult() {
        return true;
    }

    private void applyRandomNegativeEffect(LivingEntity target) {
        List<RegistryEntry<StatusEffect>> negativeEffects = new ArrayList<>();
        negativeEffects.add(StatusEffects.WITHER);
        negativeEffects.add(StatusEffects.POISON);
        negativeEffects.add(StatusEffects.DARKNESS);
        negativeEffects.add(StatusEffects.INFESTED);
        negativeEffects.add(StatusEffects.HUNGER);
        negativeEffects.add(StatusEffects.SLOWNESS);
        negativeEffects.add(StatusEffects.WEAKNESS);
        negativeEffects.add(StatusEffects.NAUSEA);
        negativeEffects.add(StatusEffects.BLINDNESS);
        negativeEffects.add(StatusEffects.MINING_FATIGUE);
        negativeEffects.add(ModEffects.VULNERABILITY);
        negativeEffects.add(ModEffects.ANXIETY);

        RegistryEntry<StatusEffect> randomEffect = negativeEffects.get(this.random.nextInt(negativeEffects.size()));

        target.addStatusEffect(new StatusEffectInstance(randomEffect, 160, 1));
    }
}