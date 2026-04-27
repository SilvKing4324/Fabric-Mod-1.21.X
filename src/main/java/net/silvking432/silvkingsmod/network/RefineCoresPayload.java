package net.silvking432.silvkingsmod.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.SilvKingsMod;

public record RefineCoresPayload() implements CustomPayload {
    public static final Id<RefineCoresPayload> ID = new Id<>(Identifier.of(SilvKingsMod.MOD_ID, "refine_cores"));

    public static final PacketCodec<RegistryByteBuf, RefineCoresPayload> CODEC =
            PacketCodec.unit(new RefineCoresPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}