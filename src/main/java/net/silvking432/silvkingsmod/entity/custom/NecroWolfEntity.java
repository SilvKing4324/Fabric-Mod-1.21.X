package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.entity.ai.NecroWolfStealGoal;

public class NecroWolfEntity extends HostileEntity {
    private int itemStealTimer = 0;
    private boolean hasStolenItem = false;
    private boolean successfullyStolenOnce = false;

    public NecroWolfEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setCanPickUpLoot(false);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 16.0)
                .add(EntityAttributes.GENERIC_ARMOR, 4.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new NecroWolfStealGoal(this));
        this.goalSelector.add(3, new FleeEntityGoal<>(this, PlayerEntity.class, 16.0f, 1.0, 1.0) {
            @Override
            public boolean canStart() {
                return hasStolenItem && super.canStart();
            }
        });
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        if (!this.getWorld().isClient) {
            ItemStack inMaul = this.getEquippedStack(EquipmentSlot.MAINHAND);
            if (hasStolenItem && inMaul.isEmpty()) {
                this.hasStolenItem = false;
                this.itemStealTimer = 0;
            }

            if (hasStolenItem) {
                itemStealTimer++;
                if (itemStealTimer > 600 || this.getHealth() < this.getMaxHealth() * 0.10f) {
                    dropStolenItem();
                }
            }
        }
    }

    public void dropStolenItem() {
        ItemStack stack = this.getEquippedStack(EquipmentSlot.MAINHAND);
        if (!stack.isEmpty()) {
            this.dropStack(stack);
            this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        this.hasStolenItem = false;
        this.itemStealTimer = 0;
    }

    @Override
    protected SoundEvent getAmbientSound() { return SoundEvents.ENTITY_WOLF_GROWL; }
    @Override
    protected SoundEvent getHurtSound(net.minecraft.entity.damage.DamageSource source) { return SoundEvents.ENTITY_WOLF_HURT; }
    @Override
    protected SoundEvent getDeathSound() { return SoundEvents.ENTITY_WOLF_DEATH; }

    public boolean hasNotStolenItem() { return !hasStolenItem; }

    public boolean canSteal() {
        return !hasStolenItem && !successfullyStolenOnce;
    }

    @Override
    protected void loot(ItemEntity item) {
    }

    @Override
    public boolean canEquip(ItemStack stack) {
        return hasStolenItem || super.canEquip(stack);
    }

    public void setHasStolenItem(boolean value) {
        this.hasStolenItem = value;
        if (value) {
            this.successfullyStolenOnce = true;
            this.itemStealTimer = 0;
        }
    }
}