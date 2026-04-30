package net.silvking432.silvkingsmod.entity.client;


import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.custom.NecrosaurusEntity;


public class NecrosaurusModel<T extends NecrosaurusEntity> extends SinglePartEntityModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final EntityModelLayer NECROSAURUS = new EntityModelLayer(Identifier.of(SilvKingsMod.MOD_ID, "necrosaurus"), "main");
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rearlegL;
    private final ModelPart rearlegR;
    private final ModelPart armL;
    private final ModelPart armR;

    public NecrosaurusModel(ModelPart root) {
        this.root = root;
        ModelPart body = root.getChild("body");
        ModelPart lower = body.getChild("lower");
        ModelPart upper = body.getChild("upper");

        this.rearlegL = lower.getChild("rearlegL");
        this.rearlegR = lower.getChild("rearlegR");
        this.armL = upper.getChild("armL");
        this.armR = upper.getChild("armR");
        this.head = upper.getChild("neck").getChild("head");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData partdefinition = modelData.getRoot();

        ModelPartData body = partdefinition.addChild("body", ModelPartBuilder.create(), ModelTransform.of(0.0F, 9.0F, 6.0F, 0.0436F, 0.0F, 0.0F));

        ModelPartData lower = body.addChild("lower", ModelPartBuilder.create().uv(72, 103).cuboid(-7.0F, -5.5F, -6.6667F, 14.0F, 11.0F, 14.0F, new Dilation(0.0F))
                .uv(78, 0).cuboid(-6.0F, -6.5F, -6.6667F, 12.0F, 1.0F, 13.0F, new Dilation(0.0F))
                .uv(78, 44).cuboid(-6.0F, 5.5F, -6.6667F, 12.0F, 1.0F, 13.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.5F, -0.3333F));

        ModelPartData rearlegL = lower.addChild("rearlegL", ModelPartBuilder.create().uv(31, 54).cuboid(-1.0F, -3.5F, -4.0F, 3.0F, 7.0F, 8.0F, new Dilation(0.0F)), ModelTransform.of(7.0F, -1.0F, 1.3333F, -0.0438F, -0.0872F, 0.0038F));
        ModelPartData upperlegL = rearlegL.addChild("upperlegL", ModelPartBuilder.create().uv(70, 63).cuboid(-1.5F, -0.5F, -3.0F, 3.0F, 9.0F, 6.0F, new Dilation(-0.01F)), ModelTransform.of(0.5F, 2.0F, -1.0F, -0.3491F, 0.0F, 0.0F));
        ModelPartData lowerlegL = upperlegL.addChild("lowerlegL", ModelPartBuilder.create().uv(30, 23).cuboid(-1.5F, -0.5F, -2.5F, 3.0F, 6.0F, 5.0F, new Dilation(-0.02F)), ModelTransform.of(0.0F, 8.0F, -0.5F, 0.3491F, 0.0F, 0.0F));
        lowerlegL.addChild("footL", ModelPartBuilder.create().uv(73, 33).cuboid(-1.5F, 0.0F, -3.0F, 3.0F, 2.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 4.2F, -0.5F));

        ModelPartData rearlegR = lower.addChild("rearlegR", ModelPartBuilder.create().uv(46, 62).mirrored().cuboid(-2.0F, -3.5F, -4.0F, 3.0F, 7.0F, 8.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-7.0F, -1.0F, 1.3333F, -0.0436F, 0.0873F, 0.0F));
        ModelPartData upperlegR = rearlegR.addChild("upperlegR", ModelPartBuilder.create().uv(89, 63).mirrored().cuboid(-1.5F, -0.5F, -3.0F, 3.0F, 9.0F, 6.0F, new Dilation(-0.01F)).mirrored(false), ModelTransform.of(-0.5F, 2.0F, -1.0F, 0.4363F, 0.0F, 0.0F));
        ModelPartData lowerlegR = upperlegR.addChild("lowerlegR", ModelPartBuilder.create().uv(14, 54).mirrored().cuboid(-1.5F, -0.5F, -2.5F, 3.0F, 6.0F, 5.0F, new Dilation(-0.02F)).mirrored(false), ModelTransform.of(0.0F, 8.0F, -0.5F, 0.0873F, 0.0F, 0.0F));
        lowerlegR.addChild("footR", ModelPartBuilder.create().uv(80, 93).mirrored().cuboid(-1.5F, 0.7F, -3.0F, 3.0F, 2.0F, 6.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 3.5F, -0.5F, -0.5236F, 0.0F, 0.0F));

        ModelPartData tail = lower.addChild("tail", ModelPartBuilder.create().uv(0, 67).cuboid(-5.0F, -4.0F, -1.0F, 10.0F, 8.0F, 6.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 7.3333F, -0.1745F, 0.0F, 0.0F));
        ModelPartData tail2 = tail.addChild("tail2", ModelPartBuilder.create().uv(18, 36).cuboid(-4.0F, -4.0F, -1.0F, 8.0F, 8.0F, 8.0F, new Dilation(-0.01F)), ModelTransform.of(0.0F, 0.0F, 5.0F, -0.2182F, 0.0F, 0.0F));
        ModelPartData tail3 = tail2.addChild("tail3", ModelPartBuilder.create().uv(100, 71).cuboid(-3.0F, -3.5F, -1.0F, 6.0F, 7.0F, 8.0F, new Dilation(-0.02F)), ModelTransform.of(0.0F, -0.5F, 7.0F, -0.1745F, 0.0F, 0.0F));
        ModelPartData tail4 = tail3.addChild("tail4", ModelPartBuilder.create().uv(52, 40).cuboid(-2.0F, -3.0F, -1.0F, 4.0F, 6.0F, 9.0F, new Dilation(-0.03F)), ModelTransform.of(0.0F, -0.5F, 7.0F, -0.0873F, 0.0F, 0.0F));
        tail4.addChild("tail5", ModelPartBuilder.create().uv(0, 20).cuboid(-2.0F, -2.5F, -1.0F, 4.0F, 5.0F, 9.0F, new Dilation(-0.04F)), ModelTransform.of(0.0F, -0.5F, 8.0F, 0.3491F, 0.0F, 0.0F));

        ModelPartData upper = body.addChild("upper", ModelPartBuilder.create().uv(0, 103).cuboid(-7.0F, -5.5F, -12.0F, 14.0F, 11.0F, 14.0F, new Dilation(-0.1F))
                .uv(80, 30).cuboid(-6.0F, -6.3F, -11.0F, 12.0F, 1.0F, 12.0F, new Dilation(-0.1F))
                .uv(80, 16).cuboid(-6.0F, 5.3F, -11.0F, 12.0F, 1.0F, 12.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 0.5F, -7.0F, -0.0436F, 0.0F, 0.0F));

        ModelPartData armL = upper.addChild("armL", ModelPartBuilder.create().uv(0, 46).cuboid(-1.5F, -3.0F, -2.5F, 3.0F, 6.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(7.0F, 1.0F, -8.5F, 0.0F, -0.0873F, 0.0F));
        ModelPartData upperarmL = armL.addChild("upperarmL", ModelPartBuilder.create().uv(61, 57).cuboid(-1.5F, -1.5F, -2.0F, 3.0F, 7.0F, 4.0F, new Dilation(-0.01F)), ModelTransform.of(0.0F, 1.5F, 0.5F, 0.3491F, 0.0F, 0.0F));
        ModelPartData lowerarmL = upperarmL.addChild("lowerarmL", ModelPartBuilder.create().uv(115, 107).cuboid(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F, new Dilation(-0.02F)), ModelTransform.of(0.0F, 4.5F, -0.5F, -0.6109F, 0.0F, 0.0F));
        lowerarmL.addChild("handL", ModelPartBuilder.create().uv(47, 82).cuboid(-1.5F, 0.0F, -2.5F, 3.0F, 2.0F, 4.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 5.5F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData armR = upper.addChild("armR", ModelPartBuilder.create().uv(112, 59).mirrored().cuboid(-1.5F, -3.0F, -2.5F, 3.0F, 6.0F, 5.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-7.0F, 1.0F, -8.5F, 0.0F, 0.0873F, 0.0F));
        ModelPartData upperarmR = armR.addChild("upperarmR", ModelPartBuilder.create().uv(0, 89).mirrored().cuboid(-1.5F, -1.5F, -2.0F, 3.0F, 7.0F, 4.0F, new Dilation(-0.01F)).mirrored(false), ModelTransform.of(0.0F, 1.5F, 0.5F, 0.3491F, 0.0F, 0.0F));
        ModelPartData lowerarmR = upperarmR.addChild("lowerarmR", ModelPartBuilder.create().uv(86, 81).mirrored().cuboid(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F, new Dilation(-0.02F)).mirrored(false), ModelTransform.of(0.0F, 4.5F, -0.5F, -0.6109F, 0.0F, 0.0F));
        lowerarmR.addChild("handR", ModelPartBuilder.create().uv(0, 82).mirrored().cuboid(-1.5F, 0.0F, -2.5F, 3.0F, 2.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(0.0F, 5.5F, 0.0F, 0.2618F, 0.0F, 0.0F));

        ModelPartData neck = upper.addChild("neck", ModelPartBuilder.create().uv(56, 99).cuboid(-4.0F, -4.0F, -4.2F, 8.0F, 8.0F, 5.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, -11.8F, 0.1309F, 0.0F, 0.0F));
        ModelPartData head = neck.addChild("head", ModelPartBuilder.create().uv(18, 82).cuboid(-5.0F, -5.0475F, -8.0281F, 10.0F, 10.0F, 8.0F, new Dilation(0.0F))
                .uv(100, 87).cuboid(-3.0F, -4.0475F, -16.0281F, 6.0F, 7.0F, 8.0F, new Dilation(0.0F))
                .uv(1, 107).cuboid(-2.0F, -3.0475F, -17.0281F, 4.0F, 7.0F, 2.0F, new Dilation(0.0F))
                .uv(16, 86).cuboid(-1.5F, 3.9525F, -17.0281F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -0.8525F, -3.1719F, -0.0873F, 0.0F, 0.0F));

        head.addChild("jaw", ModelPartBuilder.create().uv(59, 19).cuboid(-2.0F, -0.1F, -8.0F, 4.0F, 2.0F, 11.0F, new Dilation(-0.1F))
                .uv(2, 60).cuboid(-1.5F, -1.2F, -8.2F, 3.0F, 3.0F, 2.0F, new Dilation(-0.1F))
                .uv(121, 92).cuboid(-1.0F, -2.0F, -8.2F, 2.0F, 1.0F, 1.0F, new Dilation(-0.1F)), ModelTransform.of(0.0F, 2.9525F, -8.0281F, 0.0873F, 0.0F, 0.0F));

        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(matrices, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    @Override
    public void setAngles(NecrosaurusEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        netHeadYaw = Math.clamp(netHeadYaw, -30.0F, 30.0F);
        headPitch = Math.clamp(headPitch, -25.0F, 45.0F);

        this.head.yaw = netHeadYaw * ((float)Math.PI / 180F);
        this.head.pitch = headPitch * ((float)Math.PI / 180F);

        this.animateMovement(NecrosaurusAnimations.TRIKE_WALKING, limbSwing, limbSwingAmount, 2f, 2.5f);
        this.updateAnimation(entity.idleAnimationState, NecrosaurusAnimations.TRIKE_IDLE, ageInTicks);
        this.updateAnimation(entity.attackAnimationState, NecrosaurusAnimations.TRIKE_ATTACK, ageInTicks);
    }
}