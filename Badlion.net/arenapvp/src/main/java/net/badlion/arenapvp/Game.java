package net.badlion.arenapvp;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.arenas.Arena;
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
	 * Tells mcp that the match is over and sends them back to lobby
	 */
	void mcpEndMatch(List<Team> teams, Team winner, int matchId);

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
