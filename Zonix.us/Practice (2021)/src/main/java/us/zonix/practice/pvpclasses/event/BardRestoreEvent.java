package us.zonix.practice.pvpclasses.event;

import java.beans.ConstructorProperties;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import us.zonix.practice.pvpclasses.PvPClass;

public class BardRestoreEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player player;
    private PvPClass.SavedPotion potions;

    @ConstructorProperties({"player", "potions"})
    public BardRestoreEvent(Player player, PvPClass.SavedPotion potions) {
        this.player = player;
        this.potions = potions;
    }

    public Player getPlayer() {
        return this.player;
    }

    public PvPClass.SavedPotion getPotions() {
        return this.potions;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
