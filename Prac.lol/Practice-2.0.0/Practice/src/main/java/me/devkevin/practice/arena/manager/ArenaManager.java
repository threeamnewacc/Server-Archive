package me.devkevin.practice.arena.manager;

import lombok.Getter;
import lombok.Setter;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.arena.chunk.manager.ChunkRestorationManager;
import me.devkevin.practice.arena.standalone.StandaloneArena;
import me.devkevin.practice.file.Config;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.location.CustomLocation;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ArenaManager {
    private final Practice plugin = Practice.getInstance();

    private final Config config = new Config("arenas", this.plugin);

    @Getter private final Map<String, Arena> arenas = new HashMap<>();
    @Getter private final Map<StandaloneArena, UUID> arenaMatchUUIDs = new HashMap<>();

    @Getter @Setter private int generatingArenaRunnable;

    public ArenaManager() {
        this.loadArenas();
    }

    private void loadArenas() {
        FileConfiguration fileConfig = config.getConfig();
        ConfigurationSection arenaSection = fileConfig.getConfigurationSection("arenas");
        if (arenaSection == null) {
            return;
        }

        arenaSection.getKeys(false).forEach(name -> {
            String icon = arenaSection.getString(name + ".icon") == null ? Material.PAPER.name() : arenaSection.getString(name + ".icon");
            int iconData = arenaSection.getInt(name + ".icon-data");
            String a = arenaSection.getString(name + ".a");
            String b = arenaSection.getString(name + ".b");
            String min = arenaSection.getString(name + ".min");
            String max = arenaSection.getString(name + ".max");
            int buildMax = arenaSection.getInt(name + ".build-max");
            int deadZone = arenaSection.getInt(name + ".dead-zone");
            int portalProt = arenaSection.getInt(name + ".portalProt");

            CustomLocation locA = CustomLocation.stringToLocation(a);
            CustomLocation locB = CustomLocation.stringToLocation(b);
            CustomLocation locMin = CustomLocation.stringToLocation(min);
            CustomLocation locMax = CustomLocation.stringToLocation(max);

            List<StandaloneArena> standaloneArenas = new ArrayList<>();
            ConfigurationSection saSection = arenaSection.getConfigurationSection(name + ".standaloneArenas");
            if (saSection != null) {
                saSection.getKeys(false).forEach(id -> {
                    String saA = saSection.getString(id + ".a");
                    String saB = saSection.getString(id + ".b");
                    String saMin = saSection.getString(id + ".min");
                    String saMax = saSection.getString(id + ".max");

                    CustomLocation locSaA = CustomLocation.stringToLocation(saA);
                    CustomLocation locSaB = CustomLocation.stringToLocation(saB);
                    CustomLocation locSaMin = CustomLocation.stringToLocation(saMin);
                    CustomLocation locSaMax = CustomLocation.stringToLocation(saMax);

                    StandaloneArena standaloneArena = new StandaloneArena(locSaA, locSaB, locSaMin, locSaMax);
                    ChunkRestorationManager.getIChunkRestoration().copy(standaloneArena);

                    standaloneArenas.add(standaloneArena);
                });
            }

            boolean enabled = arenaSection.getBoolean(name + ".enabled", false);

            Arena arena = new Arena(name, standaloneArenas, new ArrayList<>(standaloneArenas), icon, iconData, locA, locB, locMin, locMax, buildMax, deadZone, portalProt, enabled, false, null);
            this.arenas.put(name, arena);
        });
    }

    public void reloadArenas() {
        this.saveArenas();
        this.arenas.clear();
        this.loadArenas();
    }

    public void saveArenas() {
        FileConfiguration fileConfig = this.config.getConfig();

        fileConfig.set("arenas", null);
        arenas.forEach((arenaName, arena) -> {
            String icon = arena.getIcon();
            int iconData = arena.getIconData();
            String a = CustomLocation.locationToString(arena.getA());
            String b = CustomLocation.locationToString(arena.getB());
            String min = CustomLocation.locationToString(arena.getMin());
            String max = CustomLocation.locationToString(arena.getMax());
            int buildMax = arena.getBuildMax();
            int deadZone = arena.getDeadZone();
            int portalProt = arena.getPortalProt();

            String arenaRoot = "arenas." + arenaName;

            fileConfig.set(arenaRoot + ".icon", icon);
            fileConfig.set(arenaRoot + ".icon-data", iconData);
            fileConfig.set(arenaRoot + ".a", a);
            fileConfig.set(arenaRoot + ".b", b);
            fileConfig.set(arenaRoot + ".min", min);
            fileConfig.set(arenaRoot + ".max", max);
            fileConfig.set(arenaRoot + ".build-max", buildMax);
            fileConfig.set(arenaRoot + ".dead-zone", deadZone);
            fileConfig.set(arenaRoot + ".portalProt", portalProt);
            fileConfig.set(arenaRoot + ".enabled", arena.isEnabled());
            fileConfig.set(arenaRoot + ".standaloneArenas", null);

            int i = 0;
            if (arena.getStandaloneArenas() != null) {
                for (StandaloneArena saArena : arena.getStandaloneArenas()) {
                    String saA = CustomLocation.locationToString(saArena.getA());
                    String saB = CustomLocation.locationToString(saArena.getB());
                    String saMin = CustomLocation.locationToString(saArena.getMin());
                    String saMax = CustomLocation.locationToString(saArena.getMax());

                    String standAloneRoot = arenaRoot + ".standaloneArenas." + i;

                    fileConfig.set(standAloneRoot + ".a", saA);
                    fileConfig.set(standAloneRoot + ".b", saB);
                    fileConfig.set(standAloneRoot + ".min", saMin);
                    fileConfig.set(standAloneRoot + ".max", saMax);

                    i++;
                }
            }
        });

        this.config.save();
    }

    public void createArena(String name) {
        this.arenas.put(name, new Arena(name));
    }

    public void deleteArena(String name) {
        this.arenas.remove(name);
    }

    public Arena getArena(String name) {
        return this.arenas.get(name);
    }

    // Recoded
    // Basicaly I added new method 'lastSelectedArena' and we get the info from Profile
    // with lastArenaPlayed so basically what we do here is a method that returns a
    // random arena from a list of available and enabled arenas.
    //
    // In this code, if there is only one arena left in the list after removing the ones
    // that are not enabled or not in the white list, it returns that arena. If there are
    // no arenas left, it returns the lastSelectedArena. If there are more than one arena left,
    // it uses a do-while loop to randomly select an arena and keeps track of how many times the
    // same arena as lastSelectedArena has been selected in a row using a counter count. The loop
    // continues to run as long as the selected arena is the same as lastSelectedArena and the counter
    // is less than 2. This ensures that the same arena is not selected more than two times in a row.
    public Arena getRandomArena(Kit kit, Arena lastSelectedArena) {
        List<Arena> arenas = new ArrayList<>(this.arenas.values());
        arenas.removeIf(arena -> !arena.isEnabled());
        arenas.removeIf(arena -> !kit.getArenaWhiteList().contains(arena.getName()));

        if (arenas.size() <= 1) {
            return arenas.size() == 1 ? arenas.get(0) : lastSelectedArena;
        }

        Arena selectedArena;
        int count = 0;
        do {
            selectedArena = arenas.get(ThreadLocalRandom.current().nextInt(arenas.size()));
            count++;
        } while (selectedArena == lastSelectedArena && count < 2);

        return selectedArena;
    }

    public void removeArenaMatchUUID(StandaloneArena arena) {
        this.arenaMatchUUIDs.remove(arena);
    }

    public UUID getArenaMatchUUID(StandaloneArena arena) {
        return this.arenaMatchUUIDs.get(arena);
    }

    public void setArenaMatchUUID(StandaloneArena arena, UUID matchUUID) {
        this.arenaMatchUUIDs.put(arena, matchUUID);
    }
}
