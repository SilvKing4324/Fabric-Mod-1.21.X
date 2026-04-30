package net.silvking432.silvkingsmod.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.custom.NecroLlamaEntity;

public class NecroLlamaRenderer extends MobEntityRenderer<NecroLlamaEntity, NecroLlamaModel<NecroLlamaEntity>> {

    private static final Identifier TEXTURE = Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/necro_llama.png");

    public NecroLlamaRenderer(EntityRendererFactory.Context context) {
        super(context, new NecroLlamaModel<>(context.getPart(EntityModelLayers.LLAMA)), 0.7f);
    }

    @Override
    public Identifier getTexture(NecroLlamaEntity entity) {
        return TEXTURE;
    }
}