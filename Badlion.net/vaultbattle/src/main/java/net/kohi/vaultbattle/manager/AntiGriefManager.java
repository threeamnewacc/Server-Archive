package net.kohi.vaultbattle.manager;

import net.kohi.vaultbattle.VaultBattlePlugin;
import net.kohi.vaultbattle.type.SimpleLocation;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class AntiGriefManager {

    private final VaultBattlePlugin plugin;

    private Map<SimpleLocation, UUID> privateBlocks = new HashMap<>();

    public AntiGriefManager(VaultBattlePlugin plugin) {
        this.plugin = plugin;
    }


    public boolean set(SimpleLocation location, Player player) {
        if (privateBlocks.get(location) != null) {
            return false;
        } else {
            if (getPrivateBlocks(player).size() > 3) {
                return false;
            }
            privateBlocks.put(location, player.getUniqueId());
            return true;
        }
    }

    public void remove(SimpleLocation location) {
        privateBlocks.remove(location);
    }


    public List<SimpleLocation> getPrivateBlocks(Player player) {
        List<SimpleLocation> locations = privateBlocks.entrySet().stream().filter(entry -> entry.getValue().equals(player.getUniqueId())).map(Map.Entry::getKey).collect(Collectors.toList());
        return locations;
    }

    public Map<SimpleLocation, UUID> getPrivateBlocks() {
        return this.privateBlocks;
    }
}
