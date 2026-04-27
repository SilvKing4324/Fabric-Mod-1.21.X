package net.silvking432.silvkingsmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.block.entity.custom.CoreRefineryBlockEntity;
import net.silvking432.silvkingsmod.network.BlockPosPayload;
import net.silvking432.silvkingsmod.screen.custom.CoreRefineryScreenHandler;
import org.jetbrains.annotations.Nullable;

public class CoreRefineryBlock extends BlockWithEntity {
    public CoreRefineryBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CoreRefineryBlockEntity(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            player.openHandledScreen(new ExtendedScreenHandlerFactory<BlockPosPayload>() {
                @Override
                public BlockPosPayload getScreenOpeningData(ServerPlayerEntity player) {
                    return new BlockPosPayload(pos);
                }

                @Override
                public Text getDisplayName() {
                    return Text.literal("Core Refinery");
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                    BlockEntity be = world.getBlockEntity(pos);

                    if (be instanceof CoreRefineryBlockEntity refineryBe) {
                        return new CoreRefineryScreenHandler(syncId, playerInventory, refineryBe);
                    }

                    return null;
                }
            });
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CoreRefineryBlockEntity be) {
                ItemScatterer.spawn(world, pos, be);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}