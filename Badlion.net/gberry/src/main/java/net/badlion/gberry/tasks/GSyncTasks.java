package net.badlion.gberry.tasks;

import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.GSyncEvent;
import net.badlion.gberry.managers.MCPManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class GSyncTasks extends BukkitRunnable {

    private boolean processing = false;

    @Override
    public void run() {
        // Still working
        if (processing) {
            return;
        }

        processing = true;

        try {
            JSONObject response = MCPManager.contactMCP(MCPManager.MCP_MESSAGE.SYNC_SERVER);


            if (response != null && response.containsKey("sync_queue")) {
                final List<String> syncMsgs = (List<String>) response.get("sync_queue");

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        for (String jsonString : syncMsgs) {
                            JSONParser parser = new JSONParser();
                            try {
                                JSONArray array = (JSONArray) parser.parse(jsonString);
                                // TODO: CONVERT TO NOT ONLY USE STRING
                                List<String> msg = new ArrayList<>();
                                for (Object o : array) {
                                    msg.add(o.toString());
                                }
                                Bukkit.getPluginManager().callEvent(new GSyncEvent(msg));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }.runTask(Gberry.plugin);

                MCPManager.contactMCP(MCPManager.MCP_MESSAGE.CLEAR_SYNC_SERVER, response);
            }
        } finally {
            processing = false; // Our "lock"
        }
    }

}
