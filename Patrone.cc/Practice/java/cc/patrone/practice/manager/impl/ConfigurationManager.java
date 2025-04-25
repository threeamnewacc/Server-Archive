package cc.patrone.practice.manager.impl;

import cc.patrone.practice.manager.Manager;
import cc.patrone.practice.manager.ManagerHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConfigurationManager extends Manager {

    private Map<UUID, FileConfiguration> fileConfigurationMap;

    public ConfigurationManager(ManagerHandler managerHandler) {
        super(managerHandler);
        createDir();
        fileConfigurationMap = new HashMap<>();
    }

    private void createDir() {
        File file = new File(managerHandler.getPlugin().getDataFolder() + File.separator + "players");
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public void createFile(UUID uuid) {
        File uFile = new File(managerHandler.getPlugin().getDataFolder() + File.separator + "players" + File.separator + uuid.toString() + ".yml");
        try {
            if (!uFile.exists()) {
                uFile.createNewFile();
                System.out.println("Created " + uuid.toString() + ".yml");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Could not make " + uuid.toString() + ".yml");
        }
    }

    public boolean hasFile(UUID uuid) {
        File uFile = new File(managerHandler.getPlugin().getDataFolder() + File.separator + "players" + File.separator + uuid.toString() + ".yml");
        return uFile.exists();
    }

    public void loadFile(UUID uuid) {
        File uFile = new File(managerHandler.getPlugin().getDataFolder() + File.separator + "players" + File.separator + uuid.toString() + ".yml");
        fileConfigurationMap.put(uuid, YamlConfiguration.loadConfiguration(uFile));
        System.out.println("Loaded " + uuid.toString() + ".yml");
    }

    public FileConfiguration getFile(UUID uuid) {
        return fileConfigurationMap.get(uuid);
    }

    public void saveFile(UUID uuid) {
        try {
            File uFile = new File(managerHandler.getPlugin().getDataFolder() + File.separator + "players" + File.separator + uuid.toString() + ".yml");
            fileConfigurationMap.get(uuid).save(uFile);
            System.out.println("Saved " + uuid.toString() + ".yml");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error saving " + uuid.toString() + ".yml");
        }
    }
}
