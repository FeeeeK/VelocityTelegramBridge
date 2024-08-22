package me.axd1x8a.velocitytelegrambridge.utils.events;

import java.util.Map;

import org.slf4j.Logger;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;

import cc.unilock.yeplib.api.event.YepAdvancementEvent;
import cc.unilock.yeplib.api.event.YepDeathEvent;
import me.axd1x8a.velocitytelegrambridge.utils.StringFormatter;
import me.axd1x8a.velocitytelegrambridge.utils.TelegramAPI;
import me.axd1x8a.velocitytelegrambridge.utils.config.ConfigWrapper;

public class BackendServerEvents {
    private final Logger logger;
    private final ConfigWrapper config;
    private final TelegramAPI telegram;

    private static final MinecraftChannelIdentifier YEP_ADVANCEMENT = MinecraftChannelIdentifier.create("yep",
            "advancement");
    private static final MinecraftChannelIdentifier YEP_DEATH = MinecraftChannelIdentifier.create("yep", "death");

    public BackendServerEvents(Logger logger, ConfigWrapper config, TelegramAPI telegram) {
        this.logger = logger;
        this.config = config;
        this.telegram = telegram;
    }

    @Subscribe
    public void onYepAdvancement(YepAdvancementEvent event) {
        if (!event.getType().equals(YEP_ADVANCEMENT)) {
            return;
        }
        logger.info("Handling YepAdvancementEvent: {}", event.toString());
        if (!config.getEvents().advancement_enabled) {
            return;
        }
        String advancementFormatting;
        switch (event.getAdvType()) {
            case CHALLENGE:
                advancementFormatting = StringFormatter.format(
                        config.getEvents().advancement_format,
                        Map.of(
                                "advancement_title", "<b>{advancement_title}</b>"));
                break;
            case GOAL:
                advancementFormatting = StringFormatter.format(
                        config.getEvents().advancement_format,
                        Map.of(
                                "advancement_title", "<i><u>{advancement_title}</u></i>"));
                break;
            default:
                advancementFormatting = StringFormatter.format(
                        config.getEvents().advancement_format,
                        Map.of(
                                "advancement_title", "<u>{advancement_title}</u>"));
                break;
        }
        telegram.sendMessage(StringFormatter.format(
                advancementFormatting,
                Map.of(
                        "player", event.getUsername(),
                        "advancement_title", event.getTitle(),
                        "advancement_description", event.getDescription())));
    }

    @Subscribe
    public void onYepDeath(YepDeathEvent event) {
        if (!event.getType().equals(YEP_DEATH)) {
            return;
        }
        logger.info("Handling YepDeathEvent: {}", event.toString());
        if (!config.getEvents().death_enabled) {
            return;
        }
        telegram.sendMessage(StringFormatter.format(
                config.getEvents().death_format,
                Map.of(
                        "player", event.getUsername(),
                        "death_message", event.getMessage())));
    }
}
