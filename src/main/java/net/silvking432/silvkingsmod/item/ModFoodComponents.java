package net.silvking432.silvkingsmod.item;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class ModFoodComponents {
    public static final FoodComponent SUPER_FLOWER = new FoodComponent.Builder().nutrition(8).saturationModifier(1.5f)
            .statusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 400,1), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.SPEED, 400,1), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 400,1), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 400,0), 1.0f)
            .alwaysEdible().snack().build();
}
