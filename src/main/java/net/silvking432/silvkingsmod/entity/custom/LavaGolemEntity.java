package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

public class LavaGolemEntity extends IronGolemEntity {

    public LavaGolemEntity(EntityType<? extends IronGolemEntity> entityType, World world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathNodeType.LAVA, 0.0F);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPersistent();
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return IronGolemEntity.createIronGolemAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 150.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 10.0)
                .add(EntityAttributes.GENERIC_ARMOR, 13.0)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 10.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);

        if (!this.getWorld().isClient) {
            Scoreboard scoreboard = this.getWorld().getScoreboard();
            String teamName = "RedGlowTeam";
            Team team = scoreboard.getTeam(teamName);

            if (team == null) {
                team = scoreboard.addTeam(teamName);
                team.setColor(Formatting.RED);
                team.setFriendlyFireAllowed(false);
                team.setNameTagVisibilityRule(AbstractTeam.VisibilityRule.ALWAYS);
            }

            scoreboard.addScoreHolderToTeam(this.getUuidAsString(), team);

            this.setGlowing(true);
        }
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.1D, false)); // Der eigentliche Angriff
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));

        this.targetSelector.add(0, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        if (this.getWorld().isClient && this.getVelocity().horizontalLengthSquared() > 0.005) {
            this.getWorld().addParticle(ParticleTypes.DRIPPING_LAVA,
                    this.getX(), this.getY() + 2.0, this.getZ(), 0, 0, 0);
        }

        if (this.isTouchingWaterOrRain()) {
            this.damage(this.getDamageSources().magic(), 1.0F);
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean success = super.tryAttack(target);
        if (success && target instanceof LivingEntity livingEntity) {
            livingEntity.setOnFireFor(5);

            if (!this.getWorld().isClient) {
                ((ServerWorld) this.getWorld()).spawnParticles(ParticleTypes.LAVA,
                        target.getX(), target.getY() + 1.0, target.getZ(), 5, 0.2, 0.2, 0.2, 0.1);
            }
        }
        return success;
    }

    @Override
    protected void mobTick() {
        super.mobTick();

        if (this.age % 20 == 0 && !this.getWorld().isClient) {
            var nearbyAllies = this.getWorld().getEntitiesByClass(LivingEntity.class,
                    this.getBoundingBox().expand(25.0),
                    entity -> entity instanceof MagnaMinionEntity || entity instanceof MagnaWitchEntity);

            for (LivingEntity ally : nearbyAllies) {
                ally.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 30, 3, true, true));

                ((ServerWorld) this.getWorld()).spawnParticles(ParticleTypes.FLAME,
                        ally.getX(), ally.getY() + ally.getHeight(), ally.getZ(), 3, 0.2, 0.2, 0.2, 0.02);
            }
        }
    }

    @Override
    protected void pushAway(Entity entity) {
        if (!this.isConnectedThroughVehicle(entity)) {
            if (!entity.noClip && !this.noClip) {
                double d = entity.getX() - this.getX();
                double e = entity.getZ() - this.getZ();
                double f = Math.max(Math.abs(d), Math.abs(e));
                if (f >= 0.01) {
                    f = Math.sqrt(f);
                    d /= f;
                    e /= f;
                    double g = 1.0 / f;
                    if (g > 1.0) g = 1.0;
                    d *= g;
                    e *= g;
                    d *= 0.05;
                    e *= 0.05;
                    if (!this.hasPassengers()) {
                        this.addVelocity(-d, 0.0, -e);
                    }
                    if (!entity.hasPassengers()) {
                        entity.addVelocity(d, 0.0, e);
                    }
                }
            }
        }
    }

    @Override
    public boolean canTarget(EntityType<?> type) {
        return type == EntityType.PLAYER;
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        if (!this.getWorld().isClient) {
            this.setGlowing(false);
            var team = this.getWorld().getScoreboard().getTeam("RedGlowTeam");
            if (team != null) {
                this.getWorld().getScoreboard().removeScoreHolderFromTeam(this.getUuidAsString(), team);
            }

            var bosses = this.getWorld().getEntitiesByClass(MagnaTitanEntity.class,
                    this.getBoundingBox().expand(70.0), entity -> true);

            for (MagnaTitanEntity boss : bosses) {
                boss.decrementMinionCount();
            }
        }
    }
}