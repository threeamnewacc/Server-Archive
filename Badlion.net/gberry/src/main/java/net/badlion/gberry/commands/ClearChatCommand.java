package net.badlion.gberry.commands;

import com.google.common.collect.ImmutableList;
import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearChatCommand implements CommandExecutor {

	public Gberry plugin;
	public static int tickCount = 0;

	public ClearChatCommand(Gberry plugin) {
		this.plugin = plugin;
	}

    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
		final ImmutableList<Player> players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());

		// Too few people to give a shit
		if (players.size() < 20) {
			for (Player p : players) {
				for (int x = 0; x < 121; x++) {
					p.sendMessage("");
				}
			}
		}

		// More players, lets load balance
		new BukkitRunnable() {
			@Override
			public void run() {
			 	if (ClearChatCommand.tickCount >= 20) {
					ClearChatCommand.tickCount = 0;
					this.cancel();
					return;
				}

				for (int i = ClearChatCommand.tickCount * (players.size() / 20); i < (ClearChatCommand.tickCount + 1) * (players.size() / 20); i++) {
					// Got too high
					if (i > players.size() - 1) {
						this.cancel();
					}

					// Clear chat
					for (int x = 0; x < 121; x++) {
						players.get(i).sendMessage("");
					}
				}

				ClearChatCommand.tickCount++;
			}
		}.runTaskTimer(this.plugin, 0, 1);

        return true;
    }
}
