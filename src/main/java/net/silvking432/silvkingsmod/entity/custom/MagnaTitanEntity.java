package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.entity.ModEntities;
import net.silvking432.silvkingsmod.entity.ai.MagnaTitanMeleeGoal;
import net.silvking432.silvkingsmod.item.ModItems;
import net.silvking432.silvkingsmod.particle.ModParticles;
import net.silvking432.silvkingsmod.sound.ModSounds;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class MagnaTitanEntity extends HostileEntity {
    private int pottingTimer = -1;
    private int pendingStrengthLevel = 0;
    private int blackHoleTimer = 0;
    private int fireballCooldown = 0;
    private int anvilCooldown = 0;
    private int anvilDropsLeft = 0;
    private int anvilJumpTimer = 0;
    private int healMoveCooldown = 0;
    private int healTicksActive = 0;
    private int idleAnimationTimeout = 0;
    private int minionCount = 0;
    private int ultCountdown = 0;
    public int attackAnimationTimeout = 0;
    private int failTimer = 0;
    private boolean isFailing = false;
    private boolean ultPhaseTriggered = false;

    private int ultPhaseTimer = 0;
    private int ultDeflections = 0;
    private BlockPos targetCirclePos;
    private boolean isUltActive = false;
    private Entity currentUltProjectile; // Der "Ball"
    private int stunTimer = 0;

    private double hoverYTarget = 0;
    private long nextMusicStartTime = 0;
    private int musicPhase = 0; // 0=Intro1, 1=Loop1, 2=Intro2, 3=Loop2, etc.
    private boolean phaseShiftPending = false;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();

    private boolean phase1Triggered = false;
    private boolean phase2Triggered = false;
    private boolean phase3Triggered = false;
    private boolean hasDone56Blowback = false;
    private boolean hasDone26Blowback = false;
    private boolean hasDoneBlackHole = false;
    private boolean isHealingPhase = false;

    private static final TrackedData<Boolean> IS_SHIELDED =
            DataTracker.registerData(MagnaTitanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> RAGE_MODE =
            DataTracker.registerData(MagnaTitanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(MagnaTitanEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> ATTACKING =
            DataTracker.registerData(MagnaTitanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> HEALING_PHASE =
            DataTracker.registerData(MagnaTitanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private final List<BlockPos> activeBeacons = new ArrayList<>();

    private BlockPos spawnPos;

    private final ServerBossBar bossBar = new ServerBossBar(Text.literal("Magna Titan"),
            BossBar.Color.RED, BossBar.Style.NOTCHED_6);

    public MagnaTitanEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setPersistent();
    }

    @Override
    public @Nullable EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        // 1. Erstmal die Standard-Initialisierung (Wichtig!)
        entityData = super.initialize(world, difficulty, spawnReason, entityData);

        // 2. Werte setzen
        this.spawnPos = this.getBlockPos();
        this.musicPhase = 0;
        // Timer auf Ende von Intro 1 setzen (14.75s)
        this.nextMusicStartTime = System.currentTimeMillis() + 14800;

        // 3. Equipment und Attribute (wie gehabt)
        this.initEquipment(world.getRandom(), difficulty);
        double baseDamage = switch (world.getDifficulty()) {
            case NORMAL -> 15.0;
            case HARD -> 20.0;
            default -> 10.0;
        };

        var attackDamageAttr = this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (attackDamageAttr != null) {
            attackDamageAttr.setBaseValue(baseDamage);
        }

        // 4. SOUND-START (Ganz am Ende, wenn alles bereit ist)
        if (!world.isClient()) {
            ServerWorld serverWorld = world.toServerWorld();

            // Kleiner Trick: Da die BossBar manchmal 1 Tick braucht,
            // spielen wir den Sound für alle Spieler in der Nähe ab,
            // falls die BossBar-Liste noch leer ist.
            if (this.bossBar.getPlayers().isEmpty()) {
                for (ServerPlayerEntity player : serverWorld.getPlayers(p -> p.squaredDistanceTo(this) < 2500)) { // 50 Blöcke Radius
                    serverWorld.playSound(null, player.getX(), player.getY(), player.getZ(),
                            ModSounds.NECRON_INTRO1, net.minecraft.sound.SoundCategory.RECORDS, 0.7f, 1.0f);
                }
            } else {
                for (ServerPlayerEntity player : this.bossBar.getPlayers()) {
                    serverWorld.playSound(null, player.getX(), player.getY(), player.getZ(),
                            ModSounds.NECRON_INTRO1, net.minecraft.sound.SoundCategory.RECORDS, 0.7f, 1.0f);
                }
            }
        }

        return entityData;
    }

    @Override
    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        var registryManager = this.getWorld().getRegistryManager();
        var enchantments = registryManager.getWrapperOrThrow(net.minecraft.registry.RegistryKeys.ENCHANTMENT);
        this.equipStack(EquipmentSlot.HEAD, new ItemStack(ModItems.STARLIGHT_ASHES));
        ItemStack sword = new ItemStack(ModItems.SPECTRE_STAFF);
        sword.addEnchantment(enchantments.getOrThrow(net.minecraft.enchantment.Enchantments.KNOCKBACK), 5);
        this.equipStack(EquipmentSlot.MAINHAND, sword);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.setEquipmentDropChance(slot, 0.0f);
        }
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new MagnaTitanMeleeGoal(this, 1.2D, true));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 4.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(0, new RevengeGoal(this));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(DATA_ID_TYPE_VARIANT,0);
        builder.add(ATTACKING,false);
        builder.add(IS_SHIELDED, false);
        builder.add(RAGE_MODE, false);
        builder.add(HEALING_PHASE, false);
    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = 40;
            this.idleAnimationState.start(this.age);
        } else {
            --this.idleAnimationTimeout;
        }
        if (this.isAttacking() && attackAnimationTimeout <= 0) {
            attackAnimationTimeout = 20;
            attackAnimationState.start(this.age);
        } else {
            --this.attackAnimationTimeout;
        }
        if (!this.isAttacking()) {
            attackAnimationState.stop();
        }
    }

    public static DefaultAttributeContainer.Builder createAttributes() {

        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH,1000)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED,0.33)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE,20)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE,128)
                .add(EntityAttributes.GENERIC_ARMOR,10)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS,3)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,1.0)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK,1);



    }

    @Override
    public void tick() {
        super.tick();
        if (this.isOnFire()) {
            this.extinguish();
        }

        if (this.getWorld().isClient()) {
            this.setupAnimationStates();

            // RAGE MODE PARTICLE
            if (this.isRaging()) {
                if (this.random.nextInt(3) == 0) {
                    double x = this.getX() + (this.random.nextDouble() - 0.5) * 1.5;
                    double y = this.getY() + this.getHeight() + 0.5; // Über dem Kopf
                    double z = this.getZ() + (this.random.nextDouble() - 0.5) * 1.5;

                    this.getWorld().addParticle(ParticleTypes.ANGRY_VILLAGER, x, y, z, 0, 0.1, 0);
                }

                this.getWorld().addParticle(ParticleTypes.SMOKE,
                        this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), 0, 0.05, 0);
            }
        }

        if (this.dataTracker.get(IS_SHIELDED)) {
            if (this.getY() < hoverYTarget) {
                this.setVelocity(0, 0.05, 0);
            } else {
                this.setVelocity(this.getVelocity().x, 0, this.getVelocity().z);
            }
            this.fallDistance = 0;

            if (this.getWorld().isClient) {
                for (int i = 0; i < 5; i++) {
                    double dx = (this.random.nextDouble() - 0.5) * 2.5;
                    double dy = this.random.nextDouble() * 3.0;
                    double dz = (this.random.nextDouble() - 0.5) * 2.5;
                    this.getWorld().addParticle(net.minecraft.particle.ParticleTypes.END_ROD,
                            this.getX() + dx, this.getY() + dy, this.getZ() + dz, 0, 0, 0);
                }
            }
        }
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        this.updateBossBar();
        this.tickBossMusic();
        this.tickUltLogic();

        if (this.isUltActive || this.stunTimer > 0) {
            return;
        }

        if (this.pottingTimer > 0) {
            this.pottingTimer--;

            this.getNavigation().stop();
            this.setVelocity(0, this.getVelocity().y, 0);

            if (this.pottingTimer == 0) {
                this.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                        net.minecraft.entity.effect.StatusEffects.STRENGTH,
                        Integer.MAX_VALUE, this.pendingStrengthLevel - 1, false, true, true));

                var registryManager = this.getWorld().getRegistryManager();
                var enchantments = registryManager.getWrapperOrThrow(net.minecraft.registry.RegistryKeys.ENCHANTMENT);

                ItemStack staff = new ItemStack(ModItems.SPECTRE_STAFF);
                staff.addEnchantment(enchantments.getOrThrow(net.minecraft.enchantment.Enchantments.KNOCKBACK), 5);

                this.equipStack(EquipmentSlot.MAINHAND, staff);

                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_PLAYER_LEVELUP, net.minecraft.sound.SoundCategory.HOSTILE, 1.0f, 1.2f);

                this.pottingTimer = -1;
            }
            return;
        }

        // 2. BASIS-DATEN
        float healthPercent = this.getHealth() / this.getMaxHealth();
        this.bossBar.setPercent(healthPercent);

        if (this.healMoveCooldown > 0) this.healMoveCooldown--;

        // 2. Trigger-Logik
        boolean canHeal = healthPercent <= 0.66f && !this.isShieldActive() && this.blackHoleTimer <= 0
                && this.anvilDropsLeft <= 0 && this.pottingTimer <= 0 && !this.isHealing();

        if (canHeal && this.healMoveCooldown <= 0) {
            if (this.age % 20 == 0) {
                float chance = this.isRaging() ? 0.10f : 0.05f;
                if (this.random.nextFloat() < chance) {
                    startHealPhase();
                }
            }
        }

        // 3. Aktive Heil-Phase
        if (this.isHealing()) {
            this.getNavigation().stop();
            this.setVelocity(0, this.getVelocity().y, 0);
            this.healTicksActive++;

            if (!this.getWorld().isClient) {
                if (this.age % 5 == 0) {
                    spawnHealingTrails();
                }
                // Prüfen, ob noch Beacons da sind
                activeBeacons.removeIf(pos -> this.getWorld().getBlockState(pos).isAir()); // Später durch Titan-Beacon Check ersetzen

                if (activeBeacons.isEmpty() || this.getHealth() >= this.getMaxHealth()) {
                    stopHealPhase();
                } else {
                    int HealTimer = 170;
                    if (this.isRaging()) {
                        HealTimer = HealTimer + 130;
                    }
                    float healPerSecond = (this.healTicksActive <= HealTimer) ? 4.0f : 10.0f;
                    if (this.isRaging()) healPerSecond *= 1.5f;

                    this.heal(healPerSecond / 20.0f);

                    if (this.age % 5 == 0) {
                        ((ServerWorld)this.getWorld()).spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                                this.getX(), this.getY() + 1, this.getZ(), 5, 0.5, 1.0, 0.5, 0.1);
                    }
                }
            }
            return;
        }

        handlePhaseTriggers(healthPercent);

        if (this.blackHoleTimer > 0) {
            this.blackHoleTimer--;
            this.getNavigation().stop();

            // SCHWEBEN: Nach oben bewegen (Ziel: ca. 10 Blöcke über dem Boden/Spieler)
            if (this.blackHoleTimer > 260) {
                this.addVelocity(0, 0.15, 0);
            } else {
                this.setVelocity(0, Math.sin(this.age * 0.05) * 0.01, 0);
            }

            if (!this.getWorld().isClient) {
                ServerWorld world = (ServerWorld) this.getWorld();

                double holeX = this.getX();
                double holeY = this.getY() + 5.0;
                double holeZ = this.getZ();

                // PARTIKEL SPAWNEN (Jeden Tick für dichte Optik)
                world.spawnParticles(ModParticles.BLACK_HOLE_PARTICLE, holeX, holeY, holeZ, 1, 0, 0, 0, 0);

                var players = world.getEntitiesByClass(PlayerEntity.class,
                        this.getBoundingBox().expand(30.0), p -> !p.isCreative() && !p.isSpectator());

                for (PlayerEntity p : players) {
                    double dx = holeX - p.getX();
                    double dy = holeY - p.getY();
                    double dz = holeZ - p.getZ();
                    double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);

                    if (dist > 0.5 && dist < 30.0) {
                        double pullStrength = 0.12;

                        if (p.isSprinting() && dist > 10.0) {
                            pullStrength = 0.04;
                        }

                        if (dist < 3.5) {
                            pullStrength = 0.3;
                            p.damage(this.getDamageSources().magic(), 4.0f);
                        }

                        // Geschwindigkeit hinzufügen
                        p.addVelocity(dx/dist * pullStrength, dy/dist * pullStrength, dz/dist * pullStrength);

                        p.velocityDirty = true;
                        p.velocityModified = true;

                        if (p instanceof ServerPlayerEntity sp) {
                            sp.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(p));
                        }
                    }
                }
                if (this.blackHoleTimer % 10 == 0) {
                    world.playSound(null, holeX, holeY, holeZ, SoundEvents.ENTITY_BREEZE_WIND_BURST, net.minecraft.sound.SoundCategory.HOSTILE, 1.5f, 0.4f);
                }
            }
            return;
        }

        // ANVIL DROP MOVE
        if (this.anvilCooldown > 0) {
            this.anvilCooldown--;
        }

        if (healthPercent <= 0.83f && !this.isShieldActive() && this.blackHoleTimer <= 0 && this.anvilCooldown <= 0 && this.anvilDropsLeft <= 0 && !this.isHealingPhase) {             // 10% Chance pro Sekunde (alle 20 Ticks)
            if (this.age % 20 == 0 && this.random.nextFloat() < 0.10f) {
                this.anvilDropsLeft = this.isRaging() ? 5 : 3;
                executeAnvilJump();
            }
        }

        if (this.anvilDropsLeft > 0 && this.isOnGround() && this.anvilJumpTimer <= 0) {
            this.anvilJumpTimer = 35;
        }

        if (this.anvilJumpTimer > 0) {
            this.anvilJumpTimer--;
            if (this.anvilJumpTimer == 0 && this.anvilDropsLeft > 0) {
                executeAnvilJump();
            }
        }

        // 4. SHIELD PHASE
        if (this.dataTracker.get(IS_SHIELDED)) {
            // FIREBALL LOGIC
            if (!this.getWorld().isClient && this.getTarget() != null) {
                LivingEntity target = this.getTarget();

                // Do not shoot at own people
                boolean isMinion = target instanceof MagnaMinionEntity ||
                        target instanceof LavaGolemEntity ||
                        target instanceof MagnaWitchEntity;

                if (!isMinion && target != this) {
                    this.fireballCooldown--;
                    if (this.fireballCooldown <= 0) {
                        shootMagnaFireball(target);
                        this.fireballCooldown = 60 + this.random.nextInt(40);
                    }
                } else {
                    // If focus on minion find player
                    this.setTarget(this.getWorld().getClosestPlayer(this, 64.0));
                }
            }

            if (this.minionCount <= 0) {
                this.dataTracker.set(IS_SHIELDED, false);
                this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE, 1.0f, 1.0f);
                this.setVelocity(0, -0.01, 0);
            }
        }
    }

    @Override
    public boolean isAttacking() {
        return this.dataTracker.get(ATTACKING);
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PLAYER_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PLAYER_DEATH;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_PLAYER_SWIM;
    }

    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_PLAYER_SPLASH;
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public boolean isFireImmune() {
        return true;
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);

        if (!this.getWorld().isClient) {
            // Wir nutzen ein neutrales Paket, das die ganze Kategorie stoppt
            StopSoundS2CPacket stopAllMusic = new StopSoundS2CPacket(null, SoundCategory.RECORDS);

            for (ServerPlayerEntity player : this.bossBar.getPlayers()) {
                // 1. Musik stoppen (Egal welche Phase gerade aktiv war)
                player.networkHandler.sendPacket(stopAllMusic);

                // 2. Sieges-Sound abspielen
                this.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 1.0f, 1.0f);

                // 3. Nachricht senden
                player.sendMessage(Text.literal("The Earth shakes as the titan falls!")
                        .formatted(Formatting.GOLD, Formatting.BOLD), false);
            }

            this.dropStack(new ItemStack(ModItems.SPECTRE_STAFF));
        }
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        // Wir geben 'false' zurück, was Minecraft sagt:
        // "Hier ist kein Fallschaden passiert, brich die Berechnung ab."
        return false;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        // 1. Immunität bei Schild, Black Hole ODER Heilphase
        boolean isInvulnerable = this.dataTracker.get(IS_SHIELDED)
                || this.blackHoleTimer > 0
                || this.isHealing(); // Nutzt die Hilfsmethode von vorhin

        if (isInvulnerable && !source.isOf(net.minecraft.entity.damage.DamageTypes.OUT_OF_WORLD)) {
            if (!this.getWorld().isClient) {
                // Sound-Feedback: Metallisches "Ping", wenn man ihn schlägt
                this.playSound(SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.5f, 2.0f);
            }
            return false; // 100% Schadens-Stop
        }

        if (this.isUltActive) {
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.HOSTILE, 1.0f, 1.5f);
            // Während der Ult nimmt er vielleicht gar keinen Schaden oder stark reduziert
            return super.damage(source, amount * 0.1f);
        }

        return super.damage(source, amount);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        // Spawn-Position dauerhaft speichern
        if (this.spawnPos != null) {
            nbt.putInt("SpawnX", this.spawnPos.getX());
            nbt.putInt("SpawnY", this.spawnPos.getY());
            nbt.putInt("SpawnZ", this.spawnPos.getZ());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);

        if (nbt.contains("SpawnX")) {
            this.spawnPos = new BlockPos(nbt.getInt("SpawnX"), nbt.getInt("SpawnY"), nbt.getInt("SpawnZ"));
        }
    }

    private void executeBlowbackWave() {
        if (this.getWorld().isClient) return;

        ServerWorld world = (ServerWorld) this.getWorld();
        double range = 10.0; // Radius der Druckwelle

        // Optisches Feedback: Große Explosion/Rauchwolke
        world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER,
                this.getX(), this.getY() + 1.0, this.getZ(), 3, 0.5, 0.5, 0.5, 0.1);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_BREEZE_WIND_BURST, this.getSoundCategory(), 2.0f, 0.5f);

        // Alle LivingEntities im Umkreis finden
        var targets = world.getEntitiesByClass(LivingEntity.class,
                this.getBoundingBox().expand(range), entity -> entity != this);

        for (LivingEntity target : targets) {
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);

            if (distance > 0) {
                dx /= distance;
                dz /= distance;

                // 1. Berechnung des Falloffs (1.0 nah dran, 0.0 weit weg)
                double falloff = Math.max(0.0, 1.0 - (distance / range));

                // 2. SCHADEN BERECHNEN (Basis 25.0)
                // Wir nutzen den Falloff, damit der Schaden nach außen hin abnimmt
                float damageAmount = (float) (50.0 * falloff);

                if (damageAmount > 1.0f) { // Nur Schaden machen, wenn noch relevant
                    // Wir nutzen 'magic' oder 'explosion', damit Rüstung es nicht komplett blockt
                    target.damage(this.getDamageSources().explosion(this, this), damageAmount);
                }

                // 3. KNOCKBACK (Wie vorher, aber mit dem Falloff-Wert)
                double horizontalPower = 2.8 * falloff;
                double verticalPower = 0.6 * falloff;

                target.addVelocity(dx * horizontalPower, verticalPower, dz * horizontalPower);

                // 4. SYNCHRONISATION
                if (target instanceof net.minecraft.server.network.ServerPlayerEntity player) {
                    player.velocityDirty = true;
                    player.velocityModified = true;
                    player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket(target));

                    // Nachricht an den Spieler
                    player.sendMessage(Text.literal("A Dark Pulse goes through the Boss!")
                            .formatted(net.minecraft.util.Formatting.DARK_RED, net.minecraft.util.Formatting.BOLD), true);
                }
            }
        }
    }

    public boolean isShieldActive() {
        return this.dataTracker.get(IS_SHIELDED);
    }

    public boolean isPotting() {
        return this.pottingTimer > 0;
    }

    private void executePottingSequence(int strengthLevel) {
        if (this.getWorld().isClient) return;
        this.pendingStrengthLevel = strengthLevel;
        this.pottingTimer = 30;

        this.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 1.0f, 1.0f);

        ItemStack potion = new ItemStack(Items.POTION);

        RegistryEntry<Potion> strengthPotion = Potions.STRENGTH;

        // 4. In die Komponente setzen
        potion.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(strengthPotion));

        this.equipStack(EquipmentSlot.MAINHAND, potion);
    }

    private void startShieldPhase(int minionCount, int golemCount, int witchCount) {
        this.dataTracker.set(IS_SHIELDED, true);
        this.hoverYTarget = this.getY() + 3.0;
        this.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.5f);

        if (!this.getWorld().isClient) {
            // Spawne Minions
            for (int i = 0; i < minionCount; i++) {
                spawnMinion();
            }
            // Spawne Golems
            for (int i = 0; i < golemCount; i++) {
                spawnLavaGolem();
            }
            // Spawne Hexen
            for (int i = 0; i < witchCount; i++) {
                spawnMagnaWitch();
            }
        }
    }

    public int getBlackHoleTimer() {
        return this.blackHoleTimer;
    }

    private void spawnMinion() {
        MagnaMinionEntity minion = new MagnaMinionEntity(ModEntities.MAGNA_MINION, this.getWorld());
        setupSpawn(minion);
    }

    private void spawnLavaGolem() {
        LavaGolemEntity golem = new LavaGolemEntity(ModEntities.LAVA_GOLEM, this.getWorld());
        setupSpawn(golem);
    }

    private void spawnMagnaWitch() {
        MagnaWitchEntity witch = new MagnaWitchEntity(ModEntities.MAGNA_WITCH, this.getWorld());
        setupSpawn(witch);
    }

    public void setAttacking(boolean attacking) {
        this.dataTracker.set(ATTACKING, attacking);
    }

    public boolean isRaging() {
        return this.dataTracker.get(RAGE_MODE);
    }

    private void setupSpawn(LivingEntity entity) {
        // Position leicht versetzt zum Boss
        entity.refreshPositionAndAngles(
                this.getX() + this.random.nextGaussian() * 4,
                this.getY() + 2.0,
                this.getZ() + this.random.nextGaussian() * 4,
                0, 0
        );
        this.getWorld().spawnEntity(entity);
        this.minionCount++; // Beide Typen erhöhen den Counter, der den Schild hält
    }

    private void shootMagnaFireball(LivingEntity target) {
        if (this.getWorld().isClient) return;

        // 1. Ursprung und Richtung berechnen
        // Nutzt getEyeY(), damit der Schuss immer von der aktuellen Boss-Höhe kommt
        double spawnX = this.getX();
        double spawnY = this.getEyeY();
        double spawnZ = this.getZ();

        double d = target.getX() - spawnX;
        double e = target.getBodyY(0.5) - spawnY;
        double f = target.getZ() - spawnZ;

        // Normalisieren sorgt für konstante Geschwindigkeit (0.5)
        Vec3d velocity = new Vec3d(d, e, f).normalize().multiply(0.5);

        // 2. DEINE NEUE ENTITY KLASSE NUTZEN
        MagnaFireballEntity fireball = new MagnaFireballEntity(this.getWorld(), this, velocity);

        // 3. Position setzen
        fireball.setPosition(spawnX, spawnY, spawnZ);

        // 4. Custom Model Data (ID 1) zuweisen
        // Das sorgt dafür, dass dein lila Feuerball-Modell angezeigt wird
        ItemStack fireballStack = new ItemStack(Items.FIRE_CHARGE);
        fireballStack.set(DataComponentTypes.CUSTOM_MODEL_DATA,
                new CustomModelDataComponent(1));
        fireball.setItem(fireballStack);

        // 5. In der Welt spawnen
        this.getWorld().spawnEntity(fireball);

        // Soundeffekt abspielen
        this.playSound(SoundEvents.ENTITY_GHAST_SHOOT, 1.0f, 1.0f);
    }

    public void decrementMinionCount() {
        this.minionCount--;
    }

    private void executeAnvilJump() {
        if (this.getTarget() instanceof PlayerEntity player) {
            // 1. Teleport: 12 Blöcke über den Spieler (etwas höher für bessere Reaktionszeit)
            this.refreshPositionAndAngles(player.getX(), player.getY() + 15.0, player.getZ(), this.getYaw(), this.getPitch());
            if (!this.getWorld().isClient) {
                ServerWorld world = (ServerWorld) this.getWorld();

                // 2. Deine Custom Projektil-Entity erzeugen
                MagnaAnvilEntity anvil = new MagnaAnvilEntity(world, this);

                // Position direkt unter dem Boss setzen
                anvil.setPosition(this.getX(), this.getY() - 1.0, this.getZ());

                // 3. Optik: Custom Model Data für lila Amboss (ID 2)
                ItemStack anvilStack = new ItemStack(Items.ANVIL);
                anvilStack.set(DataComponentTypes.CUSTOM_MODEL_DATA,
                        new CustomModelDataComponent(2));
                anvil.setItem(anvilStack);

                // 4. Den Amboss spawnen
                world.spawnEntity(anvil);

                // 5. Sound-Effekte
                world.playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_BREEZE_WIND_BURST, SoundCategory.HOSTILE, 2.0f, 0.5f);

                // Ein schweres "Metall-Sausen" beim Start
                world.playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.BLOCK_ANVIL_USE, SoundCategory.HOSTILE, 1.0f, 0.2f);
            }

            this.anvilDropsLeft--;

            // Wenn alle Sprünge durch sind, Cooldown starten (500 Ticks = 25 Sek)
            if (this.anvilDropsLeft <= 0) {
                this.anvilCooldown = 500;
            }
        }
    }

    private void teleportToCenter() {
        if (this.spawnPos != null) {
            // Zielkoordinaten (Block-Mitte)
            double targetX = this.spawnPos.getX() + 0.5;
            double targetY = this.spawnPos.getY();
            double targetZ = this.spawnPos.getZ() + 0.5;

            if (this.getWorld() instanceof ServerWorld world) {
                // 1. Partikel am aktuellen Ort (vor dem Teleport)
                world.spawnParticles(ParticleTypes.REVERSE_PORTAL, this.getX(), this.getY() + 1, this.getZ(), 40, 0.5, 1, 0.5, 0.1);

                // 2. Der eigentliche Teleport
                this.refreshPositionAndAngles(targetX, targetY, targetZ, this.getYaw(), this.getPitch());

                // 3. Partikel & Sound am Zielort
                world.spawnParticles(ParticleTypes.PORTAL, targetX, targetY + 1, targetZ, 50, 0.5, 1.5, 0.5, 0.2);
                world.playSound(null, targetX, targetY, targetZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.HOSTILE, 1.5f, 0.8f);
            }

            // KI stoppen, damit er nicht sofort wieder losrennt
            this.getNavigation().stop();
            this.setVelocity(0, 0, 0);
        }
    }

    private void startHealPhase() {
        this.setHealing(true);
        this.teleportToCenter();
        this.activeBeacons.clear();

        if (!this.getWorld().isClient) {
            ServerWorld world = (ServerWorld) this.getWorld();

            // --- DYNAMISCHE ANZAHL ---
            // Wenn Rage-Mode aktiv ist (ab 33% HP), spawnen 5 statt 3 Beacons
            int beaconCount = this.isRaging() ? 5 : 3;

            // Radius evtl. leicht erhöhen bei 5 Beacons, damit sie nicht zu eng stehen
            double radius = this.isRaging() ? 20.0 : 18.0;

            for (int i = 0; i < beaconCount; i++) {
                // Der Winkel berechnet sich jetzt automatisch:
                // Bei 3 Beacons = 120° Schritte
                // Bei 5 Beacons = 72° Schritte
                double baseAngle = i * (360.0 / beaconCount);
                double variation = this.random.nextDouble() * 20 - 10; // Etwas weniger Zufall bei 5 Beacons
                double radians = Math.toRadians(baseAngle + variation);

                int bx = (int) (this.spawnPos.getX() + Math.cos(radians) * radius);
                int bz = (int) (this.spawnPos.getZ() + Math.sin(radians) * radius);

                BlockPos beaconPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(bx, 0, bz));

                world.setBlockState(beaconPos, ModBlocks.TITANIUM_BEACON.getDefaultState());
                this.activeBeacons.add(beaconPos);

                // Optischer Effekt
                world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, beaconPos.getX() + 0.5, beaconPos.getY() + 0.5, beaconPos.getZ() + 0.5, 1, 0, 0, 0, 0);
            }

            float pitch = this.isRaging() ? 0.4f : 0.5f; // Tieferer Sound im Rage Mode
            world.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.HOSTILE, 2.0f, pitch);
        }
    }

    private void stopHealPhase() {
        this.setHealing(false);
        this.healTicksActive = 0;
        this.healMoveCooldown = this.isRaging() ? 600 : 900; // 30s oder 45s

        if (!this.getWorld().isClient) {
            // Alle verbleibenden Beacons entfernen
            for (BlockPos pos : activeBeacons) {
                this.getWorld().breakBlock(pos, false);
            }
            this.activeBeacons.clear();
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.HOSTILE, 2.0f, 1.0f);
        }
    }

    private void handlePhaseTriggers(float healthPercent) {
        // Phase 1: 100% HP (Erster Spawn)
        if (!phase1Triggered) {
            if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.setWeather(12000, 0, false, false);
                serverWorld.setTimeOfDay(6000);

                // Optional: Nachricht an Spieler
                for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                    player.sendMessage(Text.literal("The sky clears as the Magna Titan awakens...")
                            .formatted(Formatting.GOLD, Formatting.ITALIC), true);
                }
            }

            teleportToCenter();
            startShieldPhase(2, 0, 1);
            phase1Triggered = true;
        }

        // 5/6 HP Schwelle (~83%) -> Blowback + Stärke 1
        if (healthPercent <= 0.83f && !hasDone56Blowback) {
            executeBlowbackWave();
            executePottingSequence(1);
            hasDone56Blowback = true;
        }

        // 4/6 HP Schwelle (~66%) -> Black Hole
        if (healthPercent <= 0.66f && !hasDoneBlackHole && this.blackHoleTimer <= 0 && !this.isHealing()) {
            if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.setTimeOfDay(13400);
            }
            this.blackHoleTimer = 300; // 15 Sekunden
            this.hasDoneBlackHole = true;

            if (!this.getWorld().isClient) {
                for (ServerPlayerEntity player : ((ServerWorld)this.getWorld()).getPlayers(p -> p.squaredDistanceTo(this) < 10000)) {
                    player.sendMessage(Text.literal("Watch This!")
                            .formatted(net.minecraft.util.Formatting.DARK_PURPLE, net.minecraft.util.Formatting.BOLD, net.minecraft.util.Formatting.ITALIC), true);
                }
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_ENDER_DRAGON_GROWL, net.minecraft.sound.SoundCategory.HOSTILE, 1.5f, 0.5f);
            }
        }

        // Phase 2: 50% HP (3/6) -> Schild + Minions
        if (healthPercent <= 0.5f && !phase2Triggered) {
            teleportToCenter();
            startShieldPhase(3, 2, 1);
            phase2Triggered = true;
        }

        // 2/6 HP Schwelle (~33%) -> Rage Mode + Potting 2
        if (healthPercent <= 0.33f && !hasDone26Blowback) {
            executeBlowbackWave();
            executePottingSequence(3);

            this.dataTracker.set(RAGE_MODE, true);
            this.bossBar.setColor(BossBar.Color.PINK);

            if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld serverWorld) {
                Text rageTitle = Text.literal("⚠ RAGE MODE ⚠")
                        .formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD);

                for (ServerPlayerEntity player : serverWorld.getPlayers(p -> p.squaredDistanceTo(this) < 2500)) { // 50 Blöcke Radius
                    player.networkHandler.sendPacket(new TitleS2CPacket(rageTitle));
                    player.networkHandler.sendPacket(new TitleFadeS2CPacket(10, 40, 10));

                }
                serverWorld.playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 1.5f, 0.0f);

                serverWorld.playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.HOSTILE, 1.0f, 1.0f);
            }

            var armorAttr = this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
            if (armorAttr != null) {
                armorAttr.setBaseValue(15.0);
            }

            var toughnessAttr = this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
            if (toughnessAttr != null) {
                toughnessAttr.setBaseValue(6.0);
            }

            var scaleAttr = this.getAttributeInstance(EntityAttributes.GENERIC_SCALE);
            if (scaleAttr != null) {
                scaleAttr.setBaseValue(1.2);
            }

            hasDone26Blowback = true;
        }

        // Phase 3: 1/4 HP (25%) -> Ult Phase
        if (healthPercent <= 0.25f && !this.isUltActive && this.ultDeflections == 0 && !this.ultPhaseTriggered) {
            this.ultPhaseTriggered = true;
            startUltPhase();
        }

        // Phase 3: 1/6 HP (~16%) -> Final Minion Wave
        if (healthPercent <= 0.166f && !phase3Triggered) {
            teleportToCenter();
            startShieldPhase(4, 2, 2);
            phase3Triggered = true;
        }
    }

    public boolean isHealing() {
        return this.dataTracker.get(HEALING_PHASE);
    }

    public void setHealing(boolean healing) {
        this.dataTracker.set(HEALING_PHASE, healing);
        this.isHealingPhase = healing; // Synchronisiert das interne Boolean-Feld
    }

    private void spawnHealingTrails() {
        if (this.getWorld() instanceof ServerWorld world) {
            for (BlockPos beaconPos : this.activeBeacons) {
                // Startpunkt: Brusthöhe des Bosses
                double startX = this.getX();
                double startY = this.getY() + 1.5;
                double startZ = this.getZ();

                // Endpunkt: Mitte des Beacons
                double endX = beaconPos.getX() + 0.5;
                double endY = beaconPos.getY() + 0.5;
                double endZ = beaconPos.getZ() + 0.5;

                // Vektor berechnen
                double dx = endX - startX;
                double dy = endY - startY;
                double dz = endZ - startZ;
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

                // Partikel entlang der Linie spawnen (alle 0.5 Blöcke ein Partikel)
                for (double d = 0; d < distance; d += 0.5) {
                    double px = startX + (dx / distance) * d;
                    double py = startY + (dy / distance) * d;
                    double pz = startZ + (dz / distance) * d;

                    // Wähle hier Partikel, die zu deinem Boss passen (z.B. Hex oder Villager Happy)
                    world.spawnParticles(ParticleTypes.WITCH, px, py, pz, 1, 0.05, 0.05, 0.05, 0.01);

                    // Optional: Ein paar "Herz"-Partikel direkt am Boss, um Heilung zu zeigen
                    if (this.random.nextInt(10) == 0) {
                        world.spawnParticles(ParticleTypes.HEART, startX, startY + 0.5, startZ, 1, 0.3, 0.3, 0.3, 0.1);
                    }
                }
            }
        }
    }

    private void tickBossMusic() {
        long currentTime = System.currentTimeMillis();
        float healthPercent = this.getHealth() / this.getMaxHealth();

        // --- 1. PHASEN-CHECK (Wann soll was passieren?) ---

        // WEICHER ÜBERGANG: Phase 1 (Loop 1) -> Intro 2 vorbereiten
        if (healthPercent <= 0.66f && musicPhase == 1) {
            this.phaseShiftPending = true;
        }

        // HARTER CUT: Phase 2/3 -> Intro 3 (Black Hole)
        if (healthPercent <= 0.33f && (musicPhase == 2 || musicPhase == 3)) {
            this.executeHardCut(4); // Sofortiger Sprung zu Intro 3
            return; // Beendet NUR tickBossMusic, mobTick läuft weiter!
        }

        // WEICHER ÜBERGANG: Phase 5 (Loop 3) -> Intro 4 vorbereiten
        if (healthPercent <= 0.17f && musicPhase == 5) {
            this.phaseShiftPending = true;
        }

        // --- 2. PLAYBACK LOGIC (Zeitgesteuert) ---
        if (currentTime >= this.nextMusicStartTime) {

            // Entscheiden, ob wir die Phase erhöhen
            if (this.phaseShiftPending) {
                // Der weiche Wechsel: Loop ist fertig, jetzt kommt das nächste Intro
                this.musicPhase++;
                this.phaseShiftPending = false;
            }
            else if (this.musicPhase % 2 == 0) {
                // Automatischer Wechsel: Intro ist fertig, jetzt kommt der Loop
                this.musicPhase++;
            }
            // Ansonsten: Normaler Loop-Restart (Phase bleibt gleich)

            this.playCurrentPhaseSound();
            this.updateNextStartTime();
        }
    }

    private void executeHardCut(int nextPhase) {
        this.stopCurrentMusicGlobally();
        this.musicPhase = nextPhase;
        this.phaseShiftPending = false; // Wir brauchen die Flagge nicht mehr
        this.playCurrentPhaseSound();
        this.updateNextStartTime();
    }

    private void playCurrentPhaseSound() {
        SoundEvent sound = switch (musicPhase) {
            case 0 -> ModSounds.NECRON_INTRO1;
            case 1 -> ModSounds.NECRON_LOOP1;
            case 2 -> ModSounds.NECRON_INTRO2;
            case 3 -> ModSounds.NECRON_LOOP2;
            case 4 -> ModSounds.NECRON_INTRO3;
            case 5 -> ModSounds.NECRON_LOOP3;
            case 6 -> ModSounds.NECRON_INTRO4;
            default -> ModSounds.NECRON_LOOP4;
        };

        if (this.getWorld() instanceof net.minecraft.server.world.ServerWorld serverWorld) {
            // Wir holen alle Spieler im Umkreis von 64 Blöcken, falls die BossBar leer ist
            java.util.List<net.minecraft.server.network.ServerPlayerEntity> targets = this.bossBar.getPlayers().isEmpty()
                    ? serverWorld.getPlayers(p -> p.squaredDistanceTo(this.getPos()) < 4096)
                    : new java.util.ArrayList<>(this.bossBar.getPlayers());

            for (net.minecraft.server.network.ServerPlayerEntity player : targets) {
                serverWorld.playSound(null, player.getX(), player.getY(), player.getZ(),
                        sound, net.minecraft.sound.SoundCategory.RECORDS, 0.7f, 1.0f);
            }
        }
    }

    private void updateNextStartTime() {
        double duration = switch (musicPhase) {
            case 0 -> 14.75;
            case 1 -> 9.65;
            case 2 -> 9.60;
            case 3 -> 29.0;
            case 4 -> 14.6;
            case 5 -> 18.35;
            case 6 -> 21.3;
            case 7 -> 19.1;
            default -> 19.1;
        };
        this.nextMusicStartTime = System.currentTimeMillis() + (long)(duration * 1000);
    }

    private void stopCurrentMusicGlobally() {
        for (net.minecraft.server.network.ServerPlayerEntity player : this.bossBar.getPlayers()) {
            // Stoppt alle Sounds aus der Kategorie RECORDS (Jukebox/Musik) für den Spieler
            player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.StopSoundS2CPacket(null, net.minecraft.sound.SoundCategory.RECORDS));
        }
    }

    private void startUltPhase() {
        this.isUltActive = true;
        this.ultPhaseTimer = 200; // Prepare time
        this.ultDeflections = 0;
        this.ultCountdown = 1800; // Ult Time

        // Attribute setzen
        var scaleAttr = this.getAttributeInstance(EntityAttributes.GENERIC_SCALE);
        if (scaleAttr != null) scaleAttr.setBaseValue(2.0);

        // Bossbar auf Gold/Gelb ändern für das "Overlay"-Feeling
        this.bossBar.setColor(BossBar.Color.YELLOW);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 2.0f, 0.5f);
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 100000, 0, false, false));
        this.bossTalk("I have enough of this! Its time to show you my real power! Just give me some time to cast my death bomb!");
    }

    private void tickUltLogic() {
        // 1. STUN LOGIK (BRUCH-PHASE)
        if (this.stunTimer > 0) {

            if (this.stunTimer == 200) {
                this.bossTalk("This... this is impossible! But don't celebrate yet, this stun won't last forever!");

                if (this.getWorld() instanceof ServerWorld sw) {
                    Text title = Text.literal("YOUR CHANCE!").formatted(Formatting.GREEN, Formatting.BOLD);
                    for (ServerPlayerEntity player : sw.getPlayers()) {
                        player.networkHandler.sendPacket(new TitleS2CPacket(title));
                        player.networkHandler.sendPacket(new TitleFadeS2CPacket(5, 40, 5));
                    }
                }
            }

            this.stunTimer--;
            this.getNavigation().stop();
            this.setNoGravity(false);
            if (this.getWorld() instanceof ServerWorld serverWorld) {
                if (this.age % 15 == 0) {
                    ParticleS2CPacket explosionPacket = new ParticleS2CPacket(
                            ParticleTypes.EXPLOSION_EMITTER, true,
                            this.getX() + (this.random.nextDouble() - 0.5) * 1.5,
                            this.getY() + this.random.nextDouble() * 2.0,
                            this.getZ() + (this.random.nextDouble() - 0.5) * 1.5,
                            0.0f, 0.0f, 0.0f, 0.0f, 1
                    );
                    for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                        if (player.squaredDistanceTo(this.getPos()) < 64 * 64) {
                            player.networkHandler.sendPacket(explosionPacket);
                        }
                    }
                }

                // Permanenter Rauch (LARGE_SMOKE) alle 2 Ticks
                if (this.age % 2 == 0) {
                    ParticleS2CPacket smokePacket = new ParticleS2CPacket(
                            ParticleTypes.LARGE_SMOKE, true,
                            this.getX(), this.getY() + 0.5, this.getZ(),
                            0.4f, 0.5f, 0.4f, 0.02f, 3
                    );
                    for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                        if (player.squaredDistanceTo(this.getPos()) < 64 * 64) {
                            player.networkHandler.sendPacket(smokePacket);
                        }
                    }
                }
            }
            // --- ENDE PARTIKEL ---

            if (this.stunTimer == 0) {
                this.isUltActive = false;
                var scaleAttr = this.getAttributeInstance(EntityAttributes.GENERIC_SCALE);
                if (scaleAttr != null) scaleAttr.setBaseValue(1.2);
                this.bossBar.setColor(BossBar.Color.PINK);
            }
            return;
        }

        if (!isUltActive) return;

        if (this.isFailing) {
            this.failTimer--;

            if (this.getWorld() instanceof ServerWorld sw) {
                BlockPos target = this.spawnPos;
                Vec3d bossPos = this.getPos().add(0, 2, 0);
                Vec3d targetPos = target.toCenterPos();

                for (double i = 0; i < 1.0; i += 0.15) {
                    Vec3d point = bossPos.lerp(targetPos, i);
                    sw.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, point.x, point.y, point.z, 1, 0, 0, 0, 0);
                    sw.spawnParticles(new DustParticleEffect(new org.joml.Vector3f(1.0f, 0.0f, 0.0f), 1.0f),
                            point.x, point.y, point.z, 1, 0, 0, 0, 0);
                }

                if (this.failTimer <= 0) {
                    executeMassiveImpact(sw, target);
                    this.isFailing = false;
                } else {
                    BlockPos center = this.spawnPos;
                    DustParticleEffect redDust = new DustParticleEffect(new Vector3f(1.0f, 0.0f, 0.0f), 2.0f);
                    for (int i = 0; i < 360; i += 3) {
                        double rad = Math.toRadians(i);
                        double px = center.getX() + Math.cos(rad) * 25;
                        double pz = center.getZ() + Math.sin(rad) * 25;
                        sw.spawnParticles(redDust, px, center.getY() + 1.5, pz, 5, 0.1, 0.1, 0.1, 0.0);
                    }
                }
            }
            return; // WICHTIG: Keine weitere Logik (auch kein Countdown) während des Schusses!
        }

        // 3. COUNTDOWN & ACTIONBAR
        if (ultPhaseTimer <= 0) {
            // Nur runterzählen, wenn nicht gerade der Fail-Schuss läuft
            this.ultCountdown--;

            if (this.age % 10 == 0) {
                // Fix: Verhindert negative Zahlen in der Anzeige
                int displayTime = Math.max(0, ultCountdown / 20);
                Text timerText = Text.literal("Time Until Death Bomb Strike: ")
                        .append(Text.literal(displayTime + "s").formatted(Formatting.RED, Formatting.BOLD));

                for (ServerPlayerEntity player : ((ServerWorld)this.getWorld()).getPlayers()) {
                    player.sendMessage(timerText, true);
                }
            }

            if (this.ultCountdown <= 0) {
                triggerUltFail();
                // Wir setzen ultCountdown auf 1, damit dieser Block nicht
                // sofort wieder triggert, bis isFailing im nächsten Tick übernimmt
                this.ultCountdown = 0;
                return;
            }
        }

        // 2. VORLAUF-PHASE (Boss teleportiert sich hoch)
        if (ultPhaseTimer > 0) {
            ultPhaseTimer--;
            if (ultPhaseTimer == 0) {
                Text instruction = Text.literal("Just give me 90 more seconds! And dont dare to ")
                        .append(Text.literal("stand in the glowing circles to deflect my Magna Bombs 5 times in a row to stop me!")
                                .formatted(Formatting.YELLOW, Formatting.ITALIC)); // Gelb und kursiv für den Fokus

                this.bossTalk(instruction);                double angle = this.random.nextDouble() * Math.PI * 2;
                double tx = this.spawnPos.getX() + Math.cos(angle) * 30;
                double tz = this.spawnPos.getZ() + Math.sin(angle) * 30;
                double ty = this.spawnPos.getY() + 45;

                this.requestTeleport(tx, ty, tz);
                this.setNoGravity(true);
                this.getWorld().playSound(null, tx, ty, tz, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.HOSTILE, 2.0f, 1.0f);

                prepareNextCircle();
                spawnUltBomb();
            }
        }

        // 3. TENNIS-LOGIK
        if (ultPhaseTimer <= 0 && this.stunTimer <= 0 && this.hasNoGravity()) {
            if (targetCirclePos != null) {
                drawUltCircleParticles();
                checkPlayerInCircle();
            }

            this.setVelocity(0, 0, 0);
            this.velocityDirty = true;
        }

        // 4. AUTO-DEFLECT & FINISH CHECK
        if (currentUltProjectile != null && currentUltProjectile.isAlive()) {
            double distToBoss = currentUltProjectile.squaredDistanceTo(this.getPos().add(0, 3, 0));

            if (distToBoss < 16.0 && currentUltProjectile.getVelocity().y > 0) {
                if (this.ultDeflections >= 5) {
                    finishUltPhase();
                } else {
                    prepareNextCircle();
                    this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.HOSTILE, 2.0f, 0.5f);

                    Vec3d targetPos = targetCirclePos.toCenterPos();
                    double speed = 0.5 + (this.ultDeflections * 0.095);
                    Vec3d velocity = targetPos.subtract(currentUltProjectile.getPos()).normalize().multiply(speed);

                    currentUltProjectile.setVelocity(velocity);
                    currentUltProjectile.velocityModified = true;
                }
            }

            if (targetCirclePos != null && currentUltProjectile.getVelocity().lengthSquared() < 0.01) {
                Vec3d dir = targetCirclePos.toCenterPos().subtract(currentUltProjectile.getPos()).normalize().multiply(0.5);
                currentUltProjectile.setVelocity(dir);
            }
        }
    }

    private void prepareNextCircle() {
        double angle = this.random.nextDouble() * Math.PI * 2;
        double dist = this.random.nextDouble() * 25;
        int cx = (int) (this.spawnPos.getX() + Math.cos(angle) * dist);
        int cz = (int) (this.spawnPos.getZ() + Math.sin(angle) * dist);

        this.targetCirclePos = this.getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(cx, 0, cz));
    }

    private void checkPlayerInCircle() {
        if (currentUltProjectile == null || !currentUltProjectile.isAlive()) return;

        double distToCircle = currentUltProjectile.getPos().squaredDistanceTo(targetCirclePos.toCenterPos());

        if (distToCircle < 4.0) {
            if (isPlayerInCircle()) {
                this.ultDeflections++;
                this.getWorld().playSound(null, targetCirclePos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.HOSTILE, 1.0f, 2.0f);

                // IMMER reflektieren, auch beim 5. Mal
                reflectProjectile();

                // Wenn es der 5. Treffer war, löschen wir keinen Kreis mehr (passiert in reflect)
                // Die Logik für den Sieg wandert jetzt in den "Auto-Deflect" Teil,
                // weil der Ball den Boss dort oben erst treffen muss.
            } else {
                // Fehler-Logik (Explosion) bleibt gleich...
                this.getWorld().createExplosion(this, targetCirclePos.getX(), targetCirclePos.getY(), targetCirclePos.getZ(), 4.0f, World.ExplosionSourceType.MOB);
                currentUltProjectile.discard();
                currentUltProjectile = null;
                this.ultDeflections = 0;
                prepareNextCircle();
                spawnUltBomb();
            }
        }
    }

    private void drawUltCircleParticles() {
        // 1. Sicherheits-Checks: Ziel vorhanden und wir sind auf dem Server
        if (this.targetCirclePos == null || !(this.getWorld() instanceof ServerWorld serverWorld)) return;

        // 2. Variablen vorbereiten
        double radius = Math.max(1.0, 5.0 - (this.ultDeflections * 0.8));
        double xCenter = (double) targetCirclePos.getX() + 0.5;
        double yPos = (double) targetCirclePos.getY() + 0.1;
        double zCenter = (double) targetCirclePos.getZ() + 0.5;

        // 3. Partikel-Typ bestimmen (Grün wenn Spieler drin, sonst Flamme)
        var particleType = isPlayerInCircle() ? ParticleTypes.HAPPY_VILLAGER : ParticleTypes.FLAME;

        // 4. Den Kreis berechnen und Pakete senden
        for (int i = 0; i < 360; i += 15) {
            double radians = Math.toRadians(i); // Hier wird radians definiert

            double x = xCenter + Math.cos(radians) * radius;
            double z = zCenter + Math.sin(radians) * radius;

            // 5. Das Netzwerk-Paket manuell erstellen (Force = true)
            // Parameter: Typ, Force, x, y, z, offsetX, offsetY, offsetZ, Speed, Count
            net.minecraft.network.packet.s2c.play.ParticleS2CPacket packet = new net.minecraft.network.packet.s2c.play.ParticleS2CPacket(
                    particleType,
                    true,               // Force-Flag (WICHTIG)
                    x, yPos, z,
                    0.0f, 0.0f, 0.0f,   // Offset (float)
                    0.0f,               // Speed (float)
                    1                   // Count
            );

            // 6. Paket an alle Spieler in der Nähe senden
            for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                // Nur senden, wenn der Spieler in der Nähe ist (z.B. 64 Blöcke), um Lag zu vermeiden
                if (player.squaredDistanceTo(x, yPos, z) < 64 * 64) {
                    player.networkHandler.sendPacket(packet);
                }
            }
        }
    }

    private boolean isPlayerInCircle() {
        double radius = 5.0 - (this.ultDeflections * 0.8);
        for (PlayerEntity p : this.getWorld().getPlayers()) {
            if (p.squaredDistanceTo(targetCirclePos.toCenterPos()) < radius * radius) {
                return true;
            }
        }
        return false;
    }

    private void spawnUltBomb() {
        if (this.targetCirclePos == null || this.getWorld().isClient) return;
        ServerWorld world = (ServerWorld) this.getWorld();

        MagnaBombEntity bomb = new MagnaBombEntity(ModEntities.MAGNA_BOMB, world);

        Vec3d startPos = this.getPos().add(0, 4, 0);
        bomb.refreshPositionAndAngles(startPos.x, startPos.y, startPos.z, 0, 0);

        bomb.setCustomScale(2.5f);
        bomb.setNoGravity(true);

        Vec3d targetPos = targetCirclePos.toCenterPos();

        // GESCHWINDIGKEIT-FIX: Startet bei 0.3 (statt 0.6) und wird langsamer gesteigert
        double speed = 0.4 + (this.ultDeflections * 0.1);

        Vec3d velocity = targetPos.subtract(startPos).normalize().multiply(speed);

        bomb.setVelocity(velocity);
        bomb.velocityModified = true;
        bomb.velocityDirty = true;

        world.spawnEntity(bomb);
        this.currentUltProjectile = bomb;
    }

    private void reflectProjectile() {
        if (currentUltProjectile != null) {
            // 1. Geschwindigkeit zum Boss umkehren
            Vec3d toBoss = this.getPos().add(0, 3, 0).subtract(currentUltProjectile.getPos()).normalize().multiply(1.5);
            currentUltProjectile.setVelocity(toBoss);
            currentUltProjectile.velocityModified = true;

            // 2. FIX: Den Kreis am Boden sofort entfernen
            this.targetCirclePos = null;
        }
    }

    private void finishUltPhase() {
        this.targetCirclePos = null;

        if (currentUltProjectile != null) {
            // Fette Explosion am Boss in der Luft
            if (this.getWorld() instanceof ServerWorld sw) {
                sw.createExplosion(this, currentUltProjectile.getX(), currentUltProjectile.getY(), currentUltProjectile.getZ(), 5.0f, World.ExplosionSourceType.MOB);
            }
            currentUltProjectile.discard();
            currentUltProjectile = null;
        }

        // Boss-Status anpassen
        this.stunTimer = 200; // 10 Sekunden Stun
        this.isUltActive = false;
        this.removeStatusEffect(StatusEffects.GLOWING);

        // Physik: Schwerkraft an, Boss fällt runter
        this.setNoGravity(false);
        this.setVelocity(0, -0.8, 0);
        this.velocityDirty = true;

        // Scale auf 0.9 reduzieren
        var scaleAttr = this.getAttributeInstance(EntityAttributes.GENERIC_SCALE);
        if (scaleAttr != null) scaleAttr.setBaseValue(0.9);

        // Optik & Sound
        this.bossBar.setColor(BossBar.Color.GREEN);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 3.0f, 0.5f);
    }

    public int getStunTimer() {
        return this.stunTimer;
    }

    private void triggerUltFail() {
        this.isFailing = true;
        this.failTimer = 80; // 2 Sekunden Zeit zum Weglaufen

        if (this.getWorld() instanceof ServerWorld sw) {
            this.bossTalk("Now, Its time to die!");
            BlockPos center = this.spawnPos;
            // Sound: Ein tiefes Grollen kündigt den Einschlag an
            sw.playSound(null, center, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 2.0f, 0.5f);

            // SOFORTIGER WARN-KREIS am Boden (20 Blöcke Radius)
            DustParticleEffect redDust = new DustParticleEffect(new Vector3f(1.0f, 0.0f, 0.0f), 2.0f);
            for (int i = 0; i < 360; i += 3) {
                double rad = Math.toRadians(i);
                double px = center.getX() + Math.cos(rad) * 25;
                double pz = center.getZ() + Math.sin(rad) * 25;
                sw.spawnParticles(redDust, px, center.getY() + 1.5, pz, 5, 0.1, 0.1, 0.1, 0.0);
            }
        }
    }

    private void executeMassiveImpact(ServerWorld sw, BlockPos center) {
        // 1. Actionbar leeren
        for (ServerPlayerEntity player : sw.getPlayers()) {
            player.sendMessage(Text.empty(), true);
        }

        // 2. Schaden & Partikel
        sw.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, center.getX(), center.getY() + 1, center.getZ(), 1, 0, 0, 0, 0);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 3.0f, 0.5f);

        float failDamage = 70.0f;
        for (ServerPlayerEntity player : sw.getPlayers()) {
            if (player.squaredDistanceTo(center.toCenterPos()) < 20 * 20) {
                player.damage(sw.getDamageSources().mobAttack(this), failDamage);

                player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 400, 0));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 400, 1));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 400, 2));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 400, 2));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 400, 1));
            }
        }

        // 3. Reset
        this.isUltActive = false;
        this.ultCountdown = 0; // Reset für den nächsten Run
        this.setNoGravity(false);
        var scaleAttr = this.getAttributeInstance(EntityAttributes.GENERIC_SCALE);
        if (scaleAttr != null) scaleAttr.setBaseValue(1.2);
        this.bossBar.setColor(BossBar.Color.PINK);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 3.0f, 0.5f);
        this.removeStatusEffect(StatusEffects.GLOWING);
        this.setVelocity(0, -1.5, 0);
    }

    private void bossTalk(Text message) {
        if (this.getWorld() instanceof ServerWorld sw) {
            Text prefix = Text.literal("[BOSS] ").formatted(Formatting.RED)
                    .append(Text.literal("Magma Titan").formatted(Formatting.GOLD, Formatting.BOLD))
                    .append(Text.literal(": ").formatted(Formatting.WHITE));

            Text fullText = prefix.copy().append(message);

            for (ServerPlayerEntity player : sw.getPlayers()) {
                player.sendMessage(fullText, false);
                player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value());
            }
        }
    }

    private void bossTalk(String message) {
        this.bossTalk(Text.literal(message));
    }

    private void updateBossBar() {
        // 1. HP Prozent berechnen
        float healthPercent = this.getHealth() / this.getMaxHealth();
        this.bossBar.setPercent(healthPercent);

        // 2. Optionale Logik: Farben basierend auf Status automatisch anpassen
        // Wenn du willst, dass die Bar während des Stuns IMMER grün ist, egal was passiert:
        if (this.stunTimer > 0) {
            this.bossBar.setColor(BossBar.Color.GREEN);
        } else if (this.isUltActive) {
            if (this.isFailing) {
                this.bossBar.setColor(BossBar.Color.RED); // Rot während des Todesstrahls
            } else {
                this.bossBar.setColor(BossBar.Color.YELLOW);
            }
        } else if (this.dataTracker.get(RAGE_MODE)) {
            this.bossBar.setColor(BossBar.Color.PINK);
        } else {
            this.bossBar.setColor(BossBar.Color.PURPLE);
        }
    }
}