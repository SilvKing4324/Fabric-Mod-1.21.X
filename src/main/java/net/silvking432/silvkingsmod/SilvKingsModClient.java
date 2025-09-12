package net.silvking432.silvkingsmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.entity.ModEntities;
import net.silvking432.silvkingsmod.entity.client.*;
import net.silvking432.silvkingsmod.particle.ModParticles;
import net.silvking432.silvkingsmod.particle.StarlightAshesParticle;
import net.silvking432.silvkingsmod.util.ModModelPredicates;

public class SilvKingsModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TITANIUM_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TITANIUM_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SUPER_FLOWER_CROP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.HONEY_BERRY_BUSH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DRIFTWOOD_SAPLING, RenderLayer.getCutout());

        ModModelPredicates.registerModelPredicates();

        EntityModelLayerRegistry.registerModelLayer(MantisModel.MANTIS, MantisModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.MANTIS, MantisRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(TomahawkProjectileModel.TOMAHAWK, TomahawkProjectileModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.TOMAHAWK, TomahawkProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.CHAIR_ENTITY, ChairRenderer::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.STARLIGHT_ASHES_PARTICLE, StarlightAshesParticle.Factory::new);
    }
}
