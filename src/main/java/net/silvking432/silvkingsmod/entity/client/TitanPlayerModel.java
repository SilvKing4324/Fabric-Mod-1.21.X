package net.silvking432.silvkingsmod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.custom.TitanPlayerEntity;

public class TitanPlayerModel<T extends TitanPlayerEntity> extends SinglePartEntityModel<T> {
    public static final EntityModelLayer TITAN_PLAYER = new EntityModelLayer(Identifier.of(SilvKingsMod.MOD_ID, "titan_player"),"main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart arms;
    private final ModelPart right_arm;
    private final ModelPart left_arm;
    private final ModelPart legs;
    private final ModelPart right_leg;
    private final ModelPart left_leg;

    public TitanPlayerModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("head");
        this.body = this.root.getChild("body");
        this.arms = this.root.getChild("arms");
        this.right_arm = this.arms.getChild("right_arm");
        this.left_arm = this.arms.getChild("left_arm");
        this.legs = this.root.getChild("legs");
        this.right_leg = this.legs.getChild("right_leg");
        this.left_leg = this.legs.getChild("left_leg");
    }
    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 6.0F, 0.0F));

        ModelPartData head = root.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -14.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData body = root.addChild("body", ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, -6.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData arms = root.addChild("arms", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData right_arm = arms.addChild("right_arm", ModelPartBuilder.create().uv(32, 48).cuboid(-4.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-4.0F, -6.0F, 0.0F));

        ModelPartData left_arm = arms.addChild("left_arm", ModelPartBuilder.create().uv(32, 48).cuboid(0.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(4.0F, -6.0F, 0.0F));

        ModelPartData legs = root.addChild("legs", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 18.0F, 0.0F));

        ModelPartData right_leg = legs.addChild("right_leg", ModelPartBuilder.create().uv(16, 48).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, -12.0F, 0.0F));

        ModelPartData left_leg = legs.addChild("left_leg", ModelPartBuilder.create().uv(16, 48).cuboid(0.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -11.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
    @Override
    public void setAngles(TitanPlayerEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        this.setHeadAngles(netHeadYaw, headPitch);

        this.animateMovement(TitanPlayerAnimations.ANIM_TITAN_PLAYER_WALKING, limbSwing, limbSwingAmount, 2f, 2.5f);
        this.updateAnimation(entity.idleAnimationState, TitanPlayerAnimations.ANIM_TITAN_PLAYER_IDLE, ageInTicks, 1f);
        this.updateAnimation(entity.attackAnimationState, TitanPlayerAnimations.ANIM_TITAN_PLAYER_ATTACK, ageInTicks, 1f);
    }


    private void setHeadAngles(float headYaw, float headPitch) {
        headYaw = MathHelper.clamp(headYaw, -30.0F, 30.0F);
        headPitch = MathHelper.clamp(headPitch, -25.0F, 45.0F);

        this.head.yaw = headYaw * 0.017453292F;
        this.head.pitch = headPitch * 0.017453292F;
    }


    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        root.render(matrices, vertexConsumer, light, overlay, color);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }
}
