package net.badlion.potpvp;

import net.badlion.potpvp.arenas.Arena;
import net.badlion.potpvp.rulesets.KitRuleSet;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Game {

    /**
     * Start a game
     */
    void startGame();

    /**
     * Get a KitRuleSet
     */
    KitRuleSet getKitRuleSet();

    /**
     * Get unmodifiable list of players involved
     */
    List<Player> getPlayers();

    /**
     * Check if a player is contained in this game mode
     */
    boolean contains(Player player);


    /**
     * Some game modes have god apple cooldowns (this is nasty, idgaf)
     */
    Map<String, Long> getGodAppleCooldowns();

    /**
     * Handle a death
     */
    void handleDeath(Player player);

    /**
     * Handle when someone quits or /spawn's
     */
    boolean handleQuit(Player player, String reason);

    /**
     * Handle respawning
     */
    Location handleRespawn(Player player);

    /**
     * Store last damage for internal usage
     */
    void putLastDamage(UUID attacker, UUID defender, double damage, double finalDamage);

    /**
     * Get arena
     */
    Arena getArena();

    /**
     * Game is over
     */
    boolean isOver();

}
