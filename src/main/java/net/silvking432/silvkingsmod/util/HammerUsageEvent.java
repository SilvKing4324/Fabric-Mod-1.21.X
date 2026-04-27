package net.silvking432.silvkingsmod.util;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.component.ModDataComponentTypes;
import net.silvking432.silvkingsmod.component.custom.CoreTrait;
import net.silvking432.silvkingsmod.item.custom.HammerItem;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HammerUsageEvent implements PlayerBlockBreakEvents.Before {
    private static final Set<BlockPos> HARVESTED_BLOCKS = new HashSet<>();

    @Override
    public boolean beforeBlockBreak(World world, PlayerEntity player, BlockPos pos,
                                    BlockState state, @Nullable BlockEntity blockEntity) {
        if (world.isClient || !(player instanceof ServerPlayerEntity serverPlayer)) return true;

        ItemStack mainHandItem = player.getMainHandStack();

        boolean isHammer = mainHandItem.getItem() instanceof HammerItem;

        List<CoreTrait> traits = mainHandItem.get(ModDataComponentTypes.CORE_TRAITS);
        boolean hasRangeTrait = traits != null && traits.stream().anyMatch(t -> t.traitId().equals("RANGE_EXTEND"));

        if (!isHammer && !hasRangeTrait) return true;

        if (HARVESTED_BLOCKS.contains(pos)) return true;

        int radius = 1;


        for (BlockPos position : HammerItem.getBlocksToBeDestroyed(radius, pos, serverPlayer)) {
            if (pos.equals(position)) continue;

            if (!mainHandItem.isSuitableFor(world.getBlockState(position))) continue;

            HARVESTED_BLOCKS.add(position);
            serverPlayer.interactionManager.tryBreakBlock(position);
            HARVESTED_BLOCKS.remove(position);
        }

        return true;
    }
}