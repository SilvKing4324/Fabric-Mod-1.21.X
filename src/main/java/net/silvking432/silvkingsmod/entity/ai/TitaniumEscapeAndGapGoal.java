package net.silvking432.silvkingsmod.entity.ai;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.Difficulty;
import net.silvking432.silvkingsmod.entity.custom.TitanPlayerEntity;
import net.silvking432.silvkingsmod.item.ModItems;

import java.util.EnumSet;

public class TitaniumEscapeAndGapGoal extends Goal {
    private final TitanPlayerEntity mob;
    private int eatTimer = 0;
    private boolean isStandingStill = false;

    public TitaniumEscapeAndGapGoal(TitanPlayerEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        // Startet nur, wenn Leben < 50%, Äpfel da sind und er ein Ziel hat, vor dem er flüchten muss
        return mob.getHealth() <= mob.getMaxHealth() / 2
                && mob.getGoldenApplesLeft() > 0
                && mob.getTarget() != null;
    }

    @Override
    public void start() {
        this.eatTimer = 15; // 3 Sekunden (60 Ticks) Flucht + Essen
        this.mob.setGapping(true);
        this.isStandingStill = false;

        this.mob.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_APPLE));
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        double distanceSq = mob.squaredDistanceTo(target);

        // Am Anfang der tick() Methode:
        if (!this.isStandingStill) {
            // 1.0f bedeutet 100% Resistenz gegen Rückstoß
            mob.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1.0f);
        } else {
            // Sobald er steht, soll er wieder normales Knockback bekommen
            mob.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(0.0f);
        }

        // 1. BEWEGUNGS-LOGIK
        if (this.isStandingStill || distanceSq >= 121.0D) {
            this.isStandingStill = true;
            mob.getNavigation().stop();
            mob.getLookControl().lookAt(target, 30.0F, 30.0F); // Höhere Speed beim Drehen sieht besser aus
        } else {

            double escapeSpeed = 1.3D; // Default für EASY / PEACEFUL
            if (mob.getWorld().getDifficulty() == Difficulty.NORMAL) escapeSpeed = 1.4D;
            if (mob.getWorld().getDifficulty() == Difficulty.HARD) escapeSpeed = 1.5D;

            // Er ist noch auf der Flucht
            double diffX = mob.getX() - target.getX();
            double diffZ = mob.getZ() - target.getZ();
            mob.getNavigation().startMovingTo(mob.getX() + diffX, mob.getY(), mob.getZ() + diffZ, escapeSpeed);

            return;
        }

        // 2. ESS-LOGIK (Wird erst erreicht, wenn isStandingStill = true)
        if (eatTimer > 5) {
            // Er isst (Phase 1: Sound & Animation)
            mob.swingHand(Hand.MAIN_HAND);
            mob.getWorld().playSound(null, mob.getX(), mob.getY(), mob.getZ(),
                    net.minecraft.sound.SoundEvents.ENTITY_GENERIC_EAT,
                    mob.getSoundCategory(), 0.5f, 1.0f);
        } else if (eatTimer == 5) {
            // Apfel verschwindet, 1 Sekunde Pause beginnt
            this.mob.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }

        // Timer zählt nur runter, wenn er steht (wegen des 'return' oben)
        eatTimer--;
    }

    @Override
    public boolean shouldContinue() {
        return eatTimer > 0;
    }

    @Override
    public void stop() {
        // Effekte geben (Regeneration II und Absorption wie ein Notch Apple)
        float healAmount = 4.0F; // EASY
        if (mob.getWorld().getDifficulty() == Difficulty.NORMAL) healAmount = 5.0F;
        if (mob.getWorld().getDifficulty() == Difficulty.HARD) healAmount = 6.0F;

        mob.heal(healAmount);
        mob.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 1));
        mob.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 0));

        mob.useGoldenApple(); // Zähler -1
        mob.setGapping(false);

        this.mob.equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.TITANIUM_SWORD));
        // Nach dem Gappen direkt wieder aggressiv zum Spieler schauen
        if (mob.getTarget() != null) {
            mob.getNavigation().startMovingTo(mob.getTarget(), 1.0D);
        }
    }
}