package us.zonix.practice.commands.management;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.CustomLocation;
import us.zonix.practice.Practice;
import us.zonix.practice.arena.Arena;
import us.zonix.practice.runnable.ArenaCommandRunnable;

public class ArenaCommand extends Command {
    private static final String NO_ARENA = ChatColor.RED + "That arena doesn't exist!";
    private final Practice plugin = Practice.getInstance();

    public ArenaCommand() {
        super("arena");
        this.setDescription("Arenas command.");
        this.setUsage(ChatColor.RED + "Usage: /arena <subcommand> [args]");
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            if (!player.hasPermission("practice.admin.arena")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
                return true;
            } else if (args.length < 2) {
                sender.sendMessage(this.usageMessage);
                return true;
            } else {
                Arena arena = this.plugin.getArenaManager().getArena(args[1]);
                String var6 = args[0].toLowerCase();
                switch (var6) {
                    case "create":
                        if (arena == null) {
                            this.plugin.getArenaManager().createArena(args[1]);
                            sender.sendMessage(ChatColor.GREEN + "Successfully created arena " + args[1] + ".");
                        } else {
                            sender.sendMessage(ChatColor.RED + "That arena already exists!");
                        }
                        break;
                    case "delete":
                        if (arena != null) {
                            this.plugin.getArenaManager().deleteArena(args[1]);
                            sender.sendMessage(ChatColor.GREEN + "Successfully deleted arena " + args[1] + ".");
                        } else {
                            sender.sendMessage(NO_ARENA);
                        }
                        break;
                    case "a":
                        if (arena != null) {
                            Location location = player.getLocation();
                            if (args.length < 3 || !args[2].equalsIgnoreCase("-e")) {
                                location.setX((double)location.getBlockX() + 0.5);
                                location.setY((double)location.getBlockY() + 3.0);
                                location.setZ((double)location.getBlockZ() + 0.5);
                            }

                            arena.setA(CustomLocation.fromBukkitLocation(location));
                            sender.sendMessage(ChatColor.GREEN + "Successfully set position A for arena " + args[1] + ".");
                        } else {
                            sender.sendMessage(NO_ARENA);
                        }
                        break;
                    case "b":
                        if (arena != null) {
                            Location location = player.getLocation();
                            if (args.length < 3 || !args[2].equalsIgnoreCase("-e")) {
                                location.setX((double)location.getBlockX() + 0.5);
                                location.setY((double)location.getBlockY() + 3.0);
                                location.setZ((double)location.getBlockZ() + 0.5);
                            }

                            arena.setB(CustomLocation.fromBukkitLocation(location));
                            sender.sendMessage(ChatColor.GREEN + "Successfully set position B for arena " + args[1] + ".");
                        } else {
                            sender.sendMessage(NO_ARENA);
                        }
                        break;
                    case "min":
                        if (arena != null) {
                            arena.setMin(CustomLocation.fromBukkitLocation(player.getLocation()));
                            sender.sendMessage(ChatColor.GREEN + "Successfully set minimum position for arena " + args[1] + ".");
                        } else {
                            sender.sendMessage(NO_ARENA);
                        }
                        break;
                    case "max":
                        if (arena != null) {
                            arena.setMax(CustomLocation.fromBukkitLocation(player.getLocation()));
                            sender.sendMessage(ChatColor.GREEN + "Successfully set maximum position for arena " + args[1] + ".");
                        } else {
                            sender.sendMessage(NO_ARENA);
                        }
                        break;
                    case "disable":
                    case "enable":
                        if (arena != null) {
                            arena.setEnabled(!arena.isEnabled());
                            sender.sendMessage(
                                arena.isEnabled()
                                    ? ChatColor.GREEN + "Successfully enabled arena " + args[1] + "."
                                    : ChatColor.RED + "Successfully disabled arena " + args[1] + "."
                            );
                        } else {
                            sender.sendMessage(NO_ARENA);
                        }
                        break;
                    case "generate":
                        if (args.length == 3) {
                            int arenas = Integer.parseInt(args[2]);
                            this.plugin.getServer().getScheduler().runTask(this.plugin, new ArenaCommandRunnable(this.plugin, arena, arenas));
                            this.plugin.getArenaManager().setGeneratingArenaRunnables(this.plugin.getArenaManager().getGeneratingArenaRunnables() + 1);
                        } else {
                            sender.sendMessage(ChatColor.RED + "Usage: /arena generate <arena> <arenas>");
                        }
                        break;
                    case "save":
                        this.plugin.getArenaManager().reloadArenas();
                        sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the arenas.");
                        break;
                    case "manage":
                        this.plugin.getArenaManager().openArenaSystemUI(player);
                        break;
                    default:
                        sender.sendMessage(this.usageMessage);
                }

                return true;
            }
        }
    }
}
