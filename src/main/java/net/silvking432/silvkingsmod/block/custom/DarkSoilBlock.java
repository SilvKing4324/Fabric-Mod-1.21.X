package net.silvking432.silvkingsmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.light.ChunkLightProvider;

public class DarkSoilBlock extends SpreadableBlock implements Fertilizable {
    public static final MapCodec<DarkSoilBlock> CODEC = createCodec(DarkSoilBlock::new);

    public DarkSoilBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends SpreadableBlock> getCodec() {
        return CODEC;
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return world.getBlockState(pos.up()).isAir();
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        BlockPos targetPos = pos.up();
        BlockState grassState = Blocks.SHORT_GRASS.getDefaultState();

        label49:
        for (int i = 0; i < 128; ++i) {
            BlockPos currentPos = targetPos;

            for (int j = 0; j < i / 16; ++j) {
                currentPos = currentPos.add(random.nextInt(3) - 1, (random.nextInt(3) - 1) * random.nextInt(3) / 2, random.nextInt(3) - 1);
                if (!world.getBlockState(currentPos.down()).isOf(this) || world.getBlockState(currentPos).isFullCube(world, currentPos)) {
                    continue label49;
                }
            }

            BlockState currentBlockState = world.getBlockState(currentPos);

            if (currentBlockState.isOf(grassState.getBlock()) && random.nextInt(10) == 0) {
                ((Fertilizable)Blocks.SHORT_GRASS).grow(world, random, currentPos, currentBlockState);
            }

            if (currentBlockState.isAir()) {
                BlockState stateToPlace;
                if (random.nextInt(8) == 0) {
                    stateToPlace = Blocks.FERN.getDefaultState();
                } else {
                    stateToPlace = grassState;
                }

                if (stateToPlace.canPlaceAt(world, currentPos)) {
                    world.setBlockState(currentPos, stateToPlace, 3);
                }
            }
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (canSurviveDarkSoil(world, pos)) {
            BlockState defaultState = this.getDefaultState();
            for (int i = 0; i < 4; ++i) {
                BlockPos targetPos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);

                if (world.getBlockState(targetPos).isOf(Blocks.DIRT) && canSurviveDarkSoil(world, targetPos)) {
                    world.setBlockState(targetPos, defaultState);
                }
            }
        }
    }

    private static boolean canSurviveDarkSoil(WorldView world, BlockPos pos) {
        BlockPos posAbove = pos.up();
        BlockState stateAbove = world.getBlockState(posAbove);

        if (stateAbove.isOf(Blocks.SNOW) && stateAbove.get(SnowBlock.LAYERS) == 1) {
            return true;
        }
        if (stateAbove.getFluidState().getLevel() == 8) {
            return false;
        }

        int opacity = ChunkLightProvider.getRealisticOpacity(world, world.getBlockState(pos), pos, stateAbove, posAbove, net.minecraft.util.math.Direction.UP, stateAbove.getOpacity(world, posAbove));
        return opacity < world.getMaxLightLevel();
    }


    @Override
    public Fertilizable.FertilizableType getFertilizableType() {
        return Fertilizable.FertilizableType.NEIGHBOR_SPREADER;
    }
}