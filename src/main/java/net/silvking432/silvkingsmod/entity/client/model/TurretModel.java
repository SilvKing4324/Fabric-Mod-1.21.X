package net.silvking432.silvkingsmod.entity.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.custom.TurretEntity;

public class TurretModel extends EntityModel<TurretEntity> {
    public static final EntityModelLayer TURRET_LAYER = new EntityModelLayer(Identifier.of(SilvKingsMod.MOD_ID, "turret_layer"), "main");


    private final ModelPart base;

    public TurretModel(ModelPart root) {
        this.base = root.getChild("base");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 0)
                .cuboid(-8F, 0F, -8F, 16F, 16F, 16F), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(TurretEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {}

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        base.render(matrices, vertices, light, overlay, color);
    }
}