package us.zonix.practice.commands;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import us.zonix.practice.Practice;

public class SilentCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public SilentCommand() {
        super("silent");
        this.setDescription("Toggles silent mdoe.");
        this.setUsage(ChatColor.RED + "Usage: /silent");
        this.setAliases(Arrays.asList("flight"));
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            if (!player.hasPermission("core.staff")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
                return true;
            } else {
                if (player.hasMetadata("modmode")) {
                    player.removeMetadata("modmode", this.plugin);
                    player.removeMetadata("invisible", this.plugin);
                    player.sendMessage(ChatColor.GREEN + "You have disabled silent mode.");
                } else {
                    player.setMetadata("modmode", new FixedMetadataValue(this.plugin, true));
                    player.setMetadata("invisible", new FixedMetadataValue(this.plugin, true));
                    player.sendMessage(ChatColor.GREEN + "You have enabled silent mode.");
                }

                return true;
            }
        }
    }
}
