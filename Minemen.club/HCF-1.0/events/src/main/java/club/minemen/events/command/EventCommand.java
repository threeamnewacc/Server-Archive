package club.minemen.events.command;

import club.minemen.core.rank.Rank;
import club.minemen.core.util.CustomLocation;
import club.minemen.core.util.cmd.CommandHandler;
import club.minemen.core.util.cmd.annotation.Text;
import club.minemen.core.util.cmd.annotation.commandTypes.BaseCommand;
import club.minemen.core.util.cmd.annotation.commandTypes.SubCommand;
import club.minemen.core.util.finalutil.CC;
import club.minemen.events.EventsPlugin;
import club.minemen.events.event.Event;
import club.minemen.events.event.events.CaptureEvent;
import club.minemen.events.event.impl.KothEvent;
import club.minemen.events.util.region.CuboidRegion;
import com.sk89q.worldedit.bukkit.selections.Selection;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class EventCommand implements CommandHandler {

	private final EventsPlugin plugin;

	@BaseCommand(name = { "event", "events", "e" }, rank = Rank.DEVELOPER)
	public void onEvent(CommandSender sender) {
		throw new IllegalArgumentException();
	}

	@SubCommand(baseCommand = "event", name = "start")
	public void onStart(CommandSender sender, @Text(name = "event") String eventName) {
		Event event = this.plugin.getEventManager().getEvent(eventName);
		if (event == null) {
			sender.sendMessage(CC.RED + "Invalid event.");
			return;
		}

		if (event.isRunning()) {
			sender.sendMessage(CC.RED + "Event is already running.");
			return;
		}

		if (!event.canStart()) {
			sender.sendFormattedMessage("{0}This event isn''t setup properly.", CC.RED);
			return;
		}

		this.plugin.getServer().broadcastMessage(CC.GOLD + "[" + event.getName() + "] A " + event.getName() + " is now starting!");
		event.onStart();
	}

	@SubCommand(baseCommand = "event", name = "createkoth")
	public void createEvent(Player player, @Text(name = "event name") String eventName) {
		Event event = this.plugin.getEventManager().getEvent(eventName);
		if (event != null) {
			player.sendFormattedMessage("{0}There is already an event called ''{1}''.", CC.RED, event.getName());
			return;
		}

		this.plugin.getEventManager().register(new KothEvent(eventName));
		player.sendFormattedMessage("{0}You''ve created the event {1]{0}.", CC.GREEN, eventName);
	}

	@SubCommand(baseCommand = "event", name = "setregion")
	public void setRegion(Player player, @Text(name = "event") String eventName) {
		Event event = this.plugin.getEventManager().getEvent(eventName);
		if (event == null) {
			player.sendFormattedMessage("{0}There is no event called ''{1}''.", CC.RED, eventName);
			return;
		}

		if (!(event instanceof CaptureEvent)) {
			player.sendFormattedMessage("{0}The event is not a capture based event.", CC.RED);
			return;
		}

		Selection selection = this.plugin.getWorldEditPlugin().getSelection(player);
		if (selection == null) {
			player.sendFormattedMessage("{0}Please make a WorldEdit selection.", CC.RED);
			return;
		}

		CuboidRegion cuboidRegion = new CuboidRegion(CustomLocation.fromBukkitLocation(selection.getMinimumPoint()), CustomLocation.fromBukkitLocation(selection.getMaximumPoint()));
		((CaptureEvent) event).setCuboidRegion(cuboidRegion);
		player.sendFormattedMessage("{0}You''ve set the region for {1}{0}.", CC.GREEN, event.getName());
	}

}
