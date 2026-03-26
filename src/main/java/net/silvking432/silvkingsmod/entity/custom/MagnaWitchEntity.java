package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.World;

public class MagnaWitchEntity extends WitchEntity implements RangedAttackMob {

    public MagnaWitchEntity(EntityType<? extends WitchEntity> entityType, World world) {
        super(entityType, world);
    }

    // --- Attribute (Etwas stärker als eine normale Hexe) ---
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
        // Der ProjectileAttackGoal sorgt dafür, dass sie Distanz hält (zwischen 2 und 10 Blöcken)
        this.goalSelector.add(2, new ProjectileAttackGoal(this, 1.0, 60, 10.0F));
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0));
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
        // Wir umgehen die WitchEntity komplett und gehen direkt zu RaiderEntity
        // Da RaiderEntity die tickMovement von PatrolEntity erbt usw.
        // nutzen wir einfach die Standard-Mob-Logik:

        // Wir simulieren hier nur die Partikel, die Bewegung macht die KI von selbst
        if (this.getWorld().isClient && this.random.nextInt(5) == 0) {
            this.getWorld().addParticle(ParticleTypes.LARGE_SMOKE,
                    this.getX(), this.getY() + 1.5, this.getZ(), 0, 0, 0);
        }

        // Rufe die Basis-Logik von LivingEntity auf (Physik & Ticking)
        // Das verhindert, dass der Code der WitchEntity (mit dem raidGoal) ausgeführt wird.
        super.tickMovement();
    }

    // --- Boss-Zusammenhalt: Counter reduzieren beim Tod ---
    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        if (!this.getWorld().isClient) {
            // Suche den Titan im Umkreis von 70 Blöcken
            var bosses = this.getWorld().getEntitiesByClass(MagnaTitanEntity.class,
                    this.getBoundingBox().expand(70.0), entity -> true);

            for (MagnaTitanEntity boss : bosses) {
                boss.decrementMinionCount();
            }
        }
    }

    @Override
    public void setRaid(Raid raid) {
        // Leer lassen
    }

    @Override
    public void addBonusForWave(ServerWorld world, int wave, boolean unused) {
        // Leer lassen
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

        // 1. Erstelle einen ItemStack für die Lingering Potion
        ItemStack potionStack = new ItemStack(net.minecraft.item.Items.LINGERING_POTION);

        // 2. Nutze PotionContentsComponent.createStack oder die direkte Component-Zuweisung
        // In 1.21 ist dies der sauberste Weg:
        potionStack.set(net.minecraft.component.DataComponentTypes.POTION_CONTENTS,
                new net.minecraft.component.type.PotionContentsComponent(Potions.AWKWARD));

        potionEntity.setItem(potionStack);

        potionEntity.setPitch(potionEntity.getPitch() - 20.0F);
        potionEntity.setVelocity(d, e + g * 0.2, f, 0.75F, 8.0F);

        this.getWorld().spawnEntity(potionEntity);

        if (!this.getWorld().isClient) {
            createLingeringMagnaCloud(target.getBlockPos());
        }
    }


    private void createLingeringMagnaCloud(net.minecraft.util.math.BlockPos pos) {
        if (this.getWorld().isClient) return;

        // Wir erstellen eine Wolke, die 3 Sekunden (60 Ticks) hält
        net.minecraft.entity.AreaEffectCloudEntity cloud = new net.minecraft.entity.AreaEffectCloudEntity(this.getWorld(), pos.getX(), pos.getY(), pos.getZ());
        cloud.setRadius(3.0F);
        cloud.setRadiusOnUse(-0.5F);
        cloud.setWaitTime(10);
        cloud.setDuration(60);
        cloud.setOwner(this);
        cloud.setParticleType(net.minecraft.particle.ParticleTypes.FLAME);

        this.getWorld().spawnEntity(cloud);

        // Ein geplanter Task (Tick-basiert), der den Effekt in der Wolke ausführt
        // Wir nutzen die mobTick der Hexe, um alle Wolken in der Nähe zu prüfen:
    }

    @Override
    protected void mobTick() {
        super.mobTick();

        // Alle 10 Ticks prüfen wir, ob Entities in einer Wolke stehen
        if (this.age % 10 == 0 && !this.getWorld().isClient) {
            var clouds = this.getWorld().getEntitiesByClass(net.minecraft.entity.AreaEffectCloudEntity.class,
                    this.getBoundingBox().expand(32.0), cloud -> true);

            for (var cloud : clouds) {
                var entitiesInside = this.getWorld().getEntitiesByClass(LivingEntity.class,
                        cloud.getBoundingBox(), entity -> true);

                for (LivingEntity entity : entitiesInside) {
                    if (entity instanceof PlayerEntity player) {
                        // SPIELER: Schaden + Feuer
                        player.damage(this.getDamageSources().magic(), 4.0F);
                        player.setOnFireFor(2);
                    } else if (entity instanceof MagnaMinionEntity || entity instanceof LavaGolemEntity || entity instanceof MagnaWitchEntity) {
                        // MAGNA MOBS: Heilung
                        ((ServerWorld)this.getWorld()).spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                                entity.getX(), entity.getY() + 1.0, entity.getZ(), 3, 0.1, 0.1, 0.1, 0.05);
                    }
                }
            }
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        // 1. Prüfen, ob die Hexe den Resistenz-Effekt (Resi 4 = Amplifier 3) hat
        var effect = this.getStatusEffect(net.minecraft.entity.effect.StatusEffects.RESISTANCE);

        if (effect != null && effect.getAmplifier() >= 3) {
            // 2. Prüfen, ob ein Spieler der Angreifer ist
            if (source.getAttacker() instanceof net.minecraft.server.network.ServerPlayerEntity player) {

                // Sound-Effekt: Magisches Abprall-Geräusch (passt gut zur Hexe)
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_BREEZE_DEFLECT,
                        net.minecraft.sound.SoundCategory.HOSTILE, 1.0f, 0.5f);

                // Nachricht in die ActionBar
                player.sendMessage(Text.literal("The Witch's dark magic is shielded by the Lava Golem!")
                        .formatted(net.minecraft.util.Formatting.DARK_PURPLE, net.minecraft.util.Formatting.BOLD), true);

                // Partikel: Hexen-Magie (Lila/Schwarz) kombiniert mit Lava-Funken
                if (!this.getWorld().isClient) {
                    ServerWorld serverWorld = (ServerWorld) this.getWorld();
                    // Hexen-Partikel
                    serverWorld.spawnParticles(ParticleTypes.WITCH,
                            this.getX(), this.getY() + 1.0, this.getZ(), 10, 0.3, 0.3, 0.3, 0.1);
                    // Lava-Funken als Hinweis auf den Golem
                    serverWorld.spawnParticles(ParticleTypes.LAVA,
                            this.getX(), this.getY() + 1.2, this.getZ(), 5, 0.2, 0.2, 0.2, 0.1);
                }
            }
        }

        // Normalen Schaden ausführen (wird durch Resi 4 fast komplett geschluckt)
        return super.damage(source, amount);
    }
}