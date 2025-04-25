package us.zonix.practice.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import us.zonix.practice.CustomLocation;
import us.zonix.practice.Practice;
import us.zonix.practice.arena.Arena;
import us.zonix.practice.arena.StandaloneArena;
import us.zonix.practice.file.ConfigFile;
import us.zonix.practice.kit.Kit;
import us.zonix.practice.util.ItemUtil;
import us.zonix.practice.util.inventory.InventoryUI;

public class ArenaManager {
    private final Practice plugin = Practice.getInstance();
    private final ConfigFile arenasFile;
    private final Map<String, Arena> arenas = new HashMap<>();
    private final Map<StandaloneArena, UUID> arenaMatchUUIDs = new HashMap<>();
    private int generatingArenaRunnables;

    public ArenaManager() {
        this.arenasFile = new ConfigFile(this.plugin, "arenas");
        this.loadArenas();
    }

    private void loadArenas() {
        FileConfiguration fileConfig = this.arenasFile.getConfiguration();
        ConfigurationSection arenaSection = fileConfig.getConfigurationSection("arenas");
        if (arenaSection != null) {
            arenaSection.getKeys(false).forEach(name -> {
                String a = arenaSection.getString(name + ".a");
                String b = arenaSection.getString(name + ".b");
                String min = arenaSection.getString(name + ".min");
                String max = arenaSection.getString(name + ".max");
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
                        standaloneArenas.add(new StandaloneArena(locSaA, locSaB, locSaMin, locSaMax));
                    });
                }

                boolean enabled = arenaSection.getBoolean(name + ".enabled", false);
                Arena arena = new Arena(name, standaloneArenas, new ArrayList<>(standaloneArenas), locA, locB, locMin, locMax, enabled);
                this.arenas.put(name, arena);
            });
        }
    }

    public void saveArenas() {
        FileConfiguration fileConfig = this.arenasFile.getConfiguration();
        fileConfig.set("arenas", null);
        this.arenas.forEach((arenaName, arena) -> {
            String a = CustomLocation.locationToString(arena.getA());
            String b = CustomLocation.locationToString(arena.getB());
            String min = CustomLocation.locationToString(arena.getMin());
            String max = CustomLocation.locationToString(arena.getMax());
            String arenaRoot = "arenas." + arenaName;
            fileConfig.set(arenaRoot + ".a", a);
            fileConfig.set(arenaRoot + ".b", b);
            fileConfig.set(arenaRoot + ".min", min);
            fileConfig.set(arenaRoot + ".max", max);
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
        this.arenasFile.save();
    }

    public void reloadArenas() {
        this.saveArenas();
        this.arenas.clear();
        this.loadArenas();
    }

    public void openArenaSystemUI(Player player) {
        if (this.arenas.size() == 0) {
            player.sendMessage(ChatColor.RED + "There's no arenas.");
        } else {
            InventoryUI inventory = new InventoryUI("Arena System", true, 6);

            for (final Arena arena : this.arenas.values()) {
                ItemStack item = ItemUtil.createItem(
                    Material.PAPER,
                    ChatColor.YELLOW
                        + arena.getName()
                        + ChatColor.GRAY
                        + " ("
                        + (arena.isEnabled() ? ChatColor.GREEN.toString() + ChatColor.BOLD + "ENABLED" : ChatColor.RED.toString() + ChatColor.BOLD + "DISABLED")
                        + ChatColor.GRAY
                        + ")"
                );
                ItemUtil.reloreItem(
                    item,
                    ChatColor.GRAY
                        + "Arenas: "
                        + ChatColor.GREEN
                        + (arena.getStandaloneArenas().size() == 0 ? "Single Arena (Invisible Players)" : arena.getStandaloneArenas().size() + " Arenas"),
                    ChatColor.GRAY
                        + "Standalone Arenas: "
                        + ChatColor.GREEN
                        + (arena.getAvailableArenas().size() == 0 ? "None" : arena.getAvailableArenas().size() + " Arenas Available"),
                    "",
                    ChatColor.YELLOW.toString() + ChatColor.BOLD + "LEFT CLICK " + ChatColor.GRAY + "Teleport to Arena",
                    ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT CLICK " + ChatColor.GRAY + "Generate Standalone Arenas"
                );
                inventory.addItem(new InventoryUI.AbstractClickableItem(item) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        Player player = (Player)event.getWhoClicked();
                        if (event.getClick() == ClickType.LEFT) {
                            player.teleport(arena.getA().toBukkitLocation());
                        } else {
                            InventoryUI generateInventory = new InventoryUI("Generate Arenas", true, 1);
                            int[] batches = new int[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150};

                            for (final int batch : batches) {
                                ItemStack item = ItemUtil.createItem(Material.PAPER, ChatColor.RED.toString() + ChatColor.BOLD + batch + " ARENAS");
                                generateInventory.addItem(new InventoryUI.AbstractClickableItem(item) {
                                    @Override
                                    public void onClick(InventoryClickEvent event) {
                                        Player player = (Player)event.getWhoClicked();
                                        player.performCommand("arena generate " + arena.getName() + " " + batch);
                                        player.sendMessage(ChatColor.GREEN + "Generating " + batch + " arenas, please check console for progress.");
                                        player.closeInventory();
                                    }
                                });
                            }

                            player.openInventory(generateInventory.getCurrentPage());
                        }
                    }
                });
            }

            player.openInventory(inventory.getCurrentPage());
        }
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

    public Arena getRandomArena(Kit kit) {
        List<Arena> enabledArenas = new ArrayList<>();

        for (Arena arena : this.arenas.values()) {
            if (arena.isEnabled()
                && !kit.getExcludedArenas().contains(arena.getName())
                && (kit.getArenaWhiteList().size() <= 0 || kit.getArenaWhiteList().contains(arena.getName()))) {
                enabledArenas.add(arena);
            }
        }

        return enabledArenas.size() == 0 ? null : enabledArenas.get(ThreadLocalRandom.current().nextInt(enabledArenas.size()));
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

    public Map<String, Arena> getArenas() {
        return this.arenas;
    }

    public Map<StandaloneArena, UUID> getArenaMatchUUIDs() {
        return this.arenaMatchUUIDs;
    }

    public int getGeneratingArenaRunnables() {
        return this.generatingArenaRunnables;
    }

    public void setGeneratingArenaRunnables(int generatingArenaRunnables) {
        this.generatingArenaRunnables = generatingArenaRunnables;
    }
}
