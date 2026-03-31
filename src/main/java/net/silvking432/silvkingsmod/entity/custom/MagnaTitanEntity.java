package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.boss.*;
import net.minecraft.entity.damage.*;
import net.minecraft.entity.data.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.particle.*;
import net.minecraft.potion.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.*;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.effect.ModEffects;
import net.silvking432.silvkingsmod.entity.ModEntities;
import net.silvking432.silvkingsmod.entity.ai.MagnaTitanJumpAttackGoal;
import net.silvking432.silvkingsmod.entity.ai.MagnaTitanMeleeGoal;
import net.silvking432.silvkingsmod.item.ModItems;
import net.silvking432.silvkingsmod.particle.ModParticles;
import net.silvking432.silvkingsmod.sound.ModSounds;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class MagnaTitanEntity extends HostileEntity {
    public enum BossState {
        ATTACKING,
        POTTING,
        HEALING,
        BLACK_HOLE,
        ULTIMATE,
        STUNNED,
        SHIELDED,
        ANVIL,
        FAILING // The player failed
    }

    private int pottingTimer = -1;
    private int idleAnimationTimeout = 0, musicPhase = 0;
    private int fireballCooldown = 0, pendingStrengthLevel = 0, minionCount = 0;
    private int anvilCooldown = 0, anvilDropsLeft = 0, anvilJumpTimer = 0;
    private int healMoveCooldown = 0, healTicksActive = 0;
    private int blackHoleTimer = 0, ultCountdown = 0, ultPhaseTimer = 0, ultDeflections = 0, failTimer = 0;
    private int stunTimer = 0;

    public int attackAnimationTimeout = 0;

    private double hoverYTarget = 0;
    private long nextMusicStartTime = 0;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();

    private boolean phase1Triggered = false, phase2Triggered = false, phase3Triggered = false, ultPhaseTriggered = false;
    private boolean hasDone56Blowback = false, hasDone26Blowback = false, hasDoneBlackHole = false;
    private boolean phaseShiftPending = false;

    private static final TrackedData<Boolean> RAGE_MODE =
            DataTracker.registerData(MagnaTitanEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> BOSS_STATE =
            DataTracker.registerData(MagnaTitanEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private final List<BlockPos> activeBeacons = new ArrayList<>();

    private BlockPos spawnPos;
    private BlockPos targetCirclePos;

    private Entity currentUltProjectile;

    private final ServerBossBar bossBar = new ServerBossBar(Text.literal("Magna Titan"), BossBar.Color.BLUE, BossBar.Style.NOTCHED_6);

    public MagnaTitanEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setPersistent();
    }

    @Override
    public @Nullable EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData);

        this.spawnPos = this.getBlockPos();
        this.musicPhase = 0;
        this.nextMusicStartTime = System.currentTimeMillis() + 14800;
        this.setState(BossState.ATTACKING);

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

        if (!world.isClient()) {
            ServerWorld serverWorld = world.toServerWorld();

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
        this.goalSelector.add(1, new MagnaTitanJumpAttackGoal(this));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 4.0F));
        this.goalSelector.add(4, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(0, new RevengeGoal(this));
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);
        builder.add(RAGE_MODE, false);
        builder.add(BOSS_STATE, BossState.ATTACKING.ordinal());
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

        if (this.getState() == BossState.SHIELDED) {
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
        if (this.healMoveCooldown > 0) this.healMoveCooldown--;

        switch (this.getState()) {
            case ULTIMATE   -> this.tickUltLogic();
            case STUNNED    -> this.tickStunLogic();
            case POTTING    -> this.tickPottingPhase();
            case HEALING    -> this.tickHealingPhase();
            case BLACK_HOLE -> this.tickBlackHolePhase();
            case SHIELDED   -> this.tickShieldPhaseLogic();
            case FAILING -> this.tickFailLogic();
            case ATTACKING  -> {
                float healthPercent = this.getHealth() / this.getMaxHealth();

                this.handlePhaseTriggers(healthPercent);
                this.tickAnvilLogic(healthPercent);
                this.checkHealTrigger(healthPercent);
            }
        }
    }

    @Override
    public boolean isAttacking() {
        return this.getState() == BossState.ATTACKING;
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
        BossState currentState = this.getState();
        boolean isInvulnerable = currentState == BossState.SHIELDED || currentState == BossState.HEALING || currentState == BossState.BLACK_HOLE;

        if (isInvulnerable && !source.isOf(DamageTypes.OUT_OF_WORLD)) {
            if (!this.getWorld().isClient) {
                this.playSound(SoundEvents.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.5f, 2.0f);
            }
            return false;
        }

        if (currentState == BossState.ULTIMATE) {
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.HOSTILE, 1.0f, 1.5f);
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

    private void executePottingSequence(int strengthLevel) {
        if (this.getWorld().isClient) return;
        this.setState(BossState.POTTING);
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
        this.setState(BossState.SHIELDED);
        this.hoverYTarget = this.getY() + 3.0;
        this.getNavigation().stop();
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

    public void setState(BossState state) {
        this.dataTracker.set(BOSS_STATE, state.ordinal());
    }

    public BossState getState() {
        return BossState.values()[this.dataTracker.get(BOSS_STATE)];
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
            this.setState(BossState.ANVIL);
            this.refreshPositionAndAngles(player.getX(), player.getY() + 15.0, player.getZ(), this.getYaw(), this.getPitch());
            if (!this.getWorld().isClient) {
                ServerWorld world = (ServerWorld) this.getWorld();

                MagnaAnvilEntity anvil = new MagnaAnvilEntity(world, this);

                anvil.setPosition(this.getX(), this.getY() - 1.0, this.getZ());

                ItemStack anvilStack = new ItemStack(Items.ANVIL);
                anvilStack.set(DataComponentTypes.CUSTOM_MODEL_DATA,
                        new CustomModelDataComponent(2));
                anvil.setItem(anvilStack);
                world.spawnEntity(anvil);
                world.playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_BREEZE_WIND_BURST, SoundCategory.HOSTILE, 2.0f, 0.5f);

                world.playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.BLOCK_ANVIL_USE, SoundCategory.HOSTILE, 1.0f, 0.2f);
            }

            this.anvilDropsLeft--;

            if (this.anvilDropsLeft <= 0) {
                this.setState(BossState.ATTACKING);
                this.anvilCooldown = 500;
            } else {
                this.setState(BossState.ATTACKING);
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
        this.setState(BossState.HEALING);
        this.teleportToCenter();
        this.activeBeacons.clear();

        if (!this.getWorld().isClient) {
            ServerWorld world = (ServerWorld) this.getWorld();

            int beaconCount = this.isRaging() ? 5 : 3;

            double radius = this.isRaging() ? 20.0 : 18.0;

            for (int i = 0; i < beaconCount; i++) {
                double baseAngle = i * (360.0 / beaconCount);
                double variation = this.random.nextDouble() * 20 - 10;
                double radians = Math.toRadians(baseAngle + variation);

                int bx = (int) (this.spawnPos.getX() + Math.cos(radians) * radius);
                int bz = (int) (this.spawnPos.getZ() + Math.sin(radians) * radius);

                BlockPos beaconPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(bx, 0, bz));

                world.setBlockState(beaconPos, ModBlocks.TITANIUM_BEACON.getDefaultState());
                this.activeBeacons.add(beaconPos);

                world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, beaconPos.getX() + 0.5, beaconPos.getY() + 0.5, beaconPos.getZ() + 0.5, 1, 0, 0, 0, 0);
            }

            float pitch = this.isRaging() ? 0.4f : 0.5f;
            world.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.HOSTILE, 2.0f, pitch);
        }
    }

    private void stopHealPhase() {
        this.setState(BossState.ATTACKING);
        this.healTicksActive = 0;
        this.healMoveCooldown = this.isRaging() ? 600 : 900; // 30s oder 45s

        if (!this.getWorld().isClient) {
            for (BlockPos pos : activeBeacons) {
                this.getWorld().breakBlock(pos, false);
            }
            this.activeBeacons.clear();
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.HOSTILE, 2.0f, 1.0f);
        }
    }

    private void handlePhaseTriggers(float healthPercent) {
        if (this.getState() != BossState.ATTACKING && phase1Triggered) return;

        // --- Phase 1: 100% HP (Erster Spawn) ---
        if (!phase1Triggered) {
            if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.setWeather(12000, 0, false, false);
                serverWorld.setTimeOfDay(6000);

                for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                    player.sendMessage(Text.literal("The sky clears as the Magna Titan awakens...")
                            .formatted(Formatting.GOLD, Formatting.ITALIC), true);
                }
            }
            teleportToCenter();
            startShieldPhase(2, 0, 1); // Diese Methode sollte setBossState(SHIELDED) rufen
            phase1Triggered = true;
        }

        // --- 5/6 HP Schwelle (~83%) -> Blowback + Trank 1 ---
        if (healthPercent <= 0.83f && !hasDone56Blowback) {
            executeBlowbackWave();
            executePottingSequence(1);
            hasDone56Blowback = true;
        }

        // --- 4/6 HP Schwelle (~66%) -> Black Hole ---
        if (healthPercent <= 0.66f && !hasDoneBlackHole) {
            if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.setTimeOfDay(13400);
                for (ServerPlayerEntity player : serverWorld.getPlayers(p -> p.squaredDistanceTo(this) < 10000)) {
                    player.sendMessage(Text.literal("Watch This!")
                            .formatted(Formatting.DARK_PURPLE, Formatting.BOLD, Formatting.ITALIC), true);
                }
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 1.5f, 0.5f);
            }

            this.setState(BossState.BLACK_HOLE);
            this.blackHoleTimer = 200;
            this.hasDoneBlackHole = true;
        }

        // --- Phase 2: 50% HP (3/6) -> Schild + Minions ---
        if (healthPercent <= 0.5f && !phase2Triggered) {
            teleportToCenter();
            startShieldPhase(3, 2, 1); // Setzt State auf SHIELDED
            phase2Triggered = true;
        }

        // --- 2/6 HP Schwelle (~33%) -> Rage Mode + Potting 2 ---
        if (healthPercent <= 0.33f && !hasDone26Blowback) {
            executeBlowbackWave();
            executePottingSequence(3);

            this.dataTracker.set(RAGE_MODE, true);

            if (!this.getWorld().isClient && this.getWorld() instanceof ServerWorld serverWorld) {
                Text rageTitle = Text.literal("⚠ RAGE MODE ⚠").formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD);
                for (ServerPlayerEntity player : serverWorld.getPlayers(p -> p.squaredDistanceTo(this) < 2500)) {
                    player.networkHandler.sendPacket(new TitleS2CPacket(rageTitle));
                    player.networkHandler.sendPacket(new TitleFadeS2CPacket(10, 40, 10));
                }
                serverWorld.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 1.5f, 0.0f);
                serverWorld.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.HOSTILE, 1.0f, 1.0f);
            }

            // Stats erhöhen
            this.updateAttributesForRage(); // Tipp: In eigene Methode auslagern für Sauberkeit
            hasDone26Blowback = true;
        }

        // --- Phase 3: 25% HP -> Ult Phase ---
        if (healthPercent <= 0.25f && !this.ultPhaseTriggered) {
            this.ultPhaseTriggered = true;
            startUltPhase(); // Muss setBossState(ULTIMATE) rufen
        }

        // --- Phase 3: 16% HP -> Final Minion Wave ---
        if (healthPercent <= 0.166f && !phase3Triggered) {
            teleportToCenter();
            startShieldPhase(4, 2, 2); // Setzt State auf SHIELDED
            phase3Triggered = true;
        }
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

        if (healthPercent <= 0.66f && musicPhase == 1) {
            this.phaseShiftPending = true;
        }

        if (healthPercent <= 0.33f && (musicPhase == 2 || musicPhase == 3)) {
            this.executeHardCut();
            return;
        }

        if (healthPercent <= 0.17f && musicPhase == 5) {
            this.phaseShiftPending = true;
        }

        if (currentTime >= this.nextMusicStartTime) {

            if (this.phaseShiftPending) {
                this.musicPhase++;
                this.phaseShiftPending = false;
            }
            else if (this.musicPhase % 2 == 0) {
                this.musicPhase++;
            }

            this.playCurrentPhaseSound();
            this.updateNextStartTime();
        }
    }

    private void executeHardCut() {
        this.stopCurrentMusicGlobally();
        this.musicPhase = 4;
        this.phaseShiftPending = false;
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

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            List<ServerPlayerEntity> targets = this.bossBar.getPlayers().isEmpty()
                    ? serverWorld.getPlayers(p -> p.squaredDistanceTo(this.getPos()) < 4096)
                    : new ArrayList<>(this.bossBar.getPlayers());

            for (ServerPlayerEntity player : targets) {
                serverWorld.playSound(null, player.getX(), player.getY(), player.getZ(),
                        sound, SoundCategory.RECORDS, 0.7f, 1.0f);
            }
        }
    }

    private void updateNextStartTime() {
        double duration = switch (musicPhase) {
            case 0 -> 14.75;
            case 1 -> 9.65;
            case 2 -> 9.55;
            case 3 -> 29.0;
            case 4 -> 14.6;
            case 5 -> 18.35;
            case 6 -> 21.3;
            default -> 19.1; // case 7
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
        this.setState(BossState.ULTIMATE);
        this.ultPhaseTimer = 200; // Prepare time
        this.ultDeflections = 0;
        this.ultCountdown = 1800; // Ult Time

        var scaleAttr = this.getAttributeInstance(EntityAttributes.GENERIC_SCALE);
        if (scaleAttr != null) scaleAttr.setBaseValue(2.0);

        this.bossBar.setColor(BossBar.Color.YELLOW);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.HOSTILE, 2.0f, 0.5f);
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 100000, 0, false, false));
        this.bossTalk("I have enough of this! Its time to show you my real power! Just give me some time to cast my death bomb!");
    }

    private void tickUltLogic() {
        if (this.getState() != BossState.ULTIMATE) return;

        if (this.ultPhaseTimer <= 0) {
            this.ultCountdown--;

            if (this.age % 10 == 0) {
                int displayTime = Math.max(0, ultCountdown / 20);
                Text timerText = Text.literal("Time Until Death Bomb Strike: ")
                        .append(Text.literal(displayTime + "s").formatted(Formatting.RED, Formatting.BOLD));

                for (ServerPlayerEntity player : ((ServerWorld)this.getWorld()).getPlayers()) {
                    player.sendMessage(timerText, true);
                }
            }

            if (this.ultCountdown <= 0) {
                this.triggerUltFail();
                return;
            }
        }

        if (this.ultPhaseTimer > 0) {
            this.ultPhaseTimer--;
            if (this.ultPhaseTimer == 0) {
                Text instruction = Text.literal("Just give me 90 more seconds! And dont dare to ")
                        .append(Text.literal("stand in the glowing circles to deflect my Magna Bombs 5 times in a row to stop me!")
                                .formatted(Formatting.YELLOW, Formatting.ITALIC));

                this.bossTalk(instruction);

                double angle = this.random.nextDouble() * Math.PI * 2;
                double tx = this.spawnPos.getX() + Math.cos(angle) * 30;
                double tz = this.spawnPos.getZ() + Math.sin(angle) * 30;
                double ty = this.spawnPos.getY() + 45;

                this.refreshPositionAndAngles(tx, ty, tz, this.getYaw(), this.getPitch());
                this.setNoGravity(true);
                this.getWorld().playSound(null, tx, ty, tz, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.HOSTILE, 2.0f, 1.0f);

                prepareNextCircle();
                spawnUltBomb();
            }
        }

        if (this.ultPhaseTimer <= 0 && this.hasNoGravity()) {
            if (targetCirclePos != null) {
                drawUltCircleParticles();
                checkPlayerInCircle();
            }

            this.setVelocity(0, 0, 0);
            this.velocityDirty = true;
        }

        if (currentUltProjectile != null && currentUltProjectile.isAlive()) {
            double distToBoss = currentUltProjectile.squaredDistanceTo(this.getPos().add(0, 3, 0));

            if (distToBoss < 16.0 && currentUltProjectile.getVelocity().y > 0) {
                if (this.ultDeflections >= 5) {
                    this.finishUltPhase(); // Hier aufrufen!
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

            if (targetCirclePos != null) {
                assert currentUltProjectile != null;
                if (currentUltProjectile.getVelocity().lengthSquared() < 0.01) {
                    Vec3d dir = targetCirclePos.toCenterPos().subtract(currentUltProjectile.getPos()).normalize().multiply(0.5);
                    currentUltProjectile.setVelocity(dir);
                }
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
        if (this.targetCirclePos == null || !(this.getWorld() instanceof ServerWorld serverWorld)) return;

        double radius = Math.max(1.0, 5.0 - (this.ultDeflections * 0.8));
        double xCenter = (double) targetCirclePos.getX() + 0.5;
        double yPos = (double) targetCirclePos.getY() + 0.1;
        double zCenter = (double) targetCirclePos.getZ() + 0.5;

        var particleType = isPlayerInCircle() ? ParticleTypes.HAPPY_VILLAGER : ParticleTypes.FLAME;

        for (int i = 0; i < 360; i += 15) {
            double radians = Math.toRadians(i);

            double x = xCenter + Math.cos(radians) * radius;
            double z = zCenter + Math.sin(radians) * radius;

            ParticleS2CPacket packet = new ParticleS2CPacket(
                    particleType,
                    true,
                    x, yPos, z,
                    0.0f, 0.0f, 0.0f,
                    0.0f,
                    1
            );

            for (ServerPlayerEntity player : serverWorld.getPlayers()) {
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
            Vec3d toBoss = this.getPos().add(0, 3, 0).subtract(currentUltProjectile.getPos()).normalize().multiply(1.5);
            currentUltProjectile.setVelocity(toBoss);
            currentUltProjectile.velocityModified = true;

            this.targetCirclePos = null;
        }
    }

    private void finishUltPhase() {
        this.setState(BossState.STUNNED);
        this.stunTimer = 200; // 10 Sekunden Stun

        this.targetCirclePos = null;
        if (currentUltProjectile != null) {
            if (this.getWorld() instanceof ServerWorld sw) {
                sw.createExplosion(this, currentUltProjectile.getX(), currentUltProjectile.getY(), currentUltProjectile.getZ(), 5.0f, World.ExplosionSourceType.MOB);
            }
            currentUltProjectile.discard();
            currentUltProjectile = null;
        }

        this.removeStatusEffect(StatusEffects.GLOWING);
        this.setNoGravity(false); // Boss fällt zu Boden
        this.setVelocity(0, -0.8, 0);
        this.velocityDirty = true;

        var scaleAttr = this.getAttributeInstance(EntityAttributes.GENERIC_SCALE);
        if (scaleAttr != null) scaleAttr.setBaseValue(0.9);

        this.bossBar.setColor(BossBar.Color.GREEN);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 3.0f, 0.5f);
    }

    private void triggerUltFail() {
        this.setState(BossState.FAILING);
        this.failTimer = 80; // 4 Sekunden Zeit bis zum Einschlag

        if (this.getWorld() instanceof ServerWorld sw) {
            this.bossTalk("Now, it's time to die!");

            BlockPos center = this.spawnPos;
            sw.playSound(null, center, SoundEvents.ENTITY_WITHER_SPAWN, SoundCategory.HOSTILE, 2.0f, 0.5f);

            // Optische Warnung (Roter Ring)
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
        for (ServerPlayerEntity player : sw.getPlayers()) {
            player.sendMessage(Text.empty(), true);
        }

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
                player.addStatusEffect(new StatusEffectInstance(ModEffects.VULNERABILITY, 400, 0));
            }
        }

        ServerPlayerEntity targetPlayer = (ServerPlayerEntity) sw.getClosestPlayer(this.getX(), this.getY(), this.getZ(), 64.0, false);
        if (targetPlayer != null) {
            double tpX = targetPlayer.getX() + (this.random.nextDouble() - 0.5) * 2;
            double tpZ = targetPlayer.getZ() + (this.random.nextDouble() - 0.5) * 2;

            sw.spawnParticles(ParticleTypes.REVERSE_PORTAL, this.getX(), this.getY() + 1, this.getZ(), 40, 0.5, 1, 0.5, 0.2);

            this.refreshPositionAndAngles(tpX, targetPlayer.getY(), tpZ, this.getYaw(), this.getPitch());
            sw.spawnParticles(ParticleTypes.PORTAL, tpX, targetPlayer.getY() + 1, tpZ, 40, 0.5, 1, 0.5, 0.2);
            this.getWorld().playSound(null, tpX, targetPlayer.getY(), tpZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.HOSTILE, 1.0f, 1.0f);

            this.lookAtEntity(targetPlayer, 360f, 360f);
        }

        this.setState(BossState.ATTACKING);
        this.ultCountdown = 0;
        this.setNoGravity(false);

        var scaleAttr = this.getAttributeInstance(EntityAttributes.GENERIC_SCALE);
        if (scaleAttr != null) scaleAttr.setBaseValue(1.2);
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.HOSTILE, 3.0f, 0.5f);

        this.removeStatusEffect(StatusEffects.GLOWING);
        this.setVelocity(0, -1.5, 0);
        this.velocityDirty = true;
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
        float healthPercent = this.getHealth() / this.getMaxHealth();
        this.bossBar.setPercent(healthPercent);
        switch (this.getState()) {
            case STUNNED -> this.bossBar.setColor(BossBar.Color.GREEN);
            case ULTIMATE -> this.bossBar.setColor(BossBar.Color.YELLOW);
            case FAILING -> this.bossBar.setColor(BossBar.Color.RED);
            case HEALING -> this.bossBar.setColor(BossBar.Color.PURPLE);
            case SHIELDED -> this.bossBar.setColor(BossBar.Color.WHITE);

            default -> {
                if (this.isRaging()) {
                    this.bossBar.setColor(BossBar.Color.PINK);
                } else {
                    this.bossBar.setColor(BossBar.Color.BLUE);
                }
            }
        }
    }

    private void tickPottingPhase() {
        if (this.getState() != BossState.POTTING) return;

        this.getNavigation().stop();
        this.setVelocity(0, this.getVelocity().y, 0);

        if (this.pottingTimer > 0) {
            this.pottingTimer--;

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

                this.setState(BossState.ATTACKING);
                this.pottingTimer = 0;
            }
        }
    }

    private void checkHealTrigger(float healthPercent) {
        if (this.getState() != BossState.ATTACKING) {
            return;
        }

        boolean healthOk = healthPercent <= 0.66f;
        boolean moveCooldownOk = this.healMoveCooldown <= 0;
        boolean noAnvils = this.anvilDropsLeft <= 0;

        if (healthOk && moveCooldownOk && noAnvils) {
            if (this.age % 20 == 0) {
                float chance = this.isRaging() ? 0.10f : 0.05f;
                if (this.random.nextFloat() < chance) {
                    this.startHealPhase();
                }
            }
        }
    }

    private void tickHealingPhase() {
        if (this.getState() != BossState.HEALING) return;
        this.getNavigation().stop();
        this.setVelocity(0, this.getVelocity().y, 0);
        this.healTicksActive++;

        if (!this.getWorld().isClient) {
            if (this.age % 5 == 0) spawnHealingTrails();

            activeBeacons.removeIf(pos -> this.getWorld().getBlockState(pos).isAir());

            if (activeBeacons.isEmpty() || this.getHealth() >= this.getMaxHealth()) {
                stopHealPhase();
            } else {
                int healLimit = this.isRaging() ? 300 : 170;
                float healPerSecond = (this.healTicksActive <= healLimit) ? 4.0f : 10.0f;
                if (this.isRaging()) healPerSecond *= 1.5f;

                this.heal(healPerSecond / 20.0f);

                if (this.age % 5 == 0) {
                    ((ServerWorld)this.getWorld()).spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                            this.getX(), this.getY() + 1, this.getZ(), 5, 0.5, 1.0, 0.5, 0.1);
                }
            }
        }
    }

    private void tickBlackHolePhase() {
        if (this.getState() != BossState.BLACK_HOLE) return;

        this.blackHoleTimer--;
        this.getNavigation().stop();

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

            if (this.blackHoleTimer <= 0) {
                this.setState(BossState.ATTACKING);

                this.setNoGravity(false);
                this.setVelocity(0, -0.2, 0);
                this.velocityDirty = true;
            }
        }
    }

    private void tickAnvilLogic(float healthPercent) {
        if (this.anvilCooldown > 0) {
            this.anvilCooldown--;
            return;
        }

        if (this.getState() == BossState.ATTACKING) {

            boolean canStartAnvils = healthPercent <= 0.83f
                    && this.anvilCooldown <= 0
                    && this.anvilDropsLeft <= 0;

            if (canStartAnvils) {
                if (this.age % 20 == 0 && this.random.nextFloat() < 0.10f) {
                    this.anvilDropsLeft = this.isRaging() ? 5 : 3;
                    executeAnvilJump();
                }
            }
        }

        if (this.anvilDropsLeft > 0) {
            if (this.isOnGround() && this.anvilJumpTimer <= 0) {
                this.anvilJumpTimer = 35;
            }

            if (this.anvilJumpTimer > 0) {
                this.anvilJumpTimer--;
                if (this.anvilJumpTimer == 0) {
                    executeAnvilJump();
                }
            }
        }
    }

    private void tickShieldPhaseLogic() {
        if (this.getState() != BossState.SHIELDED) return;
        if (!this.getWorld().isClient && this.getTarget() != null) {
            LivingEntity target = this.getTarget();
            boolean isMinion = target instanceof MagnaMinionEntity || target instanceof LavaGolemEntity || target instanceof MagnaWitchEntity;

            if (!isMinion && target != this) {
                this.fireballCooldown--;
                if (this.fireballCooldown <= 0) {
                    shootMagnaFireball(target);
                    this.fireballCooldown = 60 + this.random.nextInt(40);
                }
            } else {
                this.setTarget(this.getWorld().getClosestPlayer(this, 64.0));
            }
        }

        if (this.minionCount <= 0) {
            this.setState(BossState.ATTACKING);
            this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE, 1.0f, 1.0f);
            this.setVelocity(0, -0.01, 0);
            this.velocityDirty = true;
        }
    }

    private void tickStunLogic() {
        if (this.getState() != BossState.STUNNED) return;

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

        if (this.getWorld() instanceof ServerWorld sw) {
            if (this.age % 15 == 0) {
                sw.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 1, this.getZ(), 1, 0.5, 0.5, 0.5, 0.0);
            }
            if (this.age % 2 == 0) {
                sw.spawnParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 3, 0.4, 0.5, 0.4, 0.02);
            }
        }

        // Ende des Stuns
        if (this.stunTimer <= 0) {
            var scaleAttr = this.getAttributeInstance(EntityAttributes.GENERIC_SCALE);
            if (scaleAttr != null) scaleAttr.setBaseValue(1.2);
            this.bossBar.setColor(BossBar.Color.PINK);

            this.setState(BossState.ATTACKING);
        }
    }

    private void tickFailLogic() {
        if (this.getState() != BossState.FAILING) return;

        this.failTimer--;

        if (this.getWorld() instanceof ServerWorld sw) {
            BlockPos target = this.spawnPos;
            Vec3d bossPos = this.getPos().add(0, 2, 0);
            Vec3d targetPos = target.toCenterPos();

            // Partikelstrahl zum Boden
            for (double i = 0; i < 1.0; i += 0.15) {
                Vec3d point = bossPos.lerp(targetPos, i);
                sw.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, point.x, point.y, point.z, 1, 0, 0, 0, 0);
                sw.spawnParticles(new DustParticleEffect(new org.joml.Vector3f(1.0f, 0.0f, 0.0f), 1.0f),
                        point.x, point.y, point.z, 1, 0, 0, 0, 0);
            }

            if (this.failTimer <= 0) {
                executeMassiveImpact(sw, target);
                } else {
                drawWarningCircle(sw, target);
            }
        }
    }

    private void drawWarningCircle(ServerWorld sw, BlockPos center) {
        DustParticleEffect redDust = new DustParticleEffect(new org.joml.Vector3f(1.0f, 0.0f, 0.0f), 2.0f);

        for (int i = 0; i < 360; i += 5) {
            double rad = Math.toRadians(i);
            double px = center.getX() + 0.5 + Math.cos(rad) * 25;
            double pz = center.getZ() + 0.5 + Math.sin(rad) * 25;

            sw.spawnParticles(redDust, px, center.getY() + 1.5, pz, 1, 0, 0, 0, 0);

            if (i % 20 == 0) {
                sw.spawnParticles(ParticleTypes.FLAME, px, center.getY() + 1.2, pz, 1, 0, 0.1, 0, 0.05);
            }
        }
    }

    private void updateAttributesForRage() {
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
    }
}