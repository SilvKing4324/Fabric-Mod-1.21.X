package net.silvking432.silvkingsmod.enchantment;

import com.mojang.serialization.MapCodec;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.enchantment.custom.LightningStrikerEnchantmentEffect;
import net.silvking432.silvkingsmod.enchantment.custom.SmeltingTouchEnchantmentEffect;

public class ModEnchantmentEffects {

    public static final MapCodec<? extends  EnchantmentEntityEffect> LIGHTNING_STRIKER =
            registerEntityEffect("lightning_striker", LightningStrikerEnchantmentEffect.CODEC);

    public static final MapCodec<? extends EnchantmentValueEffect> SMELTING_TOUCH =
            registerValueEffect("smelting_touch", SmeltingTouchEnchantmentEffect.CODEC);



    private static MapCodec<? extends EnchantmentEntityEffect> registerEntityEffect(String name,
                                                                                    MapCodec<? extends EnchantmentEntityEffect> codec) {
        return Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Identifier.of(SilvKingsMod.MOD_ID, name), codec);
    }

    private static MapCodec<? extends EnchantmentValueEffect> registerValueEffect(String name,
                                                                                    MapCodec<? extends EnchantmentValueEffect> codec) {
        return Registry.register(Registries.ENCHANTMENT_VALUE_EFFECT_TYPE, Identifier.of(SilvKingsMod.MOD_ID, name), codec);
    }

    public static void registerEnchantmentEffects() {
        SilvKingsMod.LOGGER.info("Registering Enchantment Effect for " + SilvKingsMod.MOD_ID);
    }
}
