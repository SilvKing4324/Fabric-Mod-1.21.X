package net.silvking432.silvkingsmod.mixin;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.player.PlayerEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.silvking432.silvkingsmod.network.DarkFogNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
public class DarkWorldFogMixin {

    @Inject(method = "applyFog", at = @At("TAIL"))
    private static void silvkingsmod$applyDarkWorldFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        if (!(camera.getFocusedEntity() instanceof PlayerEntity player)) return;

        if (player.getWorld().getRegistryKey().getValue().getPath().equals("dark_world")) {
            int timer = DarkFogNetworking.clientFogTimer;
            float extraVision = DarkFogNetworking.clientExtraVision;

            if (timer > 0) {
                float fogFactor = 1.0f - (Math.min(timer, 24000) / 24000.0f);
                float end = (viewDistance * fogFactor) + extraVision;
                float start = end * 0.95f;

                RenderSystem.setShaderFogStart(start);
                RenderSystem.setShaderFogEnd(end);
            }
        }
    }
}