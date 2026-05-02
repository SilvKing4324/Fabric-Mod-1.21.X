package net.silvking432.silvkingsmod.entity.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.client.animations.RhinoAnimations;
import net.silvking432.silvkingsmod.entity.custom.RhinoEntity;

public class RhinoModel<T extends RhinoEntity> extends SinglePartEntityModel<T> {
    public static final EntityModelLayer RHINO_LAYER = new EntityModelLayer(Identifier.of(SilvKingsMod.MOD_ID, "rhino_layer"), "main");


    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rhino;
    private final ModelPart body;


    public RhinoModel(ModelPart root) {
        this.root = root;
        this.rhino = root.getChild("rhino");
        this.body = rhino.getChild("body");

        ModelPart torso = body.getChild("torso");

        this.head = torso.getChild("head");

    }
    public static TexturedModelData getTexturedModelData() {

        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData rhino = modelPartData.addChild("rhino", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 2.5F));

        ModelPartData body = rhino.addChild("body", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData torso = body.addChild("torso", ModelPartBuilder.create().uv(0, 0).cuboid(-10.0F, -38.0F, -26.0F, 20.0F, 24.0F, 16.0F, new Dilation(0.0F))
                .uv(0, 41).cuboid(-9.0F, -37.0F, -10.0F, 18.0F, 25.0F, 16.0F, new Dilation(0.0F))
                .uv(53, 67).cuboid(-7.0F, -37.0F, 6.0F, 14.0F, 21.0F, 16.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData head = torso.addChild("head", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, -21.0F, -26.0F));

        ModelPartData skull = head.addChild("skull", ModelPartBuilder.create().uv(69, 29).cuboid(-6.0F, -9.0F, -12.0F, 12.0F, 20.0F, 12.0F, new Dilation(0.0F))
                .uv(0, 8).cuboid(-1.0F, -1.0F, -15.0F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData horn = skull.addChild("horn", ModelPartBuilder.create().uv(0, 102).cuboid(-5.0F, -5.0F, -9.0F, 5.0F, 5.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(2.5F, 8.5F, -11.0F, -0.1745F, 0.0F, 0.0F));

        ModelPartData horn2 = horn.addChild("horn2", ModelPartBuilder.create().uv(103, 15).cuboid(-3.0F, -3.0F, -9.0F, 3.0F, 3.0F, 9.0F, new Dilation(0.0F)), ModelTransform.of(-1.0F, -1.0F, -9.0F, -0.4363F, 0.0F, 0.0F));

        ModelPartData left_ear = skull.addChild("left_ear", ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -5.0F, -0.5F, 4.0F, 6.0F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(5.0F, -8.0F, -9.5F));

        ModelPartData right_ear = skull.addChild("right_ear", ModelPartBuilder.create().uv(0, 0).mirrored().cuboid(-3.0F, -5.0F, -0.5F, 4.0F, 6.0F, 1.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-5.0F, -8.0F, -9.5F));

        ModelPartData left_eye = skull.addChild("left_eye", ModelPartBuilder.create().uv(55, 46).cuboid(-0.45F, -0.4F, -0.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(57, 46).cuboid(-0.55F, -1.6F, -1.1F, 1.0F, 3.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(5.8F, 5.1F, -9.4F, 0.2618F, 0.0F, 0.0F));

        ModelPartData left_eyelid = skull.addChild("left_eyelid", ModelPartBuilder.create().uv(42, 85).cuboid(-0.55F, -2.1F, -1.6F, 1.0F, 4.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(5.05F, 5.1F, -9.4F));

        ModelPartData right_eyelid = skull.addChild("right_eyelid", ModelPartBuilder.create().uv(42, 85).mirrored().cuboid(-0.45F, -2.1F, -1.6F, 1.0F, 4.0F, 4.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-5.05F, 5.1F, -9.4F));

        ModelPartData right_eye = skull.addChild("right_eye", ModelPartBuilder.create().uv(55, 46).mirrored().cuboid(-0.55F, -0.4F, -0.9F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F)).mirrored(false)
                .uv(57, 46).mirrored().cuboid(-0.45F, -1.6F, -1.1F, 1.0F, 3.0F, 3.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.of(-5.8F, 5.1F, -9.4F, 0.2618F, 0.0F, 0.0F));

        ModelPartData tail = torso.addChild("tail", ModelPartBuilder.create().uv(16, 88).cuboid(-1.5F, 0.0F, 0.0F, 3.0F, 0.0F, 13.0F, new Dilation(0.0F))
                .uv(57, 0).cuboid(-2.5F, 0.0F, 13.0F, 5.0F, 0.0F, 7.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -35.0F, 22.0F, -1.309F, 0.0F, 0.0F));

        ModelPartData left_back_leg = body.addChild("left_back_leg", ModelPartBuilder.create().uv(73, 0).cuboid(-4.5F, -4.5F, -5.0F, 9.0F, 13.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(7.0F, -28.5F, 15.5F));

        ModelPartData left_back_knee = left_back_leg.addChild("left_back_knee", ModelPartBuilder.create().uv(98, 62).cuboid(-3.5F, 0.0F, -1.0F, 7.0F, 10.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 8.5F, -3.0F));

        ModelPartData left_back_heel = left_back_knee.addChild("left_back_heel", ModelPartBuilder.create().uv(54, 105).cuboid(-3.0F, 0.0F, 0.0F, 6.0F, 10.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 10.0F, -0.5F));

        ModelPartData right_back_leg = body.addChild("right_back_leg", ModelPartBuilder.create().uv(73, 0).mirrored().cuboid(-4.5F, -4.5F, -5.0F, 9.0F, 13.0F, 10.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-7.0F, -28.5F, 15.5F));

        ModelPartData right_back_knee = right_back_leg.addChild("right_back_knee", ModelPartBuilder.create().uv(98, 62).mirrored().cuboid(-3.5F, 0.0F, -1.0F, 7.0F, 10.0F, 7.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 8.5F, -3.0F));

        ModelPartData right_back_heel = right_back_knee.addChild("right_back_heel", ModelPartBuilder.create().uv(54, 105).mirrored().cuboid(-3.0F, 0.0F, 0.0F, 6.0F, 10.0F, 6.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 10.0F, -0.5F));

        ModelPartData right_front_leg = body.addChild("right_front_leg", ModelPartBuilder.create().uv(100, 111).mirrored().cuboid(-3.5F, -3.0F, -3.0F, 7.0F, 10.0F, 7.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(-7.0F, -17.0F, -20.5F));

        ModelPartData right_front_knee = right_front_leg.addChild("right_front_knee", ModelPartBuilder.create().uv(54, 105).mirrored().cuboid(-3.0F, 0.0F, 0.0F, 6.0F, 10.0F, 6.0F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, 7.0F, -2.5F));

        ModelPartData left_front_leg = body.addChild("left_front_leg", ModelPartBuilder.create().uv(100, 111).cuboid(-3.5F, -3.0F, -3.0F, 7.0F, 10.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(7.0F, -17.0F, -20.5F));

        ModelPartData left_front_knee = left_front_leg.addChild("left_front_knee", ModelPartBuilder.create().uv(54, 105).cuboid(-3.0F, 0.0F, 0.0F, 6.0F, 10.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 7.0F, -2.5F));
        return TexturedModelData.of(modelData, 128, 128);
    }
    @Override
    public void setAngles(RhinoEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.applyHeadRotation(netHeadYaw, headPitch);

        this.animateMovement(RhinoAnimations.RHINO_WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
        this.updateAnimation(entity.idleAnimationState, RhinoAnimations.RHINO_IDLE, ageInTicks, 1f);
        this.updateAnimation(entity.sitAnimationState, RhinoAnimations.RHINO_SIT, ageInTicks, 1f);
        this.updateAnimation(entity.attackAnimationState, RhinoAnimations.RHINO_ATTACK, ageInTicks, 1f);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    private void applyHeadRotation(float headYaw, float headPitch) {
        headYaw = Math.clamp(headYaw, -30f, 30f);
        headPitch = Math.clamp(headPitch, -25f, 45);

        this.head.yaw = headYaw * ((float)Math.PI / 180f);
        this.head.pitch = headPitch *  ((float)Math.PI / 180f);

    }
}