package us.zonix.core.misc.command.staff;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import us.zonix.core.CorePlugin;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.Clickable;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class FreezeCommand extends BaseCommand implements Listener {

	private final Map<UUID, Location> frozenPlayers = new HashMap<>();

	public FreezeCommand() {
		CorePlugin.getInstance().getServer().getScheduler().runTaskTimer(CorePlugin.getInstance(), new FrozenMessageTask(this), 20L * 10, 20L);
		CorePlugin.getInstance().getServer().getPluginManager().registerEvents(this, CorePlugin.getInstance());
	}

	@Command(name = "freeze", aliases = {"ss", "screenshare", "fr"}, rank = Rank.TRIAL_MOD)
	public void onCommand(CommandArgs command) {

		CommandSender sender = command.getSender();
		String[] args = command.getArgs();

		if (args.length < 1) {
			Clickable clickable = new Clickable(ChatColor.RED + "Usage: /freeze <target>", ChatColor.YELLOW + "Freeze a player.", "");

			if(sender instanceof Player) {
				clickable.sendToPlayer((Player) sender);
			}

			return;
		}

		Player target = Bukkit.getPlayer(args[0]);

		if (target == null || !target.isOnline()) {
			sender.sendMessage(ChatColor.RED + "That player is not online.");
			return;
		}

		if (this.frozenPlayers.remove(target.getUniqueId()) != null) {

			for(Player online : Bukkit.getOnlinePlayers()) {
				Profile profile = Profile.getByUuidIfAvailable(online.getUniqueId());
				if(profile != null && profile.getRank().isAboveOrEqual(Rank.TRIAL_MOD)) {
					online.sendMessage(ChatColor.GREEN + target.getName() + " was unfrozen by " + (sender instanceof ConsoleCommandSender ? "CONSOLE" : sender.getName()) + ".");
				}
			}

			target.sendMessage(ChatColor.GREEN + "You have been unfrozen.");

			target.setAllowFlight(false);
			target.setFlying(false);
			this.frozenPlayers.remove(target.getUniqueId());

		} else {

			this.frozenPlayers.put(target.getUniqueId(), target.getLocation());

			for(Player online : Bukkit.getOnlinePlayers()) {
				Profile profile = Profile.getByUuidIfAvailable(online.getUniqueId());
				if(profile != null && profile.getRank().isAboveOrEqual(Rank.TRIAL_MOD)) {
					online.sendMessage(ChatColor.GREEN + target.getName() + " was frozen by " + (sender instanceof ConsoleCommandSender ? "CONSOLE" : sender.getName()) + ".");
				}
			}

			target.setAllowFlight(true);
			target.setFlying(true);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (this.frozenPlayers.remove(event.getPlayer().getUniqueId()) != null) {

			for(Player online : Bukkit.getOnlinePlayers()) {
				Profile profile = Profile.getByUuidIfAvailable(online.getUniqueId());
				if(profile != null && profile.getRank().isAboveOrEqual(Rank.TRIAL_MOD)) {
					online.sendMessage(ChatColor.GREEN + event.getPlayer().getName() + " logged out while frozen.");
				}
			}
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (this.frozenPlayers.containsKey(event.getPlayer().getUniqueId())) {
			this.frozenPlayers.put(event.getPlayer().getUniqueId(), event.getTo());
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (this.frozenPlayers.containsKey(event.getEntity().getUniqueId())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		String command = event.getMessage().split(" ")[0];

		if (command.equalsIgnoreCase("msg") || command.equalsIgnoreCase("r")
				|| command.equalsIgnoreCase("m") || command.equalsIgnoreCase("tell")
				|| command.equalsIgnoreCase("message")) {
			return;
		}

		if (this.frozenPlayers.containsKey(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChat(AsyncPlayerChatEvent event) {
		Profile profile = Profile.getByUuidIfAvailable(event.getPlayer().getUniqueId());

		if (profile == null) {
			return;
		}

		Rank rank = profile.getRank();

		if (this.frozenPlayers.containsKey(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);

			for (Player online : Bukkit.getOnlinePlayers()) {
				Profile onlineProfile = Profile.getByUuidIfAvailable(online.getUniqueId());

				if (onlineProfile != null && onlineProfile.getRank().isAboveOrEqual(Rank.TRIAL_MOD)) {
					online.sendMessage(ChatColor.DARK_RED + "(Frozen) " + ChatColor.WHITE + event.getPlayer().getName() + ": " + event.getMessage());
				}
			}

			event.getPlayer().sendMessage(event.getMessage());

			return;
		}

		if (!rank.isAboveOrEqual(Rank.TRIAL_MOD)) {
			event.getRecipients().removeIf(player -> this.frozenPlayers.containsKey(player.getUniqueId()));
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (this.frozenPlayers.containsKey(event.getDamager().getUniqueId())) {
			event.setCancelled(true);
		}
	}

	@RequiredArgsConstructor
	private class FrozenMessageTask implements Runnable {

		private final FreezeCommand command;

		@Override
		public void run() {
			Set<UUID> remove = new HashSet<>();
			this.command.frozenPlayers.forEach((uuid, location) -> {
				Player player = CorePlugin.getInstance().getServer().getPlayer(uuid);
				if (player == null) {
					remove.add(uuid);
					return;
				}

				String[] message = new String[] {
						"§8§m----------------------------------------------------",
						" ",
						"§c§lYOU HAVE BEEN FROZEN.",
						"§7Join teamspeak: §ets.zonix.us",
						"§7* Logging frozen will result in a ban.",
						"§7* You have 5 minutes.",
						" ",
						"§8§m----------------------------------------------------"
				};

				player.sendMessage(message);

				location.setPitch(player.getLocation().getPitch());
				location.setYaw(player.getLocation().getYaw());

				player.teleport(location);
			});

			for (UUID uuid : remove) {
				this.command.frozenPlayers.remove(uuid);
			}
		}

	}

}
