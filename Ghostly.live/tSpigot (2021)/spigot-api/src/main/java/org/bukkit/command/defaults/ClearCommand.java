package org.bukkit.command.defaults;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.ImmutableList;

@Deprecated
public class ClearCommand extends VanillaCommand {
	
    public ClearCommand() {
        super("clearinventory");
        
        this.description = "Clears the player's inventory.";
        this.usageMessage = "/clearinventory <player>";
        
        this.setAliases(Arrays.asList("clear", "ci"));
        this.setPermission("bukkit.command.clear");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;

        if(args.length < 1) {
        	if(!(sender instanceof Player)) {
        		sender.sendMessage("&cUsage: " + usageMessage);
        		return false;
        	}
        	
        	Player player = (Player) sender;
        	
        	player.getInventory().clear();
        	player.getInventory().setArmorContents(new ItemStack[4]);
        	player.updateInventory();
        	
        	player.sendMessage("&aYour inventory has been cleared.");
        	return false;
        } else if(args.length == 1) {
        	Player target = Bukkit.getPlayer(args[0]);
        	if(target == null) {
        		sender.sendMessage("&cFailed to find player '" + args[0] + "' on server.");
        		return false;
        	}
        	
        	if(target.equals(sender)) {
        		sender.sendMessage("&cYou can'not do this in yourself.");
        		return false;
        	}
        	
        	target.getInventory().clear();
        	target.getInventory().setArmorContents(new ItemStack[4]);
        	target.updateInventory();
        	
        	Command.broadcastCommandMessage(sender, "&aCleared the inventory of &r" + target.getName());
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 1) {
            return super.tabComplete(sender, alias, args);
        }
        return ImmutableList.of();
    }
}
