package net.silvking432.silvkingsmod.entity.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.ChickenEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.custom.NecroChickenEntity;

public class NecroChickenRenderer extends MobEntityRenderer<NecroChickenEntity, ChickenEntityModel<NecroChickenEntity>> {

    private static final Identifier TEXTURE = Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/necro_chicken.png");

    public NecroChickenRenderer(EntityRendererFactory.Context context) {
        super(context, new ChickenEntityModel<>(context.getPart(EntityModelLayers.CHICKEN)), 1.0f);
    }

    @Override
    public Identifier getTexture(NecroChickenEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(NecroChickenEntity entity, MatrixStack matrices, float amount) {
        matrices.scale(3.0f, 3.0f, 3.0f);
    }
}