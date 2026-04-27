package net.silvking432.silvkingsmod.entity.client;

import net.minecraft.client.render.entity.BeeEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.entity.passive.BeeEntity;
import net.silvking432.silvkingsmod.SilvKingsMod;

public class NecroBeeRenderer extends BeeEntityRenderer {
    private static final Identifier TEXTURE = Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/necro_bee/necro_bee.png");
    private static final Identifier ANGRY_TEXTURE = Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/necro_bee/necro_bee_angry.png");

    public NecroBeeRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(BeeEntity beeEntity) {
        return beeEntity.hasAngerTime() ? ANGRY_TEXTURE : TEXTURE;
    }
}