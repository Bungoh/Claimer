package com.bungoh.claimer.files;

import com.bungoh.claimer.text.TextColors;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;
import java.util.function.Function;

public class Config {

    public static final ConfigCache CACHE = new ConfigCacheImpl();
    private final File file;
    private final YamlConfiguration config;

    protected Config(@Nullable String directory, @NotNull String filename) {
        this(directory, filename, loadDefaultsFromJar(directory, filename));
    }

    protected Config(@Nullable String directory, @NotNull String filename, @Nullable FileConfiguration defaults) {
        final File pluginFolder = JavaPlugin.getProvidingPlugin(getClass()).getDataFolder();
        if (directory != null) {
            final File dir = new File(pluginFolder, directory);
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
            this.file = new File(dir, filename);
        } else {
            this.file = new File(pluginFolder, filename);
        }
        if (this.file.exists()) {
            this.config = YamlConfiguration.loadConfiguration(file);
        } else {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException("Unable to write out file.", e);
            }
            this.config = new YamlConfiguration();
        }
        if (defaults != null) {
            config.setDefaults(defaults);
        }
    }

    /**
     * Save the config to disk.
     *
     * @throws IllegalStateException if an IO error occurs
     */
    public void save() throws IllegalStateException {
        synchronized (config) {
            try {
                config.save(file);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to save config to " + file, e);
            }
        }
    }

    /**
     * Reload the config from disk.
     * <p>
     * Defaults are preserved.
     *
     * @throws IllegalStateException if an IO error occurs
     */
    public void reload() throws IllegalStateException {
        synchronized (config) {
            try {
                this.config.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                throw new IllegalStateException("Unable to reload " + file, e);
            }
        }
    }

    /**
     * Perform a modification-based operation.
     *
     * @param consumer the operation(s) to perform
     */
    public void modify(Consumer<FileConfiguration> consumer) {
        synchronized (config) {
            consumer.accept(config);
        }
    }

    /**
     * Perform an operation that indicates its success.
     *
     * @param operation the operation(s) to perform
     */
    public boolean update(Function<FileConfiguration, Boolean> operation) {
        return readValue(operation);
    }

    /**
     * Perform an operation which returns a value.
     *
     * @param function the operations to perform to obtain data
     * @param <R> the return type of the operation (may be inferred)
     * @return value returned by the function provided
     */
    public <R> R readValue(Function<FileConfiguration, R> function) {
        synchronized (config) {
            return function.apply(config);
        }
    }

    /**
     * Retrieve a String for a string path key.
     *
     * @param key path to getString from
     * @return {@link TextColors#translate}-processed string
     */
    public @NotNull String getMessage(@NotNull String key) {
        final String readValue = readValue(fc -> fc.getString(key));
        return TextColors.translate(readValue);
    }

    /**
     * Sanitizes path input and searches the jar for a matching resource.
     *
     * @param directory subdirectory of the jar
     * @param filename filename in the jar
     * @return a valid and supported FileConfiguration, if present
     */
    private static @Nullable FileConfiguration loadDefaultsFromJar(@Nullable String directory, @NotNull String filename) {
        if (directory != null && !directory.startsWith("/")) {
            // ignore absolute paths
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        if (directory != null) {
            sb.append(directory);
            if (!directory.endsWith("/")) {
                sb.append('/');
            }
        }
        sb.append(filename);
        if (!filename.endsWith(".yml")) {
            sb.append(".yml");
        }
        final InputStream resource = JavaPlugin.getProvidingPlugin(Config.class).getResource(sb.toString());
        if (resource == null) return null;
        return YamlConfiguration.loadConfiguration(new InputStreamReader(resource));
    }

}
