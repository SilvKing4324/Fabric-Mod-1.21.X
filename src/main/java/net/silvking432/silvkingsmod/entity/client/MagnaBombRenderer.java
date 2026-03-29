package net.silvking432.silvkingsmod.entity.client;

import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TntMinecartEntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
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

        // 1. Skalierung zuerst
        float scale = entity.getCustomScale();
        matrices.scale(scale, scale, scale);

        // 2. Rotation anwenden
        // Da wir wollen, dass er um seine Mitte rotiert, müssen wir VOR der Rotation
        // das Zentrum zum Nullpunkt schieben.

        // Wir heben ihn um 0.5 an, damit die Mitte des Blocks auf der Entity-Position liegt
        matrices.translate(0, 0.5, 0);

        // Jetzt rotieren wir (um alle Achsen für einen "chaotischen" Bombenflug, wenn du magst)
        float rotation = (entity.age + tickDelta) * 10;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation * 0.5f));

        // 3. Jetzt schieben wir den Block-Ursprung (Ecke) so zurück,
        // dass die Mitte des Modells exakt auf dem Rotationspunkt liegt.
        matrices.translate(-0.5, -0.5, -0.5);

        // 4. Den Block rendern
        this.blockRenderManager.renderBlockAsEntity(
                Blocks.TNT.getDefaultState(),
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