package net.silvking432.silvkingsmod.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.custom.NecroPigEntity;

public class NecroPigRenderer extends MobEntityRenderer<NecroPigEntity, PigEntityModel<NecroPigEntity>> {

    private static final Identifier TEXTURE = Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/necro_pig.png");

    public NecroPigRenderer(EntityRendererFactory.Context context) {
        super(context, new PigEntityModel<>(context.getPart(EntityModelLayers.PIG)), 0.7f);
    }

    @Override
    public Identifier getTexture(NecroPigEntity entity) {
        return TEXTURE;
    }
}