// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.manager;

import org.bukkit.entity.Player;
import java.net.InetAddress;
import club.mineman.core.util.ttl.TtlArrayList;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.List;
import club.mineman.core.mineman.Mineman;
import java.util.UUID;
import java.util.Map;

public class MinemanManager
{
    private final Map<UUID, Mineman> players;
    private final List<UUID> commandCoolingDown;
    private final List<UUID> chatCoolingDown;
    private boolean chatSilenced;
    
    public MinemanManager() {
        this.players = new HashMap<UUID, Mineman>();
        this.commandCoolingDown = new TtlArrayList<UUID>(TimeUnit.SECONDS, 1L);
        this.chatCoolingDown = new TtlArrayList<UUID>(TimeUnit.SECONDS, 3L);
    }
    
    public Mineman addPlayer(final UUID uuid, final String name, final InetAddress ipAddress) {
        final Mineman mineman = new Mineman(uuid, name, ipAddress);
        this.players.put(uuid, mineman);
        return mineman;
    }
    
    public Mineman getPlayer(final UUID uuid) {
        return this.players.get(uuid);
    }
    
    public boolean isCommandCoolingDown(final Player player) {
        return this.commandCoolingDown.contains(player.getUniqueId());
    }
    
    public boolean isChatCoolingDown(final Player player) {
        return this.chatCoolingDown.contains(player.getUniqueId());
    }
    
    public void addCommandCoolingDown(final Player player) {
        this.commandCoolingDown.add(player.getUniqueId());
    }
    
    public void addChatCoolingDown(final Player player) {
        this.chatCoolingDown.add(player.getUniqueId());
    }
    
    public void removePlayer(final UUID player) {
        this.players.remove(player);
    }
    
    public Map<UUID, Mineman> getPlayers() {
        return this.players;
    }
    
    public List<UUID> getCommandCoolingDown() {
        return this.commandCoolingDown;
    }
    
    public List<UUID> getChatCoolingDown() {
        return this.chatCoolingDown;
    }
    
    public boolean isChatSilenced() {
        return this.chatSilenced;
    }
    
    public void setChatSilenced(final boolean chatSilenced) {
        this.chatSilenced = chatSilenced;
    }
}
