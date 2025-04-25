package us.zonix.practice.file;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {
    private final FileConfiguration config;
    private final File configFile;
    protected boolean wasCreated;

    public Config(String name, JavaPlugin plugin) {
        this.configFile = new File(plugin.getDataFolder() + "/" + name + ".yml");
        if (!this.configFile.exists()) {
            try {
                this.configFile.getParentFile().mkdirs();
                this.configFile.createNewFile();
                this.wasCreated = true;
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public void save() {
        try {
            this.config.save(this.configFile);
        } catch (IOException var2) {
            var2.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public File getConfigFile() {
        return this.configFile;
    }

    public boolean isWasCreated() {
        return this.wasCreated;
    }
}
