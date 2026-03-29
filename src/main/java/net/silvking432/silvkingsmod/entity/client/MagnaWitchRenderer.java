package net.silvking432.silvkingsmod.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.custom.MagnaWitchEntity;

public class MagnaWitchRenderer extends MobEntityRenderer<MagnaWitchEntity, WitchEntityModel<MagnaWitchEntity>> {
    private static final Identifier TEXTURE = Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/magna_witch/magna_witch.png");

    public MagnaWitchRenderer(EntityRendererFactory.Context context) {
        super(context, new WitchEntityModel<>(context.getPart(EntityModelLayers.WITCH)), 0.5F);
    }

    @Override
    public Identifier getTexture(MagnaWitchEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(MagnaWitchEntity entity, MatrixStack matrices, float amount) {
        matrices.scale(1.1F, 1.1F, 1.1F);
        super.scale(entity, matrices, amount);
    }
}