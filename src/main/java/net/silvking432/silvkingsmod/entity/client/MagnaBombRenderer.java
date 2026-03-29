package net.silvking432.silvkingsmod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.silvking432.silvkingsmod.entity.custom.MagnaBombEntity;

public class MagnaBombRenderer extends EntityRenderer<MagnaBombEntity> {
    // Wir nutzen die Textur des Fire-Charge oder ein eigenes lila Asset
    private static final Identifier TEXTURE = Identifier.of("minecraft", "textures/entity/enderdragon/dragon_fireball.png");

    public MagnaBombRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(MagnaBombEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        // 1. Größe aus dem DataTracker holen und anwenden
        float scale = entity.getCustomScale();
        matrices.scale(scale, scale, scale);

        // 2. Die Bombe soll sich drehen (sieht dynamischer aus)
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((entity.age + tickDelta) * 10));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45));

        // Hier könntest du ein Modell rendern.
        // Für den Anfang reicht es, wenn wir ein Billboard (wie beim Feuerball) nutzen oder
        // einfach nur die Partikel wirken lassen.

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.pop();
    }

    @Override
    public Identifier getTexture(MagnaBombEntity entity) {
        return TEXTURE;
    }
}