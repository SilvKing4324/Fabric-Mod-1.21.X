package net.silvking432.silvkingsmod.mixin;

import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.SaveLoader;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(IntegratedServerLoader.class)
public interface IntegratedServerLoaderInvoker {
    @Invoker("start")
    void invokeStart(LevelStorage.Session session, SaveLoader saveLoader, ResourcePackManager dataPackManager, Runnable onCancel);
}