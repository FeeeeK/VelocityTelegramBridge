package me.axd1x8a.velocitytelegrambridge.utils.events;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import me.axd1x8a.velocitytelegrambridge.utils.StringFormatter;
import me.axd1x8a.velocitytelegrambridge.utils.TelegramAPI;
import me.axd1x8a.velocitytelegrambridge.utils.config.ConfigWrapper;

public class ProxyEvents {

    private final Logger logger;

    private final ConfigWrapper config;
    private final TelegramAPI telegram;

    private Map<UUID, String> onlinePlayers = new HashMap<>();

    public ProxyEvents(Logger logger, ConfigWrapper config, TelegramAPI telegram) {
        this.logger = logger;

        this.config = config;
        this.telegram = telegram;
    }

    @Subscribe(order = PostOrder.LATE)
    private void onDisconnect(DisconnectEvent event) {
        logger.debug("Handling DisconnectEvent");
        if (!config.getEvents().leave_enabled) {
            return;
        }
        Player player = event.getPlayer();
        Optional<ServerConnection> server = player.getCurrentServer();
        if (!server.isPresent()) {
            return;
        }
        if (config.getEvents().status_enabled) {
            handleJoinLeaveStatus(
                    player,
                    server.get().getServerInfo().getName(),
                    player.getUsername(),
                    false);
        } else {
            String serverName = server.get().getServerInfo().getName();
            String playerName = player.getUsername();
            telegram.sendMessage(StringFormatter.format(
                    config.getEvents().leave_format,
                    Map.of(
                            "player", playerName,
                            "server", serverName)));
        }
    }

    @Subscribe(order = PostOrder.LATE)
    private void onConnect(ServerPostConnectEvent event) {
        logger.debug("Handling ServerPostConnectEvent");
        RegisteredServer previousServer = event.getPreviousServer();
        Player player = event.getPlayer();
        Optional<ServerConnection> server = player.getCurrentServer();

        if (!server.isPresent()) {
            return;
        }

        if (config.getEvents().status_enabled) {
            handleJoinLeaveStatus(
                    player,
                    server.get().getServerInfo().getName(),
                    player.getUsername(), true);
        } else if (previousServer == null) {
            handleFirstJoin(event, player, server.get());
        } else {
            handleServerChange(event, player, server.get());
        }
    }

    private void handleFirstJoin(ServerPostConnectEvent event, Player player, ServerConnection server) {
        if (!config.getEvents().join_enabled) {
            return;
        }
        String serverName = server.getServerInfo().getName();
        String playerName = player.getUsername();
        telegram.sendMessage(StringFormatter.format(
                config.getEvents().join_format,
                Map.of(
                        "player", playerName,
                        "server", serverName)));
    }

    private void handleServerChange(ServerPostConnectEvent event, Player player, ServerConnection newServer) {
        if (!config.getEvents().server_change_enabled) {
            return;
        }
        String playerName = player.getUsername();
        String prevServerName = event.getPreviousServer().getServerInfo().getName();
        String newServerName = newServer.getServerInfo().getName();
        telegram.sendMessage(StringFormatter.format(
                config.getEvents().server_change_format,
                Map.of(
                        "player", playerName,
                        "server", prevServerName,
                        "new_server", newServerName)));
    }

    private void handleJoinLeaveStatus(Player player, String serverName, String playerName, Boolean join) {
        if (!config.getEvents().status_enabled) {
            return;
        }
        UUID playerUUID = player.getUniqueId();
        Boolean exists = onlinePlayers.containsKey(playerUUID);
        if (join && exists) {
            return;
        } else if (!join && !exists) {
            return;
        } else if (!join) {
            onlinePlayers.remove(playerUUID);
        } else {
            onlinePlayers.put(playerUUID, playerName);
        }
        String onlinePlayersCount = String.valueOf(onlinePlayers.size());
        String players = String.join(", ", onlinePlayers.values());
        String lastEvent = StringFormatter.format(
                join ? config.getEvents().join_format : config.getEvents().leave_format,
                Map.of(
                        "player", playerName,
                        "server", serverName));
        telegram.updatePinnedMessage(StringFormatter.format(
                config.getEvents().status_format,
                Map.of(
                        "player_count", onlinePlayersCount,
                        "players", players,
                        "last_event", lastEvent)));
    }

    @Subscribe(order = PostOrder.LATE)
    private void onMessage(PlayerChatEvent event) {
        logger.debug("Handling PlayerChatEvent");
        if (!config.getEvents().message_enabled) {
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
        telegram.sendMessage(StringFormatter.format(
                config.getEvents().message_format,
                Map.of(
                        "player", playerName,
                        "server", serverName,
                        "message", message)));
    }
}
