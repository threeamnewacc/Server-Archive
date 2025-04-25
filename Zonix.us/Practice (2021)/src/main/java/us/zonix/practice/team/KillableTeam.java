package us.zonix.practice.team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.player.PlayerData;

public class KillableTeam {
    protected final Practice plugin = Practice.getInstance();
    private final List<UUID> players;
    private final List<UUID> alivePlayers = new ArrayList<>();
    private final String leaderName;
    private UUID leader;

    public KillableTeam(UUID leader, List<UUID> players) {
        this.leader = leader;
        this.leaderName = this.plugin.getServer().getPlayer(leader).getName();
        this.players = players;
        this.alivePlayers.addAll(players);
    }

    public void killPlayer(UUID playerUUID) {
        this.alivePlayers.remove(playerUUID);
    }

    public Stream<Player> alivePlayers() {
        return this.alivePlayers.stream().<Player>map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
    }

    public Stream<Player> players() {
        return this.players.stream().<Player>map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
    }

    public int onlinePlayers() {
        int count = 0;

        for (UUID uuid : this.players) {
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(uuid);
            if (playerData != null && !playerData.isLeaving()) {
                count++;
            }
        }

        return count;
    }

    public void revivePlayers() {
        this.alivePlayers.clear();
        this.alivePlayers.addAll(this.players);
    }

    public Practice getPlugin() {
        return this.plugin;
    }

    public List<UUID> getPlayers() {
        return this.players;
    }

    public List<UUID> getAlivePlayers() {
        return this.alivePlayers;
    }

    public String getLeaderName() {
        return this.leaderName;
    }

    public UUID getLeader() {
        return this.leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }
}
