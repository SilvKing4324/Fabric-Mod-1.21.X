package net.silvking432.silvkingsmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TitaniumBeaconBlock extends Block {
    public TitaniumBeaconBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        // Wir prüfen, ob es ein Spieler ist (und kein Monster)
        if (!world.isClient && entity instanceof PlayerEntity player) {
            if (!player.isSpectator()) {
                // Sound abspielen
                world.playSound(null, pos, SoundEvents.BLOCK_BEACON_DEACTIVATE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                // Partikel erzeugen
                ((ServerWorld) world).spawnParticles(ParticleTypes.LARGE_SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.2, 0.2, 0.2, 0.05);

                // Block zerstören
                world.breakBlock(pos, false);
            }
        }
        super.onSteppedOn(world, pos, state, entity);
    }

}