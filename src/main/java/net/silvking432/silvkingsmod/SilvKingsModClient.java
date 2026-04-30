package net.silvking432.silvkingsmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.render.entity.ShulkerBulletEntityRenderer;
import net.minecraft.util.math.BlockPos;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.block.entity.ModBlockEntities;
import net.silvking432.silvkingsmod.block.entity.custom.EternalShulkerBoxBlockEntity;
import net.silvking432.silvkingsmod.block.entity.renderer.PedestalBlockEntityRenderer;
import net.silvking432.silvkingsmod.command.ShaderTestCommand;
import net.silvking432.silvkingsmod.entity.ModEntities;
import net.silvking432.silvkingsmod.entity.client.*;
import net.silvking432.silvkingsmod.item.ModItems;
import net.silvking432.silvkingsmod.network.DarkFogNetworking;
import net.silvking432.silvkingsmod.network.DarkFogPayload;
import net.silvking432.silvkingsmod.particle.BlackHoleParticle;
import net.silvking432.silvkingsmod.particle.ModParticles;
import net.silvking432.silvkingsmod.particle.StarlightAshesParticle;
import net.silvking432.silvkingsmod.screen.ModScreenHandlers;
import net.silvking432.silvkingsmod.screen.custom.CoreRefineryScreen;
import net.silvking432.silvkingsmod.screen.custom.DarkAnvilScreen;
import net.silvking432.silvkingsmod.screen.custom.GrowthChamberScreen;
import net.silvking432.silvkingsmod.screen.custom.PedestalScreen;
import net.silvking432.silvkingsmod.util.EffectShaderHandler;
import net.silvking432.silvkingsmod.util.ModModelPredicates;

import static net.silvking432.silvkingsmod.entity.client.NecroWolfEntityModel.NECRO_WOLF;

public class SilvKingsModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModModelPredicates.registerModelPredicates();

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TITANIUM_DOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TITANIUM_TRAPDOOR, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SUPER_FLOWER_CROP, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.HONEY_BERRY_BUSH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DRIFTWOOD_SAPLING, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TITANIUM_BEACON, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.DARK_WORLD_PORTAL, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GOLDEN_GLOWING_GLASS_PANE, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.GOLDEN_GLOWING_GLASS, RenderLayer.getTranslucent());

        EntityModelLayerRegistry.registerModelLayer(MantisModel.MANTIS, MantisModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(GeckoModel.GECKO, GeckoModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(NecroGeckoModel.NECRO_GECKO, NecroGeckoModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(TitanPlayerModel.TITAN_PLAYER, TitanPlayerModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MagnaTitanModel.MAGNA_TITAN, MagnaTitanModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MagnaMinionModel.MAGNA_MINION, MagnaMinionModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(TomahawkProjectileModel.TOMAHAWK, TomahawkProjectileModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(NecroSheepEntityModel.NECRO_SHEEP, NecroSheepEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(NECRO_WOLF, () -> NecroWolfEntityModel.getTexturedModelData(Dilation.NONE));
        EntityModelLayerRegistry.registerModelLayer(NecroLlamaModel.NECRO_LLAMA, NecroLlamaModel::getTexturedModelData);

        EntityRendererRegistry.register(ModEntities.DARK_SHADOW, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.ABYSSAL_SHADOW, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.PULL, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.TORNADO, EmptyEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.MANTIS, MantisRenderer::new);
        EntityRendererRegistry.register(ModEntities.GECKO, GeckoRenderer::new);
        EntityRendererRegistry.register(ModEntities.NECRO_GECKO, NecroGeckoRenderer::new);
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
        EntityRendererRegistry.register(ModEntities.ETERNAL_SHULKER, EternalShulkerRenderer::new);
        EntityRendererRegistry.register(ModEntities.ETERNAL_BULLET, ShulkerBulletEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.NECRO_PIG, NecroPigRenderer::new);
        EntityRendererRegistry.register(ModEntities.NECRO_COW, NecroCowRenderer::new);
        EntityRendererRegistry.register(ModEntities.NECRO_CHICKEN, NecroChickenRenderer::new);
        EntityRendererRegistry.register(ModEntities.NECRO_MINI_CHICKEN, NecroMiniChickenRenderer::new);
        EntityRendererRegistry.register(ModEntities.NECRO_SHEEP, NecroSheepRenderer::new);
        EntityRendererRegistry.register(ModEntities.NECRO_BEE, NecroBeeRenderer::new);
        EntityRendererRegistry.register(ModEntities.NECRO_WOLF, NecroWolfRenderer::new);
        EntityRendererRegistry.register(ModEntities.NECRO_LLAMA, NecroLlamaRenderer::new);

        ParticleFactoryRegistry.getInstance().register(ModParticles.STARLIGHT_ASHES_PARTICLE, StarlightAshesParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.BLACK_HOLE_PARTICLE, BlackHoleParticle.Factory::new);

        BlockEntityRendererFactories.register(ModBlockEntities.ETERNAL_SHULKER_BOX_BE, EternalShulkerBoxRenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.PEDESTAL_BE, PedestalBlockEntityRenderer::new);
        HandledScreens.register(ModScreenHandlers.PEDESTAL_SCREEN_HANDLER, PedestalScreen::new);
        HandledScreens.register(ModScreenHandlers.GROWTH_CHAMBER_SCREEN_HANDLER, GrowthChamberScreen::new);
        HandledScreens.register(ModScreenHandlers.CORE_REFINERY_SCREEN_HANDLER, CoreRefineryScreen::new);
        HandledScreens.register(ModScreenHandlers.DARK_ANVIL_SCREEN_HANDLER, DarkAnvilScreen::new);

        ShaderTestCommand.register();
        EffectShaderHandler.register();
        DarkFogNetworking.registerClient();

        BuiltinItemRendererRegistry.INSTANCE.register(ModItems.ETERNAL_SHULKER_BOX, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
            EternalShulkerBoxBlockEntity be = new EternalShulkerBoxBlockEntity(BlockPos.ORIGIN, ModBlocks.ETERNAL_SHULKER_BOX.getDefaultState());
            MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(be, matrices, vertexConsumers, light, overlay);
        });

        ClientPlayNetworking.registerGlobalReceiver(DarkFogPayload.ID, (payload, context) -> {
            int value = payload.fogValue();
            context.client().execute(() -> DarkFogNetworking.clientFogTimer = value);
        });
    }
}
