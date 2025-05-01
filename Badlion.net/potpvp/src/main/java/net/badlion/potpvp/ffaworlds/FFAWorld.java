package net.badlion.potpvp.ffaworlds;

import net.badlion.gguard.ProtectedRegion;
import net.badlion.potpvp.Game;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.arenas.Arena;
import net.badlion.potpvp.bukkitevents.FollowedPlayerTeleportEvent;
import net.badlion.potpvp.bukkitevents.MessageEvent;
import net.badlion.potpvp.helpers.KitHelper;
import net.badlion.potpvp.helpers.PlayerHelper;
import net.badlion.potpvp.inventories.lobby.FFAInventory;
import net.badlion.potpvp.inventories.spectator.SpectateFFAInventory;
import net.badlion.potpvp.managers.MessageManager;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.potpvp.states.matchmaking.FFAState;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class FFAWorld implements Game {

    public static int COMBAT_TAG_TIME = 15000;

    private static Map<KitRuleSet, FFAWorld> ffaWorlds = new LinkedHashMap<>();
    private static Map<String, FFAWorld> ffaItemWorlds = new HashMap<>();

    protected Location spawn;
	protected ItemStack ffaItem;
    protected KitRuleSet kitRuleSet;

	protected List<Player> players = new ArrayList<>();
	protected Map<UUID, UUID> lastDamage = new HashMap<>();

    public FFAWorld(ItemStack ffaItem, KitRuleSet kitRuleSet) {
	    this.ffaItem = ffaItem;
        this.kitRuleSet = kitRuleSet;

        FFAWorld.ffaWorlds.put(kitRuleSet, this);
	    FFAWorld.ffaItemWorlds.put(ffaItem.getItemMeta().getDisplayName(), this);
    }

    /**
     * Start a game
     */
    public void startGame() {
        // Always started
    }

    /**
     * Get a KitRuleSet
     */
    public KitRuleSet getKitRuleSet() {
        return this.kitRuleSet;
    }

    /**
     * Add player
     */
    public boolean addPlayer(Player player) {
	    player.setFallDistance(0F);
        player.teleport(this.spawn);
        PlayerHelper.healAndPrepPlayerForBattle(player);

	    // Update item player count
	    FFAInventory.updateFFAInventory();
	    SpectateFFAInventory.updateSpectateFFAInventory();

        PotPvP.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(player));

        return this.players.add(player);
    }

    /**
     * Remove player
     */
    public boolean removePlayer(Player player) {
	    boolean bool = this.players.remove(player);

	    if (bool) {
		    // Update item player count
		    FFAInventory.updateFFAInventory();
            SpectateFFAInventory.updateSpectateFFAInventory();
	    }

	    return bool;
    }

    /**
     * Get unmodifiable list of players involved
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(this.players);
    }

    /**
     * Check if a player is contained in this game mode
     */
    public boolean contains(Player player) {
        return this.players.contains(player);
    }

    /**
     * Some game modes have god apple cooldowns (this is nasty, idgaf)
     */
    public Map<String, Long> getGodAppleCooldowns() {
        return null; // Not supported
    }

    /**
     * Handle a death
     */
    public void handleDeath(Player player) {
        GroupStateMachine.ffaState.handleScoreboardDeath(player, this);
    }

    @Override
    public Location handleRespawn(Player player) {
        PotPvP.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(player));

        PlayerHelper.healAndPrepPlayerForBattle(player);
        KitHelper.loadKits(PotPvP.getInstance().getPlayerGroup(player), this.kitRuleSet);

        return this.spawn;
    }

    /**
     * Handle when someone quits or /spawn's
     */
    public boolean handleQuit(Player player, String reason) {
        ProtectedRegion region = PotPvP.getInstance().getgGuardPlugin().getProtectedRegion(player.getLocation(),
                PotPvP.getInstance().getgGuardPlugin().getProtectedRegions());

        if (region != null && region.getRegionName().startsWith("ffa")) {
            return true; // Let the state machine do it's thing
        } else if (reason.equals("spawn")) {
            if (FFAState.lastDamageTime.containsKey(player.getName())
                    && FFAState.lastDamageTime.get(player.getName()) + FFAWorld.COMBAT_TAG_TIME >= System.currentTimeMillis()) {

                long timeRemaining = FFAState.lastDamageTime.get(player.getName()) + FFAWorld.COMBAT_TAG_TIME - System.currentTimeMillis();

                player.sendMessage(ChatColor.RED + "Cannot use /spawn when in combat on the FFA World. You have "
		                + ((double) Math.round(((double) timeRemaining / 1000) * 10) / 10) + " seconds remaining.");

                return false; // Don't change states
            }

            player.teleport(this.spawn);

            PlayerHelper.healAndPrepPlayerForBattle(player);
            KitHelper.loadKits(PotPvP.getInstance().getPlayerGroup(player), this.kitRuleSet);

	        if (this instanceof SoupFFAWorld) {
		        // Add soup to empty inventory slots
		        for (int i = 0; i < player.getInventory().getContents().length; i++) {
			        ItemStack itemStack = player.getInventory().getItem(i);
			        if (itemStack == null || itemStack.getType() == Material.AIR) {
				        player.getInventory().setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
			        }
		        }

		        player.updateInventory();
	        }

            return false; // Don't change states
        }

        // They logged off out of safe zone, give them a death
        GroupStateMachine.ffaState.handleScoreboardDeath(player, this);
        return true;
    }

    public void sendMessage(String msg, Player... force) {
        MessageEvent messageEvent = new MessageEvent(MessageManager.MessageType.FFA_MESSAGES, msg, this.players, force);
        PotPvP.getInstance().getServer().getPluginManager().callEvent(messageEvent);
    }

    /**
     * Store last damage for internal usage
     */
    public void putLastDamage(UUID attacker, UUID defender, double damage, double finalDamage) {
        this.lastDamage.put(defender, attacker);
    }

    public static Map<KitRuleSet, FFAWorld> getFfaWorlds() {
        return FFAWorld.ffaWorlds;
    }

    public Map<UUID, UUID> getLastDamage() {
        return lastDamage;
    }

    public static FFAWorld getFFAWorld(KitRuleSet kitRuleSet) {
        return FFAWorld.ffaWorlds.get(kitRuleSet);
    }

    public static FFAWorld getFFAWorld(ItemStack itemStack) {
        return FFAWorld.ffaItemWorlds.get(itemStack.getItemMeta().getDisplayName());
    }

    public ItemStack getFFAItem() {
	    // Update player count
	    ItemStack item = this.ffaItem;
	    ItemMeta itemMeta = item.getItemMeta();
	    List<String> lore = new ArrayList<>();
	    lore.add(ChatColor.YELLOW + "Players: " + this.players.size());
	    itemMeta.setLore(lore);
	    item.setItemMeta(itemMeta);

	    return item;
    }

    /**
     * @return - Spawn location
     */
    public Location getSpawn() {
        return spawn;
    }

    /**
     * Get arena
     */
    public Arena getArena() {
        return null;
    }

    /**
     * Game is over
     */
    public boolean isOver() {
        return false;
    }

}
