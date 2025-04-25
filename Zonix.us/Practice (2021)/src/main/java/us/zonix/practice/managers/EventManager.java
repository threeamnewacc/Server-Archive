package us.zonix.practice.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import us.zonix.practice.CustomLocation;
import us.zonix.practice.Practice;
import us.zonix.practice.events.EventState;
import us.zonix.practice.events.PracticeEvent;
import us.zonix.practice.events.lights.LightsEvent;
import us.zonix.practice.events.oitc.OITCEvent;
import us.zonix.practice.events.parkour.ParkourEvent;
import us.zonix.practice.events.redrover.RedroverEvent;
import us.zonix.practice.events.sumo.SumoEvent;
import us.zonix.practice.events.tnttag.TNTTagEvent;
import us.zonix.practice.events.waterdrop.WaterDropEvent;
import us.zonix.practice.events.woolmixup.WoolMixUpEvent;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;

public class EventManager {
    private final Map<Class<? extends PracticeEvent>, PracticeEvent> events = new HashMap<>();
    private final Practice plugin = Practice.getInstance();
    private HashMap<UUID, PracticeEvent> spectators;
    private long cooldown;
    private final World eventWorld;

    public EventManager() {
        Arrays.asList(
                SumoEvent.class,
                OITCEvent.class,
                ParkourEvent.class,
                RedroverEvent.class,
                WaterDropEvent.class,
                WoolMixUpEvent.class,
                LightsEvent.class,
                TNTTagEvent.class
            )
            .forEach(this::addEvent);
        boolean newWorld;
        if (this.plugin.getServer().getWorld("event") == null) {
            this.eventWorld = this.plugin.getServer().createWorld(new WorldCreator("event"));
            newWorld = true;
        } else {
            this.eventWorld = this.plugin.getServer().getWorld("event");
            newWorld = false;
        }

        this.spectators = new HashMap<>();
        this.cooldown = 0L;
        if (this.eventWorld != null) {
            if (newWorld) {
                this.plugin.getServer().getWorlds().add(this.eventWorld);
            }

            this.eventWorld.setTime(2000L);
            this.eventWorld.setGameRuleValue("doDaylightCycle", "false");
            this.eventWorld.setGameRuleValue("doMobSpawning", "false");
            this.eventWorld.setStorm(false);
            this.eventWorld.getEntities().stream().filter(entity -> !(entity instanceof Player)).forEach(Entity::remove);
        }
    }

    public PracticeEvent getByName(String name) {
        return this.events.values().stream().filter(event -> event.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase())).findFirst().orElse(null);
    }

    public void hostEvent(PracticeEvent event, Player host) {
        event.setState(EventState.WAITING);
        event.setHost(host);
        event.startCountdown();
    }

    private void addEvent(Class<? extends PracticeEvent> clazz) {
        PracticeEvent event = null;

        try {
            event = clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException var4) {
            var4.printStackTrace();
        }

        this.events.put(clazz, event);
    }

    public void addSpectatorRedrover(Player player, PlayerData playerData, RedroverEvent event) {
        this.addSpectator(player, playerData, event);
        if (event.getSpawnLocations().size() == 1) {
            player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
        } else {
            List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
            player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
        }

        for (Player eventPlayer : event.getBukkitPlayers()) {
            player.showPlayer(eventPlayer);
        }

        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public void addSpectatorSumo(Player player, PlayerData playerData, SumoEvent event) {
        this.addSpectator(player, playerData, event);
        if (event.getSpawnLocations().size() == 1) {
            player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
        } else {
            List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
            player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
        }

        for (Player eventPlayer : event.getBukkitPlayers()) {
            player.showPlayer(eventPlayer);
        }

        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public void addSpectatorOITC(Player player, PlayerData playerData, OITCEvent event) {
        this.addSpectator(player, playerData, event);
        if (event.getSpawnLocations().size() == 1) {
            player.teleport(event.getSpawnLocations().get(0).toBukkitLocation());
        } else {
            List<CustomLocation> spawnLocations = new ArrayList<>(event.getSpawnLocations());
            player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
        }

        for (Player eventPlayer : event.getBukkitPlayers()) {
            player.showPlayer(eventPlayer);
        }

        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public void addSpectatorParkour(Player player, PlayerData playerData, ParkourEvent event) {
        this.addSpectator(player, playerData, event);
        player.teleport(this.plugin.getSpawnManager().getParkourGameLocation().toBukkitLocation());

        for (Player eventPlayer : event.getBukkitPlayers()) {
            player.showPlayer(eventPlayer);
        }

        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public void addSpectatorWoolMixUp(Player player, PlayerData playerData, WoolMixUpEvent event) {
        this.addSpectator(player, playerData, event);
        player.teleport(this.plugin.getSpawnManager().getWoolCenter().toBukkitLocation());

        for (Player eventPlayer : event.getBukkitPlayers()) {
            player.showPlayer(eventPlayer);
        }

        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public void addSpectatorLights(Player player, PlayerData playerData, LightsEvent event) {
        this.addSpectator(player, playerData, event);
        player.teleport(this.plugin.getSpawnManager().getLightsStart().toBukkitLocation());

        for (Player eventPlayer : event.getBukkitPlayers()) {
            player.showPlayer(eventPlayer);
        }

        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    public void addSpectatorTntTag(Player player, PlayerData playerData, TNTTagEvent event) {
        this.addSpectator(player, playerData, event);
        player.teleport(this.plugin.getSpawnManager().getTntTagSpawn().toBukkitLocation());

        for (Player eventPlayer : event.getBukkitPlayers()) {
            player.showPlayer(eventPlayer);
        }

        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(true);
        player.setFlying(true);
    }

    private void addSpectator(Player player, PlayerData playerData, PracticeEvent event) {
        playerData.setPlayerState(PlayerState.SPECTATING);
        this.spectators.put(player.getUniqueId(), event);
        player.getInventory().setContents(this.plugin.getItemManager().getSpecItems());
        player.updateInventory();
        this.plugin.getServer().getOnlinePlayers().forEach(online -> {
            online.hidePlayer(player);
            player.hidePlayer(online);
        });
    }

    public void removeSpectator(Player player) {
        this.getSpectators().remove(player.getUniqueId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
    }

    public boolean isPlaying(Player player, PracticeEvent event) {
        return event.getPlayers().containsKey(player.getUniqueId());
    }

    public PracticeEvent getSpectatingEvent(UUID uuid) {
        return this.spectators.get(uuid);
    }

    public PracticeEvent getEventPlaying(Player player) {
        return this.events.values().stream().filter(event -> this.isPlaying(player, event)).findFirst().orElse(null);
    }

    public Map<Class<? extends PracticeEvent>, PracticeEvent> getEvents() {
        return this.events;
    }

    public Practice getPlugin() {
        return this.plugin;
    }

    public HashMap<UUID, PracticeEvent> getSpectators() {
        return this.spectators;
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public World getEventWorld() {
        return this.eventWorld;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }
}
