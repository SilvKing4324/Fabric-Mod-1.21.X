package net.silvking432.silvkingsmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.block.entity.ModBlockEntities;
import net.silvking432.silvkingsmod.block.entity.renderer.PedestalBlockEntityRenderer;
import net.silvking432.silvkingsmod.entity.ModEntities;
import net.silvking432.silvkingsmod.entity.client.*;
import net.silvking432.silvkingsmod.particle.BlackHoleParticle;
import net.silvking432.silvkingsmod.particle.ModParticles;
import net.silvking432.silvkingsmod.particle.StarlightAshesParticle;
import net.silvking432.silvkingsmod.screen.ModScreenHandlers;
import net.silvking432.silvkingsmod.screen.custom.GrowthChamberScreen;
import net.silvking432.silvkingsmod.screen.custom.PedestalScreen;
import net.silvking432.silvkingsmod.util.ModModelPredicates;

public class SilvKingsModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TITANIUM_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TITANIUM_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SUPER_FLOWER_CROP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.HONEY_BERRY_BUSH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DRIFTWOOD_SAPLING, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TITANIUM_BEACON, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DARK_WORLD_PORTAL, RenderLayer.getTranslucent());

        ModModelPredicates.registerModelPredicates();

        EntityModelLayerRegistry.registerModelLayer(MantisModel.MANTIS, MantisModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(TitanPlayerModel.TITAN_PLAYER, TitanPlayerModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MagnaTitanModel.MAGNA_TITAN, MagnaTitanModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MagnaMinionModel.MAGNA_MINION, MagnaMinionModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(TomahawkProjectileModel.TOMAHAWK, TomahawkProjectileModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.MANTIS, MantisRenderer::new);
        EntityRendererRegistry.register(ModEntities.TITAN_PLAYER, TitanPlayerRenderer::new);
        EntityRendererRegistry.register(ModEntities.MAGNA_TITAN, MagnaTitanRenderer::new);
        EntityRendererRegistry.register(ModEntities.MAGNA_MINION, MagnaMinionRenderer::new);
        EntityRendererRegistry.register(ModEntities.LAVA_GOLEM, LavaGolemRenderer::new);
        EntityRendererRegistry.register(ModEntities.MAGNA_WITCH, MagnaWitchRenderer::new);
        EntityRendererRegistry.register(ModEntities.MAGNA_FIREBALL, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.MAGNA_ANVIL, MagnaAnvilRenderer::new);
        EntityRendererRegistry.register(ModEntities.TOMAHAWK, TomahawkProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.CHAIR_ENTITY, ChairRenderer::new);
        EntityRendererRegistry.register(ModEntities.TITANIUM_TNT_ENTITY, TitaniumTntRenderer::new);
        EntityRendererRegistry.register(ModEntities.MAGNA_BOMB, MagnaBombRenderer::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.STARLIGHT_ASHES_PARTICLE, StarlightAshesParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.BLACK_HOLE_PARTICLE, BlackHoleParticle.Factory::new);

        BlockEntityRendererFactories.register(ModBlockEntities.PEDESTAL_BE, PedestalBlockEntityRenderer::new);
        HandledScreens.register(ModScreenHandlers.PEDESTAL_SCREEN_HANDLER, PedestalScreen::new);
        HandledScreens.register(ModScreenHandlers.GROWTH_CHAMBER_SCREEN_HANDLER, GrowthChamberScreen::new);
    }
}
