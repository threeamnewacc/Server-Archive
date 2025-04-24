// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.commands;

import org.bukkit.entity.Player;
import club.mineman.core.util.finalutil.PlayerUtil;
import club.mineman.core.rank.Rank;
import org.bukkit.command.CommandSender;
import club.mineman.antigamingchair.commands.subcommands.FilterCommand;
import club.mineman.antigamingchair.commands.subcommands.ToggleCommand;
import club.mineman.antigamingchair.commands.subcommands.RangeVlCommand;
import club.mineman.antigamingchair.commands.subcommands.BanWaveCommand;
import club.mineman.antigamingchair.commands.subcommands.LogsCommand;
import club.mineman.antigamingchair.commands.subcommands.InfoCommand;
import club.mineman.antigamingchair.commands.subcommands.WatchCommand;
import club.mineman.antigamingchair.commands.subcommands.SniffCommand;
import club.mineman.antigamingchair.commands.subcommands.AlertsCommand;
import club.mineman.core.util.finalutil.StringUtil;
import club.mineman.core.util.finalutil.CC;
import java.util.HashMap;
import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.commands.subcommands.SubCommand;
import java.util.Map;
import org.bukkit.command.Command;

public class AGCCommand extends Command
{
    private final Map<String, SubCommand> subCommandMap;
    private final AntiGamingChair plugin;
    
    public AGCCommand(final AntiGamingChair plugin) {
        super("agc");
        this.subCommandMap = new HashMap<String, SubCommand>();
        this.plugin = plugin;
        this.setUsage(CC.RED + "Usage: /agc <subcommand> [player]");
        this.setPermissionMessage(StringUtil.NO_PERMISSION);
        this.subCommandMap.put("alerts", new AlertsCommand(plugin));
        this.subCommandMap.put("sniff", (SubCommand)new SniffCommand(plugin));
        this.subCommandMap.put("watch", new WatchCommand(plugin));
        this.subCommandMap.put("info", new InfoCommand(plugin));
        this.subCommandMap.put("logs", new LogsCommand(plugin));
        this.subCommandMap.put("banwave", new BanWaveCommand(plugin));
        this.subCommandMap.put("rangevl", new RangeVlCommand(plugin));
        this.subCommandMap.put("toggle", new ToggleCommand());
        this.subCommandMap.put("filter", new FilterCommand(plugin));
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!PlayerUtil.testPermission(sender, Rank.ADMIN)) {
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(StringUtil.PLAYER_ONLY);
            return true;
        }
        final Player player = (Player)sender;
        final String subCommandString = (args.length < 1) ? "help" : args[0].toLowerCase();
        final SubCommand subCommand = this.subCommandMap.get(subCommandString);
        if (subCommand == null) {
            player.sendMessage(CC.RED + this.getUsage());
            return true;
        }
        final Player target = (args.length > 1) ? this.plugin.getServer().getPlayer(args[1]) : null;
        subCommand.execute(player, target, args);
        return true;
    }
}
