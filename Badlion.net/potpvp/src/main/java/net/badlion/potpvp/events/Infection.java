package net.badlion.potpvp.events;

import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.rulesets.KitRuleSet;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Infection extends Event {

    public Infection(Player creator, ItemStack eventItem, KitRuleSet kitRuleSet, ItemStack[] armorContents, ItemStack[] inventoryContents) {
        super(creator, eventItem, kitRuleSet, EventType.INFECTION, ArenaManager.ArenaType.INFECTION);

	    this.armorContents = armorContents;
	    this.inventoryContents = inventoryContents;
    }

    @Override
    public void startGame() {
	    this.started = true;

	    // Update event item
	    this.handleItemsForStart();
    }

    @Override
    public void handleDeath(Player player) {

    }

    @Override
    public Location handleRespawn(Player player) {
        return PotPvP.getInstance().getDefaultRespawnLocation();
    }

    @Override
    public boolean handleQuit(Player player, String reason) {
        return false;
    }

}
