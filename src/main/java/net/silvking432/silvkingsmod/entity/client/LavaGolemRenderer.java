package net.silvking432.silvkingsmod.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IronGolemEntityRenderer;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;

public class LavaGolemRenderer extends IronGolemEntityRenderer {
    private static final Identifier TEXTURE = Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/lava_golem/lava_golem.png");

    public LavaGolemRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(net.minecraft.entity.passive.IronGolemEntity entity) {
        return TEXTURE;
    }
}