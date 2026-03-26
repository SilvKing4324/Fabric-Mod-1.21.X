package net.silvking432.silvkingsmod.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.silvking432.silvkingsmod.entity.custom.MagnaTitanEntity;

public class MagnaTitanMeleeGoal extends MeleeAttackGoal {
    private final MagnaTitanEntity entity;
    private int ticksUntilNextAttack = 0;
    private boolean shouldCountTillNextAttack = false;

    public MagnaTitanMeleeGoal(MagnaTitanEntity mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
        this.entity = mob;
    }

    @Override
    public boolean canStart() {
        // Attacke nur, wenn:
        // 1. Basis-Bedingungen erfüllt (Ziel vorhanden etc.)
        // 2. KEIN Schild aktiv
        // 3. Er NICHT gerade trinkt (Potting)
        // 4. Der Black Hole Move NICHT aktiv ist (Timer <= 0)
        return super.canStart()
                && !this.entity.isShieldActive()
                && !this.entity.isPotting()
                && this.entity.getBlackHoleTimer() <= 0
                && !this.entity.isHealing(); // NEU: Höre auf zu jagen, wenn geheilt wird
    }

    @Override
    public boolean shouldContinue() {
        // Sofort abbrechen, wenn eine der Spezial-Phasen startet
        return super.shouldContinue()
                && !this.entity.isShieldActive()
                && !this.entity.isPotting()
                && this.entity.getBlackHoleTimer() <= 0
                && !this.entity.isHealing(); // NEU: Höre auf zu jagen, wenn geheilt wird
    }

    @Override
    public void start() {
        super.start();
        ticksUntilNextAttack = 0;
    }

    @Override
    protected void attack(LivingEntity pEnemy) {
        if (isEnemyWithinAttackDistance(pEnemy)) {
            shouldCountTillNextAttack = true;

            if (isTimeToAttack()) {
                this.mob.getLookControl().lookAt(pEnemy.getX(), pEnemy.getEyeY(), pEnemy.getZ());
                performMagnaAttack(pEnemy);
            }
        } else {
            shouldCountTillNextAttack = false;
            this.ticksUntilNextAttack = 0;
        }
    }

    private boolean isEnemyWithinAttackDistance(LivingEntity pEnemy) {
        // Deine gewünschten 3 Blöcke Reichweite
        return this.entity.distanceTo(pEnemy) <= 3.0f;
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected void performMagnaAttack(LivingEntity pEnemy) {
        // Cooldown setzen (z.B. 20 Ticks = 1 Sekunde zwischen Schlägen)
        this.ticksUntilNextAttack = 20;

        // 1. DER CRIT-SPRUNG (Simuliert Spieler-Verhalten)
        if (this.mob.isOnGround()) {
            // Ein kleiner Hopser nach oben/vorne für die Wucht
            this.mob.addVelocity(0, 0.3, 0);
            this.mob.velocityDirty = true;
        }

        // 2. DER SCHLAG
        this.mob.swingHand(Hand.MAIN_HAND);
        this.mob.tryAttack(pEnemy);

        // 3. BACK-PUSH (Der Boss springt nach dem Hit zurück)
        double deltaX = this.mob.getX() - pEnemy.getX();
        double deltaZ = this.mob.getZ() - pEnemy.getZ();
        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        if (distance > 0) {
            deltaX /= distance;
            deltaZ /= distance;

            // 0.6 Stärke für den Rückwärtssprung
            float pushBackStrength = 0.6f;
            Vec3d currentVel = this.mob.getVelocity();
            // Wir addieren den Push zur aktuellen Velocity
            this.mob.setVelocity(currentVel.x + (deltaX * pushBackStrength), 0.1, currentVel.z + (deltaZ * pushBackStrength));
            this.mob.velocityDirty = true;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (shouldCountTillNextAttack) {
            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
        }
    }

    @Override
    public void stop() {
        super.stop();
    }
}