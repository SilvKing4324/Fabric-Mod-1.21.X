package net.silvking432.silvkingsmod.entity.client.other;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.entity.client.model.NecroSheepEntityModel;
import net.silvking432.silvkingsmod.entity.custom.NecroSheepEntity;

public class NecroSheepWoolLayer extends FeatureRenderer<NecroSheepEntity, NecroSheepEntityModel> {
    private static final Identifier WOOL_TEXTURE = Identifier.of("minecraft", "textures/entity/sheep/sheep_fur.png");
    private final NecroSheepEntityModel woolModel;

    public NecroSheepWoolLayer(FeatureRendererContext<NecroSheepEntity, NecroSheepEntityModel> context, EntityModelLoader loader) {
        super(context);
        // Wir nutzen das Modell für die Wolle
        this.woolModel = new NecroSheepEntityModel(loader.getModelPart(EntityModelLayers.SHEEP_FUR));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, NecroSheepEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (!entity.isSheared()) {
            this.getContextModel().copyStateTo(this.woolModel);
            this.woolModel.animateModel(entity, limbAngle, limbDistance, tickDelta);
            this.woolModel.setAngles(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);

            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.woolModel.getLayer(WOOL_TEXTURE));
            this.woolModel.render(matrices, vertexConsumer, light, LivingEntityRenderer.getOverlay(entity, 0.0F), 0xFFFFFFFF);
        }
    }
}