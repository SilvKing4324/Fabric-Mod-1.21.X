package net.silvking432.silvkingsmod.entity.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.silvking432.silvkingsmod.entity.custom.MagnaAnvilEntity;

public class MagnaAnvilRenderer extends FlyingItemEntityRenderer<MagnaAnvilEntity> {

    public MagnaAnvilRenderer(EntityRendererFactory.Context context) {
        // Die 3.0f hier ist die Basis-Skalierung
        super(context, 3.0f, true);
    }

    @Override
    public void render(MagnaAnvilEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        // 1. Skalierung erzwingen (falls der super-Konstruktor ignoriert wird)
        matrices.scale(3.0f, 3.0f, 3.0f);

        // 2. Rotation fixieren: Wir setzen die Rotation manuell auf 0,
        // damit er nicht "schräg" fliegt wie ein Schneeball
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(0));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(0));

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);

        matrices.pop();
    }
}