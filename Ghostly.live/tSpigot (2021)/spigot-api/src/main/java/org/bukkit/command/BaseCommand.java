package org.bukkit.command;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;

public abstract class BaseCommand implements CommandExecutor, TabCompleter {
	
	/**
     * The default amount of tab completion entries to limit to.
     */
    private static int DEFAULT_COMPLETION_LIMIT = 80;
	
	private String name, description, permission;
    private String[] aliases;
    private boolean onlyPlayer = false;
    
    public BaseCommand(String name) {
		this(name, "");
    }
    
    public BaseCommand(String name, String description) {
		this(name, description, ArrayUtils.EMPTY_STRING_ARRAY);
    }
    
    public BaseCommand(String name, String description, String[] aliases) {		
		this.name = name;
		this.description = description;
		this.aliases = Arrays.copyOf(aliases, aliases.length);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if(onlyPlayer) {
    		if(!(sender instanceof Player)) {
    			sender.sendMessage("&cThis command does not support execution from the console.");
        		return true;
    		}
    	}
    	
    	if(permission != null && !sender.hasPermission(permission)) {
    		sender.sendMessage("&cYou don't have the required permission to perform this command.");
        	return false;
        }
    	
    	runCommand(sender, label, args);
    	return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String flag, String[] args) {
    	return runCompleter(sender, flag, args);
    }
    
    public abstract boolean runCommand(CommandSender sender, String flag, String[] args);
    
    public List<String> runCompleter(CommandSender sender, String flag, String[] args) {
    	return null;
    }
    
    public List<String> getCompletions(String[] args, List<String> input) {
        return getCompletions(args, input, DEFAULT_COMPLETION_LIMIT);
    }

    public List<String> getCompletions(String[] args, List<String> input, int limit) {
        Preconditions.checkNotNull(args);
        Preconditions.checkArgument(args.length != 0);

        String argument = args[(args.length - 1)];

        /** Non Java 8 version
		return FluentIterable.from(Arrays.asList(args)).filter(new Predicate<String>() {
	        
	        @Override public boolean apply(String string) {
	        	return string.regionMatches(true, 0, argument, 0, argument.length());
	        }
	           
        }).limit(limit).toList();
        */
        return input.stream().filter(string -> string.regionMatches(true, 0, argument, 0, argument.length())).limit(limit).collect(Collectors.toList());
    }
    
    public String getName() {
		return name;
	}
    
    public String getDescription() {
		return description;
	}
    
    public BaseCommand description(String description) {
    	this.description = description;
    	return this;
    }
    
    public String getPermission() {
		return permission;
	}
    
    public BaseCommand permission(String permission) {
		this.permission = permission;
		return this;
    }
    

	public String[] getAliases() {
		if(this.aliases == null) {
			this.aliases = ArrayUtils.EMPTY_STRING_ARRAY;
		}
		
		return Arrays.copyOf(this.aliases, this.aliases.length);
	}
	
	public boolean isOnlyPlayer() {
		return onlyPlayer;
	}
	
	public void setOnlyPlayer(boolean onlyPlayer) {
		this.onlyPlayer = onlyPlayer;
	}
}
