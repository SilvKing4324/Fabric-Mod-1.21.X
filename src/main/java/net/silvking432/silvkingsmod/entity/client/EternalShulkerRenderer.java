package net.silvking432.silvkingsmod.entity.client;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ShulkerEntityRenderer;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.silvking432.silvkingsmod.SilvKingsMod;

public class EternalShulkerRenderer extends ShulkerEntityRenderer {
    private static final Identifier TEXTURE = Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/eternal_shulker/eternal_shulker.png");

    public EternalShulkerRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(ShulkerEntity shulkerEntity) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLight(ShulkerEntity entity, BlockPos pos) {
        return 15;
    }
}