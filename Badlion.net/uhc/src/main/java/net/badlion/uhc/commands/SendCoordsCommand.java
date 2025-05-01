package net.badlion.uhc.commands;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCTeam;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class SendCoordsCommand implements CommandExecutor {

    private static DecimalFormat df = new DecimalFormat("#.00");

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
	        // Did game start and is it a team game?
	        if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.STARTED && BadlionUHC.getInstance().getGameType() != UHCTeam.GameType.SOLO) {
		        Location loc = ((Player) sender).getLocation();

		        ((Player) sender).performCommand("tc " + df.format(loc.getX()) + ", " + df.format(loc.getY()) + ", " + df.format(loc.getZ()));
	        }
        }
        return true;
    }

}
