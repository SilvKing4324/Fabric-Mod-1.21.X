package net.silvking432.silvkingsmod.command;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.silvking432.silvkingsmod.command.custom.DebugCommand;

public class ModCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            DebugCommand.register(dispatcher);

            // Später kannst du hier einfach erweitern:
            // ConfigCommand.register(dispatcher);
            // StatsCommand.register(dispatcher);
        });
    }
}