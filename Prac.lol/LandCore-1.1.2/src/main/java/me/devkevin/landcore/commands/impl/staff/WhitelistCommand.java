package me.devkevin.landcore.commands.impl.staff;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.BaseCommand;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.server.ServerSettings;
import me.devkevin.landcore.server.WhitelistMode;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class WhitelistCommand extends BaseCommand {
    private final LandCore plugin;

    public WhitelistCommand(LandCore plugin) {
        super("whitelist", Rank.ADMIN);
        this.plugin = plugin;
        setAliases("wl");
        setUsage(CC.RED + "Usage: /whitelist <none|ranks|staff>");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(usageMessage);
            return;
        }

        ServerSettings settings = plugin.getServerSettings();

        switch (args[0].toLowerCase()) {
            case "none":
            case "off":
                settings.setServerWhitelistMode(WhitelistMode.NONE);
                break;
            case "ranks":
            case "donors":
                settings.setServerWhitelistMode(WhitelistMode.RANKS);
                break;
            case "staff":
            case "on":
                settings.setServerWhitelistMode(WhitelistMode.STAFF);
                break;
            default:
                sender.sendMessage(CC.RED + "That's not a valid whitelist mode!");
                return;
        }

        WhitelistMode whitelistMode = settings.getServerWhitelistMode();
        Server server = plugin.getServer();

        if (whitelistMode == WhitelistMode.NONE) {
            server.broadcastMessage(CC.GREEN + "The server is no longer whitelisted!");
        } else {
            whitelistMode.activate();
            server.broadcastMessage(CC.RED + "The server is now whitelisted (Mode: " + whitelistMode + ").");
        }
    }
}
