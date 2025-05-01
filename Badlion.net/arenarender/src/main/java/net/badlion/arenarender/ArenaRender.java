package net.badlion.arenarender;

import net.badlion.arenarender.util.EmptyChunkGenerator;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ArenaRender extends JavaPlugin {

	private static ArenaRender plugin;
	private World arenaWorld;
	public static JSONObject defaultRederConfig;
	public static File renderConfigs;

	public static ArenaRender getInstance() {
		return plugin;
	}

	public ArenaRender() {
		plugin = this;
	}

	public void onEnable() {
		WorldCreator worldCreater = new WorldCreator("arenaWorld");
		worldCreater.type(WorldType.FLAT);
		worldCreater.generator(EmptyChunkGenerator.getInstance());
		this.arenaWorld = plugin.getServer().createWorld(worldCreater);
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		renderConfigs = new File(getDataFolder(), "renderConfgs");
		if (!renderConfigs.exists()) {
			renderConfigs.mkdir();
		}
		File jsonFile = new File(getDataFolder(), "defaultRenderConfig.json");
		if (jsonFile.exists()) {
			try {
				defaultRederConfig = (JSONObject) JSONValue.parse(new String(Files.readAllBytes(Paths.get(jsonFile.getAbsolutePath(), new String[0]))));
			} catch (IOException e) {
				getLogger().info("Error reading " + jsonFile);
			}
		} else {
			getLogger().info("File missing for Default Render Config");
		}
		new ArenaManager();
		ArenaManager.initialize();
	}

	public World getArenaWorld() {
		return this.arenaWorld;
	}

}
