package me.axd1x8a.velocitytelegrambridge.utils;

import java.util.function.Function;

import org.slf4j.Logger;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public class TelegramAPI {

    Logger logger;

    TelegramBot bot;
    long chatId;

    public TelegramAPI(Logger logger, String token, long chatId) {
        this.logger = logger;

        this.bot = new TelegramBot(token);
        this.chatId = chatId;

    }

    public void sendMessage(String message) {
        bot.execute(new SendMessage(chatId, message));
    }

    public void registerUpdatesListener(Function<Update, Void> callback) {
        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                if (update.message() != null) {
                    callback.apply(update);
                }
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            if (e.response() != null) {
                logger.error("Telegram API error({}): {}", e.response().errorCode(), e.response().description());
            } else {
                // probably network error
                e.printStackTrace();
            }
        });
    }

}
