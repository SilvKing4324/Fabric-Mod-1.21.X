package net.silvking432.silvkingsmod.entity.client;


import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.custom.RhinoEntity;

public class RhinoRenderer extends MobEntityRenderer<RhinoEntity, RhinoModel<RhinoEntity>> {
    public RhinoRenderer(EntityRendererFactory.Context context) {
        super(context, new RhinoModel<>(context.getPart(RhinoModel.RHINO_LAYER)), 1.5f);
    }

    @Override
    public void render(RhinoEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if(livingEntity.isBaby()) {
            matrixStack.scale(0.45f, 0.45f, 0.45f);
        } else {
            matrixStack.scale(1f, 1f, 1f);
        }

        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }


    @Override
    public Identifier getTexture(RhinoEntity entity) {
        return Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/rhino/rhino.png");
    }
}