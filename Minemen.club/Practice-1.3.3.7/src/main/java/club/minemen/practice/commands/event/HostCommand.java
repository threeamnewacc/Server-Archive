package club.minemen.practice.commands.event;

import club.minemen.core.clickable.Clickable;
import club.minemen.core.rank.Rank;
import club.minemen.core.util.cmd.CommandHandler;
import club.minemen.core.util.cmd.annotation.Param;
import club.minemen.core.util.cmd.annotation.commandTypes.Command;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import club.minemen.practice.events.EventState;
import club.minemen.practice.events.PracticeEvent;
import org.bukkit.entity.Player;

public class HostCommand implements CommandHandler {
	private final Practice plugin = Practice.getInstance();

	@Command(name = "host", rank = Rank.PARTYMAN, description = "Host an event.")
	public void hostEvent(Player player, @Param(name = "event") String eventName) {
		if (eventName == null) {
			return;
		}

		if (plugin.getEventManager().getByName(eventName) == null) {
			player.sendMessage(CC.RED + eventName + " doesn't exist.");
			return;
		}

		PracticeEvent event = plugin.getEventManager().getByName(eventName);
		if (event.getState() != EventState.UNANNOUNCED) {
			player.sendMessage(CC.RED + "This event is already in progress!");
			return;
		}

		boolean eventBeingHosted = plugin.getEventManager().getEvents().values().stream().anyMatch(e -> e.getState() != EventState.UNANNOUNCED);
		if (eventBeingHosted) {
			player.sendMessage(CC.RED + "An event is already being hosted!");
			return;
		}

		Clickable message = new Clickable(CC.B_GOLD + player.getName() + " is hosting " + event.getName() + "! Click to join!",
				CC.GREEN + "Click to join!",
				"/joinevent " + event.getName());
		plugin.getServer().getOnlinePlayers().forEach(message::sendToPlayer);

		Practice.getInstance().getEventManager().hostEvent(event, player);
	}
}
