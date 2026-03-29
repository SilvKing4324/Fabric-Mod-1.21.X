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
import net.silvking432.silvkingsmod.entity.custom.MagnaTitanEntity;

public class MagnaTitanRenderer extends MobEntityRenderer<MagnaTitanEntity, MagnaTitanModel<MagnaTitanEntity>> {

    public MagnaTitanRenderer(EntityRendererFactory.Context context) {
        super(context, new MagnaTitanModel<>(context.getPart(MagnaTitanModel.MAGNA_TITAN)), 0.8f);

        this.addFeature(new HeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));
        this.addFeature(new ArmorFeatureRenderer<>(
                this,
                new BipedEntityModel<>(context.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)),
                new BipedEntityModel<>(context.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager()
        ));
    }

    @Override
    public Identifier getTexture(MagnaTitanEntity entity) {
        return Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/magna_titan/magna_titan.png");
    }

    @Override
    public void render(MagnaTitanEntity livingEntity, float f, float g, MatrixStack matrixStack,
                       net.minecraft.client.render.VertexConsumerProvider vertexConsumerProvider, int i) {

        matrixStack.push();
        matrixStack.scale(1.6f, 1.6f, 1.6f);
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }
}