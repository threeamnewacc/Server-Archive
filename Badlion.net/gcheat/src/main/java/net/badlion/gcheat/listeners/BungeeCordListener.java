package net.badlion.gcheat.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.badlion.gcheat.GCheat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.json.simple.parser.JSONParser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BungeeCordListener implements PluginMessageListener, Listener {

    private final JSONParser parser = new JSONParser();
    public static final Map<UUID, Map<String, String>> forgeMods = new HashMap<>();

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] data) {
        ByteArrayDataInput input = ByteStreams.newDataInput(data);
        if ("ForgeMods".equals(input.readUTF())) {
            String json = input.readUTF();
            try {
                Map<String, String> mods = (Map<String, String>) parser.parse(json);
                forgeMods.put(player.getUniqueId(), mods);
                String client = getClientType(player);
                if (client != null) {
                    GCheat.plugin.logMessage(player, player.getName() + " logged in with " + client);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        forgeMods.remove(event.getPlayer().getUniqueId());
    }

    public static String getClientType(Player player) {
        Map<String, String> mods = forgeMods.get(player.getUniqueId());
        if (mods != null) {
            if (mods.containsKey("gc")) {
                return "Hacked Client Type A";
            } else if (mods.containsKey("ethylene")) {
                return "Hacked Client Type B";
            } else if ("1.0".equals(mods.get("OpenComputers"))) {
                return "Hacked Client Type C";
            } else if ("1.7.6.git".equals(mods.get("Schematica"))) {
                return "Hacked Client Type D";
            }
        }
        return null;
    }
}
