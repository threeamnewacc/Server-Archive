package net.badlion.bungeeprivate.commands;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Map;

public class TestCommand extends Command {

    public TestCommand() {
        super("test", "redisbungee.command.sendtoall");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args[0].equalsIgnoreCase("check")) {
            Map<String, String> map = player.getModList();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                player.sendMessage(entry.getKey() + " " + entry.getValue());
            }

            if (map.size() == 0) {
                if (player instanceof UserConnection) {
                    UserConnection connection = (UserConnection) player;
                    connection.getForgeClientHandler().resetHandshake();
                }
            }
            return;
        } else if (args[0].equalsIgnoreCase("send")) {
            if (player instanceof UserConnection) {
                UserConnection connection = (UserConnection) player;
                connection.getForgeClientHandler().resetHandshake();
            }
            return;
        }
    }
}
