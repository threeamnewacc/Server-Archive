package net.badlion.uhc.listeners.gamemodes;

import net.badlion.gberry.UnregistrableListener;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public interface GameMode extends UnregistrableListener, Listener {

    /**
     * Explains the game mode
     *
     * @return ItemStack item with meta/lore set
     */
    public ItemStack getExplanationItem();

    /**
     * Get's the author
     *
     * @return String author
     */
    public String getAuthor();

}
