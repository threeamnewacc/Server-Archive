package net.kohi.vaultbattle;

import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.kohi.vaultbattle.type.Phase;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.UUID;

public class MCPTask extends BukkitRunnable {

    private static final UUID uuid = UUID.randomUUID();
    private final VaultBattlePlugin plugin;
    private final String mcpType;

    public MCPTask(VaultBattlePlugin plugin) {
        this.plugin = plugin;
        if (Gberry.serverName.startsWith("navb")) {
            mcpType = "navaultbattle";
        } else if (Gberry.serverName.startsWith("euvb")) {
            mcpType = "euvaultbattle";
        } else {
            mcpType = "devvaultbattle";
        }
    }

    @Override
    public void run() {
        JSONObject payload = new JSONObject();
        payload.put("server_name", Gberry.serverName);
        payload.put("uuid", uuid.toString());
        payload.put("type", mcpType);
        payload.put("teamsize", 1); // TODO: make mcp not require this teamsize miniuhc thing
        payload.put("player_count", Bukkit.getOnlinePlayers().size());

        // if the walls have not yet dropped, send pre_start and in_countdown
        if (plugin.getGameManager().getPhase() == Phase.STARTED && !plugin.getGameManager().isWallsDropped()) {
            payload.put("state", Phase.PRE_START.name());
            payload.put("in_countdown", true);
        } else {
            payload.put("state", plugin.getGameManager().getPhase().name());
            payload.put("in_countdown", false);
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Gberry.contactMCP("lobby-keep-alive", payload);
            } catch (HTTPRequestFailException ex) {
                ex.printStackTrace();
                plugin.getLogger().warning(ex.getResponseCode() + ": " + ex.getResponse());
            }
        });
    }
}
