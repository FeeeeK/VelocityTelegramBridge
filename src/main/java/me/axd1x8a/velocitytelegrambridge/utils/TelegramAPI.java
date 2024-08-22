package me.axd1x8a.velocitytelegrambridge.utils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.slf4j.Logger;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.ChatFullInfo;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.PinChatMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.GetChatResponse;
import com.pengrad.telegrambot.response.SendResponse;

public class TelegramAPI {

    private final Logger logger;

    private final TelegramBot bot;
    private final Long chatId;
    private Long botId;

    private static final int MAX_REQUESTS_PER_SECOND = 30;
    private static final int MAX_REQUESTS_PER_MINUTE_PER_CHAT = 20;

    private final Semaphore secondSemaphore = new Semaphore(MAX_REQUESTS_PER_SECOND);
    private final Semaphore minuteSemaphore = new Semaphore(MAX_REQUESTS_PER_MINUTE_PER_CHAT);

    public TelegramAPI(Logger logger, String token, long chatId) {
        this.logger = logger;

        if (token.isEmpty()) {
            logger.warn("Telegram token is not set, edid config and use '/vtb reload' command to apply changes");
            this.botId = 0L;
        } else {
            this.botId = Long.parseLong(token.split(":")[0]);
        }
        this.bot = new TelegramBot(token);
        this.chatId = chatId;
        logger.info("Logged as {} and listening to chat {}", botId, chatId);
    }

    public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(T request, Callback<T, R> callback) {
        try {
            secondSemaphore.acquire();
            minuteSemaphore.acquire();

            bot.execute(request, new Callback<T, R>() {
                @Override
                public void onResponse(T request, R response) {
                    callback.onResponse(request, response);
                    releasePermits();
                }

                @Override
                public void onFailure(T request, IOException e) {
                    callback.onFailure(request, e);
                    releasePermits();
                }

                private void releasePermits() {
                    Executors.newScheduledThreadPool(1).schedule(() -> {
                        secondSemaphore.release();
                    }, 1, TimeUnit.SECONDS);
                    Executors.newScheduledThreadPool(1).schedule(() -> {
                        minuteSemaphore.release();
                    }, 1, TimeUnit.MINUTES);
                }
            });
        } catch (InterruptedException e) {
            throw new RuntimeException("Request was interrupted", e);
        }
    }

    public <T extends BaseRequest<T, R>, R extends BaseResponse> R execute(T request) {
        try {
            secondSemaphore.acquire();
            minuteSemaphore.acquire();

            R response = bot.execute(request);
            if (!response.isOk()) {
                logger.error(
                        "Telegram API error({}): {}, params: {}",
                        response.errorCode(),
                        response.description(),
                        request.getParameters());
            }

            Executors.newScheduledThreadPool(1).schedule(() -> {
                secondSemaphore.release();
            }, 1, TimeUnit.SECONDS);

            Executors.newScheduledThreadPool(1).schedule(() -> {
                minuteSemaphore.release();
            }, 1, TimeUnit.MINUTES);

            return response;
        } catch (InterruptedException e) {
            throw new RuntimeException("Request was interrupted", e);
        }
    }

    public void updatePinnedMessage(String message) {
        GetChatResponse chatResponse = execute(new GetChat(chatId));
        ChatFullInfo chat = chatResponse.chat();
        Message pinnedMessage = chat.pinnedMessage();

        if (pinnedMessage != null) {
            logger.debug("Found pinned message '{}' from {}", pinnedMessage.text(), pinnedMessage.from().id());
            if (pinnedMessage.from().id().equals(botId)) {
                if (pinnedMessage.text().equals(message)) {
                    return;
                }
                execute(new EditMessageText(
                        chatId,
                        pinnedMessage.messageId(),
                        message).parseMode(ParseMode.HTML));
                return;
            }
        }
        Optional<Message> newMessageResponse = sendMessage(message);
        if (newMessageResponse.isEmpty()) {
            return;
        }
        Message newMessage = newMessageResponse.get();
        try {
            execute(new PinChatMessage(chatId, newMessage.messageId()).disableNotification(true));
        } catch (CompletionException e) {
            logger.error("Failed to send message and get new message", e);
        }
    }

    public Optional<Message> sendMessage(String message) {
        SendResponse sendResponse = execute(new SendMessage(chatId, message).parseMode(ParseMode.HTML));
        if (!sendResponse.isOk()) {
            return Optional.empty();
        }
        return Optional.ofNullable(sendResponse.message());
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

    public void stop() {
        bot.removeGetUpdatesListener();
        bot.shutdown();
    }

}
