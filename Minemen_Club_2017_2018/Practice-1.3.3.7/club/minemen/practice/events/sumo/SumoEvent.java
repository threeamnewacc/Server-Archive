// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.events.sumo;

import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import java.beans.ConstructorProperties;
import java.util.Arrays;
import club.minemen.practice.util.PlayerUtil;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import club.minemen.practice.events.EventPlayer;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.scheduler.BukkitTask;
import club.minemen.core.util.finalutil.CC;
import org.bukkit.entity.Player;
import java.util.function.Consumer;
import java.util.Collections;
import club.minemen.core.util.CustomLocation;
import java.util.List;
import club.minemen.practice.events.EventCountdownTask;
import org.bukkit.plugin.Plugin;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;
import club.minemen.practice.events.PracticeEvent;

public class SumoEvent extends PracticeEvent<SumoPlayer>
{
    private final Map<UUID, SumoPlayer> players;
    private final SumoCountdownTask countdownTask;
    
    public SumoEvent() {
        super("Sumo");
        this.players = new HashMap<UUID, SumoPlayer>();
        this.countdownTask = new SumoCountdownTask(this);
        new WaterCheckTask().runTaskTimer((Plugin)this.getPlugin(), 0L, 20L);
    }
    
    @Override
    public Map<UUID, SumoPlayer> getPlayers() {
        return this.players;
    }
    
    @Override
    public EventCountdownTask getCountdownTask() {
        return this.countdownTask;
    }
    
    @Override
    public List<CustomLocation> getSpawnLocations() {
        return Collections.singletonList(new CustomLocation(this.getPlugin().getEventManager().getEventWorld().getName(), 18.5, 18.5, 0.5, 90.0f, 0.0f));
    }
    
    @Override
    public void onStart() {
        this.selectPlayers();
    }
    
    @Override
    public Consumer<Player> onJoin() {
        return player -> {
            final SumoPlayer sumoPlayer = this.players.put(player.getUniqueId(), new SumoPlayer(player.getUniqueId(), this));
        };
    }
    
    @Override
    public Consumer<Player> onDeath() {
        return player -> {
            final SumoPlayer data = this.getPlayer(player);
            if (data.getState() == SumoPlayer.SumoState.FIGHTING && data.getFighting() != null) {
                this.getPlayers().remove(player.getUniqueId());
                final SumoPlayer killerData = data.getFighting();
                final Player killer = this.getPlugin().getServer().getPlayer(killerData.getUuid());
                data.getFightTask().cancel();
                killerData.getFightTask().cancel();
                this.sendMessage(CC.GREEN + killer.getName() + " won against " + player.getName() + "!");
                if (this.getPlayers().size() == 1) {
                    final Player winner = this.getPlugin().getServer().getPlayer((UUID)this.getPlayers().keySet().toArray()[0]);
                    this.getPlugin().getServer().broadcastMessage(CC.B_GOLD + winner.getName() + " won Sumo!");
                    this.end();
                }
                else {
                    this.getPlugin().getServer().getScheduler().runTaskLater((Plugin)this.getPlugin(), () -> this.selectPlayers(), 60L);
                }
            }
        };
    }
    
    private CustomLocation[] getSumoLocations() {
        final CustomLocation[] array = { new CustomLocation(this.getPlugin().getEventManager().getEventWorld().getName(), 4.5, 18.0, 0.5, 90.0f, 0.0f), new CustomLocation(this.getPlugin().getEventManager().getEventWorld().getName(), -3.5, 18.0, 0.5, -90.0f, 0.0f) };
        return array;
    }
    
