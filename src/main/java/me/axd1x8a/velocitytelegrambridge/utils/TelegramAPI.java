package me.axd1x8a.velocitytelegrambridge.utils;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import org.slf4j.Logger;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

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
        bot.execute(new SendMessage(chatId, message), new Callback<SendMessage, SendResponse>() {
            @Override
            public void onResponse(SendMessage request, SendResponse response) {
                logger.debug("Telegram message sent: {}", message);
            }

            @Override
            public void onFailure(SendMessage request, IOException e) {
                logger.error("Telegram message failed to send");
            }
        });
    }

    public void registerUpdatesListener(Function<Update, Void> callback) {
        bot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(List<Update> updates) {
                updates.forEach(update -> {
                    if (update.message() != null) {
                        callback.apply(update);
                    }
                });
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        }, new ExceptionHandler() {
            @Override
            public void onException(TelegramException e) {
                if (e.response() != null) {
                    logger.error("Telegram API error({}): {}", e.response().errorCode(),
                            e.response().description());
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

}
