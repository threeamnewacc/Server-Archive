package net.badlion.potpvp.states.matchmaking;

import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.potpvp.Game;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.ffaworlds.FFAWorld;
import net.badlion.potpvp.ffaworlds.SGFFAWorld;
import net.badlion.potpvp.ffaworlds.SoupFFAWorld;
import net.badlion.potpvp.helpers.KitHelper;
import net.badlion.potpvp.helpers.PlayerHelper;
import net.badlion.potpvp.managers.FFAManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public class FFAState extends GameState implements Listener {

    public static Map<String, String> mapOfLastDamage = new HashMap<>();
    public static Map<String, Long> lastDamageTime = new HashMap<>();
    public static Map<String, Long> lastKillTime = new HashMap<>();
    public static Map<String, Integer> lastMultiKill = new HashMap<>();
    public static Map<String, BukkitTask> multiKillTaskMap = new HashMap<>();

    public FFAState() {
        super("ffa_state", "they are in an FFA.", GroupStateMachine.getInstance());
    }

    @Override
    public void before(Group group, Object o) {
	    super.before(group, o);

        Game game = GameState.getGroupGame(group);
        if (!(game instanceof FFAWorld)) {
            PotPvP.getInstance().somethingBroke(group.getLeader(), group);
            return;
        }

        FFAWorld ffaWorld = (FFAWorld) game;
        ffaWorld.addPlayer(group.getLeader());

	    // Send them a message about teaming in FFAs
	    group.sendMessage(ChatColor.DARK_RED + "WARNING: " + ChatColor.DARK_AQUA + "If you team in FFAs you will " +
			    "receive a punishment (ArenaPvP Rule 7).");

        if (!(ffaWorld instanceof SGFFAWorld)) {
            KitHelper.loadKits(group, ffaWorld.getKitRuleSet());

            if (ffaWorld instanceof SoupFFAWorld) {
                // Add soup to empty inventory slots
                for (int i = 0; i < group.getLeader().getInventory().getContents().length; i++) {
                    ItemStack itemStack = group.getLeader().getInventory().getItem(i);
                    if (itemStack == null || itemStack.getType() == Material.AIR) {
                        group.getLeader().getInventory().setItem(i, new ItemStack(Material.MUSHROOM_SOUP));
                    }
                }
            }
        }

        this.giveScoreboard(group.getLeader(), ffaWorld);
    }

    @Override
    public void after(Group group) {
        FFAState.mapOfLastDamage.remove(group.getLeader().getName());
        FFAState.lastDamageTime.remove(group.getLeader().getName());
        FFAState.lastKillTime.remove(group.getLeader().getName());
        FFAState.lastMultiKill.remove(group.getLeader().getName());
        FFAState.multiKillTaskMap.remove(group.getLeader().getName());

        Game game = this.removeGroupGame(group);
        if (!(game instanceof FFAWorld)) {
            PotPvP.getInstance().somethingBroke(group.getLeader(), group);
            return;
        }

        FFAWorld ffaWorld = (FFAWorld) game;

        for (Player player : group.players()) {
            ffaWorld.removePlayer(player);
	        this.removeTeams(player);
        }
    }

    private void removeTeams(Player player) {
        ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "ks", ChatColor.GOLD + "Kill", ChatColor.GOLD + " Streak: " + ChatColor.WHITE).unregister();
        ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "dths", "", ChatColor.GOLD + "Deaths: " + ChatColor.WHITE).unregister();
        ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "kls", "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE).unregister();
        ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "sp1", "", ScoreboardUtil.SAFE_TEAM_PREFIX + " ").unregister();
        ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "tkls", ChatColor.GOLD + "Total", ChatColor.GOLD + " Kills: " + ChatColor.WHITE).unregister();
        ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "tdths", ChatColor.GOLD + "Total", ChatColor.GOLD + " Deaths: " + ChatColor.WHITE).unregister();

        player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
    }

    public void giveScoreboard(Player player, FFAWorld ffaWorld) {
        final FFAManager.FFAStats ffaStats = FFAManager.getFFAStats(player.getUniqueId(), ffaWorld.getKitRuleSet());

	    Objective objective = ScoreboardUtil.getObjective(player.getScoreboard(), "ffa", DisplaySlot.SIDEBAR, ChatColor.AQUA + "Badlion FFA");

	    Team team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "ks", ChatColor.GOLD + "Kill", ChatColor.GOLD + " Streak: " + ChatColor.WHITE);
	    team.setSuffix("0");
	    team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "kls", "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE);
	    team.setSuffix("0");
	    team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "dths", "", ChatColor.GOLD + "Deaths: " + ChatColor.WHITE);
	    team.setSuffix("0");
	    team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "tkls", ChatColor.GOLD + "Total", ChatColor.GOLD + " Kills: " + ChatColor.WHITE);
	    team.setSuffix(ffaStats.getTotalKills() + "");
	    team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "tdths", ChatColor.GOLD + "Total", ChatColor.GOLD + " Deaths: " + ChatColor.WHITE);
	    team.setSuffix(ffaStats.getTotalDeaths() + "");

	    objective.getScore(ChatColor.GOLD + " Streak: " + ChatColor.WHITE).setScore(6);
	    objective.getScore(ChatColor.GOLD + "Kills: " + ChatColor.WHITE).setScore(5);
	    objective.getScore(ChatColor.GOLD + "Deaths: " + ChatColor.WHITE).setScore(4);
	    objective.getScore(ChatColor.GOLD + " Kills: " + ChatColor.WHITE).setScore(2);
	    objective.getScore(ChatColor.GOLD + " Deaths: " + ChatColor.WHITE).setScore(1);

	    // Spacer
	    ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "sp1", "", ScoreboardUtil.SAFE_TEAM_PREFIX + " ");

	    objective.getScore(" ").setScore(3);
    }

    public Player handleScoreboardDeath(Player player, FFAWorld ffaWorld) {
        FFAState.lastDamageTime.remove(player.getName());
	    // Add to stats
	    FFAManager.FFAStats ffaStats = FFAManager.addDeath(player.getUniqueId(), ffaWorld.getKitRuleSet());

	    Team team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "dths", "", ChatColor.GOLD + "Deaths: " + ChatColor.WHITE);
	    team.setSuffix(ffaStats.getDeaths() + "");
	    team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "tdths", "", ChatColor.GOLD + " Deaths: " + ChatColor.WHITE);
	    team.setSuffix(ffaStats.getTotalDeaths() + "");
	    team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "ks", ChatColor.GOLD + "Kill", ChatColor.GOLD + " Streak: " + ChatColor.WHITE);
	    team.setSuffix("0");

        // Handle if the person who died had a kill streak
        Player killer = this.getKiller(player);
        if (killer != null) {
	        if (ffaStats.getKillstreak() >= 5) {
		        ffaWorld.sendMessage(killer.getDisplayName() + ChatColor.DARK_AQUA + " has ended " + player.getName()
				        + "'s killstreak of " + ChatColor.YELLOW + ffaStats.getKillstreak());
	        }
        }


        // If they exist...
        if (killer != null) {
            GroupStateMachine.ffaState.addKillToScoreboard(killer, ffaWorld);

            // Multikill messages
            this.handleMultiKill(killer, ffaWorld);
        }

        if (player.getLastDamageCause() != null) {
            switch (player.getLastDamageCause().getCause()) {
                case BLOCK_EXPLOSION:
                    ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " just got blown the hell up", player);
                    break;
                case CONTACT:
                    if (killer != null) {
                        ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " walked into a cactus whilst trying to escape " +
                                ChatColor.YELLOW + killer.getName() + PlayerHelper.getHeartsLeftString(ChatColor.YELLOW, killer.getHealth()), player, killer);
                    } else {
                        ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " was pricked to death", player);
                    }
                    break;
                case CUSTOM:
                    ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " was killed by an unknown cause", player);
                    break;
                case DROWNING:
                    if (killer != null) {
                        ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " drowned whilst trying to escape " +
                                ChatColor.YELLOW + killer.getName() + PlayerHelper.getHeartsLeftString(ChatColor.YELLOW, killer.getHealth()), player, killer);
                    } else {
                        ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " drowned", player);
                    }
                    break;
                case ENTITY_ATTACK:
                    if (killer != null) {
                        ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " was slain by " +
                                ChatColor.YELLOW + killer.getName() + PlayerHelper.getHeartsLeftString(ChatColor.YELLOW, killer.getHealth()), player, killer);
                    }
                    break;
                case ENTITY_EXPLOSION:
                    if (killer != null) {
                        ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " got blown the hell up by " +
                                ChatColor.YELLOW + killer.getName() + PlayerHelper.getHeartsLeftString(ChatColor.YELLOW, killer.getHealth()), player, killer);
                    }
                    break;
                case FALL:
                    if (killer != null) {
                        ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " was doomed to fall by " +
                                ChatColor.YELLOW + killer.getName() + PlayerHelper.getHeartsLeftString(ChatColor.YELLOW, killer.getHealth()), player, killer);
                    } else {
                        if (player.getFallDistance() > 5) {
                            ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " fell from a high place", player);
                        } else {
                            ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " hit the ground too hard", player);
                        }
                    }
                    break;
                case FALLING_BLOCK:
                    ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " got freaking squashed by a block", player);
                    break;
                case FIRE:
                    if (killer != null) {
                        ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " walked into a fire whilst fighting " +
                                ChatColor.YELLOW + killer.getName() + PlayerHelper.getHeartsLeftString(ChatColor.YELLOW, killer.getHealth()), player, killer);
                    } else {
                        ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " went up in flames", player);
                    }
                    break;
                case FIRE_TICK:
                    if (killer != null) {
                        ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " was burnt to a crisp whilst fighting " +
                                ChatColor.YELLOW + killer.getName() + PlayerHelper.getHeartsLeftString(ChatColor.YELLOW, killer.getHealth()), player, killer);
                    } else {
                        ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " burned to death", player);
                    }
                    break;
                case LAVA:
                    if (killer != null) {
                        ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY +
                                " tried to swim in lava while trying to escape " + ChatColor.YELLOW + killer.getName() + PlayerHelper.getHeartsLeftString(ChatColor.YELLOW, killer.getHealth()), player, killer);
                    } else {
                        ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " tried to swim in lava", player);
                    }
                    break;
                case LIGHTNING:
                    ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " got lit the hell up by lightnin'", player);
                    break;
                case MAGIC:
                    if (killer != null) {
                        ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY +
                                " was killed by " + ChatColor.YELLOW + killer.getName() + ChatColor.GRAY + " using magic" + PlayerHelper.getHeartsLeftString(ChatColor.YELLOW, killer.getHealth()), player, killer);
                    } else {
                        ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " was killed by magic", player);
                    }
                    break;
                case POISON:
                    ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " was poisoned", player);
                    break;
                case PROJECTILE:
                    if (killer != null) {
	                    // Get distance of travel
	                    int distance = (int) Math.floor(killer.getLocation().distance(player.getLocation()));

                        ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " was shot by " +
                                ChatColor.YELLOW + killer.getName() + PlayerHelper.getHeartsLeftString(ChatColor.YELLOW, killer.getHealth()) + ChatColor.GRAY + " from " + distance + " blocks", player, killer);
                    } else {
                        ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " was shot", player);
                    }
                    break;
                case STARVATION:
                    ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " starved to death", player);
                    break;
                case SUFFOCATION:
                    ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " suffocated in a wall", player);
                    break;
                case SUICIDE:
                    ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " took his own life like a peasant", player);
                    break;
                case THORNS:
                    ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " killed themself by trying to kill someone LOL", player);
                    break;
                case VOID:
                    if (killer != null) {
                        ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY +
                                " was knocked into the void by " + ChatColor.YELLOW + killer.getName() + PlayerHelper.getHeartsLeftString(ChatColor.YELLOW, killer.getHealth()), player, killer);
                    } else {
                        ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " fell out of the world", player);
                    }
                    break;
                case WITHER:
                    ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " withered away", player);
                    break;
            }
        } else {
            ffaWorld.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.GRAY + " died.", player);
        }

        FFAState.mapOfLastDamage.remove(player.getName());
        FFAState.lastMultiKill.remove(player.getName());
        FFAState.lastKillTime.remove(player.getName());

        return killer;
    }

    private Player getKiller(Player player) {
	    String lastDamage = FFAState.mapOfLastDamage.get(player.getName());
	    if (lastDamage != null) {
		    return PotPvP.getInstance().getServer().getPlayerExact(lastDamage);
	    }

        return null;
    }

    public void addKillToScoreboard(Player player, FFAWorld ffaWorld) {
	    // Add to stats
	    FFAManager.FFAStats ffaStats = FFAManager.addKill(player.getUniqueId(), ffaWorld.getKitRuleSet());

	    Team team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "kls", "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE);
	    team.setSuffix(ffaStats.getKills() + "");
	    team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "tkls", "", ChatColor.GOLD + " Kills: " + ChatColor.WHITE);
	    team.setSuffix(ffaStats.getTotalKills() + "");
	    team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "ks", ChatColor.GOLD + "Kill", ChatColor.GOLD + " Streak: " + ChatColor.WHITE);
	    team.setSuffix(ffaStats.getKillstreak() + "");

        // Kill streak hooked in here
        this.handleKillStreak(player, ffaStats.getKillstreak(), ffaWorld);
    }

    private void handleKillStreak(Player player, int killstreak, FFAWorld ffaWorld) {
	    if (killstreak != 0 && killstreak % 5 == 0) {
		    String msg = ChatColor.YELLOW + player.getName() + ChatColor.DARK_AQUA + " has gotten a killstreak of " + ChatColor.DARK_RED + killstreak;
		    ffaWorld.sendMessage(msg);
	    }
    }

	@EventHandler
	public void onPlayerDropEnderpearlEvent(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Group group = PotPvP.getInstance().getPlayerGroup(player);
		if (this.contains(group)) {
			Game game = GameState.getGroupGame(group);
			if (game instanceof SGFFAWorld) {
				if (event.getItemDrop().getItemStack().getType() == Material.ENDER_PEARL) {
					event.getItemDrop().remove();
				}
			}
		}
	}

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Group group = PotPvP.getInstance().getPlayerGroup(event.getEntity());

            if (this.contains(group)) {
                Entity damager = event.getDamager();
                Player player = null;
                if (damager instanceof Projectile) {
                    player = ((Projectile) damager).getShooter() != null && ((Projectile) damager).getShooter() instanceof Player ? (Player) ((Projectile) damager).getShooter() : null;
                } else if (damager instanceof Player) {
                    player = (Player) damager;
                }

                // Track last damage
                if (player != null) {
                    if (player.getGameMode() == GameMode.CREATIVE) { // Cancel damage if gamemode is 1
                        return;
                    }

	                // Don't track damage that we did to ourselves
	                if (player != event.getEntity()) {
		                FFAState.mapOfLastDamage.put(((Player) event.getEntity()).getName(), player.getName());
		                FFAState.lastDamageTime.put(((Player) event.getEntity()).getName(), System.currentTimeMillis());
	                }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Group group = PotPvP.getInstance().getPlayerGroup(event.getEntity());

            if (this.contains(group)) {
                FFAState.lastDamageTime.put(((Player) event.getEntity()).getName(), System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void onPlayerTakeFallDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (event.getEntity() instanceof Player) {
                Group group = PotPvP.getInstance().getPlayerGroup(event.getEntity());

                if (this.contains(group)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    private void handleMultiKill(final Player player, FFAWorld ffaWorld) {
        Long lastKillTime = FFAState.lastKillTime.get(player.getName());
        if (lastKillTime != null) {
            // They already have a multi-kill
            int kills = FFAState.lastMultiKill.get(player.getName());

            // Cancel old task
            FFAState.multiKillTaskMap.get(player.getName()).cancel();

            String msg = "";
            if (kills == 1) {
                msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "DOUBLE KILL";
            } else if (kills == 2) {
                msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "TRIPLE KILL";
            } else if (kills == 3) {
                msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "QUADRA KILL";
            } else if (kills == 4) {
                msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "PENTA KILL!!!";
            } else if (kills == 5) {
                msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "HEXA KILL!!!";
            } else if (kills == 6) {
                msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "HEPTA KILL!!!";
            } else if (kills == 7) {
                msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "OCTA KILL!!!";
            } else if (kills == 8) {
                msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "NONA KILL!!!";
            } else if (kills == 9) {
                msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "DECA KILL!!!";
            } else {
                msg += player.getDisplayName() + " has gotten a " + ChatColor.BOLD + ChatColor.GOLD + "MULTI KILL!!! (More than 10 kills)";
            }

            // PRINT THE DAMN MESSAGE
            ffaWorld.sendMessage(msg);

            // Add one kill for future messages
            FFAState.lastMultiKill.put(player.getName(), kills + 1);
        } else {
            FFAState.lastMultiKill.put(player.getName(), 1);
        }

        // Always start off a new task
        FFAState.multiKillTaskMap.put(player.getName(), PotPvP.getInstance().getServer().getScheduler().runTaskLater(PotPvP.getInstance(), new Runnable() {
            public void run() {
                FFAState.lastKillTime.remove(player.getName());
                FFAState.lastMultiKill.remove(player.getName());
            }
        }, 20 * 10)); // 10s

        // Add new last kill time
        FFAState.lastKillTime.put(player.getName(), System.currentTimeMillis());
    }

}
