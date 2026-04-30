package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.entity.ModEntities;

public class NecroLlamaEntity extends HostileEntity {

    private boolean hasSpawnedTornado = false;
    private int tornadoSpawnTimer = -1;
    private Vec3d tornadoTargetPos = null;

    public NecroLlamaEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(EntityAttributes.GENERIC_ARMOR, 5.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 20.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.7);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient) {
            if (!hasSpawnedTornado && this.getHealth() < this.getMaxHealth() * 0.7) {
                prepareTornado();
                hasSpawnedTornado = true;
            }

            if (tornadoSpawnTimer > 0) {
                spawnMagicBeamParticles();
                tornadoSpawnTimer--;

                if (tornadoSpawnTimer == 0) {
                    finalizeTornadoSpawn();
                }
            }
        }
    }

    private void prepareTornado() {
        for (int i = 0; i < 10; i++) {
            double rx = this.getX() + (this.random.nextDouble() - 0.5) * 16;
            double rz = this.getZ() + (this.random.nextDouble() - 0.5) * 16;
            double ry = this.getY() + 1;

            BlockPos targetPos = BlockPos.ofFloored(rx, ry, rz);

            if (this.getWorld().getBlockState(targetPos).isAir()) {
                this.tornadoTargetPos = new Vec3d(rx, ry, rz);
                this.tornadoSpawnTimer = 40;
                this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON, this.getSoundCategory(), 1.0f, 0.5f);
                break;
            }
        }
    }

    private void spawnMagicBeamParticles() {
        if (tornadoTargetPos == null) return;

        Vec3d startPos = this.getEyePos();
        Vec3d direction = tornadoTargetPos.subtract(startPos);
        double steps = 10;

        for (int i = 0; i < steps; i++) {
            double progress = (double) i / steps;
            double px = startPos.x + direction.x * progress;
            double py = startPos.y + direction.y * progress;
            double pz = startPos.z + direction.z * progress;

            ((ServerWorld) this.getWorld()).spawnParticles(
                    ParticleTypes.WITCH, px, py, pz, 1, 0, 0, 0, 0.01
            );
        }
    }

    private void finalizeTornadoSpawn() {
        if (tornadoTargetPos != null) {
            TornadoEntity tornado = new TornadoEntity(ModEntities.TORNADO, this.getWorld());
            tornado.refreshPositionAndAngles(tornadoTargetPos.x, tornadoTargetPos.y, tornadoTargetPos.z, 0, 0);
            this.getWorld().spawnEntity(tornado);

            this.getWorld().playSound(null, tornadoTargetPos.x, tornadoTargetPos.y, tornadoTargetPos.z, SoundEvents.ENTITY_GENERIC_EXPLODE, this.getSoundCategory(), 1.0f, 1.2f);
            tornadoTargetPos = null;
        }
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }
}