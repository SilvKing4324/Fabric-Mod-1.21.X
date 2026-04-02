package net.silvking432.silvkingsmod.block.custom;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.silvking432.silvkingsmod.dimension.ModDimensions;

public class DarkWorldPortalBlock extends Block implements Portal {
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    public DarkWorldPortalBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity.canUsePortals(false)) {
            if (!world.isClient ) {
                if (entity instanceof LivingEntity livingEntity) {
                    livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 300, 0));
                }
                entity.tryUsePortal(this, pos);
            }
        }
    }

    @Override
    public TeleportTarget createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos) {
        RegistryKey<World> destKey = world.getRegistryKey() == ModDimensions.DARK_WORLD_KEY
                ? World.OVERWORLD
                : ModDimensions.DARK_WORLD_KEY;

        ServerWorld destWorld = world.getServer().getWorld(destKey);
        if (destWorld == null) return null;

        Vec3d targetVec;
        if (destKey == ModDimensions.DARK_WORLD_KEY) {
            targetVec = new Vec3d(0.5, 100, 0.5);
        } else {
            targetVec = destWorld.getSpawnPos().toBottomCenterPos();
        }

        return new TeleportTarget(
                destWorld,
                targetVec,
                Vec3d.ZERO,
                entity.getYaw(),
                entity.getPitch(),
                TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET)
        );
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        // Deine Partikel passend zur lila Textur
        if (random.nextInt(5) == 0) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            world.addParticle(ParticleTypes.PORTAL, x, y, z, 0.0, 0.0, 0.0);
        }
    }
}