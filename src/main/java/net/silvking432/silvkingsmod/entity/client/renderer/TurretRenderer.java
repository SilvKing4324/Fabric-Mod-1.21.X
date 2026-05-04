package net.silvking432.silvkingsmod.entity.client.renderer;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.custom.TurretEntity;
import org.joml.Matrix4f;

public class TurretRenderer extends EntityRenderer<TurretEntity> {
    private static final Identifier EYE_TEXTURE = Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/turret_eyes.png");
    private final BlockRenderManager blockRenderManager;

    public TurretRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.blockRenderManager = ctx.getBlockRenderManager();
    }

    @Override
    public void render(TurretEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        matrices.scale(0.999f, 0.999f, 0.999f);
        matrices.translate(-0.5, 0, -0.5);

        int overlay = getOverlay(entity, tickDelta);

        ItemStack stack = entity.getDataTracker().get(TurretEntity.BLOCK_STATE_STACK);
        BlockState state = (stack.getItem() instanceof BlockItem bi) ? bi.getBlock().getDefaultState() : Blocks.BEDROCK.getDefaultState();

        renderBlock(state, matrices, vertexConsumers, light, overlay);

        matrices.translate(0, 1.0, 0);
        renderBlock(state, matrices, vertexConsumers, light, overlay);

        VertexConsumer eyeBuffer = vertexConsumers.getBuffer(RenderLayer.getEyes(EYE_TEXTURE));
        renderAllEyeSides(matrices, eyeBuffer, overlay);

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    public static int getOverlay(LivingEntity entity, float tickDelta) {
        return OverlayTexture.getUv(OverlayTexture.getU(entity.hurtTime > 0 ? 1.0f : 0.0f), false);
    }

    private void renderBlock(BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        this.blockRenderManager.getModelRenderer().render(
                matrices.peek(),
                vertexConsumers.getBuffer(RenderLayer.getCutout()),
                state,
                this.blockRenderManager.getModel(state),
                1.0f, 1.0f, 1.0f,
                light,
                overlay
        );
    }

    private void renderAllEyeSides(MatrixStack matrices, VertexConsumer vertices, int overlay) {
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();

        // Ein winziger Abstand nach außen (0.001 Blocklänge)
        float o = 0.001f;

        // NORD (Z-Achse negativ)
        // Punkte: oben-links bis unten-rechts, Z ist fest auf -o
        drawFace(positionMatrix, vertices, 1, -o, 0, -o, overlay);

        // SÜD (Z-Achse positiv)
        // Z ist fest auf 1 + o
        drawFace(positionMatrix, vertices, 0, 1 + o, 1, 1 + o, overlay);

        // WEST (X-Achse negativ)
        // X ist fest auf -o
        drawFace(positionMatrix, vertices, -o, 0, -o, 1, overlay);

        // OST (X-Achse positiv)
        // X ist fest auf 1 + o
        drawFace(positionMatrix, vertices, 1 + o, 1, 1 + o, 0, overlay);
    }

    private void drawFace(Matrix4f matrix, VertexConsumer vertices, float x1, float z1, float x2, float z2, int overlay) {
        vertices.vertex(matrix, x1, 0, z1).color(255, 255, 255, 255).texture(1, 1).overlay(overlay).light(15728880).normal(0, 1, 0);
        vertices.vertex(matrix, x2, 0, z2).color(255, 255, 255, 255).texture(0, 1).overlay(overlay).light(15728880).normal(0, 1, 0);
        vertices.vertex(matrix, x2, 1, z2).color(255, 255, 255, 255).texture(0, 0).overlay(overlay).light(15728880).normal(0, 1, 0);
        vertices.vertex(matrix, x1, 1, z1).color(255, 255, 255, 255).texture(1, 0).overlay(overlay).light(15728880).normal(0, 1, 0);
    }

    @Override
    public Identifier getTexture(TurretEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}