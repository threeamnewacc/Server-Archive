package us.zonix.practice.events;

import com.boydti.fawe.util.EditSessionBuilder;
import com.boydti.fawe.util.TaskManager;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.zonix.practice.CustomLocation;
import us.zonix.practice.Practice;
import us.zonix.practice.event.EventStartEvent;
import us.zonix.practice.events.lights.LightsEvent;
import us.zonix.practice.events.oitc.OITCEvent;
import us.zonix.practice.events.oitc.OITCPlayer;
import us.zonix.practice.events.parkour.ParkourEvent;
import us.zonix.practice.events.redrover.RedroverEvent;
import us.zonix.practice.events.redrover.RedroverPlayer;
import us.zonix.practice.events.sumo.SumoEvent;
import us.zonix.practice.events.sumo.SumoPlayer;
import us.zonix.practice.events.tnttag.TNTTagEvent;
import us.zonix.practice.events.waterdrop.WaterDropEvent;
import us.zonix.practice.events.woolmixup.WoolMixUpEvent;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;
import us.zonix.practice.util.PlayerUtil;

public abstract class PracticeEvent<K extends EventPlayer> {
    private final Practice plugin = Practice.getInstance();
    private final String name;
    private final ItemStack item;
    private final boolean enabled;
    private int limit = 30;
    private Player host;
    private EventState state = EventState.UNANNOUNCED;

    public void startCountdown() {
        if (this.getCountdownTask().isEnded()) {
            this.getCountdownTask().setTimeUntilStart(this.getCountdownTask().getCountdownTime());
            this.getCountdownTask().setEnded(false);
        } else {
            this.getCountdownTask().runTaskTimerAsynchronously(this.plugin, 20L, 20L);
        }
    }

    public void sendMessage(String... messages) {
        for (String message : messages) {
            this.getBukkitPlayers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', message)));
        }
    }

    public Set<Player> getBukkitPlayers() {
        return this.getPlayers()
            .keySet()
            .stream()
            .filter(uuid -> this.plugin.getServer().getPlayer(uuid) != null)
            .<Player>map(this.plugin.getServer()::getPlayer)
            .collect(Collectors.toSet());
    }

    public void join(Player player) {
        if (this.getPlayers().size() < this.limit || player.hasPermission("practice.bypass.join")) {
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            playerData.setPlayerState(PlayerState.EVENT);
            PlayerUtil.clearPlayer(player);
            if (this.onJoin() != null) {
                this.onJoin().accept(player);
            }

            if (this.getSpawnLocations().size() == 1) {
                player.teleport(this.getSpawnLocations().get(0).toBukkitLocation());
            } else {
                List<CustomLocation> spawnLocations = new ArrayList<>(this.getSpawnLocations());
                player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
            }

            this.plugin.getPlayerManager().giveLobbyItems(player);

            for (Player other : this.getBukkitPlayers()) {
                other.showPlayer(player);
                player.showPlayer(other);
            }

            this.sendMessage("&c" + player.getName() + " &ehas joined the event. &7(" + this.getPlayers().size() + "/" + this.getLimit() + ")");
        }
    }

    public void leave(Player player) {
        if (this instanceof OITCEvent) {
            OITCEvent oitcEvent = (OITCEvent)this;
            OITCPlayer oitcPlayer = oitcEvent.getPlayer(player);
            oitcPlayer.setState(OITCPlayer.OITCState.ELIMINATED);
        }

        if (this.onDeath() != null) {
            this.onDeath().accept(player);
        }

        this.getPlayers().remove(player.getUniqueId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
    }

    public void start() {
        new EventStartEvent(this).call();
        this.setState(EventState.STARTED);
        this.onStart();
        this.plugin.getEventManager().setCooldown(0L);
    }

    public void end() {
        this.plugin
            .getServer()
            .getScheduler()
            .runTaskLater(
                this.plugin,
                () -> this.plugin
                        .getPlayerManager()
                        .getPlayersByState(PlayerState.EVENT)
                        .forEach(player -> this.plugin.getPlayerManager().sendToSpawnAndReset(player)),
                2L
            );
        this.plugin.getEventManager().setCooldown(System.currentTimeMillis() + 300000L);
        if (this instanceof SumoEvent) {
            SumoEvent sumoEvent = (SumoEvent)this;

            for (SumoPlayer sumoPlayer : sumoEvent.getPlayers().values()) {
                if (sumoPlayer.getFightTask() != null) {
                    sumoPlayer.getFightTask().cancel();
                }
            }
        } else if (this instanceof OITCEvent) {
            OITCEvent oitcEvent = (OITCEvent)this;
            if (oitcEvent.getGameTask() != null) {
                oitcEvent.getGameTask().cancel();
            }
        } else if (this instanceof RedroverEvent) {
            RedroverEvent redroverEvent = (RedroverEvent)this;

            for (RedroverPlayer redroverPlayer : redroverEvent.getPlayers().values()) {
                if (redroverPlayer.getFightTask() != null) {
                    redroverPlayer.getFightTask().cancel();
                }
            }

            if (redroverEvent.getGameTask() != null) {
                redroverEvent.getGameTask().cancel();
            }
        } else if (this instanceof ParkourEvent) {
            ParkourEvent parkourEvent = (ParkourEvent)this;
            if (parkourEvent.getGameTask() != null) {
                parkourEvent.getGameTask().cancel();
            }
        } else if (this instanceof WaterDropEvent) {
            WaterDropEvent waterDropEvent = (WaterDropEvent)this;
            if (waterDropEvent.getGameTask() != null) {
                waterDropEvent.getGameTask().cancel();
            }

            if (waterDropEvent.getWaterCheckTask() != null) {
                waterDropEvent.getWaterCheckTask().cancel();
            }

            TaskManager.IMP
                .async(
                    () -> {
                        if (waterDropEvent.getCuboid() != null) {
                            EditSession editSession = new EditSessionBuilder(waterDropEvent.getCuboid().getWorld().getName())
                                .fastmode(true)
                                .allowedRegionsEverywhere()
                                .autoQueue(false)
                                .limitUnlimited()
                                .build();

                            for (Block entry : waterDropEvent.getCuboid()) {
                                try {
                                    editSession.setBlock(
                                        new Vector((double)entry.getLocation().getBlockX(), (double)entry.getLocation().getBlockY(), entry.getLocation().getZ()),
                                        new BaseBlock(35, 0)
                                    );
                                } catch (Exception var5x) {
                                }
                            }
                        }
                    }
                );
        } else if (this instanceof WoolMixUpEvent) {
            WoolMixUpEvent woolMixUpEvent = (WoolMixUpEvent)this;
            Bukkit.getScheduler().cancelTask(woolMixUpEvent.getTaskId());
            woolMixUpEvent.setTaskId(-1);
            woolMixUpEvent.setCurrentColor(-1);
            Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> woolMixUpEvent.regenerateArena(woolMixUpEvent.getBlocksRegen()), 20L);
        } else if (this instanceof LightsEvent) {
            LightsEvent lightsEvent = (LightsEvent)this;
            Bukkit.getScheduler().cancelTask(lightsEvent.getTaskId());
            lightsEvent.setTaskId(-1);
        } else if (this instanceof TNTTagEvent) {
            TNTTagEvent tagEvent = (TNTTagEvent)this;
            if (tagEvent.getTask() != null) {
                tagEvent.getTask().cancel();
            }

            tagEvent.setRound(0);
            tagEvent.setTntTagState(TNTTagEvent.TNTTagState.NOT_STARTED);
        }

        this.getPlayers().clear();
        this.setState(EventState.UNANNOUNCED);
        Iterator<UUID> iterator = this.plugin.getEventManager().getSpectators().keySet().iterator();

        while (iterator.hasNext()) {
            UUID spectatorUUID = iterator.next();
            Player spectator = Bukkit.getPlayer(spectatorUUID);
            if (spectator != null) {
                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.plugin.getPlayerManager().sendToSpawnAndReset(spectator));
                iterator.remove();
            }
        }

