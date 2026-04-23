package net.silvking432.silvkingsmod.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.silvking432.silvkingsmod.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(method = "checkFallFlying", at = @At("HEAD"), cancellable = true)
    private void allowEternalElytraStart(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);

        if (chest.isOf(ModItems.ETERNAL_ELYTRA) && ElytraItem.isUsable(chest)) {
            // Wir prüfen, ob der Spieler gerade die Sprung-Taste drückt (Doppelsprung)
            // Minecraft prüft das intern über 'player.jumping'
            if (!player.isOnGround() && !player.isFallFlying() && !player.isTouchingWater() && !player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.LEVITATION)) {

                // WICHTIG: Wir rufen startFallFlying auf UND geben true zurück,
                // aber nur wenn die Bedingungen für den Start-Moment stimmen.
                player.startFallFlying();
                cir.setReturnValue(true);
            }
        }
    }
}
