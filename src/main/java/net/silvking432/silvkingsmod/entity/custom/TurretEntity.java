package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.entity.ai.DoubleLaserAttackGoal;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TurretEntity extends MobEntity {
    public static final TrackedData<ItemStack> BLOCK_STATE_STACK = DataTracker.registerData(TurretEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    public TurretEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
        this.setNoGravity(true);
        this.setPersistent();
        this.intersectionChecked = true;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0)
                .add(EntityAttributes.GENERIC_ARMOR, 8.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 60.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 60.0F));
        this.goalSelector.add(2, new DoubleLaserAttackGoal(this));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public double getEyeY() {
        return this.getY() + 1.5f;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(BLOCK_STATE_STACK, new ItemStack(Items.BEDROCK));
    }

    @Override
    public void tick() {
        super.tick();
        double centerX = Math.floor(this.getX()) + 0.5;
        double centerZ = Math.floor(this.getZ()) + 0.5;
        if (this.getX() != centerX || this.getZ() != centerZ) {
            this.updatePosition(centerX, this.getY(), centerZ);
        }
    }

    @Override
    public boolean isPushable() { return false; }

    @Override
    public void takeKnockback(double strength, double x, double z) {

    }

    @Override
    public boolean isCollidable() {
        return this.isAlive();
    }

    @Override
    public Iterable<ItemStack> getArmorItems() { return List.of(); }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) { return ItemStack.EMPTY; }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {}

    @Override
    public Arm getMainArm() { return Arm.RIGHT; }

    @Override
    public void pushAwayFrom(Entity entity) {}

    @Override
    public Box getVisibilityBoundingBox() {
        return this.getBoundingBox();
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("TurretBlock", this.dataTracker.get(BLOCK_STATE_STACK).encode(this.getRegistryManager()));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("TurretBlock", 10)) {
            ItemStack stack = ItemStack.fromNbt(this.getRegistryManager(), nbt.getCompound("TurretBlock")).orElse(new ItemStack(Items.BEDROCK));
            this.dataTracker.set(BLOCK_STATE_STACK, stack);
        }
    }

    public void shootLaserAt(LivingEntity target) {
        Vec3d targetPos = target.getEyePos();
        Vec3d origin = this.getPos().add(0, 1.5, 0);
        Vec3d direction = targetPos.subtract(origin).normalize();

        for (int i = -1; i <= 1; i += 2) {
            Vec3d sideOffset = new Vec3d(-direction.z, 0, direction.x).multiply(i * 0.3);
            Vec3d spawnPos = origin.add(sideOffset);

            MagnaFireballEntity laser = new MagnaFireballEntity(this.getWorld(), this, direction);
            laser.setPosition(spawnPos.x, spawnPos.y, spawnPos.z);
            ItemStack fireballStack = new ItemStack(Items.FIRE_CHARGE);
            fireballStack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(1));
            laser.setItem(fireballStack);

            this.getWorld().spawnEntity(laser);
        }
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_GLASS_BREAK;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_GENERIC_EXPLODE.value();
    }
}