package net.silvking432.silvkingsmod.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class DarkFogNetworking {
    public static int clientFogTimer = 0;
    // Neue Variable für den Mixin
    public static float clientExtraVision = 0.0f;

    public static void registerServer() { }

    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(DarkFogPayload.ID, (payload, context) -> {
            // Werte aus der Payload extrahieren
            int timer = payload.fogValue();
            float vision = payload.extraVision();

            context.client().execute(() -> {
                clientFogTimer = timer;
                clientExtraVision = vision;
            });
        });
    }

    // Angepasste Methode mit zwei Parametern
    public static void sendSyncPacket(ServerPlayerEntity player, int timerValue, float visionValue) {
        ServerPlayNetworking.send(player, new DarkFogPayload(timerValue, visionValue));
    }
}