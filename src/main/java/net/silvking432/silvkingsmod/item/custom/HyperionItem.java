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
            var dir = player.getRotationVec(1.0F).normalize();
            var start = player.getCameraPosVec(1.0F);
            double max = 6.0;
            var intended = start.add(dir.multiply(max));
            var hit = world.raycast(new RaycastContext(
                    start,
                    intended,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    player
            ));

            if (hit.getType() == HitResult.Type.BLOCK) {
                var p = hit.getPos();
                intended = p.subtract(dir.multiply(0.3));
            }

            double dx = intended.x - player.getX();
            double dy = intended.y - (player.getY());
            double dz = intended.z - player.getZ();

            var targetBox = player.getBoundingBox().offset(dx, dy, dz);
            var finalPos = intended;

            if (!world.isSpaceEmpty(player, targetBox)) {

                boolean found = false;
                double step = 0.1;
                for (double back = 0.0; back <= max; back += step) {
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

                if (!found) {
                    finalPos = player.getPos();
                }
            }

            player.teleport(finalPos.x, finalPos.y, finalPos.z, true);
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
