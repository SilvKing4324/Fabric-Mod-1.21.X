package net.silvking432.silvkingsmod.entity.client;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.block.custom.EternalShulkerBoxBlock;
import net.silvking432.silvkingsmod.block.entity.custom.EternalShulkerBoxBlockEntity;

public class EternalShulkerBoxRenderer implements BlockEntityRenderer<EternalShulkerBoxBlockEntity> {
    public static final Identifier TEXTURE = Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/eternal_shulker/eternal_shulker.png");
    private final ShulkerEntityModel<?> model;

    public EternalShulkerBoxRenderer(BlockEntityRendererFactory.Context ctx) {
        this.model = new ShulkerEntityModel<>(ctx.getLayerModelPart(EntityModelLayers.SHULKER));
    }

    @Override
    public void render(EternalShulkerBoxBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Direction direction = entity.getCachedState().get(EternalShulkerBoxBlock.FACING);

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);

        matrices.scale(0.9995f, 0.9995f, 0.9995f);

        matrices.multiply(direction.getRotationQuaternion());
        matrices.scale(1.0f, -1.0f, -1.0f);
        matrices.translate(0.0f, -1.0f, 0.0f);

        ModelPart lid = this.model.getLid();
        float progress = entity.getAnimationProgress(tickDelta);
        lid.setPivot(0.0f, 24.0f - progress * 0.5f * 16.0f, 0.0f);

        lid.yaw = 270.0f * progress * ((float)Math.PI / 180.0f);

        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));
        this.model.render(matrices, vertexConsumer, light, overlay);

        matrices.pop();
    }
}