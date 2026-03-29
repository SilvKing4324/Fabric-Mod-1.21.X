package net.silvking432.silvkingsmod.entity.client;

import net.minecraft.block.Blocks;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.entity.custom.MagnaAnvilEntity;

public class MagnaAnvilRenderer extends EntityRenderer<MagnaAnvilEntity> {
    private final BlockRenderManager blockRenderManager;

    public MagnaAnvilRenderer(EntityRendererFactory.Context context) {
        // Die 3.0f hier ist die Basis-Skalierung
        super(context);
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public void render(MagnaAnvilEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        // 1. Skalierung (3x so groß wie ein normaler Block)
        matrices.scale(3.0f, 3.0f, 3.0f);

        // 2. Zentrierung: Block-Modelle hängen am (0,0,0) Eckpunkt.
        // Wir schieben ihn um 0.5 zurück, damit er mittig am Entity-Zentrum hängt.
        matrices.translate(-0.5, 0, -0.5);

        // 3. Den echten Vanilla-Amboss rendern
        // Wir nutzen hier Blocks.ANVIL.getDefaultState()
        this.blockRenderManager.renderBlockAsEntity(Blocks.ANVIL.getDefaultState(), matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(MagnaAnvilEntity entity) {
        return PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;    }
}