    private void selectPlayers() {
        if (this.getByState(SumoPlayer.SumoState.WAITING).size() < 2) {
            this.players.values().forEach(player -> player.setState(SumoPlayer.SumoState.WAITING));
        }
        final Player picked1 = this.getRandomPlayer();
        final Player picked2 = this.getRandomPlayer();
        final SumoPlayer picked1Data = this.getPlayer(picked1);
        final SumoPlayer picked2Data = this.getPlayer(picked2);
        picked1Data.setFighting(picked2Data);
        picked2Data.setFighting(picked1Data);
        picked1.teleport(this.getSumoLocations()[0].toBukkitLocation());
        picked2.teleport(this.getSumoLocations()[1].toBukkitLocation());
        this.sendMessage(CC.YELLOW + picked1.getName() + " VS " + picked2.getName());
        final BukkitTask task = new SumoFightTask(picked1, picked2).runTaskTimer((Plugin)this.getPlugin(), 0L, 20L);
        picked1Data.setFightTask(task);
        picked2Data.setFightTask(task);
    }
    
    private Player getRandomPlayer() {
        final List<UUID> waiting = this.getByState(SumoPlayer.SumoState.WAITING);
        Collections.shuffle(waiting);
        final UUID uuid = waiting.get(ThreadLocalRandom.current().nextInt(waiting.size()));
        final SumoPlayer data = this.getPlayer(uuid);
        data.setState(SumoPlayer.SumoState.FIGHTING);
        return this.getPlugin().getServer().getPlayer(uuid);
    }
    
    private List<UUID> getByState(final SumoPlayer.SumoState state) {
        return this.players.values().stream().filter(player -> player.getState() == state).map((Function<? super SumoPlayer, ?>)EventPlayer::getUuid).collect((Collector<? super Object, ?, List<UUID>>)Collectors.toList());
    }
    
    private class SumoFightTask extends BukkitRunnable
    {
        private final Player player;
        private final Player other;
        private int time;
        
        public void run() {
            if (this.player == null || this.other == null || !this.player.isOnline() || !this.other.isOnline()) {
                this.cancel();
                return;
            }
            if (this.time == 60) {
                PlayerUtil.sendMessage(CC.D_RED + "3...", this.player, this.other);
            }
            else if (this.time == 59) {
                PlayerUtil.sendMessage(CC.RED + "2...", this.player, this.other);
            }
            else if (this.time == 58) {
                PlayerUtil.sendMessage(CC.YELLOW + "1...", this.player, this.other);
            }
            else if (this.time == 57) {
                PlayerUtil.sendMessage(CC.GREEN + "Fight!", this.player, this.other);
            }
            else if (this.time <= 0) {
                final List<Player> players = Arrays.asList(this.player, this.other);
                final Player winner = players.get(ThreadLocalRandom.current().nextInt(players.size()));
                players.stream().filter(pl -> !pl.equals(winner)).forEach(pl -> SumoEvent.this.onDeath().accept(pl));
                this.cancel();
                return;
            }
            if (Arrays.asList(45, 30, 15, 10).contains(this.time)) {
                PlayerUtil.sendMessage(CC.GOLD + "Fight ends in " + this.time + " seconds.", this.player, this.other);
            }
            else if (Arrays.asList(5, 4, 3, 2, 1).contains(this.time)) {
                PlayerUtil.sendMessage(CC.GOLD + "A winner will be automatically selected in " + this.time + " seconds.", this.player, this.other);
            }
            --this.time;
        }
        
        @ConstructorProperties({ "player", "other" })
        public SumoFightTask(final Player player, final Player other) {
            this.time = 60;
            this.player = player;
            this.other = other;
        }
    }
    
    private class WaterCheckTask extends BukkitRunnable
    {
        public void run() {
            if (SumoEvent.this.getPlayers().size() <= 1) {
                return;
            }
            SumoEvent.this.getBukkitPlayers().forEach(player -> {
                if (SumoEvent.this.getPlayer(player).getState() == SumoPlayer.SumoState.FIGHTING) {
                    final Block legs = player.getLocation().getBlock();
                    final Block head = legs.getRelative(BlockFace.UP);
                    if (legs.getType() == Material.WATER || legs.getType() == Material.STATIONARY_WATER || head.getType() == Material.WATER || head.getType() == Material.STATIONARY_WATER) {
                        SumoEvent.this.onDeath().accept(player);
                    }
                }
            });
        }
        
        public WaterCheckTask() {
        }
    }
}
