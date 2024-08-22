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
import me.axd1x8a.velocitytelegrambridge.utils.commands.CommandHandler;
import me.axd1x8a.velocitytelegrambridge.utils.config.ConfigWrapper;
import me.axd1x8a.velocitytelegrambridge.utils.events.BackendServerEvents;
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
    private ConfigWrapper config;
    private TelegramAPI telegram;
    private CommandHandler commandHandler;
    public String version;

    @Inject
    public VelocityTelegramBridge(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.version = server.getPluginManager().getPlugin("velocity-telegram-bridge").get()
                .getDescription()
                .getVersion().get();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        load();
    }

    private void registerCommands(ConfigWrapper config) {
        this.commandHandler = new CommandHandler(server.getCommandManager(), this);
        commandHandler.registerCommands();
    }

    private void unregisterCommands() {
        commandHandler.unregisterCommands();
    }

    private void registerEvents() {
        server.getEventManager().register(this, new ProxyEvents(logger, config, telegram));
        if (server.getPluginManager().isLoaded("yeplib")) {
            server.getEventManager().register(this, new BackendServerEvents(logger, config, telegram));
        }
        TelegramEvents telegramEvents = new TelegramEvents(config, server);
        telegram.registerUpdatesListener(
                update -> {
                    logger.debug("Handling Telegram message: {}", update.toString());
                    telegramEvents.onMessage(update);
                    return null;
                });
    }

    private void unregisterEvents() {
        server.getEventManager().unregisterListeners(this);
        telegram.stop();
    }

    public void load() {
        logger.info("Velocity Telegram Bridge is loading...");
        this.config = new ConfigWrapper(logger, dataDirectory);
        this.config.load();
        logger.info("Loaded configuration file.");
        logger.info(config.getConfigObject().toString());
        registerCommands(this.config);
        this.telegram = new TelegramAPI(logger, config.getToken(), config.getChatId());
        registerEvents();
    }

    public void unload() {
        logger.info("Velocity Telegram Bridge is unloading...");
        this.server.getEventManager().unregisterListeners(this);
        unregisterEvents();
        unregisterCommands();
    }
}
