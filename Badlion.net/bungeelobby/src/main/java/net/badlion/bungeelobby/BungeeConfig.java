package net.badlion.bungeelobby;

import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * BungeeCord configuration utility class.
 *
 * @author rylinaux
 */
public class BungeeConfig {

    /**
     * Configuration provider we want for this config
     */
    private static final ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);

    /**
     * The plugin instance
     */
    private final Plugin plugin;

    /**
     * The file we're working with
     */
    private final File file;

    /**
     * The config
     */
    private Configuration config;

    /**
     * Construct our object (using the default config name).
     *
     * @param plugin the plugin instance
     */
    public BungeeConfig(Plugin plugin) {
        this(plugin, "config.yml");
    }

    /**
     * Construct our object (using a custom config name).
     *
     * @param plugin the plugin instance
     * @param name   the name of the config file
     */
    public BungeeConfig(Plugin plugin, String name) {
        this(plugin, new File(plugin.getDataFolder(), name));
    }

    /**
     * Construct our object (using a custom file, use for sub-folders).
     *
     * @param plugin the plugin instance
     * @param file   the config file
     */
    public BungeeConfig(Plugin plugin, File file) {
        this.plugin = plugin;
        this.file = file;
        load();
    }

    /**
     * Load our config, copying defaults if the file doesn't exist.
     */
    public void load() {

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {

            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, String.format("Could not create config file '%s'.", file.getName()), e);
            }

            try (InputStream in = plugin.getResourceAsStream(file.getName()); OutputStream out = new FileOutputStream(file)) {
                ByteStreams.copy(in, out);
            } catch (FileNotFoundException e) {
                plugin.getLogger().log(Level.SEVERE, String.format("Config file '%s' not found.", file.getName()), e);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, String.format("Could not copy defaults to file '%s'.", file.getName()), e);
            }

        }

        try {
            config = provider.load(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Could not load config '%s'.", file.getName()), e);
        }

    }

    /**
     * Saves the configuration.
     */
    public void save() {
        try {
            provider.save(config, file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Could not save config '%s'.", file.getName()), e);
        }
    }

    /**
     * Returns the backing YAML configuration implementation.
     *
     * @return the backing YAML configuration
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * Returns the file associated with the config.
     *
     * @return the config's file
     */
    public File getFile() {
        return file;
    }

}