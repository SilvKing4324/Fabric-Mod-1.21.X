package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.component.DataComponentTypes;
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
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MagnaTitanEntity extends HostileEntity {

    private long nextMusicStartTime = 0; // Speichert den Zeitpunkt (Echtzeit), wann der nächste Song starten soll
    private double hoverYTarget = 0;
    private int pottingTimer = -1; // -1 heißt: Er trinkt gerade nicht
    private int pendingStrengthLevel = 0;
    private int blackHoleTimer = 0;
    private int fireballCooldown = 0;
    private int anvilCooldown = 0;       // Cooldown von 25 Sek (500 Ticks)
    private int anvilDropsLeft = 0;      // Wie viele Sprünge noch übrig sind
    private int anvilJumpTimer = 0;      // Timer für die Pause zwischen den Sprüngen

    private int healMoveCooldown = 0;
    private boolean isHealingPhase = false;
    private int healTicksActive = 0;
    private final List<BlockPos> activeBeacons = new ArrayList<>();

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    private BlockPos spawnPos;

    private int idleAnimationTimeout = 0;
    public int attackAnimationTimeout = 0;
    private int minionCount = 0;

    private boolean phase1Triggered = false; // 100% (First Spawn)
    private boolean phase2Triggered = false; // 50%
    private boolean phase3Triggered = false; // 1/6 HP (~16%)    public final AnimationState attackAnimationState = new AnimationState();
    private boolean hasDone56Blowback = false;
    private boolean hasDone26Blowback = false;
    private boolean hasDoneBlackHole = false;

    private int goldenApplesLeft = 3;

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

    private final ServerBossBar bossBar = new ServerBossBar(Text.literal("Magna Titan"),
            BossBar.Color.RED, BossBar.Style.NOTCHED_6);

    public MagnaTitanEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public @Nullable EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData);
        this.spawnPos = this.getBlockPos();

        // 1. Ausrüstung anlegen (Schwert & Rüstung)
        this.initEquipment(world.getRandom(), difficulty);

        // 2. Schaden basierend auf Difficulty setzen
        double baseDamage = 10.0;
        if (world.getDifficulty() == net.minecraft.world.Difficulty.NORMAL) baseDamage = 15.0;
        if (world.getDifficulty() == net.minecraft.world.Difficulty.HARD) baseDamage = 20.0;

        var attackDamageAttr = this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (attackDamageAttr != null) {
            attackDamageAttr.setBaseValue(baseDamage);
        }

        return entityData;
    }

    @Override
    protected void initEquipment(net.minecraft.util.math.random.Random random, LocalDifficulty localDifficulty) {
        var registryManager = this.getWorld().getRegistryManager();
        var enchantments = registryManager.getWrapperOrThrow(net.minecraft.registry.RegistryKeys.ENCHANTMENT);
        // Rüstung setzen
        this.equipStack(EquipmentSlot.HEAD, new ItemStack(ModItems.STARLIGHT_ASHES));
        // Waffe setzen
        ItemStack sword = new ItemStack(ModItems.SPECTRE_STAFF);
        sword.addEnchantment(enchantments.getOrThrow(net.minecraft.enchantment.Enchantments.KNOCKBACK), 5);
        this.equipStack(EquipmentSlot.MAINHAND, sword);
        // Drop-Chancen auf 0 setzen (damit nichts gedroppt wird)
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
        this.targetSelector.add(2, new ActiveTargetGoal<>(
                this,
                LivingEntity.class,
                true,  // Sicht prüfen
                target -> !(target instanceof MagnaTitanEntity) // hier alle TitanPlayer ausschließen
        ));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {

        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH,1000)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED,0.33)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE,20)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE,64)
                .add(EntityAttributes.GENERIC_ARMOR,10)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS,0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,1.0)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK,1);



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

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) {
            this.setupAnimationStates();

            // RAGE MODE PARTIKEL
            if (this.isRaging()) {
                // "Angry Villager" Partikel (die roten Blitze)
                if (this.random.nextInt(3) == 0) { // Nicht in jedem Tick, damit es nicht laggt
                    double x = this.getX() + (this.random.nextDouble() - 0.5) * 1.5;
                    double y = this.getY() + this.getHeight() + 0.5; // Über dem Kopf
                    double z = this.getZ() + (this.random.nextDouble() - 0.5) * 1.5;

                    this.getWorld().addParticle(ParticleTypes.ANGRY_VILLAGER, x, y, z, 0, 0.1, 0);
                }

                // Optional: Ein paar Rauch-Partikel dazu für den Effekt
                this.getWorld().addParticle(ParticleTypes.SMOKE,
                        this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), 0, 0.05, 0);
            }
        }

        if (this.dataTracker.get(IS_SHIELDED)) {
            // 1. Hover-Logik: Nur steigen, wenn Ziel noch nicht erreicht
            if (this.getY() < hoverYTarget) {
                this.setVelocity(0, 0.05, 0); // Sanftes Aufsteigen
            } else {
                // Ziel erreicht: In der Luft halten (Schwerkraft ausgleichen)
                this.setVelocity(this.getVelocity().x, 0, this.getVelocity().z);
            }
            this.fallDistance = 0;

            // 2. Partikel (unverändert)
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

    public void setAttacking(boolean attacking) {
        this.dataTracker.set(ATTACKING, attacking);
    }

    @Override
    public boolean isAttacking() {
        return this.dataTracker.get(ATTACKING);
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

    /* BossBar */

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
    protected void mobTick() {
        super.mobTick();

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
                    // Heilungs-Berechnung
                    float healPerSecond = (this.healTicksActive <= 200) ? 4.0f : 10.0f;
                    if (this.isRaging()) healPerSecond *= 1.5f;

                    this.heal(healPerSecond / 20.0f); // Pro Tick heilen

                    // Optik
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
            this.anvilJumpTimer = 40;
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

        // 5. MUSIC SYSTEM Not tick based
        long currentTime = System.currentTimeMillis();

        if (currentTime >= this.nextMusicStartTime) {
            for (net.minecraft.server.network.ServerPlayerEntity player : this.bossBar.getPlayers()) {
                this.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.NECRON_DOOM, net.minecraft.sound.SoundCategory.RECORDS, 0.7f, 1.0f);
            }

            // Time * 1000ms
            this.nextMusicStartTime = currentTime + (146 * 1000);
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        // 1. Zuerst die Standard-Logik (Animationen, etc.)
        super.onDeath(damageSource);

        if (!this.getWorld().isClient) {
            // 2. Musik stoppen und Challenge-Sound für alle Beteiligten abspielen
            for (ServerPlayerEntity player : this.bossBar.getPlayers()) {
                // Stoppt die Boss-Musik
                Objects.requireNonNull(player.getServer()).getCommandManager().executeWithPrefix(
                        player.getCommandSource(), "stopsound @s record silvkingsmod:necron_doom"
                );

                // Spielt den Challenge-Complete Sound ab (den vom UI/Erfolg)
                this.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, net.minecraft.sound.SoundCategory.MASTER, 1.0f, 1.0f);
            }

            // 3. Den Spectre Staff droppen
            // 'this.dropStack' spawnt das Item genau an der Position, wo der Boss stirbt
            this.dropStack(new ItemStack(ModItems.SPECTRE_STAFF));

            // Optional: Eine Nachricht an alle Kämpfer
            for (ServerPlayerEntity player : this.bossBar.getPlayers()) {
                player.sendMessage(Text.literal("The Earth shakes as the titan falls. Everything breaks apart!")
                        .formatted(net.minecraft.util.Formatting.GOLD, net.minecraft.util.Formatting.BOLD), false);
            }
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

        return super.damage(source, amount);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("GoldenApplesLeft", this.goldenApplesLeft);

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
        if (nbt.contains("GoldenApplesLeft")) {
            this.goldenApplesLeft = nbt.getInt("GoldenApplesLeft");
        }

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

    public boolean isRaging() {
        return this.dataTracker.get(RAGE_MODE);
    }

    private void setupSpawn(LivingEntity entity) {
        // Position leicht versetzt zum Boss
        entity.refreshPositionAndAngles(
                this.getX() + this.random.nextGaussian() * 4,
                this.getY(),
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
                new net.minecraft.component.type.CustomModelDataComponent(1));
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
            this.refreshPositionAndAngles(player.getX(), player.getY() + 12.0, player.getZ(), this.getYaw(), this.getPitch());
            if (!this.getWorld().isClient) {
                ServerWorld world = (ServerWorld) this.getWorld();

                // 2. Deine Custom Projektil-Entity erzeugen
                MagnaAnvilEntity anvil = new MagnaAnvilEntity(world, this);

                // Position direkt unter dem Boss setzen
                anvil.setPosition(this.getX(), this.getY() - 1.0, this.getZ());

                // 3. Optik: Custom Model Data für lila Amboss (ID 2)
                ItemStack anvilStack = new ItemStack(Items.ANVIL);
                anvilStack.set(net.minecraft.component.DataComponentTypes.CUSTOM_MODEL_DATA,
                        new net.minecraft.component.type.CustomModelDataComponent(2));
                anvil.setItem(anvilStack);

                // 4. Den Amboss spawnen
                world.spawnEntity(anvil);

                // 5. Sound-Effekte
                world.playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_BREEZE_WIND_BURST, net.minecraft.sound.SoundCategory.HOSTILE, 2.0f, 0.5f);

                // Ein schweres "Metall-Sausen" beim Start
                world.playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.BLOCK_ANVIL_USE, net.minecraft.sound.SoundCategory.HOSTILE, 1.0f, 0.2f);
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

            // 3 Beacons an zufälligen Positionen spawnen (gleiche Y wie der Boss)
            for (int i = 0; i < 3; i++) {
                double angle = this.random.nextDouble() * Math.PI * 2;
                double distance = 5.0 + this.random.nextDouble() * 5.0; // 5 bis 10 Blöcke weg

                int bx = (int) (this.getX() + Math.cos(angle) * distance);
                int bz = (int) (this.getZ() + Math.sin(angle) * distance);
                BlockPos beaconPos = new BlockPos(bx, (int)this.getY(), bz);

                // Den Block setzen (Wir nutzen erstmal Obsidian/Gold als Platzhalter, falls dein Block noch nicht fertig ist)
                world.setBlockState(beaconPos, ModBlocks.TITANIUM_BEACON.getDefaultState());
                this.activeBeacons.add(beaconPos);

                // Effekt beim Erscheinen
                world.spawnParticles(ParticleTypes.EXPLOSION, bx + 0.5, this.getY() + 0.5, bz + 0.5, 1, 0, 0, 0, 0);
            }

            world.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.HOSTILE, 2.0f, 0.5f);
        }
    }

    private void stopHealPhase() {
        this.setHealing(false);
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
            executePottingSequence(2);

            this.dataTracker.set(RAGE_MODE, true);
            this.bossBar.setColor(BossBar.Color.PINK);

            var scaleAttr = this.getAttributeInstance(EntityAttributes.GENERIC_SCALE);
            if (scaleAttr != null) {
                scaleAttr.setBaseValue(1.1);
            }

            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ENTITY_ENDER_DRAGON_GROWL, net.minecraft.sound.SoundCategory.HOSTILE, 1.5f, 0.0f);

            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, net.minecraft.sound.SoundCategory.HOSTILE, 1.0f, 1.0f);

            hasDone26Blowback = true;
        }

        // Phase 3: 1/6 HP (~16%) -> Finale Minion-Welle
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
}