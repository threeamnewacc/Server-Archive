package net.badlion.worldrotator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class WorldRotator extends JavaPlugin {

	private static WorldRotator plugin;
	
	private ArrayList<GWorld> worlds;
    private Map<String, GWorld> nameToGWorld = new HashMap<>();
	private Map<GWorld, GWorld> worldToNextWorldMap;
	private YamlConfiguration config;
	private ArrayList<String> worldsCopiedSuccessfully = new ArrayList<>();

	private boolean disable = false;
	
	public WorldRotator() {
		WorldRotator.plugin = this;
		this.saveDefaultConfig();

		this.worlds = new ArrayList<>();
		this.worldToNextWorldMap = new HashMap<>();
		this.config = YamlConfiguration.loadConfiguration(new File("plugins/WorldRotator/config.yml"));

		// Delete current worlds
        /*if (this.config.getBoolean("worldrotator.delete_on_boot")) {
            File f = new File("worlds/"); // Current plugin directory by default
            File[] directories = f.listFiles();
            if (directories != null) {
                ArrayList<File> files = new ArrayList<>(Arrays.asList(directories));
                for (File file : files) {
                    // Recursively delete any folders/files
                    deleteDirectory(file);
                }
            }
        }*/

        // Always delete .dat files
        File f = new File("worlds/"); // Current plugin directory by default
        File[] directories = f.listFiles();
        if (directories != null) {
            ArrayList<File> files = new ArrayList<>(Arrays.asList(directories));
            for (File file : files) {
                if (file.isDirectory()) {
                    File playerData = new File("worlds/" + file.getName() + "/playerdata/");
                    deleteDirectory(playerData);
					File statsData = new File("worlds/" + file.getName() + "/stats/");
					deleteDirectory(statsData);
                }
            }
        }

		// Force load our config b4 the worlds load
		List<String> worldsToLoad = (List<String>) this.config.getList("worldrotator.worlds_to_load");

		// Copy from some directory
		String pathToWorlds = this.config.getString("worldrotator.path_to_worlds");
		if (pathToWorlds.equals("default")) {
			this.disable = true;
			return;
		}

		for (String worldString : worldsToLoad) {
			Bukkit.getLogger().info(worldString);
		 	//try {
				//WorldRotator.copyFolder(new File(pathToWorlds + worldString), new File("worlds/" + worldString));
				this.worldsCopiedSuccessfully.add(worldString);
			//} catch (IOException e) {
			//	e.printStackTrace();
			//	System.out.printf("Error when loading world %s", worldString);
			//}
		}

		this.getLogger().info("Successfully loaded WorldRotator.");
	}
	
	@Override
	public void onEnable() {
		if (this.disable) {
			Bukkit.getLogger().info(ChatColor.RED + "You are not allowed to use the default config for worldrotator.");
			getServer().dispatchCommand(getServer().getConsoleSender(), "stop");
			return;
		}

		GWorld prev = null;
		for (String world : this.worldsCopiedSuccessfully) {
			GWorld gWorld = new GWorld(world);
			this.worlds.add(gWorld);
            this.nameToGWorld.put(world, gWorld);

			// "Linked List"
			if (prev != null) {
				this.worldToNextWorldMap.put(prev, gWorld);
			}

			prev = gWorld;
		}

		this.worldToNextWorldMap.put(prev, null); // "sentinel"
	}
	
	@Override
	public void onDisable() {
		this.getLogger().info("Disabling WorldRotator.");

		// Disable all world savings
		for (World world : this.getServer().getWorlds()) {
			world.setAutoSave(false);
		}
	}

	public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null) {
				return false;
			}

			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}

		return (path.delete());
	}

	public static void copyFolder(File src, File dest) throws IOException {

		if (src.isDirectory()){

			//if directory not exists, create it
			if(!dest.exists()){
				dest.mkdir();
				System.out.println("Directory copied from "
										   + src + "  to " + dest);
			}

			//list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				//construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				//recursive copy
				copyFolder(srcFile,destFile);
			}

		} else {
			//if file, then copy it
			//Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			//copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
			System.out.println("File copied from " + src + " to " + dest);
		}
	}

	public static WorldRotator getInstance() {
		return WorldRotator.plugin;
	}
	
	public ArrayList<GWorld> getWorlds() {
		return worlds;
	}

	public GWorld getGWorld(String name) {
		return this.nameToGWorld.get(name);
	}

	public GWorld getNextWorld(GWorld gWorld) {
		return this.worldToNextWorldMap.get(gWorld);
	}

	public void setWorlds(ArrayList<GWorld> worlds) {
		this.worlds = worlds;
	}

	public Map<GWorld, GWorld> getWorldToNextWorldMap() {
		return worldToNextWorldMap;
	}

	public void setWorldToNextWorldMap(Map<GWorld, GWorld> worldToNextWorldMap) {
		this.worldToNextWorldMap = worldToNextWorldMap;
	}

	public ArrayList<String> getWorldsCopiedSuccessfully() {
		return worldsCopiedSuccessfully;
	}

}