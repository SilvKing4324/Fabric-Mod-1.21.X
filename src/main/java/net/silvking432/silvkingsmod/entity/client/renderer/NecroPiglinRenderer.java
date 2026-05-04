package net.silvking432.silvkingsmod.entity.client.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PiglinEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;

public class NecroPiglinRenderer extends PiglinEntityRenderer {
    private static final Identifier TEXTURE = Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/piglin/necro_piglin.png");

    public NecroPiglinRenderer(EntityRendererFactory.Context context) {
        super(context, EntityModelLayers.PIGLIN, EntityModelLayers.PIGLIN_INNER_ARMOR, EntityModelLayers.PIGLIN_OUTER_ARMOR, false);
    }

    @Override
    public Identifier getTexture(MobEntity mobEntity) {
        return TEXTURE;
    }
}