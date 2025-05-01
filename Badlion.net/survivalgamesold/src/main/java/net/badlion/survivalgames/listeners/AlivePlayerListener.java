package net.badlion.survivalgames.listeners;

//import com.topcat.npclib.entity.NPC;
import net.badlion.gberry.Gberry;
import net.badlion.survivalgames.SGGame;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.managers.SGPlayerManager;
import net.badlion.survivalgames.tasks.DeathMatchCountdownTask;
import net.badlion.survivalgames.SGPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class AlivePlayerListener implements Listener {

    @EventHandler(priority=EventPriority.LAST)
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        if (SurvivalGames.getInstance().getState().ordinal() < SurvivalGames.SGState.START_COUNTDOWN.ordinal()) {
            return;
        }

        SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(event.getPlayer().getUniqueId());
        if (sgPlayer.getState() != SGPlayer.State.ALIVE) {
            event.setCancelled(true);
        } else {
            Material material = event.getBlock().getType();
            if (material == Material.LONG_GRASS || material == Material.YELLOW_FLOWER || material == Material.LEAVES || material == Material.LEAVES_2
                    || material == Material.RED_ROSE || material == Material.DOUBLE_PLANT || material == Material.VINE) {
                event.setCancelled(false);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerOpenChest(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.CHEST) {
            SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(event.getPlayer().getUniqueId());
            if (sgPlayer.getState() == SGPlayer.State.ALIVE) {
                if (!SurvivalGames.getInstance().getGame().isChestsRefilled()) {
                    sgPlayer.addChestOpened(event.getClickedBlock());
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerDied(PlayerDeathEvent event) {
        event.setDeathMessage(null);

        this.handleDeath(event.getEntity());

        Gberry.log("SG", event.getEntity().getName() + " has died");

        if (event.getEntity().getKiller() != null) {
            event.getEntity().sendMessage(ChatColor.GOLD + event.getEntity().getKiller().getPlayerListName() + ChatColor.YELLOW + " had " + ChatColor.DARK_RED + Math.round(event.getEntity().getKiller().getHealth() * 10 / 2) / 10 + " ♥ " + ChatColor.YELLOW + "when killing you.");
        }
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        //if (!SurvivalGames.RANKED_SERVER) {
        SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(event.getPlayer().getUniqueId());
        if (sgPlayer.getState() == SGPlayer.State.ALIVE && SurvivalGames.getInstance().getState().ordinal() >= SurvivalGames.SGState.START_COUNTDOWN.ordinal()) {
            Gberry.log("SG", event.getPlayer().getName() + " has quit");
            this.handleDeath(event.getPlayer());

            // Drop their shit
            for (ItemStack item : event.getPlayer().getInventory().getContents()) {
                if (item == null || item.getType() == Material.AIR) {
                    continue;
                }

                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), item);
            }

            for (ItemStack item : event.getPlayer().getInventory().getArmorContents()) {
                if (item == null || item.getType() == Material.AIR) {
                    continue;
                }

                event.getPlayer().getWorld().dropItemNaturally(event.getPlayer().getLocation(), item);
            }

            SpectatorListener.playersBeingWarpedMap.remove(event.getPlayer().getName());

        }
        //} else {

        //}
    }

    /*@EventHandler
    public void onNPCDeath(EntityDeathEvent event) {
        UUID id = SurvivalGames.getInstance().getCombatTag().getPlayerUUID(event.getEntity());
        NPC npc = SurvivalGames.getInstance().getCombatTag().npcm.getNPC(id);
        UUID uuid = SurvivalGames.getInstance().getCombatTag().npcm.getNPCIdFromEntity(event.getEntity());

        org.bukkit.Bukkit.getLogger().info("UUID is " + uuid);
    }*/



    private void handleDeath(Player player) {
        if (SurvivalGames.getInstance().getState().ordinal() >= SurvivalGames.SGState.STARTED.ordinal() && SurvivalGames.getInstance().getState().ordinal() <= SurvivalGames.SGState.DEATH_MATCH.ordinal()) {
            Gberry.log("SG", "Handling death for " + player.getName());

            SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(player.getUniqueId());

            // Handle any special stuff
            SurvivalGames.getInstance().getGame().getGameMode().handleDeath(player);

            // Nicer death messages
            this.handleDeathMessage(player, player.getKiller());

            // Track Kills
            if (player.getKiller() != null) {
                SGPlayer sgPlayerKiller = SGPlayerManager.getSGPlayer(player.getKiller().getUniqueId());
                sgPlayerKiller.addKill();
            }

            // Lightning bitch
            player.getWorld().strikeLightningEffect(player.getLocation());

            this.handleDeath(sgPlayer);
        } else if (SurvivalGames.getInstance().getState() == SurvivalGames.SGState.START_COUNTDOWN) {
            Gberry.log("SG", "Handling death v2 for " + player.getName());

            SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(player.getUniqueId());
            this.handleDeath(sgPlayer);
        }
    }

    private void handleDeath(SGPlayer sgPlayer) {
        if (SurvivalGames.getInstance().getState().ordinal() >= SurvivalGames.SGState.STARTED.ordinal() && SurvivalGames.getInstance().getState().ordinal() <= SurvivalGames.SGState.DEATH_MATCH.ordinal()) {
            if (sgPlayer.getState() == SGPlayer.State.ALIVE) {
                SurvivalGames.getInstance().getGame().handleDeath(sgPlayer);

                if (!SurvivalGames.getInstance().getGame().isDeathMatch() && SGPlayerManager.getPlayersByState(SGPlayer.State.ALIVE).size() <= SGGame.NUM_OF_PLAYERS_FOR_DEATH_MATCH) {
                    new DeathMatchCountdownTask().runTaskTimer(SurvivalGames.getInstance(), 0, 20);
                    SurvivalGames.getInstance().getGame().setDeathMatch(true);
                }

                // See if we won
                SurvivalGames.getInstance().getGame().checkForEndGame();
            }
        } else if (SurvivalGames.getInstance().getState() == SurvivalGames.SGState.START_COUNTDOWN) {
            if (sgPlayer.getState() == SGPlayer.State.ALIVE) {
                SurvivalGames.getInstance().getGame().handleDeath(sgPlayer);

                // See if we won
                SurvivalGames.getInstance().getGame().checkForEndGame();
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (SurvivalGames.getInstance().getState().ordinal() >= SurvivalGames.SGState.STARTED.ordinal()) {
            event.setRespawnLocation(event.getPlayer().getLocation());
            SGPlayerManager.updateState(event.getPlayer().getUniqueId(), SGPlayer.State.SPECTATOR);
        } else {
            SurvivalGames.getInstance().prepLobby(event.getPlayer());
        }
    }

    public void handleDeathMessage(Player player, Player killer) {
        if (player.getLastDamageCause() != null) {
            switch (player.getLastDamageCause().getCause()) {
                case BLOCK_EXPLOSION:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " just got blown the hell up");
                    break;
                case CONTACT:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " walked into a cactus whilst trying to escape " +
                                                                 ChatColor.YELLOW + killer.getPlayerListName());
                    } else {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " was pricked to death");
                    }
                    break;
                case CUSTOM:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " was killed by an unknown cause");
                    break;
                case DROWNING:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " drowned whilst trying to escape " +
                                                                 ChatColor.YELLOW + killer.getPlayerListName());
                    } else {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " drowned");
                    }
                    break;
                case ENTITY_ATTACK:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " was slain by " +
                                                                 ChatColor.YELLOW + killer.getPlayerListName());
                    }
                    break;
                case ENTITY_EXPLOSION:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " got blown the hell up by " +
                                                                 ChatColor.YELLOW + killer.getPlayerListName());
                    }
                    break;
                case FALL:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " was doomed to fall by " +
                                                                 ChatColor.YELLOW + killer.getPlayerListName());
                    } else {
                        if (player.getFallDistance() > 5) {
                            Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " fell from a high place");
                        } else {
                            Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " hit the ground too hard");
                        }
                    }
                    break;
                case FALLING_BLOCK:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " got freaking squashed by a block");
                    break;
                case FIRE:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " walked into a fire whilst fighting " +
                                                                 ChatColor.YELLOW + killer.getPlayerListName());
                    } else {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " went up in flames");
                    }
                    break;
                case FIRE_TICK:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " was burnt to a crisp whilst fighting " +
                                                                 ChatColor.YELLOW + killer.getPlayerListName());
                    } else {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " burned to death");
                    }
                    break;
                case LAVA:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED +
                                                                 " tried to swim in lava while trying to escape " + ChatColor.YELLOW + killer.getPlayerListName());
                    } else {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " tried to swim in lava");
                    }
                    break;
                case LIGHTNING:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " got lit the hell up by lightnin'");
                    break;
                case MAGIC:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED +
                                                                 " was killed by " + ChatColor.YELLOW + killer.getPlayerListName() + ChatColor.RED + " using magic");
                    } else {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " was killed by magic");
                    }
                    break;
                case POISON:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " was poisoned");
                    break;
                case PROJECTILE:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " was shot by " +
                                                                 ChatColor.YELLOW + killer.getPlayerListName());
                    } else {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " was shot");
                    }
                    break;
                case STARVATION:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " starved to death");
                    break;
                case SUFFOCATION:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " suffocated in a wall");
                    break;
                case SUICIDE:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " took his own life like a peasant");
                    break;
                case THORNS:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " killed themself by trying to kill someone LOL");
                    break;
                case VOID:
                    if (killer != null) {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED +
                                                                 " was knocked into the void by " + ChatColor.YELLOW + killer.getPlayerListName());
                    } else {
                        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " fell out of the world");
                    }
                    break;
                case WITHER:
                    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " withered away");
                    break;
            }
        } else {
            Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + player.getPlayerListName() + ChatColor.RED + " died.");
        }
    }

}
