package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.entity.ai.WindChargeMaceGoal;
import net.silvking432.silvkingsmod.item.ModItems;
import org.joml.Vector3f;

public class NecroJumperEntity extends HostileEntity {

    public NecroJumperEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        var armorAttribute = this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
        var toughnessAttribute = this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
        if (armorAttribute != null && toughnessAttribute != null) {
            armorAttribute.setBaseValue(18.0);
            toughnessAttribute.setBaseValue(10.0);
        }
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(1, new WindChargeMaceGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.2, false));

        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 10.0)
                .add(EntityAttributes.GENERIC_ARMOR, 18.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 25.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35);
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (!(target instanceof LivingEntity livingTarget)) return super.tryAttack(target);

        float baseDamage = (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        float maceBonus = 0;
        boolean isSmash = this.fallDistance > 1.5F && !this.isFallFlying();

        if (isSmash) {
            float h = this.fallDistance;
            if (h <= 3.0F) maceBonus = 4.0F * h;
            else if (h <= 8.0F) maceBonus = 12.0F + 2.0F * (h - 3.0F);
            else maceBonus = 22.0F + h - 8.0F;
        }

        boolean success = target.damage(this.getDamageSources().mobAttack(this), baseDamage + maceBonus);

        if (success) {
            if (!isSmash && this.getMainHandStack().isOf(ModItems.TITANIUM_SWORD)) {

                livingTarget.setOnFireFor(20);

                double kResistance = livingTarget.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);
                double kStrength = 5.0 * (1.3 - kResistance);

                if (kStrength > 0) {
                    livingTarget.takeKnockback(kStrength,
                            Math.sin(this.getYaw() * (float) (Math.PI / 180.0)),
                            -Math.cos(this.getYaw() * (float) (Math.PI / 180.0)));
                }
            }

            this.onAttacking(target);
            if (isSmash) {
                double diffX = target.getX() - this.getX();
                double diffZ = target.getZ() - this.getZ();

                double distance = Math.sqrt(diffX * diffX + diffZ * diffZ);
                double pushX = (diffX / distance) * 0.3;
                double pushZ = (diffZ / distance) * 0.3;

                double randomY = 0.8 + (this.random.nextDouble() * 0.6);

                this.setVelocity(pushX, randomY, pushZ);
                this.velocityDirty = true;

                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ITEM_MACE_SMASH_GROUND, this.getSoundCategory(), 1.0F, 1.0F);
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST, this.getSoundCategory(), 1.0F, 1.0F);

                if (this.getWorld() instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.GUST_EMITTER_LARGE,
                            target.getX(), target.getY() + 3, target.getZ(), 2, 1.2, 0.5, 1.2, 1);
                    serverWorld.spawnParticles(new DustParticleEffect(new Vector3f(1.0f, 0.0f, 0.0f), 1.0f),
                            target.getX(), target.getY() + 1.0, target.getZ(), 20, 0.5, 0.5, 0.5,0.1);
                }

                this.onLanding();
            }

            this.swingHand(Hand.MAIN_HAND);
        }

        return success;
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.MACE));
        this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0f);
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            LivingEntity target = this.getTarget();

            if (this.fallDistance > 0.5F) {
                if (!this.getMainHandStack().isOf(Items.MACE)) {
                    this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.MACE));
                }

                if (target != null) {
                    this.getLookControl().lookAt(target, 30.0F, 30.0F);

                    double distanceSq = this.squaredDistanceTo(target);
                    if (distanceSq <= 25.0) {
                        this.tryAttack(target);
                    }
                }
            } else {
                if (!this.getMainHandStack().isOf(ModItems.TITANIUM_SWORD)) {
                    this.equipStack(EquipmentSlot.MAINHAND, createTitanSword());
                    this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0f);
                }
            }
        }
    }
    private ItemStack createTitanSword() {
        ItemStack sword = new ItemStack(ModItems.TITANIUM_SWORD);
        var lookup = this.getWorld().getRegistryManager().getWrapperOrThrow(RegistryKeys.ENCHANTMENT);

        sword.addEnchantment(lookup.getOrThrow(Enchantments.SHARPNESS), 5);
        sword.addEnchantment(lookup.getOrThrow(Enchantments.KNOCKBACK), 4);
        sword.addEnchantment(lookup.getOrThrow(Enchantments.FIRE_ASPECT), 4);

        return sword;
    }
}