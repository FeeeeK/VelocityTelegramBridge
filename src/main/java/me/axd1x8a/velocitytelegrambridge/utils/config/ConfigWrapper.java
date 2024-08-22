package me.axd1x8a.velocitytelegrambridge.utils.config;

import java.nio.file.Path;

import org.slf4j.Logger;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.serde.ObjectDeserializer;
import com.electronwill.nightconfig.core.serde.ObjectSerializer;

public class ConfigWrapper {
    private final Logger logger;
    private CommentedFileConfig config;

    private Path dataDirectory;
    private BaseConfigObject configObject = new BaseConfigObject();
    private BaseConfigObject defaultConfigObject = new BaseConfigObject();
    private ObjectSerializer objectSerializer = ObjectSerializer.standard();
    private ObjectDeserializer objectDeserializer = ObjectDeserializer.standard();

    public ConfigWrapper(Logger logger, Path dataDirectory) {
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.config = CommentedFileConfig
                .builder(dataDirectory.resolve("config.toml"))
                .sync()
                .onFileNotFound(null)
                .preserveInsertionOrder()
                .build();
        this.objectSerializer.serializeFields(defaultConfigObject, config);
    }

    public CommentedFileConfig getConfig() {
        return config;
    }

    public BaseConfigObject getConfigObject() {
        return configObject;
    }

    public BaseConfigObject.EventConfigObject getEvents() {
        return configObject.Events;
    }

    public String getToken() {
        return configObject.Telegram.token;
    }

    public Long getChatId() {
        return configObject.Telegram.chat_id;
    }

    private void tryCreateDataDir() {
        if (!dataDirectory.toFile().exists()) {
            logger.info("Data directory does not exist, creating..., {}", dataDirectory.toString());

            if (!dataDirectory.toFile().mkdir()) {
                throw new IllegalStateException("Failed to create data directory");
            }
        }
    }

    public void load() {
        tryCreateDataDir();
        if (!config.getNioPath().toFile().exists()) {
            config.save();
            return;
        }
        config.load();
        objectDeserializer.deserializeFields(config, configObject);
    }

    public void save() {
        tryCreateDataDir();
        config.save();
    }

    public void reload() {
        load();
    }

    public void unload() {
        config.close();
    }

    public void reset() {
        config.getFile().delete();
        load();
    }
}
