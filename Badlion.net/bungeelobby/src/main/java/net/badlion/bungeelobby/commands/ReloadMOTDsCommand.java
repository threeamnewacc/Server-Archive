package net.badlion.bungeelobby.commands;

import net.badlion.bungeelobby.BungeeLobby;
import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import org.json.simple.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ReloadMOTDsCommand extends Command {

    public ReloadMOTDsCommand() {
        super("reload-motds", "bungeecord.command.alert");
    }

    @Override
    public void execute(final CommandSender sender, String[] args) {
        BungeeLobby.motdsConfig.load();
        final List<String> localMotds = new ArrayList<>();

        for (String motd : BungeeLobby.motdsConfig.getConfig().getStringList("motds")) {
            // Encode to base 64 http://stackoverflow.com/a/29991733/1247832
            localMotds.add(DatatypeConverter.printBase64Binary(motd.getBytes()));
        }

        BungeeLobby.plugin.getProxy().getScheduler().runAsync(BungeeLobby.plugin, new Runnable() {
            @Override
            public void run() {
                JSONObject data = new JSONObject();
                data.put("motds", localMotds);

                try {
                    HTTPCommon.executePOSTRequest(BungeeLobby.mcpURL + "motds-submit/" + BungeeLobby.mcpKey, data, BungeeLobby.mcpTimeout);
                    sender.sendMessage(ChatColor.GREEN + "MOTDS will be updated.");
                } catch (HTTPRequestFailException e) {
                    sender.sendMessage(ChatColor.RED + "Failed to send MOTD update");
                }
            }
        });

    }

}
