package net.silvking432.silvkingsmod.entity.client;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.entity.custom.MagnaTitanEntity;
import org.joml.Vector3f;

public class MagnaTitanModel<T extends MagnaTitanEntity> extends BipedEntityModel<T> {
    public static final EntityModelLayer MAGNA_TITAN = new EntityModelLayer(Identifier.of(SilvKingsMod.MOD_ID, "magna_titan"), "main");

    private final ModelPart root;

    public MagnaTitanModel(ModelPart root) {
        super(root);
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0F);
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root.traverse().forEach(ModelPart::resetTransform);

        super.setAngles(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        SinglePartEntityModel<T> dummy = new SinglePartEntityModel<>() {
            @Override public ModelPart getPart() { return root; }
            @Override public void setAngles(T e, float f, float g, float h, float i, float j) {}
        };

        Vector3f vec = new Vector3f();

        // Idle
        AnimationHelper.animate(dummy, TitanPlayerAnimations.ANIM_TITAN_PLAYER_IDLE, (long)(ageInTicks * 50.0F), 1.0F, vec);

        // Walking
        AnimationHelper.animate(dummy, TitanPlayerAnimations.ANIM_TITAN_PLAYER_WALKING, (long)(limbSwing * 50.0F), limbSwingAmount, vec);

        // Attack
        entity.attackAnimationState.run(state -> AnimationHelper.animate(dummy, TitanPlayerAnimations.ANIM_TITAN_PLAYER_ATTACK, state.getTimeRunning(), 1.0F, vec));
    }
}