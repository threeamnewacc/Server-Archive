package me.devkevin.landcore.commands.impl.staff;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.BaseCommand;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.StringUtil;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class BroadcastCommand extends BaseCommand {
    private final LandCore plugin;

    public BroadcastCommand(LandCore plugin) {
        super("broadcast", Rank.ADMIN);
        this.plugin = plugin;
        setAliases("bc");
        setUsage(CC.RED + "Usage: /broadcast <message> [-god]");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(usageMessage);
            return;
        }

        String message = CC.SECONDARY + "[Alert] " + CC.PRIMARY
                + ChatColor.translateAlternateColorCodes('&', StringUtil.buildString(args, 0)).trim();

        if (message.endsWith(" -god")) {
            message = message.substring(12, message.length() - 5).trim();
        }

        plugin.getServer().broadcastMessage(message);
    }
}
