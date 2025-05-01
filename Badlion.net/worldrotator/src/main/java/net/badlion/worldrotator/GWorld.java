package net.badlion.worldrotator;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class GWorld {

	private boolean isLoaded = false;

	private String author;
	private String niceWorldName;
    private String internalName;

	private File file;
	private File directory;

	private World bukkitWorld;
	private YamlConfiguration yml;

	public GWorld(String internalName) {
        this.internalName = internalName;
		this.niceWorldName = this.internalName.replace("_", " ");
		this.directory = new File("worlds/" + this.internalName);
        this.file = new File("worlds/" + this.internalName + "/" + this.internalName + "_config.yml");

		if (this.file.exists()) {
			this.yml = YamlConfiguration.loadConfiguration(this.file);
			this.author = this.yml.getString("author", "N/A");
		} else {
			Bukkit.getLogger().info("World configuration file for " + internalName + " not found! Is this on purpose?");
		}
	}

	public String getAuthor() {
		return this.author;
	}

	public String getInternalName() {
        return this.internalName;
    }

    public String getNiceWorldName() {
		return this.niceWorldName;
	}

	public boolean isLoaded() {
		return this.isLoaded;
	}

	public void setLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;
	}

	public File getDirectory() {
		return this.directory;
	}

	public YamlConfiguration getYml() {
		return this.yml;
	}

    public void save() throws IOException {
        this.yml.save(this.file);
    }

	public World getBukkitWorld() {
		return this.bukkitWorld;
	}

	public void setBukkitWorld(World bukkitWorld) {
		this.bukkitWorld = bukkitWorld;
		this.bukkitWorld.setAutoSave(false);
	}

}
