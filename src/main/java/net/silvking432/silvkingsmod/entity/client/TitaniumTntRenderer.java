package net.silvking432.silvkingsmod.entity.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TntMinecartEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.block.ModBlocks;
import net.silvking432.silvkingsmod.entity.custom.TitaniumTntEntity;

public class TitaniumTntRenderer extends EntityRenderer<TitaniumTntEntity> {

    private static final Identifier TEXTURE = Identifier.of(SilvKingsMod.MOD_ID, "block/titanium_tnt");

    public TitaniumTntRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(TitaniumTntEntity entity, float yaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        // TNT etwas anheben (wie beim Vanilla-TNT)
        matrices.translate(0.0F, 0.5F, 0.0F);

        int fuse = entity.getFuse();
        if (fuse - tickDelta + 1.0F < 10.0F) {
            float f = 1.0F - (fuse - tickDelta + 1.0F) / 10.0F;
            f = MathHelper.clamp(f, 0.0F, 1.0F);
            f = f * f;
            f = f * f;
            float scale = 1.0F + f * 0.3F;
            matrices.scale(scale, scale, scale);
        }

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
        matrices.translate(-0.5F, -0.5F, 0.5F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));

        // Hier den Custom TNT Block rendern, mit Blink-Effekt
        TntMinecartEntityRenderer.renderFlashingBlock(
                MinecraftClient.getInstance().getBlockRenderManager(),
                ModBlocks.TITANIUM_TNT.getDefaultState(), // Dein Custom Block
                matrices,
                vertexConsumers,
                light,
                fuse / 5 % 2 == 0 // blink toggle
        );

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }




    @Override
    public Identifier getTexture(TitaniumTntEntity entity) {
        return TEXTURE;
    }
}
