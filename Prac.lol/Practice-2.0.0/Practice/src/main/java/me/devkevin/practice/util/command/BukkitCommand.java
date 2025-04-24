package me.devkevin.practice.util.command;

import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Command Framework - BukkitCommand <br>
 * An implementation of Bukkit's Command class allowing for registering of
 * commands without plugin.yml
 * 
 * @author minnymin3
 * 
 */
public class BukkitCommand extends org.bukkit.command.Command {

	private final Plugin owningPlugin;
	private CommandExecutor executor;
	protected BukkitCompleter completer;

	protected BukkitCommand(String label, CommandExecutor executor, Plugin owner) {
		super(label);
		this.executor = executor;
		this.owningPlugin = owner;
		this.usageMessage = "";
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		boolean success = false;

		if (!owningPlugin.isEnabled()) {
			return false;
		}

		if (!testPermission(sender)) {
			return true;
		}

		try {
			success = executor.onCommand(sender, this, commandLabel, args);
		} catch (Throwable ex) {
			throw new CommandException("Error while executing '" + commandLabel + "' in plugin "
					+ owningPlugin.getDescription().getFullName(), ex);
		}

		if (!success && usageMessage.length() > 0) {
			for (String line : usageMessage.replace("<command>", commandLabel).split("\n")) {
				sender.sendMessage(line);
			}
		}

		return success;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args)
			throws CommandException, IllegalArgumentException {
		Validate.notNull(sender, "The player is not valid.");
		Validate.notNull(args, "Invalid arguments.");
		Validate.notNull(alias, "Sub-command is not valid.");

		List<String> completions = null;
		try {
			if (completer != null) {
				completions = completer.onTabComplete(sender, this, alias, args);
			}
			if (completions == null && executor instanceof TabCompleter) {
				completions = ((TabCompleter) executor).onTabComplete(sender, this, alias, args);
			}
		} catch (Throwable ex) {
			StringBuilder message = new StringBuilder();
			message.append("Error while executing '/").append(alias).append(' ');
			for (String arg : args) {
				message.append(arg).append(' ');
			}
			message.deleteCharAt(message.length() - 1).append("' in plugin ")
					.append(owningPlugin.getDescription().getFullName());
			throw new CommandException(message.toString(), ex);
		}

		if (completions == null) {
			return super.tabComplete(sender, alias, args);
		}
		return completions;
	}
}
