package me.axd1x8a.velocitytelegrambridge.utils.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.velocitypowered.api.command.CommandSource;

public interface CommandInterface extends Command<CommandSource> {

    @Override
    default int run(CommandContext<CommandSource> context) {
        execute(context.getSource());
        return Command.SINGLE_SUCCESS;
    }

    void execute(CommandSource source);
}
