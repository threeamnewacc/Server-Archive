// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands.management;

import club.minemen.core.util.CustomLocation;
import club.minemen.core.util.finalutil.PlayerUtil;
import club.minemen.core.rank.Rank;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import org.bukkit.command.Command;

public class SpawnsCommand extends Command
{
    private final Practice plugin;
    
    public SpawnsCommand() {
        super("spawns");
        this.plugin = Practice.getInstance();
        this.setDescription("Manage server spawns.");
        this.setUsage(CC.RED + "Usage: /spawn <subcommand>");
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player) || !PlayerUtil.testPermission(sender, Rank.ADMIN)) {
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(this.usageMessage);
            return true;
        }
        final Player player = (Player)sender;
        final String lowerCase = args[0].toLowerCase();
        switch (lowerCase) {
            case "spawnlocation": {
                this.plugin.getSpawnManager().setSpawnLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the spawn location.");
                break;
            }
            case "spawnmin": {
                this.plugin.getSpawnManager().setSpawnMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the spawn min.");
                break;
            }
            case "spawnmax": {
                this.plugin.getSpawnManager().setSpawnMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the spawn max.");
                break;
            }
            case "editorlocation": {
                this.plugin.getSpawnManager().setEditorLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the editor location.");
                break;
            }
            case "editormin": {
                this.plugin.getSpawnManager().setEditorMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the editor min.");
                break;
            }
            case "editormax": {
                this.plugin.getSpawnManager().setEditorMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                player.sendMessage(CC.GREEN + "Successfully set the editor max.");
                break;
            }
        }
        return false;
    }
}
