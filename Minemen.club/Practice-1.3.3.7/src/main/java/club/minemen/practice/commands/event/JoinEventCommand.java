package club.minemen.practice.commands.event;

import club.minemen.core.rank.Rank;
import club.minemen.core.util.cmd.CommandHandler;
import club.minemen.core.util.cmd.annotation.Param;
import club.minemen.core.util.cmd.annotation.commandTypes.Command;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import club.minemen.practice.events.EventState;
import club.minemen.practice.events.PracticeEvent;
import org.bukkit.entity.Player;

public class JoinEventCommand implements CommandHandler {
	private final Practice plugin = Practice.getInstance();

	@Command(name = "joinevent", rank = Rank.NORMAL, description = "Join an event.")
	public void joinEvent(Player player, @Param(name = "join") String eventName) {
		if (eventName == null) {
			return;
		}

		if (plugin.getEventManager().getByName(eventName) == null) {
			player.sendMessage(CC.RED + eventName + " doesn't exist.");
			return;
		}

		PracticeEvent event = plugin.getEventManager().getByName(eventName);
		if (event.getState() != EventState.WAITING) {
			player.sendMessage(CC.RED + "You cannot join this event!");
			return;
		}

		if (event.getPlayers().containsKey(player.getUniqueId())) {
			player.sendMessage(CC.RED + "You are already playing " + event.getName() + "!");
			return;
		}

		event.join(player);
	}
}
