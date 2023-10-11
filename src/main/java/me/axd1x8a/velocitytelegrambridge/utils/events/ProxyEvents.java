package me.axd1x8a.velocitytelegrambridge.utils.events;

import java.util.Optional;

import org.slf4j.Logger;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.axd1x8a.velocitytelegrambridge.utils.TelegramAPI;
import me.axd1x8a.velocitytelegrambridge.utils.config.ConfigWrapper;

public class ProxyEvents {

    private final Logger logger;

    private final ConfigWrapper config;
    private final TelegramAPI telegram;

    public ProxyEvents(Logger logger, ConfigWrapper config, TelegramAPI telegram) {
        this.logger = logger;

        this.config = config;
        this.telegram = telegram;
    }

    @Subscribe(order = PostOrder.LATE)
    private void onDisconnect(DisconnectEvent event) {
        logger.debug("Handling DisconnectEvent");
        if (!config.isLeaveEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        Optional<ServerConnection> server = player.getCurrentServer();
        if (server == null) {
            return;
        }
        String serverName = server.get().getServerInfo().getName();
        String playerName = player.getUsername();
        String message = config.getLeaveFormat().replace("{player}", playerName).replace("{server}", serverName);
        telegram.sendMessage(message);
    }

    @Subscribe(order = PostOrder.LATE)
    private void onConnect(ServerPostConnectEvent event) {
        logger.debug("Handling ServerPostConnectEvent");
        RegisteredServer previousServer = event.getPreviousServer();
        if (previousServer == null) {
            this.handleFirstJoin(event);
        } else {
            this.handleServerChange(event);
        }

    }

    private void handleFirstJoin(ServerPostConnectEvent event) {
        if (!config.isJoinEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        Optional<ServerConnection> server = player.getCurrentServer();
        if (server == null) {
            return;
        }
        String serverName = server.get().getServerInfo().getName();
        String playerName = player.getUsername();
        String message = config.getJoinFormat().replace("{player}", playerName).replace("{server}", serverName);
        telegram.sendMessage(message);
    }

    private void handleServerChange(ServerPostConnectEvent event) {
        if (!config.isServerChangeEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        Optional<ServerConnection> newServer = player.getCurrentServer();
        if (newServer.isEmpty()) {
            return;
        }
        String playerName = player.getUsername();
        String prevServerName = event.getPreviousServer().getServerInfo().getName();
        String newServerName = newServer.get().getServerInfo().getName();
        String message = config.getServerChangeFormat().replace("{player}", playerName)
                .replace("{server}", prevServerName).replace("{new_server}", newServerName);
        telegram.sendMessage(message);
    }

    @Subscribe(order = PostOrder.LATE)
    private void onMessage(PlayerChatEvent event) {
        logger.debug("Handling PlayerChatEvent");
        if (!config.isMessageEnabled()) {
            return;
        }
        Player player = event.getPlayer();
        Optional<ServerConnection> server = player.getCurrentServer();
        if (server == null) {
            return;
        }
        String serverName = server.get().getServerInfo().getName();
        String playerName = player.getUsername();
        String message = event.getMessage();
        String tgMessage = config.getMessageFormat().replace("{player}", playerName).replace("{server}", serverName)
                .replace("{message}", message);
        telegram.sendMessage(tgMessage);
    }
}
