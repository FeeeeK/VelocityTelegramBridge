package me.axd1x8a.velocitytelegrambridge.utils.events;

import java.util.Map;

import com.pengrad.telegrambot.model.Update;
import com.velocitypowered.api.proxy.ProxyServer;

import me.axd1x8a.velocitytelegrambridge.utils.StringFormatter;
import me.axd1x8a.velocitytelegrambridge.utils.config.ConfigWrapper;
import net.kyori.adventure.text.Component;

public class TelegramEvents {
    private final ConfigWrapper config;
    private final ProxyServer server;

    public TelegramEvents(ConfigWrapper config, ProxyServer server) {
        this.config = config;
        this.server = server;
    }

    public void onMessage(Update update) {
        if (!config.getEvents().message_from_telegram_enabled) {
            return;
        }
        String message = update.message().text();
        if (message == null) {
            return;
        }
        String authorFirstName = update.message().from().firstName();
        String authorLastName = update.message().from().lastName();
        String author = authorFirstName;
        if (authorLastName != null) {
            author += " " + authorLastName;
        }
        String formattedMessage = StringFormatter.format(config.getEvents().message_from_telegram_format,
                Map.of("message", message, "author", author));
        server.sendMessage(Component.text(formattedMessage));
    }
}
