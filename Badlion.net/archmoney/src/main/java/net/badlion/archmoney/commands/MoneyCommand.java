package net.badlion.archmoney.commands;

import net.badlion.archmoney.ArchMoney;
import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MoneyCommand implements CommandExecutor {
	
	private ArchMoney plugin;
	
	public MoneyCommand(ArchMoney plugin){
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		final Player player = (Player) sender;
		if (args.length == 0) {
			int da_bank = this.plugin.checkBalance(player.getUniqueId().toString());
			player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.WHITE + "Money" + ChatColor.DARK_GREEN + "] Balance: " + ChatColor.WHITE + da_bank + " Dollars");
		} else {
			final Player request = this.plugin.getServer().getPlayerExact(args[0]);

			this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
				@Override
				public void run() {
					if (request != null) {
						int da_bank = MoneyCommand.this.plugin.checkBalance(request.getUniqueId().toString());
						player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.WHITE + "Money" + ChatColor.DARK_GREEN + "] "
								+ request.getName() + "'s Balance: " + ChatColor.WHITE + da_bank + " Dollars");
					} else {
						UUID uuid = Gberry.getOfflineUUID(args[0]);

                        if (uuid == null) {
                            player.sendMessage(ChatColor.RED + "Could not find " + args[0]);
                            return;
                        }

                        String uuidString = uuid.toString();
						int da_bank = MoneyCommand.this.plugin.checkBalanceSQL(uuidString);
						player.sendMessage(ChatColor.DARK_GREEN + "[" + ChatColor.WHITE + "Money" + ChatColor.DARK_GREEN + "] "
								+ args[0] + "'s Balance: " + ChatColor.WHITE + da_bank + " Dollars");
					}
				}
			});
		}
		return true;
	}

}
