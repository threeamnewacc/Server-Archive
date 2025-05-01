package net.badlion.gedit.commands;

import net.badlion.gedit.GEdit;
import net.badlion.gedit.util.SchematicUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class LoadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)){
            return false;
        }
        Player player = (Player) commandSender;
        try {
            File toLoad = new File(GEdit.schematics, args[0] + ".schematic");
            player.sendMessage("Trying to load " + toLoad);
            SchematicUtil.loadSession(toLoad, player);
            player.sendMessage("Schematic loaded!");
        } catch (FileNotFoundException ex){
            player.sendMessage(ChatColor.RED + "Could not find a schematic by the name " + args[0] + ".schematic");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}
