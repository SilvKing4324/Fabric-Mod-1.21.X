package net.silvking432.silvkingsmod.entity.client;

import net.silvking432.silvkingsmod.SilvKingsMod;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.entity.custom.TitanPlayerEntity;

public class TitanPlayerRenderer extends MobEntityRenderer<TitanPlayerEntity, TitanPlayerModel<TitanPlayerEntity>> {
    public TitanPlayerRenderer(EntityRendererFactory.Context context) {
        super(context, new TitanPlayerModel<>(context.getPart(TitanPlayerModel.TITAN_PLAYER)), 0.5f); // Schatten
    }

    @Override
    public Identifier getTexture(TitanPlayerEntity entity) {
        return Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/titan_player/titan_player.png");
    }

    @Override
    public void render(TitanPlayerEntity livingEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i) {
        if(livingEntity.isBaby()) {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }
}