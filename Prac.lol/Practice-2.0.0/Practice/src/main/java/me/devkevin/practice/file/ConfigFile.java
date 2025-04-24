package me.devkevin.practice.file;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigFile {

    @Getter private File file;
    @Getter private YamlConfiguration configuration;

    public ConfigFile(JavaPlugin plugin, String name) {
        file = new File(plugin.getDataFolder(), name + ".yml");

        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }

        plugin.saveResource(name + ".yml", false);

        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public double getDouble(String path) {
        if (configuration.contains(path)) {
            return configuration.getDouble(path);
        }
        return 0;
    }

    public int getInt(String path) {
        if (configuration.contains(path)) {
            return configuration.getInt(path);
        }
        return 0;
    }

    public boolean getBoolean(String path) {
        if (configuration.contains(path)) {
            return configuration.getBoolean(path);
        }
        return false;
    }

    public String getString(String path) {
        if (configuration.contains(path)) {
            return ChatColor.translateAlternateColorCodes('&', configuration.getString(path));
        }
        return "ERROR: STRING NOT FOUND";
    }

    public String getString(String path, String callback, boolean colorize) {
        if (configuration.contains(path)) {
            if (colorize) {
                return ChatColor.translateAlternateColorCodes('&', configuration.getString(path));
            } else {
                return configuration.getString(path);
            }
        }
        return callback;
    }

    public List<String> getReversedStringList(String path) {
        List<String> list = getStringList(path);
        if (list != null) {
            int size = list.size();
            List<String> toReturn = new ArrayList<>();
            for (int i = size - 1; i >= 0; i--) {
                toReturn.add(list.get(i));
            }
            return toReturn;
        }
        return Arrays.asList("ERROR: STRING LIST NOT FOUND!");
    }

    public List<String> getStringList(String path) {
        if (configuration.contains(path)) {
            ArrayList<String> strings = new ArrayList<>();
            for (String string : configuration.getStringList(path)) {
                strings.add(ChatColor.translateAlternateColorCodes('&', string));
            }
            return strings;
        }
        return Arrays.asList("ERROR: STRING LIST NOT FOUND!");
    }

    public List<String> getStringListOrDefault(String path, List<String> toReturn) {
        if (configuration.contains(path)) {
            ArrayList<String> strings = new ArrayList<>();
            for (String string : configuration.getStringList(path)) {
                strings.add(ChatColor.translateAlternateColorCodes('&', string));
            }
            return strings;
        }
        return toReturn;
    }
}
