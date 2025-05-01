package net.badlion.gfactions.commands;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (sender instanceof Player){
            final Player p = (Player) sender;
            switch (args[0].toLowerCase()){
                default:
                    p.sendMessage("Command not recognised. To see a list of info commands use \"/info list\".");
                    break;
                case "list" :
                    p.sendMessage("");
                    p.sendMessage("");
                    p.sendMessage(ChatColor.GOLD + "Info Command : ");
                    p.sendMessage("    me - Information about you");
                    p.sendMessage("    show <PlayerName> - Information about a player");
                    p.sendMessage("    koth - information about the koth event");
                    //staff
                    //rules
                    //appeals/reports
                    p.sendMessage("");
                    p.sendMessage("");
                case "me" :
                    if (args[1].equalsIgnoreCase("long")) {
                    p.sendMessage("");
                    p.sendMessage(ChatColor.GOLD + "HEALTH : " + ChatColor.DARK_GRAY + p.getHealth());
                    p.sendMessage(ChatColor.GOLD + "FOOD : " + ChatColor.DARK_GRAY + p.getFoodLevel());
                    p.sendMessage(ChatColor.GOLD + "SATURATION : " + ChatColor.DARK_GRAY + p.getSaturation());
                    p.sendMessage(ChatColor.GOLD + "EXHAUSTION : " + ChatColor.DARK_GRAY + p.getExhaustion());
                    p.sendMessage(ChatColor.GOLD + "LEVEL : " + ChatColor.DARK_GRAY + p.getLevel());
                    p.sendMessage(ChatColor.GOLD + "TOTAL EXPERIENCE : " + ChatColor.DARK_GRAY + p.getExp());
                    p.sendMessage(ChatColor.GOLD + "XP UNTIL LVL UP : " + ChatColor.DARK_GRAY + p.getExpToLevel());
                    p.sendMessage(ChatColor.GOLD + "LAST DAMAGED BY : " + ChatColor.DARK_GRAY + p.getLastDamageCause());
                    p.sendMessage(ChatColor.GOLD + "BED SPAWN LOCATION : " + ChatColor.DARK_GRAY + p.getBedSpawnLocation());
                    p.sendMessage(ChatColor.GOLD + "TIME ALIVE : " + ChatColor.DARK_GRAY + p.getTicksLived() * 1600 + " Minutes");
                    p.sendMessage(ChatColor.GOLD + "TIME SPENT ON FIRE : " + ChatColor.DARK_GRAY + p.getFireTicks() * 1600 + " Minutes");
                    p.sendMessage(ChatColor.GOLD + "LAST KILLER : " + ChatColor.DARK_GRAY + p.getKiller());

                    FPlayer fPlayer = FPlayers.i.get(p.getName());
                    p.sendMessage(ChatColor.GOLD + "FACTION : " + ChatColor.DARK_GRAY + fPlayer.getFaction());
                    }
                    break;
                case "show" :
                    break;
                case "koth" : // REMEMBER TO ADD SUBCOMMAND FOR COORDINATES
                    break;


            }


        } else {
            sender.sendMessage("Piss off Gberry");
        }


        return false;
    }
}
