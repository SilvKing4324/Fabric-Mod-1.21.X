package net.silvking432.silvkingsmod.mixin;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Structure.class)
public class StructureDistanceMixin {

    @Inject(
            method = "getValidStructurePosition(Lnet/minecraft/world/gen/structure/Structure$Context;)Ljava/util/Optional;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void silvkingsmod$limitRefineryDistance(Structure.Context context, CallbackInfoReturnable<Optional<Structure.StructurePosition>> cir) {
        Structure structure = (Structure) (Object) this;

        context.dynamicRegistryManager().getOptional(RegistryKeys.STRUCTURE).ifPresent(registry -> {
            Identifier id = registry.getId(structure);
            if (id != null && id.getNamespace().equals("silvkingsmod") && id.getPath().equals("refinery")) {
                ChunkPos chunkPos = context.chunkPos();

                long x = chunkPos.getCenterX();
                long z = chunkPos.getCenterZ();
                double distanceSq = (double) (x * x + z * z);
                double minDistance = 2000.0;

                if (distanceSq < (minDistance * minDistance)) {
                    cir.setReturnValue(Optional.empty());
                }
            }
        });
    }
}