// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.events;

import java.beans.ConstructorProperties;
import java.util.function.Consumer;
import java.util.Map;
import java.util.UUID;
import club.minemen.practice.event.EventStartEvent;
import java.util.List;
import club.minemen.practice.player.PlayerData;
import club.minemen.core.util.finalutil.CC;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Collection;
import java.util.ArrayList;
import club.minemen.core.util.CustomLocation;
import club.minemen.practice.util.PlayerUtil;
import club.minemen.practice.player.PlayerState;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Set;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import club.minemen.practice.Practice;

public abstract class PracticeEvent<K extends EventPlayer>
{
    private final Practice plugin;
    private final String name;
    private Player host;
    private EventState state;
    
    public void startCountdown() {
        if (this.getCountdownTask().isEnded()) {
            this.getCountdownTask().setTimeUntilStart(this.getCountdownTask().getCountdownTime());
            this.getCountdownTask().setEnded(false);
        }
        else {
            this.getCountdownTask().runTaskTimer((Plugin)this.plugin, 20L, 20L);
        }
    }
    
    public void sendMessage(final String message) {
        this.getBukkitPlayers().forEach(player -> player.sendMessage(message));
    }
    
    public Set<Player> getBukkitPlayers() {
        return this.getPlayers().keySet().stream().filter(uuid -> this.plugin.getServer().getPlayer(uuid) != null).map((Function<? super Object, ?>)this.plugin.getServer()::getPlayer).collect((Collector<? super Object, ?, Set<Player>>)Collectors.toSet());
    }
    
    public void join(final Player player) {
        final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
        playerData.setPlayerState(PlayerState.EVENT);
        PlayerUtil.clearPlayer(player);
        if (this.onJoin() != null) {
            this.onJoin().accept(player);
        }
        if (this.getSpawnLocations().size() == 1) {
            player.teleport(this.getSpawnLocations().get(0).toBukkitLocation());
        }
        else {
            final List<CustomLocation> spawnLocations = new ArrayList<CustomLocation>(this.getSpawnLocations());
            player.teleport(spawnLocations.remove(ThreadLocalRandom.current().nextInt(spawnLocations.size())).toBukkitLocation());
        }
        this.getBukkitPlayers().forEach(other -> other.showPlayer(player));
        this.getBukkitPlayers().forEach(other -> player.showPlayer(other));
        player.sendMessage(CC.SECONDARY + "You are now playing " + CC.PRIMARY + this.name + CC.SECONDARY + ".");
    }
    
    public void leave(final Player player, final boolean disconnect) {
        this.getPlayers().remove(player.getUniqueId());
        if (!disconnect) {
            this.plugin.getPlayerManager().sendToSpawnAndReset(player);
            if (this.onDeath() != null) {
                this.onDeath().accept(player);
            }
        }
    }
    
    public void start() {
        new EventStartEvent(this).call();
        this.setState(EventState.STARTED);
        this.onStart();
    }
    
    public void end() {
        this.plugin.getEventManager().getEventWorld().getPlayers().forEach(player -> this.plugin.getPlayerManager().sendToSpawnAndReset(player));
        this.getPlayers().clear();
        this.setState(EventState.UNANNOUNCED);
    }
    
    public K getPlayer(final Player player) {
        return this.getPlayer(player.getUniqueId());
    }
    
    public K getPlayer(final UUID uuid) {
        return this.getPlayers().get(uuid);
    }
    
    public abstract Map<UUID, K> getPlayers();
    
    public abstract EventCountdownTask getCountdownTask();
    
    public abstract List<CustomLocation> getSpawnLocations();
    
    public abstract void onStart();
    
    public abstract Consumer<Player> onJoin();
    
    public abstract Consumer<Player> onDeath();
    
    public Practice getPlugin() {
        return this.plugin;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Player getHost() {
        return this.host;
    }
    
    public EventState getState() {
        return this.state;
    }
    
    public void setHost(final Player host) {
        this.host = host;
    }
    
    public void setState(final EventState state) {
        this.state = state;
    }
    
    @ConstructorProperties({ "name" })
    public PracticeEvent(final String name) {
        this.plugin = Practice.getInstance();
        this.state = EventState.UNANNOUNCED;
        this.name = name;
    }
}
