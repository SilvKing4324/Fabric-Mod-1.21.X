package net.silvking432.silvkingsmod.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.entity.ModEntities;

public class MagnaFireballEntity extends SmallFireballEntity {

    public MagnaFireballEntity(EntityType<? extends SmallFireballEntity> entityType, World world) {
        super(entityType, world);
    }

    public MagnaFireballEntity(World world, LivingEntity owner, Vec3d velocity) {
        super(ModEntities.MAGNA_FIREBALL, world);
        this.setOwner(owner);
        this.setVelocity(velocity);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (!this.getWorld().isClient) {
            Entity target = entityHitResult.getEntity();


            boolean isAlly = target instanceof MagnaTitanEntity ||
                    target instanceof MagnaMinionEntity ||
                    target instanceof LavaGolemEntity ||
                    target instanceof MagnaWitchEntity;

            if (isAlly) {
                // Wenn es ein Verbündeter ist, machen wir GAR NICHTS.
                // Wir rufen auch nicht super.onEntityHit auf, damit der Ball weiterfliegt.
                return;
            }

            // HIER SETZT DU DEN SCHADEN (z.B. 15.0f für 7.5 Herzen)
            float damage = 30.0f;
            target.damage(this.getDamageSources().magic(), damage);
            target.setOnFireFor(15);

            this.discard(); // Projektil nach Treffer entfernen
        }
    }

    @Override
    public void tick() {
        // Ruft die Standard-Logik auf (Bewegung, Kollision etc.)
        super.tick();

        // Partikel werden nur auf dem Client gerendert
        if (this.getWorld().isClient) {
            // Erzeugt pro Tick 2-3 Partikel hinter dem Projektil
            for (int i = 0; i < 3; i++) {
                this.getWorld().addParticle(
                        net.minecraft.particle.ParticleTypes.SOUL_FIRE_FLAME,
                        this.getParticleX(0.5),    // Zufällige X-Position in der Hitbox
                        this.getRandomBodyY(),     // Zufällige Y-Position
                        this.getParticleZ(0.5),    // Zufällige Z-Position
                        0.0, 0.0, 0.0              // Geschwindigkeit der Partikel (0 = sie bleiben stehen)
                );
            }

            // Optional: Ein bisschen blauer Rauch für mehr Dichte
            if (this.age % 2 == 0) {
                this.getWorld().addParticle(
                        net.minecraft.particle.ParticleTypes.SOUL,
                        this.getX(), this.getY(), this.getZ(),
                        0.0, 0.02, 0.0
                );
            }
        }
    }

    @Override
    public boolean isOnFire() {
        // Das sorgt dafür, dass das Entity-Rendering immer das Feuer-Overlay anzeigt
        return false;
    }
}