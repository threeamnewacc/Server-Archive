package club.minemen.classes.task;

import club.minemen.classes.ClassesPlugin;
import club.minemen.classes.playerclass.IPlayerClass;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @since 12/31/2017
 */
@RequiredArgsConstructor
public final class PlayerTickTask extends BukkitRunnable {

	private final ClassesPlugin plugin;

	@Override
	public void run() {
		this.plugin.getServer().getOnlinePlayers().forEach(player -> {
			IPlayerClass playerClass = this.plugin.getClassManager().getPlayersClass(player);

			if (playerClass != null) {
				if (!playerClass.hasClassEquipped(player)) {
					playerClass.onDequip(player);
					this.plugin.getClassManager().setPlayersClass(player, null);
					return;
				}

				playerClass.onTick(player);
			} else {
				for (IPlayerClass availableClass : this.plugin.getClassManager().getClasses()) {
					if (availableClass.hasClassEquipped(player)) {
						this.plugin.getClassManager().setPlayersClass(player, availableClass);
						availableClass.onEquip(player);
						availableClass.onTick(player);
						break;
					}
				}
			}

			this.plugin.getEffectManager().tick(player);
		});
	}
}
