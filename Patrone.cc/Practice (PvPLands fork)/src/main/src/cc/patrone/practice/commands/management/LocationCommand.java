package cc.patrone.practice.commands.management;

import zone.potion.commands.PlayerCommand;
import zone.potion.player.rank.Rank;
import zone.potion.utils.message.CC;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.managers.LocationManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LocationCommand extends PlayerCommand {
	private final PracticePlugin plugin;

	public LocationCommand(PracticePlugin plugin) {
		super("location", Rank.ADMIN);
		this.plugin = plugin;
		setAliases("setlocation");
		setUsage(CC.RED + "Usage: /location <spawn|kiteditor>");
	}

	private static Location fixedLocationOf(Player player) {
		Location location = player.getLocation();

		location.setX(location.getBlockX() + 0.5);
		location.setY(location.getBlockY() + 3.0);
		location.setZ(location.getBlockZ() + 0.5);

		return location;
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) {
			player.sendMessage(usageMessage);
			return;
		}

		LocationManager locationManager = plugin.getLocationManager();

		switch (args[0].toLowerCase()) {
			case "spawn":
				locationManager.setSpawn(fixedLocationOf(player));
				player.sendMessage(CC.GREEN + "Set the spawn!");
				break;
			case "kiteditor":
				locationManager.setKitEditor(fixedLocationOf(player));
				player.sendMessage(CC.GREEN + "Set the kit editor spawn!");
				break;
			default:
				player.sendMessage(usageMessage);
				break;
		}
	}
}
