package net.badlion.bungeelobby.commands;

import net.badlion.bungeelobby.BungeeLobby;
import net.badlion.bungeelobby.managers.MCPManager;
import net.badlion.common.GetCommon;
import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SendPlayersToMCPCommand extends Command {

    public SendPlayersToMCPCommand() {
        super("sendplayerstomcp", "bungeecord.command.alert");
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        final JSONObject data = new JSONObject();
        data.put("bungee", BungeeLobby.BUNGEE_NAME);
        List<JSONObject> players = new ArrayList<>();

        for (ProxiedPlayer player : BungeeCord.getInstance().getPlayers()) {
            JSONObject playerData = new JSONObject();
            playerData.put("uuid", player.getUniqueId().toString());
            playerData.put("username", player.getName());
            playerData.put("ip", BungeeLobby.toLongIP(player.getPendingConnection().getAddress().getAddress().getAddress()));
            playerData.put("version", player.getPendingConnection().getVersion());
            players.add(playerData);
        }

        data.put("players", players);

        BungeeLobby.plugin.getProxy().getScheduler().runAsync(BungeeLobby.plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    // ONLY CONTACT MASTER OR BAD THINGS WILL HAPPEN WITH PERFORMANCE OF COMMANDS
                    HTTPCommon.executePOSTRequest("http://" + GetCommon.getIpForDB() + ":9000/" + MCPManager.MCP_MESSAGE.BUNGEE_PLAYER_INFO.name().replace("_", "-") + "/" + BungeeLobby.mcpKey, data, BungeeLobby.mcpTimeout);
                } catch (HTTPRequestFailException e) {
                    sender.sendMessage(ChatColor.RED + "Failed");
                }
                sender.sendMessage(ChatColor.GREEN + "Success");
            }
        });
    }

}
