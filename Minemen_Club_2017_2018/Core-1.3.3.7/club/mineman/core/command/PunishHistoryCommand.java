// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command;

import club.mineman.core.api.request.RequestCallback;
import java.util.Iterator;
import org.bukkit.plugin.Plugin;
import club.mineman.core.util.finalutil.HttpUtil;
import club.mineman.core.api.APIMessage;
import club.mineman.core.api.impl.DataFromIDRequest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import club.mineman.core.api.request.AbstractCallback;
import club.mineman.core.api.impl.PunishHistoryRequest;
import club.mineman.core.util.finalutil.CC;
import club.mineman.core.util.finalutil.PlayerUtil;
import club.mineman.core.rank.Rank;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Collections;
import club.mineman.core.CorePlugin;
import org.bukkit.command.Command;

public class PunishHistoryCommand extends Command
{
    private final CorePlugin plugin;
    
    public PunishHistoryCommand(final CorePlugin plugin) {
        super("history", "Get a player's punishment history.", "/hist <player>", (List)Collections.singletonList("hist"));
        this.plugin = plugin;
    }
    
    public boolean execute(final CommandSender sender, final String s, final String[] args) {
        if (!PlayerUtil.testPermission(sender, Rank.TRAINEE)) {
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(CC.RED + this.getUsage());
            return true;
        }
        final String name = args[0];
        this.plugin.getRequestManager().sendRequest(new PunishHistoryRequest(name), new AbstractCallback("Error fetching punishment history for " + name) {
            @Override
            public void callback(final JSONObject data) {
                final String s;
                final String response = s = (String)data.get((Object)"response");
                switch (s) {
                    case "player-not-found": {
                        sender.sendMessage(CC.RED + "Player not found.");
                        break;
                    }
                    case "no-punishments": {
                        sender.sendMessage(CC.RED + "Player has not been punished.");
                        break;
                    }
                    case "success": {
                        final String name = (String)data.get((Object)"name");
                        final StringBuilder sb = new StringBuilder("Punishment History for " + name + "\n");
                        PunishHistoryCommand.this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)PunishHistoryCommand.this.plugin, () -> {
                            final Object val$sender = sender;
                            final JSONArray array = (JSONArray)data.get((Object)"punishments");
                            array.iterator();
                            final Iterator iterator;
                            while (iterator.hasNext()) {
                                final Object object = iterator.next();
                                final JSONObject punishment = (JSONObject)object;
                                final int punisherId = ((Long)punishment.get((Object)"punisher")).intValue();
                                String punisher = "CONSOLE";
                                if (punisherId != -1) {
                                    final JSONObject requestNow = PunishHistoryCommand.this.plugin.getRequestManager().sendRequestNow(new DataFromIDRequest(punisherId));
                                    final String res = (String)requestNow.get((Object)"response");
                                    if (res.equalsIgnoreCase("success")) {
                                        punisher = (String)requestNow.get((Object)"name");
                                    }
                                }
                                sb.append("[").append(punishment.get((Object)"time").toString().replace("T", "").replace("Z", "")).append("] [").append(punishment.get((Object)"type").toString().toUpperCase()).append("] ").append(name).append(" was punished by ").append(punisher).append(" for \"").append(punishment.get((Object)"reason")).append("\"");
                                final String duration = (String)data.get((Object)"duration");
                                if (duration != null) {
                                    sb.append(" until ").append(duration);
                                }
                                sb.append("\n");
                            }
                            sender.sendMessage(CC.GREEN + "Punishment history for " + name + ": https://www.hastebin.com/" + HttpUtil.getHastebin(sb.toString()));
                            return;
                        });
                        break;
                    }
                }
            }
        });
        return true;
    }
}
