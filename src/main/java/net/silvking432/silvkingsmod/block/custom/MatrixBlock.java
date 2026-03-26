package net.silvking432.silvkingsmod.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.entity.ModEntities;

public class MatrixBlock extends Block {
    public MatrixBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        // Hole das Item aus der Hand, die der Spieler gerade benutzt
        ItemStack itemStack = player.getStackInHand(player.getActiveHand());

        if (!world.isClient) {
            // Prüfen, ob es ein Drachenei ist
            if (itemStack.isOf(Items.DRAGON_EGG)) {

                // 1. Nachricht senden
                player.sendMessage(Text.literal("A mythical Creature has been called! The Magna Titan arrives!")
                        .formatted(Formatting.RED, Formatting.BOLD), false);

                // 2. Ei abziehen
                if (!player.isCreative()) {
                    itemStack.decrement(1);
                }
                double dx = player.getX() - (pos.getX() + 0.5);
                double dz = player.getZ() - (pos.getZ() + 0.5);
                double distance = Math.sqrt(dx * dx + dz * dz);

                if (distance > 0) {
                    // Kraft festlegen (ca. 10 Blöcke weit entspricht etwa einer Stärke von 2.5 bis 3.0)
                    double strength = 2.5;
                    player.addVelocity((dx / distance) * strength, 0, (dz / distance) * strength);
                    player.velocityModified = true; // Wichtig, damit der Server die Bewegung an den Client schickt
                }

                // 3. Sound & Partikel
                ServerWorld serverWorld = (ServerWorld) world;
                serverWorld.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 2.0f, 0.5f);

                serverWorld.spawnParticles(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 100, 0.5, 0.5, 0.5, 0.15);
                serverWorld.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 0, 0, 0, 0.0);

                // 4. Boss Spawn
                ModEntities.MAGNA_TITAN.spawn(serverWorld, pos, SpawnReason.SPAWNER);

                // 5. Block entfernen
                world.breakBlock(pos, false);

                return ActionResult.SUCCESS;
            }

            // Nachricht wenn kein Ei benutzt wurde
            player.sendMessage(Text.literal("The Mystical Aura of this block doesn't let you do this!"), true);
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
