package net.silvking432.silvkingsmod.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.component.ModDataComponentTypes;
import net.silvking432.silvkingsmod.component.custom.CoreTrait;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnderPearlItem.class)
public class EnderPearlItemMixin {

    @Inject(method = "use", at = @At("TAIL"))
    private void silvkingsmod$reducePearlCooldown(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (world.isClient()) return;

        boolean hasSkill = false;
        for (ItemStack stack : user.getArmorItems()) {
            List<CoreTrait> traits = stack.get(ModDataComponentTypes.CORE_TRAITS);
            if (traits != null) {
                for (CoreTrait trait : traits) {
                    if (trait.traitId().equals("SKILL_PEARL_MAYHEM")) {
                        hasSkill = true;
                        break;
                    }
                }
            }
            if (hasSkill) break;
        }

        if (hasSkill) {
            user.getItemCooldownManager().set((EnderPearlItem)(Object)this, 10);
        }
    }
}