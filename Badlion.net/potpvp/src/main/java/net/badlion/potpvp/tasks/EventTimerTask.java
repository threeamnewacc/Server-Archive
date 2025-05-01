package net.badlion.potpvp.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.bukkitevents.MessageEvent;
import net.badlion.potpvp.events.Event;
import net.badlion.potpvp.exceptions.NotEnoughPlayersException;
import net.badlion.potpvp.managers.MessageManager;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class EventTimerTask extends BukkitRunnable {

    private Event event;
    private int minRemaining = 0;

    public EventTimerTask(Event event, int minRemaining) {
        this.event = event;
        this.minRemaining = minRemaining + 1; // Cuz we call this right away
    }

    @Override
    public void run() {
        if (--this.minRemaining <= 0) {
            Gberry.log("EVENT", "Starting event");
	        Bukkit.getLogger().info("######## STARTING EVENT " + this.event.getEventType() + " WITH KIT "
			        + this.event.getKitRuleSet() + " AND # PLAYERS " + this.event.getNumberInQueue());

            try {
                this.event.startGame();
            } catch (NotEnoughPlayersException e) {
                // Pass
            }

            this.cancel();
        } else {
            Bukkit.getLogger().info("Event starting soon");

            BaseComponent[] baseComponents = new ComponentBuilder(this.event.getEventType().getName() + " with kit ")
                                                     .color(net.md_5.bungee.api.ChatColor.GOLD)
                                                     .append(this.event.getKitRuleSet().getName())
                                                     .color(net.md_5.bungee.api.ChatColor.GOLD)
                                                     .append(" starts in " + this.minRemaining + (this.minRemaining == 1 ? " minute. " : " minutes. "))
                                                     .color(net.md_5.bungee.api.ChatColor.GOLD)
                                                     .append("Click to join!")
                                                     .color(net.md_5.bungee.api.ChatColor.YELLOW)
                                                     .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                                                           new ComponentBuilder("Click to join!")
                                                                                   .color(net.md_5.bungee.api.ChatColor.YELLOW)
                                                                                   .create()))
                                                     .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                                           "/joinevent " + Event.getUUIDForEvent(this.event)))
                                                     .create();

	        MessageEvent messageEvent = new MessageEvent(MessageManager.MessageType.EVENT_MESSAGES, baseComponents);
	        PotPvP.getInstance().getServer().getPluginManager().callEvent(messageEvent);
        }
    }

}
