package cc.patrone.practice.commands;

import zone.potion.commands.PlayerCommand;
import zone.potion.utils.message.CC;
import cc.patrone.practice.inventory.InventorySnapshot;
import cc.patrone.practice.managers.SnapshotManager;
import org.bukkit.entity.Player;

public class InventoryCommand extends PlayerCommand {
	private final SnapshotManager manager;

	public InventoryCommand(SnapshotManager manager) {
		super("inventory");
		this.manager = manager;
		setAliases("inv");
		setUsage(CC.RED + "Usage: /inv <player>");
	}

	@Override
	public void execute(Player player, String[] args) {
		if (args.length < 1) {
			player.sendMessage(usageMessage);
			return;
		}

		InventorySnapshot snapshot = manager.getSnapshot(args[0]);

		if (snapshot == null) {
			player.sendMessage(CC.RED + "Invalid inventory or the inventory snapshot has expired!");
		} else {
			snapshot.openTo(player);
		}
	}
}
