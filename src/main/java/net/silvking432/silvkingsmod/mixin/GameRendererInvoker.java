package net.silvking432.silvkingsmod.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererInvoker {

    // Macht loadPostProcessor verfügbar
    @Invoker("loadPostProcessor")
    void invokeLoadPostProcessor(Identifier id);

    // Macht disablePostProcessor verfügbar (zum Ausschalten)
    @Invoker("disablePostProcessor")
    void invokeDisablePostProcessor();
}