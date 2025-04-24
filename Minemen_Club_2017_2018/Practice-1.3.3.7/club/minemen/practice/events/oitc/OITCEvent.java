// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.events.oitc;

import org.bukkit.entity.Player;
import java.util.function.Consumer;
import club.minemen.core.util.CustomLocation;
import java.util.List;
import java.util.HashMap;
import club.minemen.practice.events.EventCountdownTask;
import java.util.UUID;
import java.util.Map;
import club.minemen.practice.events.PracticeEvent;

public class OITCEvent extends PracticeEvent<OITCPlayer>
{
    private final Map<UUID, OITCPlayer> players;
    private final EventCountdownTask countdownTask;
    
    public OITCEvent() {
        super("OITC");
        this.players = new HashMap<UUID, OITCPlayer>();
        this.countdownTask = new OITCCountdownTask(this);
    }
    
    @Override
    public Map<UUID, OITCPlayer> getPlayers() {
        return this.players;
    }
    
    @Override
    public EventCountdownTask getCountdownTask() {
        return this.countdownTask;
    }
    
    @Override
    public List<CustomLocation> getSpawnLocations() {
        return null;
    }
    
    @Override
    public void onStart() {
    }
    
    @Override
    public Consumer<Player> onJoin() {
        return player -> {
            final OITCPlayer oitcPlayer = this.players.put(player.getUniqueId(), new OITCPlayer(player.getUniqueId(), this));
        };
    }
    
    @Override
    public Consumer<Player> onDeath() {
        return player -> {};
    }
}
