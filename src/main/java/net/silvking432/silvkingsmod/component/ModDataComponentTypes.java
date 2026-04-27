package net.silvking432.silvkingsmod.component;

import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.silvking432.silvkingsmod.SilvKingsMod;
import net.silvking432.silvkingsmod.component.custom.CoreTrait;

import java.util.List;
import java.util.function.UnaryOperator;

public class ModDataComponentTypes {

    public static final ComponentType<BlockPos> COORDINATES = register("coordinates", builder -> builder.codec(BlockPos.CODEC));

    public static final ComponentType<List<CoreTrait>> CORE_TRAITS = register("core_traits", builder -> builder.codec(CoreTrait.CODEC.listOf()).packetCodec(CoreTrait.PACKET_CODEC.collect(PacketCodecs.toList())));

    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(SilvKingsMod.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build());

    }

    public static void registerDataComponentTypes() {
        SilvKingsMod.LOGGER.info("Registering Data Component Types for " + SilvKingsMod.MOD_ID);
    }
}
