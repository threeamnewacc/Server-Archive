package net.badlion.skywars.tasks;

import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.ministats.MiniStats;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.skywars.SkyPlayer;
import net.badlion.skywars.SkyWars;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KeepAliveTask extends BukkitRunnable {

    private static UUID uuid = UUID.randomUUID();

    public void run() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("server_name", Gberry.serverName.toUpperCase());

            if (MPG.getInstance().getServerState().ordinal() <= MPG.ServerState.LOBBY.ordinal()) {
                jsonObject.put("status", "want_a_game");
            } else {
                jsonObject.put("status", "match");
            }

            jsonObject.put("player_count", SkyWars.getInstance().getServer().getOnlinePlayers().size());
            final JSONObject response = HTTPCommon.executePUTRequest("http://127.0.0.1:9014/KeepAlive/" + KeepAliveTask.uuid.toString() + "/4jzyuUGb5AQUvVGLeUpx11ih4vGFF", jsonObject);
            if (response != null && response.containsKey("players")) {
                // Do this here to avoid any race conditions
                if (response.get("type").equals("ffa_op")) {
                    SkyWars.getInstance().getCurrentGame().setGamemode(SkyWars.overPoweredGamemode);
                    Gberry.coudhDBDatabase = "sw_op_ffa_beta";
	                MiniStats.TABLE_NAME = SkyWars.getInstance().getCurrentGame().getGamemode().getName().equals("Classic") ? "swcffa_ministats" : "swopffa_ministats";
                } else if (response.get("type").equals("ffa_classic")) {
                    SkyWars.getInstance().getCurrentGame().setGamemode(SkyWars.classicGamemode);
                    Gberry.coudhDBDatabase = "sw_classic_ffa_beta";
                    MiniStats.TABLE_NAME = SkyWars.getInstance().getCurrentGame().getGamemode().getName().equals("Classic") ? "swcffa_ministats" : "swopffa_ministats";
                }

                MPG.getInstance().setServerState(MPG.ServerState.GAME);

                final Map<String, List<String>> data = (Map<String, List<String>>) response.get("players");
                final List<String> usernames = data.get("usernames");
                final List<String> uuidStrings = data.get("uuids");

                // Contact ArchyPi and sendtoall
                JSONObject object = new JSONObject();
                List<String> cmds = new ArrayList<>();
                for (String username : usernames) {
                    cmds.add("send " + username + " " + Gberry.serverName);
                }
                object.put("commands", cmds);
                try {
                    HTTPCommon.executePUTRequest("http://158.69.52.59:9011/AddToCommandQueueServerBulk/8qPqqR324esK9hGrNkTzT3DUPp9UC9pC", object);
                } catch (HTTPRequestFailException e) {
                    Bukkit.getLogger().info("Failed to sendtoall");
                }

                new BukkitRunnable() {
                    public void run() {
                        MPG.getInstance().getMPGGame().setGameState(MPGGame.GameState.PRE_GAME);

                        for (int i = 0; i < usernames.size(); i++) {
                            new SkyPlayer(UUID.fromString(uuidStrings.get(i)), usernames.get(i));
                        }
                    }
                }.runTask(SkyWars.getInstance());
            }
        } catch (HTTPRequestFailException e) {
            Bukkit.getLogger().info("Failed to reach keep alive with error " + e.getResponseCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
