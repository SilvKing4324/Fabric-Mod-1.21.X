package net.silvking432.silvkingsmod.entity.client.renderer;


import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.client.model.NecrosaurusModel;
import net.silvking432.silvkingsmod.entity.custom.NecrosaurusEntity;

public class NecrosaurusRenderer extends MobEntityRenderer<NecrosaurusEntity, NecrosaurusModel<NecrosaurusEntity>> {
    public NecrosaurusRenderer(EntityRendererFactory.Context context) {
        super(context, new NecrosaurusModel<>(context.getPart(NecrosaurusModel.NECROSAURUS)), 1.5f);
    }

    @Override
    public void render(NecrosaurusEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if(livingEntity.isBaby()) {
            matrixStack.scale(0.45f, 0.45f, 0.45f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }


    @Override
    public Identifier getTexture(NecrosaurusEntity entity) {
        return Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/necrosaurus/necrosaurus.png");
    }
}