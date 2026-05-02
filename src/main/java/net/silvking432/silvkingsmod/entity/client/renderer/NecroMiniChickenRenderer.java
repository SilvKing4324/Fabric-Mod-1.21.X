package net.silvking432.silvkingsmod.entity.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.custom.NecroMiniChickenEntity;

public class NecroMiniChickenRenderer extends MobEntityRenderer<NecroMiniChickenEntity, ChickenEntityModel<NecroMiniChickenEntity>> {

    private static final Identifier TEXTURE = Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/necro_chicken.png");

    public NecroMiniChickenRenderer(EntityRendererFactory.Context context) {
        super(context, new ChickenEntityModel<>(context.getPart(EntityModelLayers.CHICKEN)), 0.3f);
    }

    @Override
    public Identifier getTexture(NecroMiniChickenEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(NecroMiniChickenEntity entity, MatrixStack matrices, float amount) {
        matrices.scale(0.8f, 0.8f, 0.8f);
    }
}