        this.plugin.getEventManager().getSpectators().clear();
        this.getCountdownTask().setEnded(true);
    }

    public List<String> getScoreboardLines(Player player) {
        return Lists.newArrayList();
    }

    public List<String> getScoreboardSpectator(Player player) {
        return this.getScoreboardLines(player);
    }

    public K getPlayer(Player player) {
        return this.getPlayer(player.getUniqueId());
    }

    public K getPlayer(UUID uuid) {
        return this.getPlayers().get(uuid);
    }

    public abstract Map<UUID, K> getPlayers();

    public abstract EventCountdownTask getCountdownTask();

    public abstract List<CustomLocation> getSpawnLocations();

    public abstract void onStart();

    public abstract Consumer<Player> onJoin();

    public abstract Consumer<Player> onDeath();

    public PracticeEvent(String name, ItemStack item, boolean enabled) {
        this.name = name;
        this.item = item;
        this.enabled = enabled;
    }

    public Practice getPlugin() {
        return this.plugin;
    }

    public String getName() {
        return this.name;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getLimit() {
        return this.limit;
    }

    public Player getHost() {
        return this.host;
    }

    public EventState getState() {
        return this.state;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setHost(Player host) {
        this.host = host;
    }

    public void setState(EventState state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PracticeEvent)) {
            return false;
        } else {
            PracticeEvent<?> other = (PracticeEvent<?>)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$plugin = this.getPlugin();
                Object other$plugin = other.getPlugin();
                if (this$plugin == null ? other$plugin == null : this$plugin.equals(other$plugin)) {
                    Object this$name = this.getName();
                    Object other$name = other.getName();
                    if (this$name == null ? other$name == null : this$name.equals(other$name)) {
                        Object this$item = this.getItem();
                        Object other$item = other.getItem();
                        if (this$item == null ? other$item == null : this$item.equals(other$item)) {
                            if (this.isEnabled() != other.isEnabled()) {
                                return false;
                            } else if (this.getLimit() != other.getLimit()) {
                                return false;
                            } else {
                                Object this$host = this.getHost();
                                Object other$host = other.getHost();
                                if (this$host == null ? other$host == null : this$host.equals(other$host)) {
                                    Object this$state = this.getState();
                                    Object other$state = other.getState();
                                    return this$state == null ? other$state == null : this$state.equals(other$state);
                                } else {
                                    return false;
                                }
                            }
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof PracticeEvent;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $plugin = this.getPlugin();
        result = result * 59 + ($plugin == null ? 43 : $plugin.hashCode());
        Object $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        Object $item = this.getItem();
        result = result * 59 + ($item == null ? 43 : $item.hashCode());
        result = result * 59 + (this.isEnabled() ? 79 : 97);
        result = result * 59 + this.getLimit();
        Object $host = this.getHost();
        result = result * 59 + ($host == null ? 43 : $host.hashCode());
        Object $state = this.getState();
        return result * 59 + ($state == null ? 43 : $state.hashCode());
    }

    @Override
    public String toString() {
        return "PracticeEvent(plugin="
            + this.getPlugin()
            + ", name="
            + this.getName()
            + ", item="
            + this.getItem()
            + ", enabled="
            + this.isEnabled()
            + ", limit="
            + this.getLimit()
            + ", host="
            + this.getHost()
            + ", state="
            + this.getState()
            + ")";
    }
}
