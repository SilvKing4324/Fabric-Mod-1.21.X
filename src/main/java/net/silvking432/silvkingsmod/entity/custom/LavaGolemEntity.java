package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
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
        // Er soll keine Angst vor Lava haben beim Pathfinding
        this.setPathfindingPenalty(net.minecraft.entity.ai.pathing.PathNodeType.LAVA, 0.0F);
        this.setPathfindingPenalty(net.minecraft.entity.ai.pathing.PathNodeType.DANGER_FIRE, 0.0F);
    }

    // --- Attribute (Leben, Schaden, Speed) ---
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

        // Wir machen das nur auf dem Server
        if (!this.getWorld().isClient) {
            Scoreboard scoreboard = this.getWorld().getScoreboard();
            String teamName = "RedGlowTeam";
            Team team = scoreboard.getTeam(teamName);

            // 1. Wenn das Team noch nicht existiert, erstelle es
            if (team == null) {
                team = scoreboard.addTeam(teamName);
                // Setze die Team-Farbe auf Rot -> Das macht den Glow-Umriss rot!
                team.setColor(Formatting.RED);
                // Optional: Verhindere, dass Teammitglieder sich gegenseitig verletzen
                team.setFriendlyFireAllowed(false);
                // Optional: Namensschilder für Feinde immer anzeigen
                team.setNameTagVisibilityRule(AbstractTeam.VisibilityRule.ALWAYS);
            }

            // 2. Füge den Golem dem Team hinzu
            // Wir nutzen die UUID der Entity als Scoreboard-Eintrag
            scoreboard.addScoreHolderToTeam(this.getUuidAsString(), team);

            // 3. Aktiviere den Glowing-Effekt permanent
            this.setGlowing(true);
        }
    }

    @Override
    protected void initGoals() {
        // 1. Basis-Bewegung (Schwimmen, Herumlaufen, Schauen)
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.1D, false)); // Der eigentliche Angriff
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));

        // 2. Target Selector (Wen soll er angreifen?)

        // Ziel 1: Greife den Spieler an, wenn er in der Nähe ist
        this.targetSelector.add(0, new ActiveTargetGoal<>(this, PlayerEntity.class, true));

        // Ziel 2: Wenn er geschlagen wird, schlag zurück (Rache)
        // WICHTIG: Wir fügen KEIN Goal für 'MobEntity' oder 'Monster' hinzu.
        // Dadurch ignoriert er Zombies, Skelette und deine eigenen Minions.
    }

    // --- Spezial-Eigenschaften ---

    @Override
    public boolean isFireImmune() {
        return true; // Er brennt nicht
    }

    @Override
    public void tickMovement() {
        super.tickMovement();

        // 1. Er verliert Lava-Partikel beim Laufen (nur Client)
        if (this.getWorld().isClient && this.getVelocity().horizontalLengthSquared() > 0.005) {
            this.getWorld().addParticle(ParticleTypes.DRIPPING_LAVA,
                    this.getX(), this.getY() + 2.0, this.getZ(), 0, 0, 0);
        }

        // 2. Er nimmt Schaden durch Wasser/Regen (wie ein Enderman)
        if (this.isTouchingWaterOrRain()) {
            this.damage(this.getDamageSources().magic(), 1.0F);
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean success = super.tryAttack(target);
        if (success && target instanceof LivingEntity livingEntity) {
            // 3. Gegner für 5 Sekunden in Brand setzen
            livingEntity.setOnFireFor(5);

            // Kleiner Lava-Explosions-Effekt beim Schlag
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

        // Alle 20 Ticks (1 Sekunde) den Buff verteilen, um Performance zu sparen
        if (this.age % 20 == 0 && !this.getWorld().isClient) {
            // Suche alle LivingEntities in einem Radius von 15 Blöcken
            var nearbyAllies = this.getWorld().getEntitiesByClass(LivingEntity.class,
                    this.getBoundingBox().expand(25.0),
                    entity -> entity instanceof MagnaMinionEntity || entity instanceof MagnaWitchEntity);

            for (LivingEntity ally : nearbyAllies) {
                // Resistenz 4 geben (Amplifier 3 = Level 4)
                // Dauer: 30 Ticks (1.5s), damit der Effekt ohne Flackern bleibt
                ally.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                        net.minecraft.entity.effect.StatusEffects.RESISTANCE, 30, 3, true, true));

                // Optional: Kleine Partikel beim Verbündeten anzeigen
                ((ServerWorld) this.getWorld()).spawnParticles(ParticleTypes.FLAME,
                        ally.getX(), ally.getY() + ally.getHeight(), ally.getZ(), 3, 0.2, 0.2, 0.2, 0.02);
            }
        }
    }

    @Override
    protected void pushAway(Entity entity) {
        // Wir rufen NICHT super.pushAway(entity) auf, weil das die IronGolem-Logik ist.
        // Wir nutzen stattdessen die Logik der darüberliegenden Klasse, um nur zu schieben,
        // ohne ein Ziel (Target) zu setzen.
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
        // Erlaubt NUR das Targeten von Spielern.
        // Alles andere (Zombies, Skelette, etc.) wird ignoriert.
        return type == EntityType.PLAYER;
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        if (!this.getWorld().isClient) {
            // 1. Glow & Team Cleanup
            this.setGlowing(false);
            var team = this.getWorld().getScoreboard().getTeam("RedGlowTeam");
            if (team != null) {
                this.getWorld().getScoreboard().removeScoreHolderFromTeam(this.getUuidAsString(), team);
            }

            // 2. Boss-Logik: Den Titan im Umkreis benachrichtigen
            // Wir suchen im Radius von 70 Blöcken nach dem MagnaTitan
            var bosses = this.getWorld().getEntitiesByClass(MagnaTitanEntity.class,
                    this.getBoundingBox().expand(70.0), entity -> true);

            for (MagnaTitanEntity boss : bosses) {
                boss.decrementMinionCount();
            }
        }
    }
}