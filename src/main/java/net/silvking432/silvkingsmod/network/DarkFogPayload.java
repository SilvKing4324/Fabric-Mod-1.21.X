package net.silvking432.silvkingsmod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;

public record DarkFogPayload(int fogValue, float extraVision) implements CustomPayload {
    public static final Id<DarkFogPayload> ID = new Id<>(Identifier.of(SilvKingsMod.MOD_ID, "dark_fog_payload"));

    public static final PacketCodec<RegistryByteBuf, DarkFogPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, DarkFogPayload::fogValue,
            PacketCodecs.FLOAT, DarkFogPayload::extraVision,
            DarkFogPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}