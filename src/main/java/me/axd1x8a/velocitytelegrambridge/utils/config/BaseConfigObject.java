package me.axd1x8a.velocitytelegrambridge.utils.config;

import com.electronwill.nightconfig.core.serde.annotations.SerdeComment;

public final class BaseConfigObject {
    @SerdeComment("Telegram bot api configuration")
    public TelegramConfigObject Telegram = new TelegramConfigObject();
    @SerdeComment("Events configuration")
    public EventConfigObject Events = new EventConfigObject();

    public static class TelegramConfigObject {
        @SerdeComment("Telegram bot api token")
        public String token = "0:ABCDEFG";

        @SerdeComment("Telegram chat id to send messages to")
        public Long chat_id = 0L;
    }

    public static class EventConfigObject {
        @SerdeComment("Should messages from chat be sent to the chat?")
        public Boolean message_enabled = true;

        @SerdeComment("Format of the message")
        @SerdeComment("Available placeholders: {player}, {message}")
        public String message_format = "<b>{player}</b>: {message}";

        @SerdeComment("Should messages from telegram be sent to the server?")
        public Boolean message_from_telegram_enabled = true;

        @SerdeComment("Format of the message")
        @SerdeComment("Available placeholders: {author}, {message}")
        public String message_from_telegram_format = "{author}: {message}";

        @SerdeComment("Should join messages be sent to the chat?")
        public Boolean join_enabled = true;

        @SerdeComment("Format of the message")
        @SerdeComment("Available placeholders: {player}")
        public String join_format = "<b>{player} joined the server</b>";

        @SerdeComment("Should leave messages be sent to the chat?")
        public Boolean leave_enabled = true;

        @SerdeComment("Format of the message")
        @SerdeComment("Available placeholders: {player}")
        public String leave_format = "<b>{player} left the server</b>";

        @SerdeComment("Should server change messages be sent to the chat?")
        public Boolean server_change_enabled = true;

        @SerdeComment("Format of the message")
        @SerdeComment("Available placeholders: {server}, {new_server}, {player}")
        public String server_change_format = "<b>{player} moved from {server} to {new_server}</b>";

        @SerdeComment("Should status messages be sent to the chat?")
        public Boolean status_enabled = true;

        @SerdeComment("Format of the message")
        @SerdeComment("Available placeholders: {player_count}, {players}")
        public String status_format = "There are <b>{player_count}</b> players online:\n{players}";

        @SerdeComment("Should death messages be sent to the chat?")
        @SerdeComment("Note: This requires YepLib installed on velocity and YepTwo on backend servers")
        public Boolean death_enabled = true;

        @SerdeComment("Format of the message")
        @SerdeComment("Available placeholders: {death_message}")
        public String death_format = "<b>{death_message}</b>";

        @SerdeComment("Should advancement messages be sent to the chat?")
        public Boolean advancement_enabled = true;

        @SerdeComment("Format of the message")
        @SerdeComment("Available placeholders: {player}, {advancement_title}, and {advancement_description}")
        public String advancement_format = "<b>{player}</b> has made the advancement {advancement_title}\n<i>{advancement_description}</i>";
    }
}
