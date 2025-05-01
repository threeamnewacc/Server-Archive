package net.badlion.arenalobby.commands;

import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.GroupStateMachine;
import net.badlion.arenalobby.managers.PotPvPPlayerManager;
import net.badlion.statemachine.State;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public abstract class GCommandExecutor implements CommandExecutor {

	private int minArgs = 0;
	private boolean expectsHashCode;

	protected Player player = null;
	protected Group group = null;

	protected State<Group> currentState;

	public GCommandExecutor(int minArgs) {
		this(minArgs, false);
	}

	public GCommandExecutor(int minArgs, boolean expectsHashCode) {
		this.minArgs = minArgs;
		this.expectsHashCode = expectsHashCode;

		// Handle the extra argument here
		if (this.expectsHashCode) {
			this.minArgs += 1;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// If they don't give us enough information send usage by default
		if (args.length < this.minArgs) {
			this.usage(sender);
			return true;
		}

		// Hashcode handling
		if (this.expectsHashCode) {
			if (!sender.isOp() && !ArenaLobby.getInstance().getCmdSignsPlugin().validateHash(args[0])) {
				sender.sendMessage("Invalid authorization.");
				return true;
			}

			// Strip out hashcode from arguments
			args = Arrays.copyOfRange(args, 1, args.length);
		}


		if (sender instanceof Player) {
			this.player = (Player) sender;
			this.group = ArenaLobby.getInstance().getPlayerGroup(this.player);

			this.currentState = GroupStateMachine.getInstance().getCurrentState(this.group);

			PotPvPPlayerManager.addDebug(this.player, "COMMAND: " + command + " " + args);

			for (Player pl : this.group.players()) {
				if (pl != player) {
					PotPvPPlayerManager.addDebug(pl, this.player.getName() + " COMMAND: " + command + " " + args);
				}
			}

			this.onGroupCommand(command, label, args);
		} else {
			this.onSenderCommand(sender, command, label, args);
		}

		return true;
	}

	/**
	 * Extend/override this method
	 */
	public void onGroupCommand(Command command, String label, String[] args) {
		throw new UnsupportedOperationException("onGroupCommand() not implemented for " + this.getClass().getName());
	}

	/**
	 * Extend/override this method
	 */
	public void onSenderCommand(CommandSender sender, Command command, String label, String[] args) {
		throw new UnsupportedOperationException("onSenderCommand() not implemented for " + this.getClass().getName());
	}


	/**
	 * Give a CommandSender the usage of a command (something nicer than default)
	 */
	public abstract void usage(CommandSender sender);

}
