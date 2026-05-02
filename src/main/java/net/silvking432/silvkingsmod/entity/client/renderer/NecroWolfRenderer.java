package net.silvking432.silvkingsmod.entity.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.client.model.NecroWolfEntityModel;
import net.silvking432.silvkingsmod.entity.custom.NecroWolfEntity;

import static net.silvking432.silvkingsmod.entity.client.model.NecroWolfEntityModel.NECRO_WOLF;


public class NecroWolfRenderer extends MobEntityRenderer<NecroWolfEntity, NecroWolfEntityModel<NecroWolfEntity>> {
    private static final Identifier TEXTURE = Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/necro_wolf.png");

    public NecroWolfRenderer(EntityRendererFactory.Context context) {
        super(context, new NecroWolfEntityModel<>(context.getPart(NECRO_WOLF)), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));
    }

    @Override
    public Identifier getTexture(NecroWolfEntity entity) {
        return TEXTURE;
    }
}