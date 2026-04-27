package net.silvking432.silvkingsmod.entity.ai;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.silvking432.silvkingsmod.entity.custom.NecroWolfEntity;

import java.util.EnumSet;

public class NecroWolfStealGoal extends Goal {
    private final NecroWolfEntity wolf;
    private PlayerEntity targetPlayer;

    public NecroWolfStealGoal(NecroWolfEntity wolf) {
        this.wolf = wolf;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = this.wolf.getTarget();
        float healthPercent = this.wolf.getHealth() / this.wolf.getMaxHealth();

        return target instanceof PlayerEntity &&
                healthPercent < 0.30f &&
                healthPercent > 0.10f &&
                this.wolf.canSteal();
    }

    @Override
    public void start() {
        this.targetPlayer = (PlayerEntity) this.wolf.getTarget();
    }

    @Override
    public void tick() {
        if (targetPlayer == null) return;

        double dist = this.wolf.squaredDistanceTo(targetPlayer);
        this.wolf.getLookControl().lookAt(targetPlayer, 30.0f, 30.0f);

        if (dist < 2.25) {
            stealFromPlayer();
        } else {
            this.wolf.getNavigation().startMovingTo(targetPlayer, 1.5);
        }
    }

    private void stealFromPlayer() {
        ItemStack mainHand = targetPlayer.getMainHandStack();
        if (!mainHand.isEmpty()) {
            ItemStack stolen = mainHand.copy();
            stolen.setCount(1);
            this.wolf.equipStack(EquipmentSlot.MAINHAND, stolen);
            if (!targetPlayer.getAbilities().creativeMode) {
                mainHand.decrement(1);
            }
            this.wolf.setHasStolenItem(true);
            this.wolf.setTarget(null);
        }
    }

    @Override
    public boolean shouldContinue() {
        // Das Goal stoppt, sobald das Item erfolgreich geklaut wurde (dann übernimmt die Flucht-KI)
        return targetPlayer != null && targetPlayer.isAlive() && this.wolf.hasNotStolenItem();
    }
}