package net.silvking432.silvkingsmod.block.entity.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.block.custom.PulseEmitterBlock;
import net.silvking432.silvkingsmod.block.entity.ModBlockEntities;

public class PulseEmitterBlockEntity extends BlockEntity {
    private int timer = 0;
    private boolean powered = false;

    public PulseEmitterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PULSE_EMITTER_BE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, PulseEmitterBlockEntity be) {
        if (world.isClient) return;

        be.timer++;

        if (be.timer >= 4) {
            be.timer = 0;

            PlayerEntity closestPlayer = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 16.0, false);

            if (closestPlayer != null) {
                be.powered = !be.powered;

                world.setBlockState(pos, state.with(PulseEmitterBlock.POWERED, be.powered), Block.NOTIFY_ALL);

                if (be.powered && world instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(
                            DustParticleEffect.DEFAULT,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            5,
                            0.2, 0.2, 0.2,
                            0.05
                    );
                }
            } else {
                if (be.powered) {
                    be.powered = false;
                    world.setBlockState(pos, state.with(PulseEmitterBlock.POWERED, false), Block.NOTIFY_ALL);
                }
            }
        }
    }
}
