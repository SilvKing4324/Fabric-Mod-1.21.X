package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.World;

public class MagnaWitchEntity extends WitchEntity implements RangedAttackMob {

    public MagnaWitchEntity(EntityType<? extends WitchEntity> entityType, World world) {
        super(entityType, world);
        this.setPersistent();
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return WitchEntity.createWitchAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0) // 25 Herzen
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.28)
                .add(EntityAttributes.GENERIC_ARMOR, 4.0);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.getGoals().clear();
        this.targetSelector.getGoals().clear();
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new ProjectileAttackGoal(this, 1.0, 60, 10.0F));
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));

        this.targetSelector.add(1, new RevengeGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public boolean isFireImmune() {
        return true; // Passend zum Lava/Magna-Thema
    }

    @Override
    public boolean canJoinRaid() {
        return false;
    }

    @Override
    public void tickMovement() {

        if (this.getWorld().isClient && this.random.nextInt(5) == 0) {
            this.getWorld().addParticle(ParticleTypes.LARGE_SMOKE,
                    this.getX(), this.getY() + 1.5, this.getZ(), 0, 0, 0);
        }

        super.tickMovement();
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        if (!this.getWorld().isClient) {
            var bosses = this.getWorld().getEntitiesByClass(MagnaTitanEntity.class,
                    this.getBoundingBox().expand(70.0), entity -> true);

            for (MagnaTitanEntity boss : bosses) {
                boss.decrementMinionCount();
            }
        }
    }

    @Override
    public void setRaid(Raid raid) {
    }

    @Override
    public void setAbleToJoinRaid(boolean ableToJoinRaid) {

    }

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        if (this.isDrinking()) return;

        double d = target.getX() - this.getX();
        double e = target.getEyeY() - 1.1F - this.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);

        PotionEntity potionEntity = new PotionEntity(this.getWorld(), this);

        ItemStack potionStack = new ItemStack(Items.LINGERING_POTION);

        potionStack.set(net.minecraft.component.DataComponentTypes.POTION_CONTENTS,
                new PotionContentsComponent(Potions.AWKWARD));

        potionEntity.setItem(potionStack);

        potionEntity.setPitch(potionEntity.getPitch() - 20.0F);
        potionEntity.setVelocity(d, e + g * 0.2, f, 0.75F, 8.0F);

        this.getWorld().spawnEntity(potionEntity);

        if (!this.getWorld().isClient) {
            createLingeringMagnaCloud(target.getBlockPos());
        }
    }


    private void createLingeringMagnaCloud(BlockPos pos) {
        if (this.getWorld().isClient) return;

        // Wir erstellen eine Wolke, die 3 Sekunden (60 Ticks) hält
        AreaEffectCloudEntity cloud = new AreaEffectCloudEntity(this.getWorld(), pos.getX(), pos.getY(), pos.getZ());
        cloud.setRadius(3.0F);
        cloud.setRadiusOnUse(-0.5F);
        cloud.setWaitTime(10);
        cloud.setDuration(60);
        cloud.setOwner(this);
        cloud.setParticleType(ParticleTypes.FLAME);

        this.getWorld().spawnEntity(cloud);

    }

    @Override
    protected void mobTick() {
        super.mobTick();

        if (this.age % 10 == 0 && !this.getWorld().isClient) {
            var clouds = this.getWorld().getEntitiesByClass(AreaEffectCloudEntity.class,
                    this.getBoundingBox().expand(32.0), cloud -> true);

            for (var cloud : clouds) {
                var entitiesInside = this.getWorld().getEntitiesByClass(LivingEntity.class,
                        cloud.getBoundingBox(), entity -> true);

                for (LivingEntity entity : entitiesInside) {
                    if (entity instanceof PlayerEntity player) {
                        player.damage(this.getDamageSources().magic(), 4.0F);
                        player.setOnFireFor(2);
                    } else if (entity instanceof MagnaMinionEntity || entity instanceof LavaGolemEntity || entity instanceof MagnaWitchEntity) {
                        ((ServerWorld)this.getWorld()).spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                                entity.getX(), entity.getY() + 1.0, entity.getZ(), 3, 0.1, 0.1, 0.1, 0.05);
                    }
                }
            }
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        var effect = this.getStatusEffect(StatusEffects.RESISTANCE);

        if (effect != null && effect.getAmplifier() >= 3) {
            if (source.getAttacker() instanceof net.minecraft.server.network.ServerPlayerEntity player) {

                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_BREEZE_DEFLECT,
                        net.minecraft.sound.SoundCategory.HOSTILE, 1.0f, 0.5f);

                player.sendMessage(Text.literal("The Witch's dark magic is shielded by the Lava Golem!")
                        .formatted(net.minecraft.util.Formatting.DARK_PURPLE, net.minecraft.util.Formatting.BOLD), true);

                if (!this.getWorld().isClient) {
                    ServerWorld serverWorld = (ServerWorld) this.getWorld();
                    serverWorld.spawnParticles(ParticleTypes.WITCH,
                            this.getX(), this.getY() + 1.0, this.getZ(), 10, 0.3, 0.3, 0.3, 0.1);
                    serverWorld.spawnParticles(ParticleTypes.LAVA,
                            this.getX(), this.getY() + 1.2, this.getZ(), 5, 0.2, 0.2, 0.2, 0.1);
                }
            }
        }

        return super.damage(source, amount);
    }
}