package net.badlion.potpvp.states.matchmaking;

import net.badlion.gberry.Gberry;
import net.badlion.potpvp.Game;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.bukkitevents.FollowedPlayerTeleportEvent;
import net.badlion.potpvp.events.Event;
import net.badlion.potpvp.events.UHCMeetup;
import net.badlion.potpvp.ffaworlds.SGFFAWorld;
import net.badlion.potpvp.helpers.PlayerHelper;
import net.badlion.potpvp.matchmaking.Match;
import net.badlion.potpvp.matchmaking.RedRoverMatch;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.statemachine.GState;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import net.badlion.statemachine.StateMachine;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class GameState extends GState<Group> {

    private static Map<Group, Game> groupGameMap = new HashMap<>();

    public GameState() {
        super("game_state", "they are in a game.", GroupStateMachine.getInstance());

        // Should never be called
        throw new NotImplementedException();
    }

    public GameState(String name, String description, StateMachine<Group> stateMachine) {
        super(name, description, stateMachine);
    }

    @Override
    public void before(Group group, Object o) {
        super.before(group, o);

        if (!(o instanceof Game)) {
            throw new RuntimeException("Invalid object passed to game state");
        }

        this.setGroupGame(group, (Game) o);
    }

    @Override
    public void after(Group group) {
        super.after(group);

        if (this.removeGroupGame(group) == null) {
            throw new RuntimeException("Game being removed that should not be null " + group);
        }
    }

    public static Game getGroupGame(Group group) {
        return GameState.groupGameMap.get(group);
    }

    public static void debugGameMappings(Map<Player, Group> playerGroupMap) {
        List<Group> toRemove = new ArrayList<>();
        Map<Player, Group> alreadyFound = new HashMap<>();
        for (Map.Entry<Group, Game> entry : GameState.groupGameMap.entrySet()) {
            boolean allPlayersAreBugged = true;
            for (Player p : entry.getKey().players()) {
                if (alreadyFound.containsKey(p)) {
                    Bukkit.getLogger().info("Player is found multiple times " + p);
                    Group otherGroup = alreadyFound.get(p);
                    Game otherGame = GameState.groupGameMap.get(otherGroup);
                    Bukkit.getLogger().info("Current group " + entry.getKey());
                    Bukkit.getLogger().info("Other group " + otherGroup);
                    Bukkit.getLogger().info("Current game " + entry.getValue());
                    Bukkit.getLogger().info("Other game " + otherGame);
                    List<String> lines = GroupStateMachine.getInstance().debugTransitionsForElement(entry.getKey());
                    for (String line : lines) {
                        Gberry.log("LAG", line);
                    }
                    lines = GroupStateMachine.getInstance().debugTransitionsForElement(otherGroup);
                    for (String line : lines) {
                        Gberry.log("LAG", line);
                    }
                }

                alreadyFound.put(p, entry.getKey());
                if (playerGroupMap.get(p) == null) {
                    Bukkit.getLogger().info("Null player group found");
                    Bukkit.getLogger().info(p.getName() + " found linked to group " + entry.getKey().toString());
                    Bukkit.getLogger().info("Group is currently set to game " + entry.getValue().toString());

                    Bukkit.getLogger().info("Debug old group");
                    for (String line : GroupStateMachine.getInstance().debugTransitionsForElement(entry.getKey())) {
                        Bukkit.getLogger().info(line);
                    }
                } else if (playerGroupMap.get(p) != entry.getKey()) {
                    Bukkit.getLogger().info("Invalid player group found");
                    Bukkit.getLogger().info(p.getName() + " found linked to group " + entry.getKey().toString());
                    Bukkit.getLogger().info("Group is currently set to game " + entry.getValue().toString());
                    Bukkit.getLogger().info("Player is actually in group " + playerGroupMap.get(p).toString());

                    Bukkit.getLogger().info("Debug old group");
                    for (String line : GroupStateMachine.getInstance().debugTransitionsForElement(entry.getKey())) {
                        Bukkit.getLogger().info(line);
                    }

                    Bukkit.getLogger().info("Debug actual player");
                    for (String line : GroupStateMachine.getInstance().debugTransitionsForElement(playerGroupMap.get(p))) {
                        Bukkit.getLogger().info(line);
                    }
                } else {
                    allPlayersAreBugged = false;
                }
            }

            if (allPlayersAreBugged) {
                toRemove.add(entry.getKey());
            }
        }

        for (Group g : toRemove) {
            GameState.groupGameMap.remove(g);
        }
    }

    public void setGroupGame(Group group, Game game) {
        // Debug code
        if (GameState.groupGameMap.containsKey(group)) {
            try {
                throw new Exception("Unexpected group already located in map " + group);
            } catch (Exception e) {
                Game g2 = GameState.groupGameMap.get(group);

                Gberry.log("LAG", "Group: " + group + " Game: " + g2 + " Game kit: " + g2.getKitRuleSet());
                Gberry.log("LAG", "Is Over: " + g2.isOver());

                if (game instanceof Match) {
                    Gberry.log("LAG", "Alive players: " + ((Match) g2).getAlivePlayers(group) + " Ladder Type: " + ((Match) g2).ladderType);
                }

                Gberry.log("LAG", "Group: " + group + " Game: " + game + " Game kit: " + game.getKitRuleSet());
                Gberry.log("LAG", "Is Over: " + game.isOver());

                if (game instanceof Match) {
                    Gberry.log("LAG", "Alive players: " + ((Match) game).getAlivePlayers(group) + " Ladder Type: " + ((Match) game).ladderType);
                }

                List<String> lines = GroupStateMachine.getInstance().debugTransitionsForElement(group);
                for (String line : lines) {
                    Gberry.log("LAG", line);
                }

                e.printStackTrace();
            }
        }

        GameState.groupGameMap.put(group, game);
    }

    public Game removeGroupGame(Group group) {
        return GameState.groupGameMap.remove(group);
    }

    public static boolean groupIsInMatchmakingAndUsingRuleSet(Group group, KitRuleSet ruleSet) {
        return GroupStateMachine.matchMakingState.contains(group)
                       && GameState.getGroupGame(group) != null // Make sure they are actually in a game and not waiting for one
                       && GameState.getGroupGame(group).getKitRuleSet().getClass().isAssignableFrom(ruleSet.getClass());
    }

	@EventHandler
	public void onEatGoldenHeadEvent(final PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if (item.getType().equals(Material.GOLDEN_APPLE)) {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.GOLD + "Golden Head")) {
				Group group = PotPvP.getInstance().getPlayerGroup(player);
				if (GameState.getGroupGame(group) != null) {
					// Add potion effect
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100 + 4/*Number of 1/2 hearts to heal*/ * 25, 1), true);
				}
			}
		}
	}

    @EventHandler(priority= EventPriority.HIGH, ignoreCancelled=true)
    public void onPlayerItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Group group = PotPvP.getInstance().getPlayerGroup(player);
        Game game = GameState.getGroupGame(group);

        if (GroupStateMachine.ffaState.contains(group) && GameState.getGroupGame(group) instanceof SGFFAWorld) {
            // Do nothing if they are in SG FFA World with the item, let it drop
        } else if (group.isParty() && GroupStateMachine.regularMatchState.contains(group)) {
	        Material type = event.getItemDrop().getItemStack().getType();
            if (type.equals(Material.BOW) || type.equals(Material.FISHING_ROD)
                        || type.equals(Material.WOOD_SWORD) || type.equals(Material.STONE_SWORD)
                        || type.equals(Material.GOLD_SWORD) || type.equals(Material.IRON_SWORD)
                        || type.equals(Material.DIAMOND_SWORD) || type.equals(Material.WOOD_AXE)
		                || type.equals(Material.STONE_AXE) || type.equals(Material.GOLD_AXE)
		                || type.equals(Material.IRON_AXE) || type.equals(Material.DIAMOND_AXE)) {
                event.setCancelled(true);
                return;
            }

	        // 1.9 items
	        if (PotPvP.getInstance().getServer().getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
		        if (type.equals(Material.SHIELD) || type.equals(Material.ELYTRA)) {
			        event.setCancelled(true);
			        return;
		        }
	        }

            Match match = GroupStateMachine.regularMatchState.getMatchFromGroup(group);

            // Is this player alive?
            if (match.getParty1AlivePlayers().contains(player) || match.getParty2AlivePlayers().contains(player)) {
                // Let them drop it in a teams match, but track so we can remove it later
                if (match.isInProgress() && !match.isOver()) {
                    // RedRover check
                    if (match instanceof RedRoverMatch && ((RedRoverMatch) match).isSelectingFighter(player)) {
                        event.setCancelled(true);
                    }

                    match.getArena().addItemDrop(event.getItemDrop());
                }
            } else {
                event.setCancelled(true);
            }
        } else if (game instanceof UHCMeetup // Did UHCMeetup start?
                           && GroupStateMachine.getInstance().getCurrentState(group) == GroupStateMachine.uhcMeetupState) {
            if (!player.isDead()) {
                if (!game.isOver()) {
                    game.getArena().addItemDrop(event.getItemDrop());
                }
            }
        } else {
            if (this.contains(group)) {
	            Material type = event.getItemDrop().getItemStack().getType();
	            if (type.equals(Material.BOW) || type.equals(Material.FISHING_ROD)
			            || type.equals(Material.WOOD_SWORD) || type.equals(Material.STONE_SWORD)
			            || type.equals(Material.GOLD_SWORD) || type.equals(Material.IRON_SWORD)
			            || type.equals(Material.DIAMOND_SWORD) || type.equals(Material.WOOD_AXE)
			            || type.equals(Material.STONE_AXE) || type.equals(Material.GOLD_AXE)
			            || type.equals(Material.IRON_AXE) || type.equals(Material.DIAMOND_AXE)) {
		            event.setCancelled(true);
		            return;
	            }

	            // 1.9 items
	            if (PotPvP.getInstance().getServer().getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
		            if (type.equals(Material.SHIELD) || type.equals(Material.ELYTRA)) {
			            event.setCancelled(true);
			            return;
		            }
	            }
            }

            event.getItemDrop().remove();
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Group group = PotPvP.getInstance().getPlayerGroup(player);
            if (this.contains(group)) {
                Game game = GameState.getGroupGame(group);

                // Party stuff
                Entity damager = event.getDamager();
                Player damagePlayer = null;
                if (damager instanceof Projectile) {
                    damagePlayer = ((Projectile) damager).getShooter() != null && ((Projectile) damager).getShooter() instanceof Player ? (Player) ((Projectile) damager).getShooter() : null;
                } else if (damager instanceof Player) {
                    damagePlayer = (Player) damager;
                }

                // Track last damage
                if (damagePlayer != null) {
                    // Don't let allies hurt each other for some reason...
                    if (group.contains(damagePlayer)) {
                        return;
                    }

                    // Don't track damage that we did to ourselves
                    if (!damagePlayer.getUniqueId().equals(player.getUniqueId())) {
                        game.putLastDamage(damagePlayer.getUniqueId(), player.getUniqueId(), event.getDamage(), event.getFinalDamage());
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.LAST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Group group = PotPvP.getInstance().getPlayerGroup(player);

        if (this.contains(group)) {
            Game game = GameState.getGroupGame(group);

            // They "died"
            if (game != null) {
                if (game.isOver()) {
                    return;
                }

                game.handleDeath(player);
            } else {
                player.teleport(PotPvP.getInstance().getSpawnLocation());
                PlayerHelper.healPlayer(player);
            }
        }
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (event.getRespawnLocation().equals(PotPvP.getInstance().getDefaultRespawnLocation())) {
            Player player = event.getPlayer();
            Group group = PotPvP.getInstance().getPlayerGroup(player);

            if (this.contains(group)) {
                Game game = GameState.getGroupGame(group);

                // They "died"
                if (game != null) {
                    event.setRespawnLocation(game.handleRespawn(player));
                } else {
                    event.setRespawnLocation(PotPvP.getInstance().getSpawnLocation());
                }

                PotPvP.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(player, event.getRespawnLocation()));
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Group group = PotPvP.getInstance().getPlayerGroup(event.getPlayer());
        if (this.contains(group)) {
            Game game = GameState.getGroupGame(group);

            // They "died"
            if (game != null) {
                Gberry.log("EVENT2", event.getPlayer().getName() + " was in group " + group.toString());
                Gberry.log("EVENT2", event.getPlayer().getName() + " was assigned to " + game.toString());
                if (game.isOver()) {
                    // Someone might die and never re-spawn?
                    try {
                        State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(group);
                        if (currentState != null) {
                            GroupStateMachine.transitionBackToDefaultState(currentState, group);
                        }
                    } catch (IllegalStateTransitionException e) {
                        PotPvP.getInstance().somethingBroke(event.getPlayer(), group);
                    }
                    return;
                }

                game.handleQuit(event.getPlayer(), "quit");
            }

            // Always force remove them from queues now...just fuck the bugs
            // Try to remove them from Event Queues
            for (Event gameEvent : Event.getEvents().values()) {
                Gberry.log("EVENT2", "Checking event " + gameEvent.getInfo());
                if (!gameEvent.isStarted()) {
                    Gberry.log("EVENT2", "Trying to remove player " + event.getPlayer().getName() + " from " + gameEvent.getInfo());
                    if (gameEvent.removeFromQueue(event.getPlayer())) {
                        Gberry.log("EVENT2", "Successfully removed " + event.getPlayer().getName());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Group group = PotPvP.getInstance().getPlayerGroup(event.getEntity());
            if (this.contains(group)) {
                Game game = GameState.getGroupGame(group);

                if (game != null && game instanceof Match) {
                    if (System.currentTimeMillis() - ((Match) game).getStartTime().getMillis() < 2000) {
                        event.setCancelled(true);
                    } else if (game instanceof RedRoverMatch && ((RedRoverMatch) game).isSelectingFighter(((Player) event.getEntity()))) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

}
