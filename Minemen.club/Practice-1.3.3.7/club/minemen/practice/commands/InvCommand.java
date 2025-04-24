// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands;

import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.inventory.InventorySnapshot;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import club.minemen.practice.Practice;
import java.util.regex.Pattern;
import org.bukkit.command.Command;

public class InvCommand extends Command
{
    private static final Pattern UUID_PATTERN;
    private static final String INVENTORY_NOT_FOUND;
    private final Practice plugin;
    
    public InvCommand() {
        super("inv");
        this.plugin = Practice.getInstance();
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        if (args.length == 0) {
            return true;
        }
        if (!args[0].matches(InvCommand.UUID_PATTERN.pattern())) {
            sender.sendMessage(InvCommand.INVENTORY_NOT_FOUND);
            return true;
        }
        final InventorySnapshot snapshot = this.plugin.getInventoryManager().getSnapshot(UUID.fromString(args[0]));
        if (snapshot == null) {
            sender.sendMessage(InvCommand.INVENTORY_NOT_FOUND);
        }
        else {
            ((Player)sender).openInventory(snapshot.getInventoryUI().getCurrentPage());
        }
        return true;
    }
    
    static {
        UUID_PATTERN = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
        INVENTORY_NOT_FOUND = CC.RED + "Inventory not found.";
    }
}
