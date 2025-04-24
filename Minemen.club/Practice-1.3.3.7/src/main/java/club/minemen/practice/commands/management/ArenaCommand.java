package club.minemen.practice.commands.management;

import club.minemen.core.rank.Rank;
import club.minemen.core.util.CustomLocation;
import club.minemen.core.util.finalutil.CC;
import club.minemen.core.util.finalutil.PlayerUtil;
import club.minemen.practice.Practice;
import club.minemen.practice.arena.Arena;
import club.minemen.practice.runnable.ArenaCommandRunnable;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArenaCommand extends Command {

	private static final String NO_ARENA = CC.RED + "That arena doesn't exist!";
	private final Practice plugin = Practice.getInstance();

	public ArenaCommand() {
		super("arena");
		this.setDescription("Manage server arenas.");
		this.setUsage(CC.RED + "Usage: /arena <subcommand> [args]");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player) || !PlayerUtil.testPermission(sender, Rank.ADMIN)) {
			return true;
		}
		if (args.length < 2) {
			sender.sendMessage(usageMessage);
			return true;
		}
		Player player = (Player) sender;
		Arena arena = this.plugin.getArenaManager().getArena(args[1]);

		switch (args[0].toLowerCase()) {
			case "create":
				if (arena == null) {
					this.plugin.getArenaManager().createArena(args[1]);
					sender.sendMessage(CC.GREEN + "Successfully created arena " + args[1] + ".");
				} else {
					sender.sendMessage(CC.RED + "That arena already exists!");
				}
				break;
			case "delete":
				if (arena != null) {
					this.plugin.getArenaManager().deleteArena(args[1]);
					sender.sendMessage(CC.GREEN + "Successfully deleted arena " + args[1] + ".");
				} else {
					sender.sendMessage(ArenaCommand.NO_ARENA);
				}
				break;
			case "a":
				if (arena != null) {
					Location location = player.getLocation();

					if (args.length < 3 || !args[2].equalsIgnoreCase("-e")) {
						location.setX(location.getBlockX() + 0.5D);
						location.setY(location.getBlockY() + 3.0D);
						location.setZ(location.getBlockZ() + 0.5D);
					}
					arena.setA(CustomLocation.fromBukkitLocation(location));
					sender.sendMessage(CC.GREEN + "Successfully set position A for arena " + args[1] + ".");
				} else {
					sender.sendMessage(ArenaCommand.NO_ARENA);
				}
				break;
			case "b":
				if (arena != null) {
					Location location = player.getLocation();

					if (args.length < 3 || !args[2].equalsIgnoreCase("-e")) {
						location.setX(location.getBlockX() + 0.5D);
						location.setY(location.getBlockY() + 3.0D);
						location.setZ(location.getBlockZ() + 0.5D);
					}
					arena.setB(CustomLocation.fromBukkitLocation(location));
					sender.sendMessage(CC.GREEN + "Successfully set position B for arena " + args[1] + ".");
				} else {
					sender.sendMessage(ArenaCommand.NO_ARENA);
				}
				break;
			case "min":
				if (arena != null) {
					arena.setMin(CustomLocation.fromBukkitLocation(player.getLocation()));
					sender.sendMessage(CC.GREEN + "Successfully set minimum position for arena " + args[1] + ".");
				} else {
					sender.sendMessage(ArenaCommand.NO_ARENA);
				}
				break;
			case "max":
				if (arena != null) {
					arena.setMax(CustomLocation.fromBukkitLocation(player.getLocation()));
					sender.sendMessage(CC.GREEN + "Successfully set maximum position for arena " + args[1] + ".");
				} else {
					sender.sendMessage(ArenaCommand.NO_ARENA);
				}
				break;
			case "disable":
			case "enable":
				if (arena != null) {
					arena.setEnabled(!arena.isEnabled());
					sender.sendMessage(arena.isEnabled() ? CC.GREEN + "Successfully enabled arena " + args[1] + "." :
							CC.RED + "Successfully disabled arena " + args[1] + ".");
				} else {
					sender.sendMessage(ArenaCommand.NO_ARENA);
				}
				break;
			case "generate":
				if (args.length == 3) {
					int arenas = Integer.parseInt(args[2]);
					this.plugin.getServer().getScheduler().runTask(this.plugin, new ArenaCommandRunnable(this.plugin, arena, arenas));
					this.plugin.getArenaManager().setGeneratingArenaRunnables(this.plugin.getArenaManager().getGeneratingArenaRunnables() + 1);
				} else {
					sender.sendMessage(CC.RED + "Usage: /arena generate <arena> <arenas>");
				}
				break;
			default:
				sender.sendMessage(this.usageMessage);
				break;
		}

		return true;
	}
}
