package net.silvking432.silvkingsmod.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.silvking432.silvkingsmod.mixin.GameRendererInvoker;

public class ShaderTestCommand {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("shadertest")
                .then(ClientCommandManager.argument("name", StringArgumentType.greedyString())
                        .executes(context -> {
                            String shaderName = StringArgumentType.getString(context, "name");
                            MinecraftClient client = MinecraftClient.getInstance();

                            if (client == null) return 0;

                            if (shaderName.equalsIgnoreCase("off")) {
                                client.execute(() -> ((GameRendererInvoker) client.gameRenderer).invokeDisablePostProcessor());
                                context.getSource().sendFeedback(Text.of("Shader disabled."));
                                return 1;
                            }

                            Identifier shaderId;
                            if (shaderName.contains(":")) {
                                String[] parts = shaderName.split(":");
                                shaderId = Identifier.of(parts[0], "shaders/post/" + parts[1] + ".json");
                            } else {
                                shaderId = Identifier.of("minecraft", "shaders/post/" + shaderName + ".json");
                            }

                            client.execute(() -> {
                                try {
                                    ((GameRendererInvoker) client.gameRenderer).invokeLoadPostProcessor(shaderId);
                                } catch (Exception ignored) {
                                }
                            });

                            context.getSource().sendFeedback(Text.of("Loading: " + shaderId));
                            return 1;
                        }))));
    }
}