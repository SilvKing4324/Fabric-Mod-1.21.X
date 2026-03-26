package net.silvking432.silvkingsmod.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class HyperionItem extends Item {
    public HyperionItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient()) {
            // Blickrichtung 3D (inkl. up/down)
            var dir = player.getRotationVec(1.0F).normalize();

            // Start von den Augen (Kopf), damit "6 Blocks nach oben" Sinn macht
            var start = player.getCameraPosVec(1.0F);

            double max = 6.0;

            // geplant: 6 Blöcke in Blickrichtung
            var intended = start.add(dir.multiply(max));

            // Raycast vom Auge zum geplanten Ziel
            var hit = world.raycast(new RaycastContext(
                    start,
                    intended,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    player
            ));

            // Wenn Block getroffen -> setze intended etwas vor dem Block (nicht INS Block)
            if (hit.getType() == HitResult.Type.BLOCK) {
                var p = hit.getPos();
                // 0.3 Blöcke zurück, damit man nicht halb im Block landet
                intended = p.subtract(dir.multiply(0.3));
            }

            // Nun: intended ist die gewünschte 3D-Position (entweder Hit-angepasst oder full 6)
            // Prüfe, ob der Spieler an dieser Position kollidieren würde.
            // Wir verschieben die BoundingBox des Spielers zur intended-Position und prüfen Leerraum.
            double dx = intended.x - player.getX();
            double dy = intended.y - (player.getY()); // player.getY() ist Fußhöhe; das ist ok
            double dz = intended.z - player.getZ();

            // BoundingBox verschoben auf intended
            var targetBox = player.getBoundingBox().offset(dx, dy, dz);

            // Wenn der Raum frei ist: teleportieren.
            // Falls nicht, schiebe dich schrittweise zurück (in 0.1-BSteps) entlang -dir,
            // bis du einen sicheren Spot findest oder wieder beim Spieler landest.
            var finalPos = intended;
            if (!world.isSpaceEmpty(player, targetBox)) {

                boolean found = false;
                double step = 0.1; // fein genug, kann angepasst werden
                for (double back = 0.0; back <= max; back += step) {
                    // probe position = intended - dir * back
                    var probe = intended.subtract(dir.multiply(back));
                    double pdx = probe.x - player.getX();
                    double pdy = probe.y - player.getY();
                    double pdz = probe.z - player.getZ();
                    var probeBox = player.getBoundingBox().offset(pdx, pdy, pdz);

                    if (world.isSpaceEmpty(player, probeBox)) {
                        finalPos = probe;
                        found = true;
                        break;
                    }
                }

                // falls nichts frei gefunden wurde (sehr selten), fallback auf Spielerposition
                if (!found) {
                    finalPos = player.getPos();
                }
            }

            // Teleportiere 3D (Y bleibt also, wie es vom intended vorgegeben ist)
            player.teleport(finalPos.x, finalPos.y, finalPos.z, true);

            // Effekte an Startposition (kannst du an finalPos setzen, wenn du willst)
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.NEUTRAL, 0.5f, 1.0f);

            ((ServerWorld) world).spawnParticles(ParticleTypes.EXPLOSION,
                    player.getX(), player.getY() + 1.0, player.getZ(),
                    1, 0, 0, 0, 1.0f
            );
        }

        return TypedActionResult.success(player.getMainHandStack());
    }


}
