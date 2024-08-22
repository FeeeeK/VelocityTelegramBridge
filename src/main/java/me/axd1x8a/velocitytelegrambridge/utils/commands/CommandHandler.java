package me.axd1x8a.velocitytelegrambridge.utils.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;

import me.axd1x8a.velocitytelegrambridge.VelocityTelegramBridge;
import net.kyori.adventure.text.Component;

public class CommandHandler {
    VelocityTelegramBridge plugin;
    CommandManager commandManager;
    CommandMeta commandMeta;

    public CommandHandler(CommandManager commandManager, VelocityTelegramBridge plugin) {
        this.commandManager = commandManager;
        this.plugin = plugin;
    }

    public void registerCommands() {
        ReloadCommand reloadCommand = new ReloadCommand(plugin);
        this.commandMeta = commandManager.metaBuilder("vtb").aliases("velocitytelegrambridge").build();
        commandManager.register(commandMeta, new BrigadierCommand(
                BrigadierCommand.literalArgumentBuilder("vtb").executes(this::defaultCommand)
                        .then(LiteralArgumentBuilder.<CommandSource>literal("reload").executes(reloadCommand)
                                .requires(
                                        source -> source.hasPermission("vtb.admin")
                                                || source.hasPermission("op")))));
    }

    public void unregisterCommands() {
        commandManager.unregister(commandMeta);
    }

    public int defaultCommand(CommandContext<CommandSource> context) {
        context.getSource().sendMessage(Component.text(
                String.format("Running Velocity Telegram Bridge version %s by axd1x8a", plugin.version)));
        return Command.SINGLE_SUCCESS;
    }
}
