package us.zonix.practice.commands.toggle;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.player.PlayerData;

public class SettingsCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public SettingsCommand() {
        super("settings");
        this.setDescription("Toggles multiple settings.");
        this.setUsage(ChatColor.RED + "Usage: /settings");
        this.setAliases(Arrays.asList("options", "toggle"));
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            player.openInventory(playerData.getOptions().getInventory());
            return true;
        }
    }
}
