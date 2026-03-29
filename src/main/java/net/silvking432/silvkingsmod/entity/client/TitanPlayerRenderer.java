package net.silvking432.silvkingsmod.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.custom.TitanPlayerEntity;

public class TitanPlayerRenderer extends MobEntityRenderer<TitanPlayerEntity, TitanPlayerModel<TitanPlayerEntity>> {

    public TitanPlayerRenderer(EntityRendererFactory.Context context) {
        super(context, new TitanPlayerModel<>(context.getPart(TitanPlayerModel.TITAN_PLAYER)), 0.5f);

        this.addFeature(new HeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));

        this.addFeature(new ArmorFeatureRenderer<>(
                this,
                new BipedEntityModel<>(context.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)),
                new BipedEntityModel<>(context.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager()
        ));
    }

    @Override
    public Identifier getTexture(TitanPlayerEntity entity) {
        return Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/titan_player/titan_player.png");
    }

    @Override
    public void render(TitanPlayerEntity livingEntity, float f, float g, MatrixStack matrixStack,
                       net.minecraft.client.render.VertexConsumerProvider vertexConsumerProvider, int i) {

        matrixStack.push();
        matrixStack.scale(1.0f, 1.0f, 1.0f);
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }
}