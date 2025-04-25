package us.zonix.practice.commands;

import java.util.UUID;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.inventory.InventorySnapshot;

public class InvCommand extends Command {
    private static final Pattern UUID_PATTERN = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
    private static final String INVENTORY_NOT_FOUND = ChatColor.RED + "Cannot find the requested inventory. Maybe it expired?";
    private final Practice plugin = Practice.getInstance();

    public InvCommand() {
        super("inventory");
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else if (args.length == 0) {
            return true;
        } else if (!args[0].matches(UUID_PATTERN.pattern())) {
            sender.sendMessage(INVENTORY_NOT_FOUND);
            return true;
        } else {
            InventorySnapshot snapshot = this.plugin.getInventoryManager().getSnapshot(UUID.fromString(args[0]));
            if (snapshot == null) {
                sender.sendMessage(INVENTORY_NOT_FOUND);
            } else {
                ((Player)sender).openInventory(snapshot.getInventoryUI().getCurrentPage());
            }

            return true;
        }
    }
}
