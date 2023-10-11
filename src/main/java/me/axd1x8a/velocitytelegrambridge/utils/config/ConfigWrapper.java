package me.axd1x8a.velocitytelegrambridge.utils.config;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.moandjiezana.toml.Toml;

public final class ConfigWrapper {
    private Toml config;
    private File configFile;

    private String token;
    private long chatId;

    private String messageFormat;
    private String joinFormat;
    private String leaveFormat;
    private String serverChangeFormat;
    private String messageFromTGFormat;

    private boolean messageEnabled;
    private boolean joinEnabled;
    private boolean leaveEnabled;
    private boolean serverChangeEnabled;
    private boolean messageFromTGEnabled;

    ConfigWrapper(Toml config) {
        this.config = config;

        token = config.getString("Telegram.token", "");
        chatId = config.getLong("Telegram.chat_id", 0L);

        messageFormat = config.getString("Events.message_format", "");
        joinFormat = config.getString("Events.join_format", "");
        leaveFormat = config.getString("Events.leave_format", "");
        serverChangeFormat = config.getString("Events.server_change_format", "");

        messageEnabled = config.getBoolean("Events.message_enabled", true);
        joinEnabled = config.getBoolean("Events.join_enabled", true);
        leaveEnabled = config.getBoolean("Events.leave_enabled", true);
        serverChangeEnabled = config.getBoolean("Events.server_change_enabled", true);

        messageFromTGFormat = config.getString("Events.message_from_telegram_format", "");
        messageFromTGEnabled = config.getBoolean("Events.message_from_telegram_enabled", true);

    }

    public static ConfigWrapper load(Path dataDirectory) {
        Path configFolderPath = createConfigFolder(dataDirectory);
        Path configPath =  createConfig(configFolderPath);
        if (configPath != null) {
            File configFile = configPath.toFile();
            Toml config = new Toml().read(configFile);
            return new ConfigWrapper(config);
        }
        return null;
    }

    private static Path createConfig(Path dataDirectory) {
        try {
            Path file = dataDirectory.resolve("config.toml");
            if (Files.notExists(file)) {
                try (InputStream stream = ConfigWrapper.class.getResourceAsStream("/config.toml")) {
                    Files.copy(Objects.requireNonNull(stream), file);
                }
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Path createConfigFolder(Path dataDirectory) {
        try {
            if (Files.notExists(dataDirectory)) {
                Files.createDirectory(dataDirectory);
            }
            return dataDirectory;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    void reload() {
        config = config.read(configFile);
        this.token = config.getString("Telegram.token", "");
        this.chatId = config.getLong("Telegram.chat_id", 0L);

        this.messageFormat = config.getString("Events.message_format", "");
        this.joinFormat = config.getString("Events.join_format", "");
        this.leaveFormat = config.getString("Events.leave_format", "");
        this.serverChangeFormat = config.getString("Events.server_change_format", "");

        this.messageEnabled = config.getBoolean("Events.message_enabled", true);
        this.joinEnabled = config.getBoolean("Events.join_enabled", true);
        this.leaveEnabled = config.getBoolean("Events.leave_enabled", true);
        this.serverChangeEnabled = config.getBoolean("Events.server_change_enabled", true);

        this.messageFromTGFormat = config.getString("Events.message_from_telegram_format", "");
        this.messageFromTGEnabled = config.getBoolean("Events.message_from_telegram_enabled", true);
    }

    public String getToken() {
        return this.token;
    }

    public long getChatId() {
        return this.chatId;
    }

    public String getMessageFormat() {
        return this.messageFormat;
    }

    public String getJoinFormat() {
        return this.joinFormat;
    }

    public String getLeaveFormat() {
        return this.leaveFormat;
    }

    public String getServerChangeFormat() {
        return this.serverChangeFormat;
    }

    public String getMessageFromTGFormat() {
        return this.messageFromTGFormat;
    }

    public boolean isMessageEnabled() {
        return this.messageEnabled;
    }

    public boolean isJoinEnabled() {
        return this.joinEnabled;
    }

    public boolean isLeaveEnabled() {
        return this.leaveEnabled;
    }

    public boolean isServerChangeEnabled() {
        return this.serverChangeEnabled;
    }

    public boolean isMessageFromTGEnabled() {
        return this.messageFromTGEnabled;
    }
}
