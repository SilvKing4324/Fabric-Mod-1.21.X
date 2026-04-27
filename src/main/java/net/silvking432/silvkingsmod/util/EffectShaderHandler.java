package net.silvking432.silvkingsmod.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.effect.ModEffects;
import net.silvking432.silvkingsmod.mixin.GameRendererInvoker;

public class EffectShaderHandler {

    private static final Identifier RED_PIXEL_SHADER = Identifier.of("silvkingsmod", "shaders/post/anxiety.json");
    private static boolean wasEffectActive = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.gameRenderer == null) return;

            boolean hasAnxiety = client.player.hasStatusEffect(ModEffects.ANXIETY);

            if (hasAnxiety) {

                if (client.gameRenderer.getPostProcessor() == null) {
                    activateShader(client);
                }
                wasEffectActive = true;
            }
            else if (wasEffectActive) {
                deactivateShader(client);
                wasEffectActive = false;
            }
        });
    }

    private static void activateShader(MinecraftClient client) {
        client.execute(() -> ((GameRendererInvoker) client.gameRenderer).invokeLoadPostProcessor(RED_PIXEL_SHADER));
    }

    private static void deactivateShader(MinecraftClient client) {
        client.execute(() -> ((GameRendererInvoker) client.gameRenderer).invokeDisablePostProcessor());
    }
}