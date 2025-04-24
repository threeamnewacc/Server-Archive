package club.minemen.practice.commands.management;

import club.minemen.core.rank.Rank;
import club.minemen.core.util.CustomLocation;
import club.minemen.core.util.finalutil.CC;
import club.minemen.core.util.finalutil.PlayerUtil;
import club.minemen.practice.Practice;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnsCommand extends Command {
	private final Practice plugin = Practice.getInstance();

	public SpawnsCommand() {
		super("spawns");
		this.setDescription("Manage server spawns.");
		this.setUsage(CC.RED + "Usage: /spawn <subcommand>");
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player) || !PlayerUtil.testPermission(sender, Rank.ADMIN)) {
			return true;
		}
		if (args.length < 1) {
			sender.sendMessage(usageMessage);
			return true;
		}
		Player player = (Player) sender;
		switch (args[0].toLowerCase()) {
			case "spawnlocation":
				this.plugin.getSpawnManager().setSpawnLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(CC.GREEN + "Successfully set the spawn location.");
				break;
			case "spawnmin":
				this.plugin.getSpawnManager().setSpawnMin(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(CC.GREEN + "Successfully set the spawn min.");
				break;
			case "spawnmax":
				this.plugin.getSpawnManager().setSpawnMax(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(CC.GREEN + "Successfully set the spawn max.");
				break;
			case "editorlocation":
				this.plugin.getSpawnManager().setEditorLocation(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(CC.GREEN + "Successfully set the editor location.");
				break;
			case "editormin":
				this.plugin.getSpawnManager().setEditorMin(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(CC.GREEN + "Successfully set the editor min.");
				break;
			case "editormax":
				this.plugin.getSpawnManager().setEditorMax(CustomLocation.fromBukkitLocation(player.getLocation()));
				player.sendMessage(CC.GREEN + "Successfully set the editor max.");
				break;
		}
		return false;
	}
}
