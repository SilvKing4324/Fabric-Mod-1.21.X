package net.silvking432.silvkingsmod.component.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record CoreTrait(String traitId, float value) {
    public static final Codec<CoreTrait> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("id").forGetter(CoreTrait::traitId),
                    Codec.FLOAT.fieldOf("value").forGetter(CoreTrait::value)
            ).apply(instance, CoreTrait::new));

    public static final PacketCodec<RegistryByteBuf, CoreTrait> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, CoreTrait::traitId,
            PacketCodecs.FLOAT, CoreTrait::value,
            CoreTrait::new);
}