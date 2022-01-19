package com.bungoh.claimer.files;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigCacheImpl implements ConfigCache {

    final ConcurrentHashMap<String, Config> configs = new ConcurrentHashMap<>();

    @Override
    public @NotNull
    Config getFile(@Nullable String directory, @NotNull String filename) {
        if (filename.startsWith("/")) throw new IllegalArgumentException("Invalid filename!");
        final StringBuilder sbFile = new StringBuilder(filename);
        if (!filename.endsWith(".yml")) {
            sbFile.append(".yml");
        }
        final StringBuilder sbDir = new StringBuilder();
        if (directory != null) {
            sbDir.append(directory);
            if (!directory.endsWith("/")) sbDir.append('/');
        }
        sbDir.append(sbFile);
        synchronized (configs) {
            final Config cached = configs.get(sbDir.toString());
            if (cached != null) {
                return cached;
            }
            final Config newInstance = new Config(directory, sbFile.toString());
            configs.put(sbDir.toString(), newInstance);
            return newInstance;
        }
    }

}
