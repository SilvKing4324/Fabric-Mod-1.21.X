package net.silvking432.silvkingsmod.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.silvking432.silvkingsmod.component.ModDataComponentTypes;
import net.silvking432.silvkingsmod.component.custom.CoreTrait;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(
            method = "damage(ILnet/minecraft/server/world/ServerWorld;Lnet/minecraft/server/network/ServerPlayerEntity;Ljava/util/function/Consumer;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isDamageable()Z"),
            cancellable = true
    )
    private void silvkingsmod$applyUnbreakingTrait(int amount, ServerWorld world, @Nullable ServerPlayerEntity player, Consumer<Item> breakCallback, CallbackInfo ci) {
        if (player == null) return;

        boolean playerHasGlobalSkill = false;
        for (ItemStack armorStack : player.getArmorItems()) {
            List<CoreTrait> traits = armorStack.get(ModDataComponentTypes.CORE_TRAITS);
            if (traits != null) {
                for (CoreTrait trait : traits) {
                    if ("SKILL_UNBREAKING".equals(trait.traitId()) || "SKILL_FRAGILE_RESISTANCE".equals(trait.traitId())) {
                        playerHasGlobalSkill = true;
                        break;
                    }
                }
            }
            if (playerHasGlobalSkill) break;
        }

        if (playerHasGlobalSkill) {
            if (world.getRandom().nextFloat() < 0.20f) {
                ci.cancel();
            }
        }
    }
}