package net.silvking432.silvkingsmod.enchantment.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.util.math.random.Random;

public record SmeltingTouchEnchantmentEffect() implements EnchantmentValueEffect {
    public static final MapCodec<SmeltingTouchEnchantmentEffect> CODEC = MapCodec.unit(SmeltingTouchEnchantmentEffect::new);


    @Override
    public float apply(int level, Random random, float inputValue) {
        return 0;
    }

    @Override
    public MapCodec<? extends EnchantmentValueEffect> getCodec() {
        return CODEC;
    }
}
