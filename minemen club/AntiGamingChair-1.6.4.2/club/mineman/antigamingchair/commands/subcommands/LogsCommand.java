// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.commands.subcommands;

import java.beans.ConstructorProperties;
import java.util.regex.Matcher;
import club.mineman.core.api.request.RequestCallback;
import club.mineman.core.api.APIMessage;
import java.util.Iterator;
import club.mineman.core.mineman.Mineman;
import java.util.Queue;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.plugin.Plugin;
import club.mineman.core.util.finalutil.HttpUtil;
import org.json.simple.JSONArray;
import club.mineman.antigamingchair.log.Log;
import java.util.LinkedList;
import org.json.simple.JSONObject;
import club.mineman.core.api.request.AbstractCallback;
import club.mineman.antigamingchair.request.AGCInfoRequest;
import club.mineman.core.CorePlugin;
import club.mineman.core.util.finalutil.CC;
import club.mineman.core.util.finalutil.TimeUtil;
import java.sql.Timestamp;
import club.mineman.core.util.finalutil.StringUtil;
import org.bukkit.entity.Player;
import club.mineman.antigamingchair.AntiGamingChair;
import java.util.regex.Pattern;

public class LogsCommand implements SubCommand
{
    private static final Pattern LOG_PATTERN;
    private final AntiGamingChair plugin;
    
    @Override
    public void execute(final Player player, final Player target, final String[] args) {
        if (args.length == 1) {
            return;
        }
        final boolean detailed = args.length > 2 && args[2].equalsIgnoreCase("-d");
        final int index = detailed ? 3 : 2;
        final String timeString = (args.length > index) ? StringUtil.buildMessage(args, index) : null;
        final Timestamp time = new Timestamp(0L);
        if (timeString != null) {
            try {
                time.setTime(System.currentTimeMillis() - TimeUtil.toMillis(timeString));
            }
            catch (final NumberFormatException e) {
                player.sendMessage(CC.RED + "Invalid time specified.");
                return;
            }
        }
        CorePlugin.getInstance().getRequestManager().sendRequest((APIMessage)new AGCInfoRequest(args[1], time), (RequestCallback)new AbstractCallback("Error fetching AGC logs for " + args[1] + ".") {
            public void callback(final JSONObject data) {
                final Queue<Log> logs = new LinkedList<Log>();
                if (target != null) {
                    final Mineman mineman = CorePlugin.getInstance().getPlayerManager().getPlayer(target.getUniqueId());
                    for (final Log log : LogsCommand.this.plugin.getLogManager().getLogQueue()) {
                        if (log.getMinemanId() == mineman.getId()) {
                            logs.add(log);
                        }
                    }
                }
                final String s;
                final String response = s = (String)data.get((Object)"response");
                switch (s) {
                    case "player-never-joined":
                    case "invalid-player": {
                        player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[1]));
                        break;
                    }
                    case "no-logs": {
                        if (logs.size() == 0) {
                            player.sendMessage(CC.L_PURPLE + "Player " + CC.D_PURPLE + args[1] + CC.L_PURPLE + " has no logs.");
                            break;
                        }
                    }
                    case "success": {
                        final StringBuilder sb = new StringBuilder();
                        if (detailed) {
                            final JSONArray array = (JSONArray)data.get((Object)"data");
                            for (final Object object : array) {
                                final JSONObject jsonObject = (JSONObject)object;
                                final Timestamp timestamp = Timestamp.valueOf(((String)jsonObject.get((Object)"timestamp")).replace("T", " ").replace("Z", ""));
                                final String log2 = (String)jsonObject.get((Object)"log");
                                sb.append(String.format("[%s] %s %s", timestamp.toString(), args[1], log2));
                                sb.append("\n");
                            }
                            for (final Log log3 : logs) {
                                sb.append(String.format("[%s] %s %s", new Timestamp(log3.getTimestamp()).toString(), args[1], log3.getLog()));
                                sb.append("\n");
                            }
                            LogsCommand.this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)LogsCommand.this.plugin, () -> {
                                final Object val$player = player;
                                final String bin = HttpUtil.getHastebin(sb.toString());
                                if (bin == null) {
                                    player.sendMessage(CC.RED + "Error uploading logs. Check console for details.");
                                    return;
                                }
                                else {
                                    player.sendMessage(CC.L_PURPLE + "Player logs: https://www.hastebin.com/" + bin);
                                    return;
                                }
                            });
                            break;
                        }
                        final Map<String, Integer> violationMap = new ConcurrentHashMap<String, Integer>();
                        final JSONArray array2 = (JSONArray)data.get((Object)"data");
                        for (final Object object2 : array2) {
                            final JSONObject jsonObject2 = (JSONObject)object2;
                            final String log2 = (String)jsonObject2.get((Object)"log");
                            LogsCommand.this.handleLog(violationMap, log2);
                        }
                        for (final Log log4 : logs) {
                            LogsCommand.this.handleLog(violationMap, log4.getLog());
                        }
                        if (violationMap.isEmpty()) {
                            player.sendMessage(CC.GREEN + "Player " + args[1] + " has no logs.");
                            return;
                        }
                        for (final String string : violationMap.keySet()) {
                            sb.append(CC.L_PURPLE).append(string).append(" ").append(CC.D_PURPLE).append("x").append(violationMap.get(string)).append("\n");
                        }
                        player.sendMessage(CC.L_PURPLE + args[1] + " logs:");
                        player.sendMessage(sb.toString());
                        break;
                    }
                }
            }
        });
    }
    
    private void handleLog(final Map<String, Integer> violations, final String log) {
        final Matcher matcher = LogsCommand.LOG_PATTERN.matcher(log);
        if (matcher.find()) {
            final String type = matcher.group(1);
            final String check = matcher.group(2);
            final String finalData = type + " Check " + check;
            int count = violations.getOrDefault(finalData, 0);
            violations.put(finalData, ++count);
        }
    }
    
    @ConstructorProperties({ "plugin" })
    public LogsCommand(final AntiGamingChair plugin) {
        this.plugin = plugin;
    }
    
    static {
        LOG_PATTERN = Pattern.compile("failed (.*) Check (\\D).(.*)");
    }
}
