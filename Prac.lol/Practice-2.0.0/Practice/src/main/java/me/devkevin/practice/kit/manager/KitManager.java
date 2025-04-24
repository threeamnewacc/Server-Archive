package me.devkevin.practice.kit.manager;

import lombok.Getter;
import me.devkevin.practice.Practice;
import me.devkevin.practice.file.Config;
import me.devkevin.practice.kit.Kit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Copyright 10/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class KitManager {

    private final Practice plugin = Practice.getInstance();
    private final Map<String, Kit> kits = new HashMap<>();
    private final List<Kit> rankedKits = new ArrayList<>();
    private final List<Kit> allKits = new ArrayList<>();
    private final Config config = new Config("ladders", this.plugin);

    public KitManager() {
        this.loadKits();
        this.kits.entrySet().stream()
                .filter(kit -> kit.getValue().isEnabled())
                .filter(kit -> kit.getValue().isRanked())
                .forEach(kit -> this.rankedKits.add(kit.getValue()));
        this.kits.forEach((key, value) -> this.allKits.add(value));
    }

    @SuppressWarnings("unchecked")
    private void loadKits() {
        FileConfiguration fileConfig = this.config.getConfig();
        ConfigurationSection kitSection = fileConfig.getConfigurationSection("ladders");

        if (kitSection != null) {
            kitSection.getKeys(false).forEach((name) -> {

                ItemStack[] contents = ((List<ItemStack>) kitSection.get(name + ".contents")).toArray(new ItemStack[0]);
                ItemStack[] armor = ((List<ItemStack>) kitSection.get(name + ".armor")).toArray(new ItemStack[0]);
                ItemStack[] kitEditContents = ((List<ItemStack>) kitSection.get(name + ".kitEditContents")).toArray(new ItemStack[0]);

                List<String> arenaWhiteList = kitSection.getStringList(name + ".arenaWhitelist");

                ItemStack icon = (ItemStack) kitSection.get(name + ".icon");

                boolean enabled = kitSection.getBoolean(name + ".enabled");
                boolean ranked = kitSection.getBoolean(name + ".ranked");
                boolean sumo = kitSection.getBoolean(name + ".sumo");
                boolean build = kitSection.getBoolean(name + ".build");
                boolean hcf = kitSection.getBoolean(name + ".hcf");
                boolean parkour = kitSection.getBoolean(name + ".parkour");
                boolean spleef = kitSection.getBoolean(name + ".spleef");
                boolean bedWars = kitSection.getBoolean(name + ".bedWars");
                boolean combo = kitSection.getBoolean(name + ".combo");
                boolean boxing = kitSection.getBoolean(name + ".boxing");

                Kit kit = new Kit(name, contents, armor, kitEditContents, icon, arenaWhiteList, enabled, ranked, sumo, build, hcf, parkour, spleef, bedWars, combo, boxing);
                this.kits.put(name, kit);
            });
        }
    }

    public void reloadKits() {
        this.saveKits();
        this.kits.clear();
        this.loadKits();
    }

    public void saveKits() {
        FileConfiguration fileConfig = this.config.getConfig();

        fileConfig.set("ladders", null);

        this.kits.forEach((kitName, kit) -> {
            if (kit.getIcon() != null && kit.getContents() != null && kit.getArmor() != null) {
                fileConfig.set("ladders." + kitName + ".contents", kit.getContents());
                fileConfig.set("ladders." + kitName + ".armor", kit.getArmor());
                fileConfig.set("ladders." + kitName + ".kitEditContents", kit.getKitEditContents());
                fileConfig.set("ladders." + kitName + ".icon", kit.getIcon());
                fileConfig.set("ladders." + kitName + ".arenaWhitelist", kit.getArenaWhiteList());
                fileConfig.set("ladders." + kitName + ".enabled", kit.isEnabled());
                fileConfig.set("ladders." + kitName + ".ranked", kit.isRanked());
                fileConfig.set("ladders." + kitName + ".sumo", kit.isSumo());
                fileConfig.set("ladders." + kitName + ".build", kit.isBuild());
                fileConfig.set("ladders." + kitName + ".hcf", kit.isHcf());
                fileConfig.set("ladders." + kitName + ".parkour", kit.isParkour());
                fileConfig.set("ladders." + kitName + ".spleef", kit.isSpleef());
                fileConfig.set("ladders." + kitName + ".bedWars", kit.isBedWars());
                fileConfig.set("ladders." + kitName + ".combo", kit.isCombo());
                fileConfig.set("ladders." + kitName + ".boxing", kit.isBoxing());
            }
        });


        this.config.save();
    }

    public void deleteKit(String name) {
        this.kits.remove(name);
    }

    public void createKit(String name) {
        this.kits.put(name, new Kit(name));
    }

    public Collection<Kit> getKits() {
        return this.kits.values();
    }

    public Kit getKit(String name) {
        return this.kits.get(name);
    }
}
