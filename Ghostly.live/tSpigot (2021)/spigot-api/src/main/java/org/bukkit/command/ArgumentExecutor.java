package org.bukkit.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

/**
 * Helper class used to build commands that have multiple
 * sub-commands referred to as {@link CommandArgument}s
 */
public abstract class ArgumentExecutor implements CommandExecutor, TabCompleter {

	private ChatColor firstColor = ChatColor.GOLD, secondColor = ChatColor.GRAY, thirdColor = ChatColor.WHITE;
	private String delimiter = ", ";

	/**
     * The default amount of tab completion entries to limit to.
     */
    private static int DEFAULT_COMPLETION_LIMIT = 80;

    private List<CommandArgument> arguments;
    private String name, description, permissionNode;
    private String[] aliases;
    private boolean onlyPlayer = false;

    public ArgumentExecutor(String name) {
		this(name, null);
    }

    public ArgumentExecutor(String name, String description) {
		this(name, description, ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public ArgumentExecutor(String name, String description, String[] aliases) {
    	arguments = new ArrayList<CommandArgument>();

    	this.name = name;
		this.description = description;
		this.aliases = Arrays.copyOf(aliases, aliases.length);
    }

    /**
     * Checks if this executor contains an {@link CommandArgument}.
     *
     * @param argument the argument to check for
     * @return true if this executor contains argument
     */
    public boolean containsArgument(CommandArgument argument) {
        return arguments.contains(argument);
    }

    /**
     * Adds an {@link CommandArgument} to this {@link ArgumentExecutor}.
     *
     * @param argument the {@link CommandArgument} to add
     */
    public void addArgument(CommandArgument argument) {
        arguments.add(argument);
    }

    /**
     * Removes an {@link CommandArgument} from this {@link ArgumentExecutor}.
     *
     * @param argument the {@link CommandArgument} to remove
     */
    public void removeArgument(CommandArgument argument) {
        arguments.remove(argument);
    }

    /**
     * Gets a command argument that has a given name
     * or alias included in this executor.
     *
     * @param id the name to search for
     * @return the command argument, null if absent
     */

    public CommandArgument getArgument(String name) {
        return this.arguments.stream().filter(argument -> argument.getName().equalsIgnoreCase(name) || Arrays.asList(argument.getAliases()).contains(name.toLowerCase())).findFirst().orElse(null);
    }

    /**
     * Gets an {@link ImmutableList} of the {@link CommandArgument}s
     * used for this executor.
     *
     * @return list of {@link CommandArgument}s for this executor
     */
    public List<CommandArgument> getArguments() {
        return ImmutableList.copyOf(arguments);
    }

    public abstract boolean runCommand(CommandSender sender, String label, String[] args);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	if(onlyPlayer) {
    		if(!(sender instanceof Player)) {
    			sender.sendMessage("&cThis command does not support execution from the console.");
        		return false;
    		}
    	}

        if(permissionNode != null && !sender.hasPermission(permissionNode)) {
        	sender.sendMessage("&cYou don't have the required permission to perform this command.");
        	return false;
        }

    	this.runCommand(sender, label, args);
    	return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
    	List<String> results = new ArrayList<String>();
        if (args.length < 2) {
            for (CommandArgument argument : arguments) {
            	String permission = argument.getPermission();
				if(permission != null && !sender.hasPermission(permission)) {
					continue;
				}

				results.add(argument.getName());
            }
        } else {
            CommandArgument argument = getArgument(args[0]);
            if (argument == null) {
                return results;
            }

            String permission = argument.getPermission();
            if (permission == null || sender.hasPermission(permission)) {
                results = argument.runCompleter(sender, label, fixArgs(args));

                if (results == null) {
                    return null;
                }
            }
        }

        return getCompletions(args, results);
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

    public String[] fixArgs(String[] args) {
        String[] subArgs = new String[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            subArgs[i - 1] = args[i];
        }

        return subArgs;
    }

    public ChatColor getFirstColor() {
		return firstColor;
	}

    public void setFirstColor(ChatColor firstColor) {
		this.firstColor = firstColor;
	}

    public ChatColor getSecondColor() {
		return secondColor;
	}

    public void setSecondColor(ChatColor secondColor) {
		this.secondColor = secondColor;
	}

    public ChatColor getThirdColor() {
		return thirdColor;
	}

    public void setThirdColor(ChatColor thirdColor) {
		this.thirdColor = thirdColor;
	}

    public String getDelimiter() {
		return delimiter;
	}

    public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

    public String getName() {
		return name;
	}

    public String getDescription() {
		return description;
	}

    public ArgumentExecutor description(String description) {
    	this.description = description;
    	return this;
    }

    public String getPermissionNode() {
		return permissionNode;
	}

    public ArgumentExecutor permissionNode(String permissionNode) {
		this.permissionNode = permissionNode;
		return this;
    }

    public String[] getAliases() {
		if(this.aliases == null) {
			this.aliases = ArrayUtils.EMPTY_STRING_ARRAY;
		}

		return Arrays.copyOf(this.aliases, this.aliases.length);
	}

    public ArgumentExecutor aliases(String[] aliases) {
		this.aliases = aliases;
		return this;
    }

    public boolean isOnlyPlayer() {
		return onlyPlayer;
	}

    public void setOnlyPlayer(boolean onlyPlayer) {
		this.onlyPlayer = onlyPlayer;
	}
}
