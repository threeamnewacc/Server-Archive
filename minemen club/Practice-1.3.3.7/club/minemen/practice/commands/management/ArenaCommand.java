// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands.management;

import org.bukkit.Location;
import club.minemen.practice.arena.Arena;
import org.bukkit.plugin.Plugin;
import club.minemen.practice.runnable.ArenaCommandRunnable;
import club.minemen.core.util.CustomLocation;
import club.minemen.core.util.finalutil.PlayerUtil;
import club.minemen.core.rank.Rank;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import org.bukkit.command.Command;

public class ArenaCommand extends Command
{
    private static final String NO_ARENA;
    private final Practice plugin;
    
    public ArenaCommand() {
        super("arena");
        this.plugin = Practice.getInstance();
        this.setDescription("Manage server arenas.");
        this.setUsage(CC.RED + "Usage: /arena <subcommand> [args]");
    }
    
    public boolean execute(final CommandSender sender, final String alias, final String[] args) {
        if (!(sender instanceof Player) || !PlayerUtil.testPermission(sender, Rank.ADMIN)) {
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(this.usageMessage);
            return true;
        }
        final Player player = (Player)sender;
        final Arena arena = this.plugin.getArenaManager().getArena(args[1]);
        final String lowerCase = args[0].toLowerCase();
        switch (lowerCase) {
            case "create": {
                if (arena == null) {
                    this.plugin.getArenaManager().createArena(args[1]);
                    sender.sendMessage(CC.GREEN + "Successfully created arena " + args[1] + ".");
                    break;
                }
                sender.sendMessage(CC.RED + "That arena already exists!");
                break;
            }
            case "delete": {
                if (arena != null) {
                    this.plugin.getArenaManager().deleteArena(args[1]);
                    sender.sendMessage(CC.GREEN + "Successfully deleted arena " + args[1] + ".");
                    break;
                }
                sender.sendMessage(ArenaCommand.NO_ARENA);
                break;
            }
            case "a": {
                if (arena != null) {
                    final Location location = player.getLocation();
                    if (args.length < 3 || !args[2].equalsIgnoreCase("-e")) {
                        location.setX(location.getBlockX() + 0.5);
                        location.setY(location.getBlockY() + 3.0);
                        location.setZ(location.getBlockZ() + 0.5);
                    }
                    arena.setA(CustomLocation.fromBukkitLocation(location));
                    sender.sendMessage(CC.GREEN + "Successfully set position A for arena " + args[1] + ".");
                    break;
                }
                sender.sendMessage(ArenaCommand.NO_ARENA);
                break;
            }
            case "b": {
                if (arena != null) {
                    final Location location = player.getLocation();
                    if (args.length < 3 || !args[2].equalsIgnoreCase("-e")) {
                        location.setX(location.getBlockX() + 0.5);
                        location.setY(location.getBlockY() + 3.0);
                        location.setZ(location.getBlockZ() + 0.5);
                    }
                    arena.setB(CustomLocation.fromBukkitLocation(location));
                    sender.sendMessage(CC.GREEN + "Successfully set position B for arena " + args[1] + ".");
                    break;
                }
                sender.sendMessage(ArenaCommand.NO_ARENA);
                break;
            }
            case "min": {
                if (arena != null) {
                    arena.setMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                    sender.sendMessage(CC.GREEN + "Successfully set minimum position for arena " + args[1] + ".");
                    break;
                }
                sender.sendMessage(ArenaCommand.NO_ARENA);
                break;
            }
            case "max": {
                if (arena != null) {
                    arena.setMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                    sender.sendMessage(CC.GREEN + "Successfully set maximum position for arena " + args[1] + ".");
                    break;
                }
                sender.sendMessage(ArenaCommand.NO_ARENA);
                break;
            }
            case "disable":
            case "enable": {
                if (arena != null) {
                    arena.setEnabled(!arena.isEnabled());
                    sender.sendMessage(arena.isEnabled() ? (CC.GREEN + "Successfully enabled arena " + args[1] + ".") : (CC.RED + "Successfully disabled arena " + args[1] + "."));
                    break;
                }
                sender.sendMessage(ArenaCommand.NO_ARENA);
                break;
            }
            case "generate": {
                if (args.length == 3) {
                    final int arenas = Integer.parseInt(args[2]);
                    this.plugin.getServer().getScheduler().runTask((Plugin)this.plugin, (Runnable)new ArenaCommandRunnable(this.plugin, arena, arenas));
                    this.plugin.getArenaManager().setGeneratingArenaRunnables(this.plugin.getArenaManager().getGeneratingArenaRunnables() + 1);
                    break;
                }
                sender.sendMessage(CC.RED + "Usage: /arena generate <arena> <arenas>");
                break;
            }
            default: {
                sender.sendMessage(this.usageMessage);
                break;
            }
        }
        return true;
    }
    
    static {
        NO_ARENA = CC.RED + "That arena doesn't exist!";
    }
}
