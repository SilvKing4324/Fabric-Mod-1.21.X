package net.silvking432.silvkingsmod.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.custom.NecroGeckoEntity;

public class NecroGeckoRenderer extends MobEntityRenderer<NecroGeckoEntity, NecroGeckoModel<NecroGeckoEntity>> {
    private static final Identifier TEXTURE = Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/gecko/necro_gecko.png");

    public NecroGeckoRenderer(EntityRendererFactory.Context context) {
        super(context, new NecroGeckoModel<>(context.getPart(NecroGeckoModel.NECRO_GECKO)), 0.75f);
    }

    @Override
    public Identifier getTexture(NecroGeckoEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(NecroGeckoEntity entity, MatrixStack matrices, float amount) {
        matrices.scale(3.0f, 3.0f, 3.0f);
    }
}