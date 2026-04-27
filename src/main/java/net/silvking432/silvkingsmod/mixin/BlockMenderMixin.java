package net.silvking432.silvkingsmod.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
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
public class BlockMenderMixin {

    @Inject(method = "afterBreak", at = @At("HEAD"), cancellable = true)
    private void silvkingsmod$applyBlockMenderSkill(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        if (world.isClient) return;

        if (!TraitUtil.getBasicBlocks().contains(state.getBlock())) return;

        ItemStack realTool = player.getMainHandStack();

        List<CoreTrait> traits = tool.get(ModDataComponentTypes.CORE_TRAITS);
        if (traits == null) return;

        boolean hasBlockMender = traits.stream().anyMatch(t -> t.traitId().equals("SKILL_BLOCK_MENDER"));
        if (!hasBlockMender) return;

        ci.cancel();

        if (realTool.isDamaged()) {
            if (world.random.nextFloat() < 0.20f) {

                int newDamage = Math.max(0, realTool.getDamage() - 1);
                realTool.setDamage(newDamage);

                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            10, 0.2, 0.2, 0.2, 0.0);
                }
            }
        }
    }
}