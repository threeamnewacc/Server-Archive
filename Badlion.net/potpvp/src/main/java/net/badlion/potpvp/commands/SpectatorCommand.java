package net.badlion.potpvp.commands;

import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectatorCommand extends GCommandExecutor {

    public SpectatorCommand() {
        super(1); // 1 args required
    }

    @Override
    public void onGroupCommand(Command command, String label, String[] args) {
        if (GroupStateMachine.spectatorState.contains(this.group)) {
	        if (args.length == 1) {
		        Player player = PotPvP.getInstance().getServer().getPlayerExact(args[0]);
		        if (player != null) {
			        if (player.hasPermission("badlion.kittrial") && !this.player.hasPermission("badlion.kittrial")
					        && GroupStateMachine.spectatorState.contains(PotPvP.getInstance().getPlayerGroup(player))) {
				        this.player.sendMessage(ChatColor.RED + "Cannot spectate this staff member at the moment.");
			        } else {
				        this.player.teleport(player.getLocation());
			        }
		        } else {
			        this.player.sendMessage(ChatColor.RED + "Player not found.");
		        }
	        } else if (args.length == 3) {
		        try {
			        int x = Integer.valueOf(args[0]);
			        int y = Integer.valueOf(args[1]);
			        int z = Integer.valueOf(args[2]);

			        this.player.teleport(new Location(this.player.getWorld(), x, y, z));
		        } catch (NumberFormatException e) {
			        this.player.sendMessage(ChatColor.RED + "Command usage: /sp <x> <y> <z> to teleport to coordinates.");
		        }
	        } else {
		        this.usage(this.player);
	        }
        } else {
            this.player.sendMessage(ChatColor.RED + "You must be in spectator mode to use this command.");
        }
    }

    public void usage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Command usage: /sp <player> to spectate a player.");
        sender.sendMessage(ChatColor.RED + "Command usage: /sp <x> <y> <z> to teleport to coordinates.");
    }

}
