package me.devkevin.landcore.storage.flatfile;

import me.devkevin.landcore.utils.structure.Cuboid;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Config {
    private final File file;
    private YamlConfiguration delegate;

    public Config(JavaPlugin plugin, String name) {
        file = new File(plugin.getDataFolder() + "/" + name + ".yml");
        reload();
    }

    public void reload() {
        delegate = YamlConfiguration.loadConfiguration(file);
    }

    public void clear() {
        file.delete();

        reload();
        save();
    }

    public void save() {
        try {
            delegate.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDefault(String path, Object defaultValue) {
        delegate.addDefault(path, defaultValue);
    }

    public void addDefaults(Map<String, Object> defaults) {
        delegate.addDefaults(defaults);
    }

    public void copyDefaults() {
        delegate.options().copyDefaults(true);
        save();
    }

    public void set(String path, Object value) {
        delegate.set(path, value);
    }

    public boolean contains(String path) {
        return delegate.contains(path);
    }

    public int getInt(String path) {
        return delegate.getInt(path);
    }

    public boolean getBoolean(String path) {
        return delegate.getBoolean(path);
    }

    public String getString(String path) {
        return delegate.getString(path);
    }

    public List<String> getStringList(String path) {
        return delegate.getStringList(path);
    }

    public Location getLocation(String path) {
        return (Location) delegate.get(path);
    }

    public Cuboid getCuboid(String path) {
        return (Cuboid) delegate.get(path);
    }

    public ItemStack getItem(String path) {
        return delegate.getItemStack(path);
    }

    @SuppressWarnings("unchecked")
    public List<ItemStack> getItemList(String path) {
        return (List<ItemStack>) delegate.get(path);
    }

    public ItemStack[] getItemArray(String path) {
        List<ItemStack> itemList = getItemList(path);

        if (itemList == null) {
            return null;
        }

        return itemList.toArray(new ItemStack[0]);
    }

    public ConfigurationSection getSection(String path) {
        return delegate.getConfigurationSection(path);
    }

    public Set<String> getKeys() {
        return delegate.getKeys(false);
    }
}
