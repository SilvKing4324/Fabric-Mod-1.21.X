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

    public static final FoodComponent HONEY_BERRY = new FoodComponent.Builder().nutrition(0).saturationModifier(0.15f)
            .statusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 400,0), 0.1f)
            .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 400,0), 0.1f)
            .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 1200,15), 0.1f)
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200,0), 0.1f)
            .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 400,4), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.HASTE, 600,0), 1.0f)
            .statusEffect(new StatusEffectInstance(StatusEffects.HASTE, 900,1), 0.1f)
            .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 1200,5), 1.0f)
            .alwaysEdible().snack().build();
}
