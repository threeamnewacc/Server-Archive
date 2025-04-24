package club.minemen.practice.commands;

import club.minemen.core.CorePlugin;
import club.minemen.core.api.abstr.AbstractBukkitCallback;
import club.minemen.core.rank.Rank;
import club.minemen.core.util.cmd.CommandHandler;
import club.minemen.core.util.cmd.annotation.Flag;
import club.minemen.core.util.cmd.annotation.Param;
import club.minemen.core.util.cmd.annotation.commandTypes.BaseCommand;
import club.minemen.core.util.cmd.annotation.commandTypes.SubCommand;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.request.PremiumRequest;
import com.google.gson.JsonElement;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PremiumCommand implements CommandHandler {

	@BaseCommand(name = {"premium"}, rank = Rank.ADMIN)
	public void premiumCommand(CommandSender sender) {
		sender.sendMessage(CC.RED + "Usage: /premium <add|take|set> [args...]");
	}

	@SubCommand(baseCommand = "premium", name = {"add"}, rank = Rank.ADMIN)
	public void addCommand(CommandSender sender, @Param(name = "target") String target,
	                       @Param(name = "amount") int amount, @Flag(name = "d") boolean donated) {
		if (amount == 0) {
			sender.sendMessage(CC.RED + "Amount must be > 0.");
			return;
		}

		if (target.trim().equals("")) {
			sender.sendMessage(CC.RED + "Invalid player.");
			return;
		}

		Player player = Practice.getInstance().getServer().getPlayer(target);
		if (player != null) {
			PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
			if (playerData != null) {
				playerData.setPremiumMatchesExtra(playerData.getPremiumMatchesExtra() + amount);
				sender.sendMessage(CC.GREEN + "Gave " + amount + " Premium Matches to " + player.getName());
				player.sendMessage(CC.PRIMARY + "You received " + CC.SECONDARY + amount + " Premium Matches" +
						CC.PRIMARY + (!donated ? " from " + CC.SECONDARY + sender.getName() :
						CC.PRIMARY + ". Thanks for supporting Minemen Club") + CC.PRIMARY +
						".");
				return;
			}
		}

		PremiumRequest request = new PremiumRequest("add", target, amount);
		CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(request,
				new AbstractBukkitCallback() {
					@Override
					public void callback(JsonElement jsonElement) {
						String response = jsonElement.getAsJsonObject().get("response").getAsString();
						if (response.equals("success")) {
							sender.sendMessage(CC.GREEN + "Gave " + amount + " Premium Matches to " + target);
						} else {
							sender.sendMessage(ChatColor.RED + "There was an issue adding premium matches...");
						}
					}
				});
	}

	@SubCommand(baseCommand = "premium", name = {"take"}, rank = Rank.ADMIN)
	public void takeCommand(CommandSender sender, @Param(name = "target") String target,
	                        @Param(name = "amount") int amount) {
		if (amount == 0) {
			sender.sendMessage(CC.RED + "Amount must be > 0.");
			return;
		}

		if (target.trim().equals("")) {
			sender.sendMessage(CC.RED + "Invalid player.");
			return;
		}

		Player player = Practice.getInstance().getServer().getPlayer(target);
		if (player != null) {
			PlayerData playerData = Practice.getInstance().getPlayerManager().getPlayerData(player.getUniqueId());
			if (playerData != null) {
				playerData.setPremiumMatchesExtra(playerData.getPremiumMatchesExtra() + amount);
				sender.sendMessage(CC.GREEN + "Removed " + amount + " Premium Matches from " + player.getName());
				return;
			}
		}

		PremiumRequest request = new PremiumRequest("remove", target, amount);
		CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(request,
				new AbstractBukkitCallback() {
					@Override
					public void callback(JsonElement jsonElement) {
						String response = jsonElement.getAsJsonObject().get("response").getAsString();
						if (response.equals("success")) {
							sender.sendMessage(CC.GREEN + "Removed " + amount + " Premium Matches from " + target);
						} else {
							sender.sendMessage(ChatColor.RED + "There was an issue removing premium matches...");
						}
					}
				});
	}

	@SubCommand(baseCommand = "premium", name = "autism", rank = Rank.DEVELOPER)
	public void autism(CommandSender sender) {
		CorePlugin.getInstance()
				.runRedisCommand(jedis -> jedis.set("practice:premium:match_reset", System.currentTimeMillis() + ""));
		sender.sendMessage(ChatColor.YELLOW + "autism done");
		CorePlugin.getInstance().runRedisCommand(jedis -> sender.sendMessage(jedis.get
				("practice:premium:match_reset")));
	}

	@SubCommand(baseCommand = "premium", name = "autism2", rank = Rank.DEVELOPER)
	public void autism2(CommandSender sender) {
		CorePlugin.getInstance().runRedisCommand(jedis -> sender.sendMessage(jedis.get
				("practice:premium:match_reset")));
		sender.sendMessage(ChatColor.YELLOW + "autism2 done");

	}

}
