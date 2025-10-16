package net.silvking432.silvkingsmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class MatrixBlock extends Block {
    public MatrixBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            player.sendMessage(Text.literal("The Mystical Aura of this block doesn't let you do this!"),true);
        }

        return ActionResult.SUCCESS;
    }


    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClient && entity instanceof PlayerEntity player && !player.isCreative()) {
            world.scheduleBlockTick(pos, this, 1); // sorgt dafür, dass tick() dauerhaft aufgerufen wird
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        // Überprüfe alle Spieler
        for (PlayerEntity player : world.getPlayers()) {

            // Check: Ist der Spieler direkt auf oder über dem Block?
            double px = player.getX();
            double py = player.getY();
            double pz = player.getZ();

            // Blockzentrum
            double bx = pos.getX() + 0.5;
            double by = pos.getY();
            double bz = pos.getZ() + 0.5;

            // Wenn Spieler in einem "1x2x1" Bereich über dem Block ist
            if (px > pos.getX() - 0.3 && px < pos.getX() + 1.3 &&
                    pz > pos.getZ() - 0.3 && pz < pos.getZ() + 1.3 &&
                    py >= pos.getY() && py <= pos.getY() + 2.1) {

                // --- Bewegung vollständig stoppen ---
                player.setVelocity(0, 0, 0);
                player.velocityModified = true;

                // --- Spieler leicht zur Blockmitte ziehen ---
                double dx = bx - px;
                double dz = bz - pz;
                player.addVelocity(dx * 0.1, -0.1, dz * 0.1);
                player.velocityModified = true;

                // Optional: Reibung / Bewegung resetten
                player.setSprinting(false);
                player.setJumping(false);
            }
        }

        // Nächsten Tick wieder planen → Dauerhafte Wirkung
        world.scheduleBlockTick(pos, this, 1);
    }

}
