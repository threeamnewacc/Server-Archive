package org.bukkit.command.defaults;

import org.bukkit.command.CommandSender;

@Deprecated
public class TestForCommand extends VanillaCommand {
	
    public TestForCommand() {
        super("testfor");
        
        this.description = "Tests whether a specifed player is online";
        this.usageMessage = "/testfor <player>";
        
        this.setPermission("bukkit.command.testfor");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        sender.sendMessage("&cThis command has been removed security methods.");
        return true;
    }

    // Spigot Start
    @Override
    public java.util.List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if ( args.length == 0 ) {	
            return super.tabComplete( sender, alias, args );
        }
        return java.util.Collections.emptyList();
    }
    // Spigot End
}
