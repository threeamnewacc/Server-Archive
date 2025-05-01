package net.badlion.potpvp.bukkitevents;

import net.badlion.potpvp.rulesets.KitRuleSet;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KitLoadEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Player player;
	private KitRuleSet kitRuleSet;

	public KitLoadEvent(Player player, KitRuleSet kitRuleSet) {
		this.player = player;
		this.kitRuleSet = kitRuleSet;
	}

	public Player getPlayer() {
		return player;
	}

	public KitRuleSet getKitRuleSet() {
		return kitRuleSet;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
