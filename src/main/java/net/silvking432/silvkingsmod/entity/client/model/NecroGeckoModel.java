package net.silvking432.silvkingsmod.entity.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.client.animations.GeckoAnimations;
import net.silvking432.silvkingsmod.entity.custom.NecroGeckoEntity;

public class NecroGeckoModel<T extends NecroGeckoEntity> extends SinglePartEntityModel<T> {
    public static final EntityModelLayer NECRO_GECKO = new EntityModelLayer(Identifier.of(SilvKingsMod.MOD_ID, "necro_gecko"), "main");

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;

    public NecroGeckoModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("Body");
        this.head = this.body.getChild("Head");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        // Hier kopierst du exakt denselben Inhalt aus deiner GeckoModel.getTexturedModelData() rein
        ModelPartData Body = modelPartData.addChild("Body", ModelPartBuilder.create().uv(0, 0).cuboid(-1.25F, -2.25F, -3.0F, 2.5F, 2.25F, 4.5F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 23.5F, 1.0F));

        ModelPartData Head = Body.addChild("Head", ModelPartBuilder.create().uv(8, 7).cuboid(-1.0F, -1.0623F, -1.9587F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-0.75F, -0.3123F, -2.4087F, 1.5F, 1.25F, 0.45F, new Dilation(0.0F))
                .uv(4, 19).cuboid(-1.25F, -0.8123F, -1.7087F, 0.75F, 0.75F, 0.75F, new Dilation(0.0F))
                .uv(4, 19).mirrored().cuboid(0.5F, -0.8123F, -1.7087F, 0.75F, 0.75F, 0.75F, new Dilation(0.0F)).mirrored(false), ModelTransform.pivot(0.0F, -1.0F, -3.0F));

        ModelPartData Tail = Body.addChild("Tail", ModelPartBuilder.create().uv(0, 7).cuboid(-1.0F, -1.0F, 0.0F, 2.0F, 1.9F, 3.5F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, -1.0F, 1.5F));

        ModelPartData FrontLegL = Body.addChild("FrontLegL", ModelPartBuilder.create(), ModelTransform.pivot(1.1986F, -0.4741F, -2.4807F));
        FrontLegL.addChild("FLegL_r1", ModelPartBuilder.create().uv(11, 12).cuboid(-0.1428F, -0.4441F, -0.6528F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.2612F, -0.0259F, 0.0752F, 0.0573F, 0.2106F, 0.2679F));

        ModelPartData FrontLegR = Body.addChild("FrontLegR", ModelPartBuilder.create(), ModelTransform.pivot(-1.275F, -0.4804F, -2.5515F));
        FrontLegR.addChild("FLegR_r1", ModelPartBuilder.create().uv(0, 13).cuboid(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.5F, 0.25F, -0.25F, 0.0573F, -0.2106F, -0.2679F));

        ModelPartData BackLegL = Body.addChild("BackLegL", ModelPartBuilder.create(), ModelTransform.pivot(1.2612F, -0.5957F, 0.7739F));
        BackLegL.addChild("BLegL_r1", ModelPartBuilder.create().uv(11, 2).cuboid(-0.832F, -0.4122F, -0.4744F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(-0.0612F, -0.0043F, -0.0239F, -0.0883F, -0.3958F, 0.2794F));

        ModelPartData BackLegR = Body.addChild("BackLegR", ModelPartBuilder.create(), ModelTransform.pivot(-1.2612F, -0.5957F, 0.7739F));
        BackLegR.addChild("BLegR_r1", ModelPartBuilder.create().uv(11, 0).cuboid(-2.0F, -1.0F, -1.0F, 3.0F, 1.0F, 1.0F, new Dilation(0.0F)), ModelTransform.of(0.2612F, 0.5957F, 0.4761F, -0.0883F, 0.3958F, -0.2794F));

        ModelPartData bb_main = modelPartData.addChild("bb_main", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));
        bb_main.addChild("camera_r1", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, -2.0F, 0.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, 0.0F, 0.0F, -2.9795F, -0.6308F, 3.1416F));

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);

        this.head.yaw = netHeadYaw * ((float)Math.PI / 180f);
        this.head.pitch = headPitch * ((float)Math.PI / 180f);

        this.animateMovement(GeckoAnimations.GECKO_WALK, limbSwing, limbSwingAmount, 2f, 2.5f);
        this.updateAnimation(entity.idleAnimationState, GeckoAnimations.GECKO_IDLE, ageInTicks, 1f);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        root.render(matrices, vertices, light, overlay, color);
    }
}