package com.bungoh.claimer.files;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ConfigCache {

    /**
     * Get a configuration file management object.
     * <p>
     * If not present in the cache a new object will be created.
     *
     * @param filename the filename to search for
     * @return Config configuration management object
     */
    default @NotNull Config getFile(@NotNull String filename) {
        return getFile(null, filename);
    }

    /**
     * Get a configuration file management object.
     * <p>
     * If not present in the cache a new object will be created.
     *
     * @param directory the directory to search under
     * @param filename the filename to search for
     * @return Config configuration management object
     */
    @NotNull Config getFile(@Nullable String directory, @NotNull String filename);

}
