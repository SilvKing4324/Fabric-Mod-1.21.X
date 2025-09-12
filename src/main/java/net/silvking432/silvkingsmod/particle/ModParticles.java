package net.silvking432.silvkingsmod.particle;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;

public class ModParticles {

    public static final SimpleParticleType STARLIGHT_ASHES_PARTICLE = registerParticle("starlight_ashes_particle", FabricParticleTypes.simple());

    private static SimpleParticleType registerParticle(String name, SimpleParticleType particleType) {
        return Registry.register(Registries.PARTICLE_TYPE, Identifier.of(SilvKingsMod.MOD_ID, name), particleType);
    }

    public static void registerParticles() {
        SilvKingsMod.LOGGER.info("Registering Particles for " + SilvKingsMod.MOD_ID);
    }
}
