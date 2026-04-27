package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.entity.ModEntities;
import net.silvking432.silvkingsmod.entity.ai.NecroChickenSpawnGoal;

public class NecroChickenEntity extends HostileEntity {
    private int spawnTicks = 0;

    public NecroChickenEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            LivingEntity target = this.getTarget();
            if (target != null && this.distanceTo(target) <= 16) {
                spawnTicks++;
                if (spawnTicks >= 40) {
                    if (getMinionCount() < 5) {
                        spawnMinion();
                    }
                    spawnTicks = 0;
                }
            } else {
                spawnTicks = 0;
            }
        }
    }

    private int getMinionCount() {
        Box box = this.getBoundingBox().expand(32.0);
        return this.getWorld().getEntitiesByClass(NecroMiniChickenEntity.class, box, entity -> true).size();
    }

    private void spawnMinion() {
        ServerWorld world = (ServerWorld) this.getWorld();
        NecroMiniChickenEntity minion = new NecroMiniChickenEntity(ModEntities.NECRO_MINI_CHICKEN, world);
        minion.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getRandom().nextFloat() * 360, 0);
        this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0f, 0.8f);
        world.spawnEntity(minion);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 70.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ARMOR, 14.0)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 6.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new NecroChickenSpawnGoal(this));
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }
}