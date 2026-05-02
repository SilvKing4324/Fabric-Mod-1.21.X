package net.silvking432.silvkingsmod.entity.client.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.custom.NecroWolfEntity;

@Environment(EnvType.CLIENT)
public class NecroWolfEntityModel<T extends NecroWolfEntity> extends TintableAnimalModel<T> implements ModelWithHead, ModelWithArms {
    public static final EntityModelLayer NECRO_WOLF = new EntityModelLayer(Identifier.of(SilvKingsMod.MOD_ID, "necro_wolf"), "main");

    private final ModelPart head;
    private final ModelPart realHead;
    private final ModelPart torso;
    private final ModelPart rightHindLeg;
    private final ModelPart leftHindLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;
    private final ModelPart realTail;
    private final ModelPart neck;

    public NecroWolfEntityModel(ModelPart root) {
        this.head = root.getChild(EntityModelPartNames.HEAD);
        this.realHead = this.head.getChild("real_head");
        this.torso = root.getChild(EntityModelPartNames.BODY);
        this.neck = root.getChild("upper_body");
        this.rightHindLeg = root.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
        this.leftHindLeg = root.getChild(EntityModelPartNames.LEFT_HIND_LEG);
        this.rightFrontLeg = root.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
        this.leftFrontLeg = root.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
        this.tail = root.getChild(EntityModelPartNames.TAIL);
        this.realTail = this.tail.getChild("real_tail");
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create(), ModelTransform.pivot(-1.0F, 13.5F, -7.0F));
        modelPartData2.addChild("real_head", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, dilation).uv(16, 14).cuboid(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, dilation).uv(16, 14).cuboid(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, dilation).uv(0, 10).cuboid(-0.5F, -0.001F, -5.0F, 3.0F, 3.0F, 4.0F, dilation), ModelTransform.NONE);

        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(18, 14).cuboid(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, dilation), ModelTransform.of(0.0F, 14.0F, 2.0F, ((float)Math.PI / 2F), 0.0F, 0.0F));
        modelPartData.addChild("upper_body", ModelPartBuilder.create().uv(21, 0).cuboid(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F, dilation), ModelTransform.of(-1.0F, 14.0F, -3.0F, ((float)Math.PI / 2F), 0.0F, 0.0F));

        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 18).cuboid(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, dilation);
        modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-2.5F, 16.0F, 7.0F));
        modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(0.5F, 16.0F, 7.0F));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(-2.5F, 16.0F, -4.0F));
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(0.5F, 16.0F, -4.0F));

        ModelPartData modelPartData3 = modelPartData.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create(), ModelTransform.of(-1.0F, 12.0F, 8.0F, ((float)Math.PI / 5F), 0.0F, 0.0F));
        modelPartData3.addChild("real_tail", ModelPartBuilder.create().uv(9, 18).cuboid(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, dilation), ModelTransform.NONE);

        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.torso, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.tail, this.neck);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        // Kopf-Rotation
        this.head.pitch = headPitch * ((float)Math.PI / 180F);
        this.head.yaw = headYaw * ((float)Math.PI / 180F);

        // Standard-Körper-Position (Da der Necro Wolf nicht sitzt)
        this.torso.setPivot(0.0F, 14.0F, 2.0F);
        this.torso.pitch = ((float)Math.PI / 2F);
        this.neck.setPivot(-1.0F, 14.0F, -3.0F);
        this.neck.pitch = this.torso.pitch;
        this.tail.setPivot(-1.0F, 12.0F, 8.0F);

        // Beinanimation
        this.rightHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
        this.leftHindLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float)Math.PI) * 1.4F * limbDistance;
        this.rightFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float)Math.PI) * 1.4F * limbDistance;
        this.leftFrontLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;

        // Schwanzwedeln (immer ein bisschen böse/nervös)
        this.tail.yaw = MathHelper.cos(limbAngle * 0.6662F) * 0.6F * limbDistance;

        // Shake-Animationen und Begging entfernt, da NecroWolf eine HostileEntity ist
        this.realHead.roll = 0;
        this.neck.roll = 0;
        this.torso.roll = 0;
        this.realTail.roll = 0;
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }

    @Override
    public void setArmAngle(Arm arm, MatrixStack matrices) {
        this.head.rotate(matrices);
    }
}