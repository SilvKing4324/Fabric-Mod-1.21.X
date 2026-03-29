package net.silvking432.silvkingsmod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.entity.custom.MagnaBombEntity;

public class MagnaBombRenderer extends EntityRenderer<MagnaBombEntity> {
    private final BlockRenderManager blockRenderManager;

    public MagnaBombRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.blockRenderManager = ctx.getBlockRenderManager();
    }

    @Override
    public void render(MagnaBombEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        float scale = entity.getCustomScale();
        matrices.scale(scale, scale, scale);
        matrices.translate(0, 0.5, 0);

        float rotation = (entity.age + tickDelta) * 10;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation * 0.5f));

        matrices.translate(-0.5, -0.5, -0.5);

        this.blockRenderManager.renderBlockAsEntity(
                ModBlocks.TITANIUM_TNT.getDefaultState(),
                matrices,
                vertexConsumers,
                light,
                net.minecraft.client.render.OverlayTexture.DEFAULT_UV
        );

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(MagnaBombEntity entity) {
        return Identifier.of("minecraft", "textures/atlas/blocks.png");
    }
}