package net.silvking432.silvkingsmod.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ElytraFeatureRenderer.class)
public class ElytraLayerMixin {

    @Unique
    private static final Identifier ETERNAL_ELYTRA_TEXTURE =
            Identifier.of(SilvKingsMod.MOD_ID, "textures/entity/eternal_elytra.png");

    @Redirect(
            method = "render*",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z")
    )
    private boolean allowEternalElytraRendering(ItemStack instance, Item item) {
        return instance.isOf(Items.ELYTRA) || instance.isOf(ModItems.ETERNAL_ELYTRA);
    }

    @ModifyVariable(
            method = "render*",
            at = @At("STORE"),
            ordinal = 0
    )
    private Identifier changeElytraTexture(Identifier original, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity) {
        ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);

        if (itemStack.isOf(ModItems.ETERNAL_ELYTRA)) {
            return ETERNAL_ELYTRA_TEXTURE;
        }
        return original;
    }
}