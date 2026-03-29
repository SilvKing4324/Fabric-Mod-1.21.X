package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.item.ModItems;
import org.jetbrains.annotations.Nullable;

public class MagnaMinionEntity extends HostileEntity {

    public final AnimationState idleAnimationState = new AnimationState();
    private int idleAnimationTimeout = 0;

    public final AnimationState attackAnimationState = new AnimationState();
    public int attackAnimationTimeout = 0;

    private int goldenApplesLeft = 3;

    private static final TrackedData<Integer> DATA_ID_TYPE_VARIANT =
            DataTracker.registerData(MagnaMinionEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> ATTACKING =
            DataTracker.registerData(MagnaMinionEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public MagnaMinionEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.setPersistent();
    }

    @Override
    public @Nullable EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        entityData = super.initialize(world, difficulty, spawnReason, entityData);

        // 1. Ausrüstung anlegen (Schwert & Rüstung)
        this.initEquipment(world.getRandom(), difficulty);

        // 2. Schaden basierend auf Difficulty setzen (Easy: 1, Normal: 4, Hard: 7)
        double baseDamage = 1.0;
        if (world.getDifficulty() == net.minecraft.world.Difficulty.NORMAL) baseDamage = 4.0;
        if (world.getDifficulty() == net.minecraft.world.Difficulty.HARD) baseDamage = 7.0;

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

        // Waffe setzen
        ItemStack sword = new ItemStack(ModItems.TITANIUM_SWORD);
        sword.addEnchantment(enchantments.getOrThrow(net.minecraft.enchantment.Enchantments.KNOCKBACK), 1);
        sword.addEnchantment(enchantments.getOrThrow(net.minecraft.enchantment.Enchantments.FIRE_ASPECT), 1);
        this.equipStack(EquipmentSlot.MAINHAND, sword);
        // Drop-Chancen auf 0 setzen (damit nichts gedroppt wird)
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.setEquipmentDropChance(slot, 0.0f);
        }
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new AttackGoal(this));

        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 4.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(0, new RevengeGoal(this));
    }

    public static DefaultAttributeContainer.Builder createAttributes() {

        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH,40)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED,0.35)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE,15)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE,64)
                .add(EntityAttributes.GENERIC_ARMOR,5)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS,5)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,-0.5)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK,2.5);



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
        }
    }

    @Override
    public boolean isFireImmune() {
        return true; // Er brennt nicht
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        if (!this.getWorld().isClient) {
            // Suche den Boss in der Nähe (z.B. 50 Blöcke Radius)
            var bosses = this.getWorld().getEntitiesByClass(MagnaTitanEntity.class,
                    this.getBoundingBox().expand(70.0), entity -> true);

            for (MagnaTitanEntity boss : bosses) {
                boss.decrementMinionCount();
            }
        }
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean success = super.tryAttack(target);

        if (success && target instanceof LivingEntity livingTarget) {
            // 1. Bestehende Heilungs-Logik
            float damageDealt = (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            this.heal(damageDealt * 0.4f);

            // 2. Rückstoß für den SPIELER (Extra Knockback)
            // Wir geben dem Spieler einen zusätzlichen Stoß nach hinten
            double d = target.getX() - this.getX();
            double e = target.getZ() - this.getZ();
            livingTarget.takeKnockback(1.5, -d, -e); // 1.5 ist die Stärke des zusätzlichen Schubs

            // 3. Rückstoß für das MINION (5 Blöcke nach hinten springen)
            if (!this.getWorld().isClient) {
                // Wir berechnen die Richtung weg vom Ziel
                double deltaX = this.getX() - target.getX();
                double deltaZ = this.getZ() - target.getZ();

                // Normalisieren (damit der Sprung immer gleich weit ist)
                double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
                if (distance > 0) {
                    deltaX /= distance;
                    deltaZ /= distance;
                }

                // Geschwindigkeit setzen: 1.5 horizontal und 0.4 nach oben für einen kleinen Hopser
                this.setVelocity(deltaX * 1.5, 0.4, deltaZ * 1.5);
                this.velocityDirty = true; // Wichtig, damit der Server die Bewegung an den Client sendet
            }

            // 4. Partikel und Sounds (deine bestehende Logik)
            if (!this.getWorld().isClient) {
                ServerWorld serverWorld = (ServerWorld) this.getWorld();
                serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.HEART,
                        this.getX(), this.getY() + 1.5, this.getZ(), 5, 0.3, 0.3, 0.3, 0.1);

                // Neuer Sound für den "Backleap"
                this.getWorld().playSound(null, this.getBlockPos(),
                        SoundEvents.ENTITY_GENERIC_SMALL_FALL, net.minecraft.sound.SoundCategory.HOSTILE, 1.0f, 1.5f);
            }
        }

        return success;
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
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        // 1. Prüfen, ob das Minion den Resistenz-Effekt vom Golem hat (Resi 4 = Amplifier 3)
        var effect = this.getStatusEffect(net.minecraft.entity.effect.StatusEffects.RESISTANCE);

        if (effect != null && effect.getAmplifier() >= 3) {
            // 2. Prüfen, ob ein Spieler der Angreifer ist
            if (source.getAttacker() instanceof net.minecraft.server.network.ServerPlayerEntity player) {

                // Sound-Effekt: Ein "Pling" oder Schild-Geräusch am Ort des Minions
                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_BREEZE_DEFLECT,
                        net.minecraft.sound.SoundCategory.HOSTILE, 0.5f, 1.5f);

                // Nachricht in die ActionBar (unten über der Hotbar)
                player.sendMessage(net.minecraft.text.Text.literal("This Minion is protected by the Lava Golem!")
                        .formatted(net.minecraft.util.Formatting.GOLD, net.minecraft.util.Formatting.BOLD), true);

                // Optionale Partikel: Rauch oder Lava-Funken beim Minion
                if (!this.getWorld().isClient) {
                    ((net.minecraft.server.world.ServerWorld)this.getWorld()).spawnParticles(
                            net.minecraft.particle.ParticleTypes.LAVA,
                            this.getX(), this.getY() + 1.0, this.getZ(), 5, 0.2, 0.2, 0.2, 0.1);
                }
            }
        }

        // Den normalen Schaden trotzdem ausführen (wird durch Resi 4 eh fast 0 sein)
        return super.damage(source, amount);
    }

    /* Sounds */


    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_BREEZE_HURT;
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
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        // Wir speichern den aktuellen Zähler unter dem Namen "GoldenApplesLeft"
        nbt.putInt("GoldenApplesLeft", this.goldenApplesLeft);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        // Wir laden den Wert beim Neustart der Welt oder beim Spawn
        if (nbt.contains("GoldenApplesLeft")) {
            this.goldenApplesLeft = nbt.getInt("GoldenApplesLeft");
        }
    }
}
