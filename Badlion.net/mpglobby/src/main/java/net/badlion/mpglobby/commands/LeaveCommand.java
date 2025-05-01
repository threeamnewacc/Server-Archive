package net.badlion.mpglobby.commands;

import net.badlion.mpglobby.MPGLobby;
import net.badlion.mpglobby.QueueType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, Command cmd, String s, String[] args) {
	    if (!(sender instanceof Player)) return true;

	    // Test environment
	    if (args.length > 0 && sender.hasPermission("badlion.tester")) {
		    StringBuilder sb = new StringBuilder();

		    for (String arg : args) {
			    sb.append(arg);
			    sb.append(" ");
		    }

		    String queue = sb.toString();
		    queue = queue.substring(0, queue.length() - 1);

		    for (QueueType queueType : QueueType.values()) {
			    if (queue.equalsIgnoreCase(queueType.getName())) {
				    MPGLobby.getInstance().joinQueue((Player) sender, queueType, true);
				    return true;
			    }
		    }

		    sender.sendMessage(ChatColor.RED + "Queue '" + queue + "' not found.");
		    return true;
	    }

	    MPGLobby.getInstance().leaveQueue((Player) sender, true);

	    return true;
    }

}
