package net.badlion.bungeelobby.commands;

import com.google.common.base.Joiner;
import net.badlion.bungeelobby.BungeeLobby;
import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SendToAllCommand extends Command {

    private boolean processing = false;

    public SendToAllCommand() {
        super("sendtoall", "bungeecord.command.alert");

        BungeeLobby.plugin.getProxy().getScheduler().schedule(BungeeLobby.plugin, new Runnable() {
            @Override
            public void run() {
                // Still working
                if (processing) {
                    return;
                }

                processing = true;

                try {
                    JSONObject response = HTTPCommon.executeGETRequest(BungeeLobby.mcpURL + "get-bungee-command-queue/" + BungeeLobby.BUNGEE_NAME + "/" + BungeeLobby.mcpKey, BungeeLobby.mcpTimeout);

                    if (response != null && response.containsKey("commands")) {
                        List<String> commands = (List<String>) response.get("commands");

                        for (String command : commands) {
                            BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(), command);
                        }

                        // Clear reliable queue with the same exact response we were given
                        HTTPCommon.executePOSTRequest(BungeeLobby.mcpURL + "clear-bungee-command-queue/" + BungeeLobby.BUNGEE_NAME + "/" + BungeeLobby.mcpKey, response, BungeeLobby.mcpTimeout);
                    }
                } catch (HTTPRequestFailException e) {
                    // Do nothing
                } finally {
                    processing = false; // Our "lock"
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        BungeeLobby.plugin.getProxy().getScheduler().runAsync(BungeeLobby.plugin, new Runnable() {
            @Override
            public void run() {
                String command = Joiner.on(" ").skipNulls().join(args);
                JSONObject object = new JSONObject();
                object.put("command", command);
                try {
                    HTTPCommon.executePOSTRequest(BungeeLobby.mcpURL + "send-to-all/" + BungeeLobby.mcpKey, object);
                    sender.sendMessage(ChatColor.GREEN + "Command sent.");
                } catch (HTTPRequestFailException e) {
                    sender.sendMessage(ChatColor.RED + "Failed to send to all");
                }
            }
        });
    }

}
