package net.silvking432.silvkingsmod.entity.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.custom.NecroLlamaEntity;

@Environment(EnvType.CLIENT)
public class NecroLlamaModel<T extends NecroLlamaEntity> extends EntityModel<T> {
    public static final EntityModelLayer NECRO_LLAMA = new EntityModelLayer(Identifier.of(SilvKingsMod.MOD_ID, "necro_llama"), "main");

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;

    public NecroLlamaModel(ModelPart root) {
        this.head = root.getChild(EntityModelPartNames.HEAD);
        this.body = root.getChild(EntityModelPartNames.BODY);
        this.rightHindLeg = root.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftHindLeg = root.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.rightFrontLeg = root.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.leftFrontLeg = root.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        Dilation dilation = Dilation.NONE;

        modelPartData.addChild(
                EntityModelPartNames.HEAD,
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-2.0F, -14.0F, -10.0F, 4.0F, 4.0F, 9.0F, dilation)
                        .uv(0, 14).cuboid("neck", -4.0F, -16.0F, -6.0F, 8.0F, 18.0F, 6.0F, dilation)
                        .uv(17, 0).cuboid("ear", -4.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, dilation)
                        .uv(17, 0).cuboid("ear", 1.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, dilation),
                ModelTransform.pivot(0.0F, 7.0F, -6.0F)
        );

        modelPartData.addChild(
                EntityModelPartNames.BODY,
                ModelPartBuilder.create().uv(29, 0).cuboid(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, dilation),
                ModelTransform.of(0.0F, 5.0F, 2.0F, (float) (Math.PI / 2), 0.0F, 0.0F)
        );

        ModelPartBuilder legBuilder = ModelPartBuilder.create().uv(29, 29).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, dilation);
        modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, legBuilder, ModelTransform.pivot(-3.5F, 10.0F, 6.0F));
        modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, legBuilder, ModelTransform.pivot(3.5F, 10.0F, 6.0F));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, legBuilder, ModelTransform.pivot(-3.5F, 10.0F, -5.0F));
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, legBuilder, ModelTransform.pivot(3.5F, 10.0F, -5.0F));

        return TexturedModelData.of(modelData, 128, 64);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.pitch = headPitch * (float) (Math.PI / 180.0);
        this.head.yaw = headYaw * (float) (Math.PI / 180.0);

        this.rightHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
        this.leftHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
        this.rightFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
        this.leftFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.head.render(matrices, vertices, light, overlay, color);
        this.body.render(matrices, vertices, light, overlay, color);
        this.rightHindLeg.render(matrices, vertices, light, overlay, color);
        this.leftHindLeg.render(matrices, vertices, light, overlay, color);
        this.rightFrontLeg.render(matrices, vertices, light, overlay, color);
        this.leftFrontLeg.render(matrices, vertices, light, overlay, color);
    }
}