package net.silvking432.silvkingsmod.entity.client;


import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.custom.GeckoEntity;

public class GeckoRenderer extends MobEntityRenderer<GeckoEntity, GeckoModel<GeckoEntity>> {
    public GeckoRenderer(EntityRendererFactory.Context context) {
        super(context, new GeckoModel<>(context.getPart(GeckoModel.GECKO)), 0.25f);
    }

    @Override
    public void render(GeckoEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if(livingEntity.isBaby()) {
            matrixStack.scale(0.45f, 0.45f, 0.45f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }


    @Override
    public Identifier getTexture(GeckoEntity entity) {
        return Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/gecko/gecko.png");
    }
}