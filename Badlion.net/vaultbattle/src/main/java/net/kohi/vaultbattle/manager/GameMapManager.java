package net.kohi.vaultbattle.manager;

import net.kohi.vaultbattle.VaultBattlePlugin;
import net.kohi.vaultbattle.type.GameMap;
import net.kohi.vaultbattle.util.EmptyChunkGenerator;
import net.kohi.vaultbattle.util.ExtensionFilter;
import net.kohi.vaultbattle.util.FileUtil;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class GameMapManager {

    private final VaultBattlePlugin plugin;

    private List<GameMap> maps = new ArrayList<>();

    private Map<GameMap, Integer> votes = new HashMap<>();

    private Map<UUID, GameMap> adminsEditing = new HashMap<>();

    private File mapFolder;
    private File mapWorldFolder;

    public GameMapManager(VaultBattlePlugin plugin) {
        this.plugin = plugin;
        this.mapFolder = new File(plugin.getDataFolder(), "maps");
        this.mapWorldFolder = new File(mapFolder, "worlds");
    }


    public boolean isEditing(Player player) {
        if (adminsEditing.containsKey(player.getUniqueId())) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEditingMap(Player player, GameMap map) {
        if (adminsEditing.containsKey(player.getUniqueId())) {
            GameMap gameMap = adminsEditing.get(player.getUniqueId());
            if (gameMap != null) {
                if (gameMap.equals(map)) {
                    return true;
                }
            }
        }
        return false;
    }

    public GameMap getMapEditing(Player player) {
        if (adminsEditing.containsKey(player.getUniqueId())) {
            GameMap map = adminsEditing.get(player.getUniqueId());
            if (map != null) {
                return map;
            }
        }
        return null;
    }

    public World loadWorld(GameMap map) {
        File world = new File(mapWorldFolder, map.getWorldName());
        File dest = new File("game_map_" + map.getWorldName());
        long start = System.currentTimeMillis();
        if (!world.exists()) {
            dest.mkdir();
        } else {
            try {
                FileUtil.copyRecursive(world, dest);
            } catch (IOException ex) {
                plugin.getLogger().log(Level.SEVERE, null, ex);
                return null;
            }
            plugin.getLogger().log(Level.INFO, "Copy: {0}ms", (System.currentTimeMillis() - start));
        }
        WorldCreator worldCreater = new WorldCreator(dest.getName());
        worldCreater.type(WorldType.FLAT);
        worldCreater.generator(EmptyChunkGenerator.getInstance());
        World gameMap = plugin.getServer().createWorld(worldCreater);

        plugin.getLogger().log(Level.INFO, "Load: {0}ms", (System.currentTimeMillis() - start));
        return gameMap;
    }

    public void purgeOldWorlds() {
        File dir = new File(".");
        for (File world : dir.listFiles()) {
            if (world.isDirectory() && world.getName().startsWith("game_map_")) {
                FileUtil.deleteRecursive(world);
                plugin.getLogger().log(Level.INFO, "Deleted old world: {0}", world);
            }
        }
    }

    public GameMap pickMap() {
        Map.Entry<GameMap, Integer> maxEntry = null;
        for (Map.Entry<GameMap, Integer> entry : votes.entrySet()) {
            if (maxEntry != null) {
                if (entry.getValue() > maxEntry.getValue()) {
                    maxEntry = entry;
                }
            } else {
                maxEntry = entry;
            }
        }
        if (maxEntry != null) {
            return maxEntry.getKey();
        } else {
            //No one voted, pick random map
            if (maps.isEmpty()) {
                return null;
            }
            if (maps.size() == 1) {
                return maps.get(0);
            }
            Random random = new Random();
            return maps.get(random.nextInt(maps.size()));
        }
    }

    public void vote(String mapVote) {
        for (GameMap map : maps) {
            if (mapVote.contains(map.getMapName())) {
                if (votes.get(map) != null) {
                    votes.put(map, votes.remove(map) + 1);
                } else {
                    votes.put(map, 1);
                }
            }
        }
    }

    public boolean containsMap(String mapName) {
        for (GameMap map : maps) {
            if (mapName.contains(map.getMapName())) {
                return true;
            }
        }
        return false;
    }

    public void load() {
        if (!mapFolder.exists()) {
            mapFolder.mkdir();
            mapWorldFolder.mkdir();
        }
        for (File file : mapFolder.listFiles(new ExtensionFilter(".json"))) {
            try (FileReader reader = new FileReader(file)) {
                GameMap map = VaultBattlePlugin.getGson().fromJson(reader, GameMap.class);
                maps.add(map);
            } catch (FileNotFoundException ex) {
                // ignore
            } catch (Exception ex) {
                plugin.getLogger().warning("Failed to load " + file.getName());
                ex.printStackTrace();
            }
        }
        //Add default template if there are no others
        if (maps.isEmpty()) {
            maps.add(new GameMap());
            save();
        }
    }

    public void save() {
        for (GameMap map : maps) {
            File file = new File(mapFolder, map.getMapName() + ".json");
            try (FileWriter writer = new FileWriter(file)) {
                VaultBattlePlugin.getGson().toJson(map, writer);
            } catch (IOException ex) {
                plugin.getLogger().warning("Failed to save " + file.getName());
                ex.printStackTrace();
            }
        }
    }

    public List<GameMap> getMaps() {
        return this.maps;
    }

    public Map<UUID, GameMap> getAdminsEditing() {
        return this.adminsEditing;
    }
}
