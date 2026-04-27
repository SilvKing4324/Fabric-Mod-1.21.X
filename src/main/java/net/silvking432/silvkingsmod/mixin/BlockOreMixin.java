package net.silvking432.silvkingsmod.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.component.ModDataComponentTypes;
import net.silvking432.silvkingsmod.component.custom.CoreTrait;
import net.silvking432.silvkingsmod.util.TraitUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(Block.class)
public class BlockOreMixin {

    @Inject(method = "afterBreak", at = @At("HEAD"))
    private void silvkingsmod$applyStoneOreSkill(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool, CallbackInfo ci) {
        if (world.isClient || !(world instanceof ServerWorld serverWorld)) return;

        if (!TraitUtil.getBasicBlocks().contains(state.getBlock())) return;

        List<CoreTrait> traits = tool.get(ModDataComponentTypes.CORE_TRAITS);
        if (traits == null) return;

        boolean hasStoneOre = traits.stream().anyMatch(t -> t.traitId().equals("SKILL_STONE_ORE"));
        if (!hasStoneOre) return;

        Random random = world.getRandom();
        for (Map.Entry<Item, Float> entry : TraitUtil.getOreChances().entrySet()) {
            if (random.nextFloat() < entry.getValue()) {
                Block.dropStack(world, pos, new ItemStack(entry.getKey()));
                serverWorld.spawnParticles(ParticleTypes.ELECTRIC_SPARK,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        3, 0.1, 0.1, 0.1, 0.05);
                break;
            }
        }
    }
}
