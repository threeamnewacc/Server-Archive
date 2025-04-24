// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command;

import com.google.common.collect.ImmutableMap;
import club.mineman.core.mineman.Mineman;
import club.mineman.core.util.finalutil.CC;
import club.mineman.core.util.finalutil.PlayerUtil;
import club.mineman.core.rank.Rank;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.List;
import java.util.Collections;
import club.mineman.core.CorePlugin;
import java.util.Map;
import org.bukkit.command.Command;

public class ColorCommand extends Command
{
    private static final Map<String, String> COLORS;
    private final CorePlugin plugin;
    
    public ColorCommand(final CorePlugin plugin) {
        super("color", "Change your chat prefix color", "/color <color>", (List)Collections.singletonList("cc"));
        this.plugin = plugin;
    }
    
    public boolean execute(final CommandSender commandSender, final String s, final String[] args) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        if (!PlayerUtil.testPermission(commandSender, Rank.CLUBBER)) {
            return true;
        }
        final Mineman mineman = this.plugin.getPlayerManager().getPlayer(((Player)commandSender).getUniqueId());
        if (mineman == null || !mineman.isDataLoaded()) {
            commandSender.sendMessage(CC.RED + "Please wait for your data to load.");
            return true;
        }
        if (mineman.isErrorLoadingData()) {
            commandSender.sendMessage(CC.RED + "There was an error loading your data. Please relog and try again, or contact an Admin if this error persists.");
            return true;
        }
        if (args.length == 0) {
            commandSender.sendMessage(CC.RED + this.usageMessage);
            commandSender.sendMessage(CC.RED + "Valid colors are: " + ColorCommand.COLORS.keySet().toString().replace("[", "").replace("]", ""));
            return true;
        }
        final String customColor = ColorCommand.COLORS.get(args[0].toLowerCase());
        if (customColor == null && !args[0].equalsIgnoreCase("default")) {
            commandSender.sendMessage(CC.RED + "Invalid color.");
            commandSender.sendMessage(CC.RED + "Valid colors are: " + ColorCommand.COLORS.keySet().toString().replace("[", "").replace("]", ""));
            return true;
        }
        mineman.setCustomColor(customColor);
        commandSender.sendMessage(CC.GREEN + "You changed your chat color to " + args[0] + ".");
        if (!args[0].equalsIgnoreCase("default")) {
            commandSender.sendMessage(CC.GREEN + "Do /color default to go back to default.");
        }
        return true;
    }
    
    static {
        COLORS = (Map)new ImmutableMap.Builder().put((Object)"pink", (Object)CC.PINK).put((Object)"light_purple", (Object)CC.PINK).put((Object)"purple", (Object)CC.DARK_PURPLE).put((Object)"dark_purple", (Object)CC.DARK_PURPLE).put((Object)"gold", (Object)CC.GOLD).put((Object)"blue", (Object)CC.BLUE).put((Object)"cyan", (Object)CC.AQUA).put((Object)"aqua", (Object)CC.AQUA).put((Object)"light_blue", (Object)CC.AQUA).put((Object)"dark_blue", (Object)CC.BLUE).put((Object)"yellow", (Object)CC.YELLOW).build();
    }
}
