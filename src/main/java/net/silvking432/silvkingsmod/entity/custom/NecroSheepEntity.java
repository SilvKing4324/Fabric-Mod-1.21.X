package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NecroSheepEntity extends HostileEntity implements Shearable {
    private static final TrackedData<Byte> COLOR_DATA = DataTracker.registerData(NecroSheepEntity.class, TrackedDataHandlerRegistry.BYTE);
    private int regrowthTimer = 0;

    public NecroSheepEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 18.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(COLOR_DATA, (byte)0);
    }

    public boolean isSheared() { return (this.dataTracker.get(COLOR_DATA) & 16) != 0; }
    public void setSheared(boolean sheared) {
        byte b = this.dataTracker.get(COLOR_DATA);
        this.dataTracker.set(COLOR_DATA, sheared ? (byte)(b | 16) : (byte)(b & -17));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            if (this.isSheared()) {
                regrowthTimer++;
                if (regrowthTimer >= 30) {
                    this.setSheared(false);
                    regrowthTimer = 0;
                }
            }
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.getWorld().isClient) {
            if (!this.isSheared()) {
                this.setSheared(true);
                this.regrowthTimer = 0;
                this.playSound(SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1.0f, 0.8f);
                if (source.getAttacker() instanceof PlayerEntity player) {
                    spawnWebAtPlayer(player);
                }
                return false;
            }

            if (source.getAttacker() instanceof PlayerEntity player) {
                spawnWebAtPlayer(player);
            }
        }
        return super.damage(source, amount);
    }

    private void spawnWebAtPlayer(PlayerEntity player) {
        BlockPos playerPos = player.getBlockPos().up();
        if (this.getWorld().getBlockState(playerPos).isAir()) {
            this.getWorld().setBlockState(playerPos, Blocks.COBWEB.getDefaultState());
        }
    }

    @Override
    protected void initGoals() {
        super.initGoals();

        this.goalSelector.getGoals().clear();
        this.targetSelector.getGoals().clear();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Sheared", this.isSheared());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setSheared(nbt.getBoolean("Sheared"));
    }


    @Override
    public void sheared(SoundCategory category) {

        this.setSheared(true);
        this.getWorld().playSoundFromEntity(null, this, SoundEvents.ENTITY_SHEEP_SHEAR, category, 1.0F, 1.0F);


        if (!this.getWorld().isClient) {
            this.dropItem(net.minecraft.item.Items.BLACK_WOOL);
        }
    }

    @Override
    public boolean isShearable() {
        return false;
    }
}
