package net.silvking432.silvkingsmod.mixin;

import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.SaveLoader;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


// Used for bypassing the annoying World is Using Experimental Features Screen
@Mixin(IntegratedServerLoader.class)
public class BypassExperimentalLoaderMixin {
    @Inject(method = "checkBackupAndStart", at = @At("HEAD"), cancellable = true)
    private void silvkingsmod$forceStartWithoutWarning(LevelStorage.Session session, SaveLoader saveLoader, ResourcePackManager dataPackManager, Runnable onCancel, CallbackInfo ci) {
        ((IntegratedServerLoaderInvoker)this).invokeStart(session, saveLoader, dataPackManager, onCancel);
        ci.cancel();
    }
}