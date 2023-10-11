package me.axd1x8a.velocitytelegrambridge;

import java.nio.file.Path;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import me.axd1x8a.velocitytelegrambridge.utils.TelegramAPI;
import me.axd1x8a.velocitytelegrambridge.utils.config.ConfigWrapper;
import me.axd1x8a.velocitytelegrambridge.utils.events.ProxyEvents;
import me.axd1x8a.velocitytelegrambridge.utils.events.TelegramEvents;

// @formatter:off
@Plugin(
        id = "velocity-telegram-bridge",
        name = "Velocity Telegram Bridge",
        version = "@version@",
        authors = {"axd1x8a"}
)
// @formatter:on
public class VelocityTelegramBridge {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    @Inject
    public VelocityTelegramBridge(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Velocity Telegram Bridge is initializing...");

        ConfigWrapper config = ConfigWrapper.load(dataDirectory);
        if (config == null) {
            return;
        }

        TelegramAPI telegram = new TelegramAPI(logger, config.getToken(), config.getChatId());

        server.getEventManager().register(this, new ProxyEvents(logger, config, telegram));

        TelegramEvents telegramEvents = new TelegramEvents(config, server);

        telegram.registerUpdatesListener(update -> {
            logger.debug("Handling Telegram message");
            telegramEvents.onMessage(update);
            return null;
        });

    }
}
