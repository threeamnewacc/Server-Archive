package cc.patrone.practice.manager.impl;

import cc.patrone.practice.arena.Arena;
import cc.patrone.practice.manager.Manager;
import cc.patrone.practice.manager.ManagerHandler;
import cc.patrone.practice.util.LocationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArenaManager extends Manager {

    private List<Arena> arenaList;

    public ArenaManager(ManagerHandler managerHandler) {
        super(managerHandler);
        fetchArenas();
    }

    private void fetchArenas() {
        arenaList = new ArrayList<>();

        if (this.managerHandler.getPlugin().getConfig().getConfigurationSection("arenas") == null) {
            return;
        }
        this.managerHandler.getPlugin().getConfig().getConfigurationSection("arenas").getKeys(false).stream().forEach(arena -> {
            arenaList.add(new Arena(arena, LocationUtil.getLocationFromString(this.managerHandler.getPlugin().getConfig().getString("arenas." + arena + ".1")), LocationUtil.getLocationFromString(this.managerHandler.getPlugin().getConfig().getString("arenas." + arena + ".2")), this.managerHandler.getPlugin().getConfig().getBoolean("arenas." + arena + ".sumo")));
        });
    }

    public void addArena(Arena arena) {
        arenaList.add(arena);
    }

    public void removeArena(Arena arena) {
        arenaList.remove(arena);
    }

    public Arena getArena(String name) {
        return arenaList.stream().filter(a -> a.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Arena randomArena(boolean sumo) {
        List<Arena> possibleArenas = new ArrayList<>();
        arenaList.stream().filter(a -> a.isSumo() == sumo).allMatch(a -> possibleArenas.add(a));
        return possibleArenas.get(new Random().nextInt(possibleArenas.size()));
    }

    public void save() {
        this.managerHandler.getPlugin().getConfig().set("arenas", null);
        arenaList.stream().forEach(a -> {
            this.managerHandler.getPlugin().getConfig().set("arenas." + a.getName() + ".1", LocationUtil.getStringFromLocation(a.getSpawn1()));
            this.managerHandler.getPlugin().getConfig().set("arenas." + a.getName() + ".2", LocationUtil.getStringFromLocation(a.getSpawn2()));
            this.managerHandler.getPlugin().getConfig().set("arenas." + a.getName() + ".sumo", a.isSumo());
        });
        this.managerHandler.getPlugin().saveConfig();
    }
}
