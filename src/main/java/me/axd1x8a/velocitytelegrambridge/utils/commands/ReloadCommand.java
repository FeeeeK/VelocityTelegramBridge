package me.axd1x8a.velocitytelegrambridge.utils.commands;

import com.velocitypowered.api.command.CommandSource;

import me.axd1x8a.velocitytelegrambridge.VelocityTelegramBridge;
import net.kyori.adventure.text.Component;

public class ReloadCommand implements CommandInterface {
    private VelocityTelegramBridge plugin;

    public ReloadCommand(VelocityTelegramBridge plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSource source) {
        plugin.unload();
        plugin.load();
        source.sendMessage(Component.text("Reloaded configuration file."));
    }
}
