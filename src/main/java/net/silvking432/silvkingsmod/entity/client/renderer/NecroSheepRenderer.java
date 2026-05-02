package net.silvking432.silvkingsmod.entity.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.client.other.NecroSheepWoolLayer;
import net.silvking432.silvkingsmod.entity.client.model.NecroSheepEntityModel;
import net.silvking432.silvkingsmod.entity.custom.NecroSheepEntity;

public class NecroSheepRenderer extends MobEntityRenderer<NecroSheepEntity, NecroSheepEntityModel> {
    private static final Identifier TEXTURE = Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/necro_sheep.png");

    public NecroSheepRenderer(EntityRendererFactory.Context context) {
        super(context, new NecroSheepEntityModel(context.getPart(EntityModelLayers.SHEEP)), 0.7f);
        this.addFeature(new NecroSheepWoolLayer(this, context.getModelLoader()));
    }

    @Override
    public Identifier getTexture(NecroSheepEntity entity) {
        return TEXTURE;
    }
}