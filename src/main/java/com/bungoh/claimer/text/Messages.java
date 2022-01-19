package com.bungoh.claimer.text;

import com.bungoh.claimer.files.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Messages implements Message {
    UNKNOWN_ERROR("unknown_error");

    private static Config config;
    public final String key;

    Messages(String key) {
        this.key = key;
    }

    public static void setup() {
        config = Config.CACHE.getFile("messages.yml");
    }

    @Override
    public @Nullable String get() {
        return config.readValue(fc -> fc.getString(key));
    }

    @Override
    public @NotNull String toString() {
        return String.valueOf(get());
    }
}
