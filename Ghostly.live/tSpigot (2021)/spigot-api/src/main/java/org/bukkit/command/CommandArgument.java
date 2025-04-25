package org.bukkit.command;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.CommandSender;

import com.google.common.base.Preconditions;

/**
 * Represents an argument used for a Bukkit command.
 */
public abstract class CommandArgument {
	
	/**
     * The default amount of tab completion entries to limit to.
     */
    private static int DEFAULT_COMPLETION_LIMIT = 80;

    private String name, description, permission;
    private String[] aliases;

    /**
     * Constructs a new {@link CommandArgument} with a given name,
     * description and set of aliases.
     *
     * @param name        the name to construct with
     * @param description the description to construct with
     */
    public CommandArgument(String name, String description) {
        this(name, description, null);
    }

    /**
     * Constructs a new {@link CommandArgument} with a given name,
     * description and set of aliases.
     *
     * @param name        the name to construct with
     * @param description the description to construct with
     * @param permission  the required permission node
     */
    public CommandArgument(String name, String description, String permission) {
    	this.name = name;
        this.description = description;
        this.permission = permission;
        
        this.aliases = ArrayUtils.EMPTY_STRING_ARRAY;
    }

    /**
     * Gets the name of this {@link CommandArgument}.
     *
     * @return the name
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Gets the description of this {@link CommandArgument}.
     *
     * @return the description
     */
    public final String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
		this.description = description;
	}

    /**
     * Gets the permission of this {@link CommandArgument}.
     *
     * @return the permission
     */
    public final String getPermission() {
        return this.permission;
    }
    
    public void setPermission(String permission) {
		this.permission = permission;
	}
    
    public String[] getAliases() {
		if(this.aliases == null) {
			this.aliases = ArrayUtils.EMPTY_STRING_ARRAY;
		}
		
		return Arrays.copyOf(this.aliases, this.aliases.length);
	}
    
    public CommandArgument aliases(String[] aliases) {
		this.aliases = aliases;
		return this;
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
    
    public abstract boolean runCommand(CommandSender sender, String label, String[] args);

    public List<String> runCompleter(CommandSender sender, String label, String[] args) {
        return null;
    }
}
