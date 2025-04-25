package org.bukkit.command.defaults;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandSender;

import com.google.common.collect.ImmutableList;

@Deprecated
public class HelpCommand extends VanillaCommand {
    
	public HelpCommand() {
        super("help");
        this.setAliases(Arrays.asList(new String[] { "?" }));
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        sender.sendMessage("&cUnknown Command");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");
        return ImmutableList.of();
    }
}
