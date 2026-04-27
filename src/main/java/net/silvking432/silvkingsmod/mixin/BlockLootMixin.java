package net.silvking432.silvkingsmod.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.component.ModDataComponentTypes;
import net.silvking432.silvkingsmod.component.custom.CoreTrait;
import net.silvking432.silvkingsmod.util.TraitUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Block.class)
public class BlockLootMixin {

    @Inject(method = "afterBreak", at = @At("HEAD"))
    private void silvkingsmod$applyBonusDropsTrait(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) return;

        boolean isTarget = TraitUtil.getBonusDropsBlocks().contains(state.getBlock()) ||
                TraitUtil.getBonusDropsTags().stream().anyMatch(state::isIn);

        if (!isTarget) return;

        var enchantments = tool.getEnchantments();
        boolean hasSilkTouch = enchantments.getEnchantments().stream()
                .anyMatch(entry -> entry.matchesKey(net.minecraft.enchantment.Enchantments.SILK_TOUCH));

        if (hasSilkTouch) return;

        List<CoreTrait> traits = tool.get(ModDataComponentTypes.CORE_TRAITS);
        if (traits == null) return;

        float bonusChance = 0;
        for (CoreTrait trait : traits) {
            if (trait.traitId().equals("BONUS_DROPS")) {
                bonusChance += trait.value();
            }
        }

        if (bonusChance > 0 && world.random.nextFloat() * 100 < bonusChance) {
            Block.getDroppedStacks(state, serverWorld, pos, blockEntity, player, tool)
                    .forEach(stack -> Block.dropStack(world, pos, stack));

            world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.5f, 1.5f);
        }
    }
}