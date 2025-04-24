// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.tournament;

import org.bukkit.entity.Player;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import club.minemen.practice.team.KillableTeam;

public class TournamentTeam extends KillableTeam
{
    private final Map<UUID, String> playerNames;
    
    public TournamentTeam(final UUID leader, final List<UUID> players) {
        super(leader, players);
        this.playerNames = new HashMap<UUID, String>();
        for (final UUID playerUUID : players) {
            this.playerNames.put(playerUUID, this.plugin.getServer().getPlayer(playerUUID).getName());
        }
    }
    
    public void broadcast(final String message) {
        this.alivePlayers().forEach(player -> player.sendMessage(message));
    }
    
    public String getPlayerName(final UUID playerUUID) {
        return this.playerNames.get(playerUUID);
    }
    
    public Map<UUID, String> getPlayerNames() {
        return this.playerNames;
    }
}
