package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.silvking432.silvkingsmod.entity.ModEntities;
import net.silvking432.silvkingsmod.entity.ai.EternalPeekGoal;
import net.silvking432.silvkingsmod.entity.ai.EternalTargetPlayerGoal;
import net.silvking432.silvkingsmod.entity.ai.ShootEternalBulletGoal;

import java.util.ArrayList;
import java.util.List;

public class EternalShulkerEntity extends ShulkerEntity {
    private final List<BlockPos> activeLightPositions = new ArrayList<>();
    private BlockPos lastAuraCenter;
    private static final Identifier COVERED_ARMOR_MODIFIER_ID = Identifier.ofVanilla("covered");
    private static final EntityAttributeModifier COVERED_ARMOR_BONUS = new EntityAttributeModifier(
            COVERED_ARMOR_MODIFIER_ID, 20.0, EntityAttributeModifier.Operation.ADD_VALUE
    );

    public EternalShulkerEntity(EntityType<? extends ShulkerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F, 0.02F, true));
        this.goalSelector.add(4, new ShootEternalBulletGoal(this));
        this.goalSelector.add(7, new EternalPeekGoal( this));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new RevengeGoal(this, this.getClass()).setGroupRevenge());
        this.targetSelector.add(2, new EternalTargetPlayerGoal(this));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return ShulkerEntity.createShulkerAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 60.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0);
    }

    private boolean isClosed() {
        return this.getPeekAmount() == 0;
    }

    private int getPeekAmount() {
        return this.dataTracker.get(PEEK_AMOUNT);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isClosed()) {
            Entity entity = source.getSource();
            if (entity instanceof PersistentProjectileEntity) {
                return false;
            }
        }

        if (!super.damage(source, amount)) {
            return false;
        } else {
            if (this.getHealth() < this.getMaxHealth() * 0.5 && this.random.nextInt(4) == 0) {
                this.tryTeleport();
            } else if (source.isIn(DamageTypeTags.IS_PROJECTILE)) {
                Entity entity = source.getSource();
                if (entity != null && entity.getType() == EntityType.SHULKER_BULLET) {
                    this.spawnNewShulker();
                }
            }

            return true;
        }
    }

    public void setEternalPeekAmount(int peekAmount) {
        if (!this.getWorld().isClient) {
            this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).removeModifier(COVERED_ARMOR_MODIFIER_ID);
            if (peekAmount == 0) {
                this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).addPersistentModifier(COVERED_ARMOR_BONUS);
                this.playSound(SoundEvents.ENTITY_SHULKER_CLOSE, 1.0F, 1.0F);
                this.emitGameEvent(GameEvent.CONTAINER_CLOSE);
            } else {
                this.playSound(SoundEvents.ENTITY_SHULKER_OPEN, 1.0F, 1.0F);
                this.emitGameEvent(GameEvent.CONTAINER_OPEN);
            }
        }

        this.dataTracker.set(PEEK_AMOUNT, (byte)peekAmount);
    }

    private boolean isInvalidPosition(BlockPos pos) {
        BlockState blockState = this.getWorld().getBlockState(pos);
        if (blockState.isAir()) {
            return false;
        } else {
            boolean bl = blockState.isOf(Blocks.MOVING_PISTON) && pos.equals(this.getBlockPos());
            return !bl;
        }
    }

    public boolean canEternallyStay(BlockPos pos, Direction direction) {
        if (this.isInvalidPosition(pos)) {
            return false;
        } else {
            Direction direction2 = direction.getOpposite();
            if (!this.getWorld().isDirectionSolid(pos.offset(direction), this, direction2)) {
                return false;
            } else {
                Box box = calculateBoundingBox(this.getScale(), direction2, 1.0F).offset(pos).contract(1.0E-6);
                return this.getWorld().isSpaceEmpty(this, box);
            }
        }
    }

    private void spawnNewShulker() {
        Vec3d vec3d = this.getPos();
        Box box = this.getBoundingBox();
        if (!this.isClosed() && this.tryTeleport()) {
            int i = this.getWorld().getEntitiesByType(ModEntities.ETERNAL_SHULKER, box.expand(8.0), Entity::isAlive).size();
            float f = (i - 1) / 5.0F;
            if (!(this.getWorld().random.nextFloat() < f)) {
                EternalShulkerEntity eternalShulkerEntity = ModEntities.ETERNAL_SHULKER.create(this.getWorld());
                if (eternalShulkerEntity != null) {
                    eternalShulkerEntity.setVariant(this.getVariant());
                    eternalShulkerEntity.refreshPositionAfterTeleport(vec3d);
                    this.getWorld().spawnEntity(eternalShulkerEntity);
                }
            }
        }
    }

    private void spawnLightAura() {
        if (this.getWorld().isClient) return;

        BlockPos center = this.getBlockPos();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos targetPos = center.add(x, y, z);

                    if (targetPos.equals(center)) continue;

                    if (this.getWorld().getBlockState(targetPos).isAir()) {
                        this.getWorld().setBlockState(targetPos, Blocks.LIGHT.getDefaultState()
                                .with(LightBlock.LEVEL_15, 14));
                        activeLightPositions.add(targetPos.toImmutable());
                    }
                }
            }
        }
        this.lastAuraCenter = center.toImmutable();
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isClient && this.age % 20 == 0) {
            BlockPos currentPos = this.getBlockPos();

            if (lastAuraCenter == null || !lastAuraCenter.equals(currentPos)) {
                clearLightAura();
                spawnLightAura();
                this.lastAuraCenter = currentPos.toImmutable();
            }
        }
    }

    @Override
    protected boolean tryTeleport() {
        clearLightAura();
        boolean success = super.tryTeleport();
        if (success) {
            spawnLightAura();
        }
        return success;
    }

    @Override
    public void remove(RemovalReason reason) {
        clearLightAura();
        super.remove(reason);
    }

    private void clearLightAura() {
        if (this.getWorld().isClient) return;

        for (BlockPos pos : activeLightPositions) {
            if (this.getWorld().getBlockState(pos).isOf(Blocks.LIGHT)) {
                this.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
        activeLightPositions.clear();
        this.lastAuraCenter = null;
    }

}
