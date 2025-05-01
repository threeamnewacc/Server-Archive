package net.badlion.survivalgames.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.survivalgames.SGPlayer;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.managers.SGPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GlobalListener implements Listener {

    @EventHandler
    public void onPlayerPreProcessCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().toLowerCase().startsWith("/help") || event.getMessage().toLowerCase().startsWith("/?") || event.getMessage().toLowerCase().startsWith("/plugin")) {
	        event.getPlayer().sendMessage("Unknown command. Type \"/help\" for help.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChunksUnload(ChunkUnloadEvent event) {
        event.setCancelled(true);
    }

    /*@EventHandler
    public void onServerCrashed(PlayerQuitEvent event) {
        if (SurvivalGames.getInstance().getServer().getOnlinePlayers().size() == 1 && SurvivalGames.getInstance().getState().ordinal() >= SurvivalGames.SGState.START_COUNTDOWN.ordinal()
                && SurvivalGames.getInstance().getState().ordinal() < SurvivalGames.SGState.END.ordinal()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
        }
    }*/

    @EventHandler
    public void onStaffJoin(final PlayerLoginEvent event) {
	    // Game started?
	    if (SurvivalGames.getInstance().getState().ordinal() >= SurvivalGames.SGState.START_COUNTDOWN.ordinal()) {
		    if (SGPlayerManager.getSGPlayer(event.getPlayer().getUniqueId()) == null) { // Uranked and new player?
			    event.setKickMessage("Match has already started.");
			    event.setResult(PlayerLoginEvent.Result.KICK_OTHER);

			    // Let staff members spectate unranked
			    if (event.getPlayer().hasPermission("badlion.staff")) {
				    event.setResult(PlayerLoginEvent.Result.ALLOWED);

				    SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(event.getPlayer().getUniqueId());
				    if (sgPlayer == null) {
					    SGPlayerManager.createSGPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
				    }

				    SurvivalGames.getInstance().getServer().getScheduler().runTaskLater(SurvivalGames.getInstance(), new Runnable() {
					    @Override
					    public void run() {
						    SGPlayerManager.updateState(event.getPlayer().getUniqueId(), SGPlayer.State.SPECTATOR);
					    }
				    }, 1);
			    }
		    }
	    }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerJoinMessage(final PlayerJoinEvent event) {
        event.getPlayer().sendMessage(ChatColor.GOLD + "==========================");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Welcome to " + ChatColor.AQUA + "Badlion Survival Games" + ChatColor.DARK_GREEN + ".");
        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Please report any issues to MasterGberry/SmellyPenguin");
        event.getPlayer().sendMessage(ChatColor.GOLD + "==========================");

	    // Only send the message if they're not famous (or staff)
	    if (!event.getPlayer().hasPermission("badlion.famous") && !event.getPlayer().hasPermission("badlion.staff")) {
		    Gberry.broadcastMessageNoBalance(SurvivalGames.SG_PREFIX + ChatColor.GOLD + event.getPlayer().getName() + ChatColor.DARK_GREEN + " has joined the server.");
	    }

		if (event.getPlayer().hasPermission("badlion.staff")) {
			SurvivalGames.getInstance().addMuteBanPerms(event.getPlayer());
		}

	    // Send disguised players
	    /*BukkitUtil.runTaskLater(new Runnable() {
		    @Override
		    public void run() {
			    for (UUID uuid : DisguiseCommand.DISGUISED_PLAYERS_PACKETS.keySet()) {
				    List<Object> packets = DisguiseCommand.DISGUISED_PLAYERS_PACKETS.get(uuid);
				    PlayerConnection connection = ((CraftPlayer) event.getPlayer()).getHandle().playerConnection;

				    try {
					    connection.sendPacket(((Packet) packets.get(0)));
					    connection.sendPacket(((Packet) packets.get(1)));
					    connection.sendPacket(((Packet) packets.get(2)));
					    connection.sendPacket(((Packet) packets.get(3)));
					    connection.sendPacket(((Packet) packets.get(4)));
				    } catch (Exception e) {
					    Gberry.log("TabList", "Error sending packet to client");
					    e.printStackTrace();
				    }
			    }
		    }
	    }, 10L);*/
    }

	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerRespawnEvent(final PlayerRespawnEvent event) {
		// Send disguised players
		/*BukkitUtil.runTaskLater(new Runnable() {
			@Override
			public void run() {
				for (UUID uuid : DisguiseCommand.DISGUISED_PLAYERS_PACKETS.keySet()) {
					List<Object> packets = DisguiseCommand.DISGUISED_PLAYERS_PACKETS.get(uuid);
					PlayerConnection connection = ((CraftPlayer) event.getPlayer()).getHandle().playerConnection;

					try {
						connection.sendPacket(((Packet) packets.get(0)));
						connection.sendPacket(((Packet) packets.get(1)));
						connection.sendPacket(((Packet) packets.get(2)));
						connection.sendPacket(((Packet) packets.get(3)));
						connection.sendPacket(((Packet) packets.get(4)));
					} catch (Exception e) {
						Gberry.log("TabList", "Error sending packet to client");
						e.printStackTrace();
					}
				}
			}
		}, 10L);*/
	}

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerQuitMessage(PlayerQuitEvent event) {
	    // Only send the message if they're not famous (or staff)
	    if (!event.getPlayer().hasPermission("badlion.famous") && !event.getPlayer().hasPermission("badlion.staff")) {
		    Gberry.broadcastMessageNoBalance(SurvivalGames.SG_PREFIX + ChatColor.GOLD + event.getPlayer().getName() + ChatColor.DARK_GREEN + " has left the server.");
	    }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (!(entity instanceof Player) && entity instanceof LivingEntity) {
                entity.remove();
            }
        }
    }

    @EventHandler
    public void onFullServerJoin(PlayerLoginEvent event) {
	    if (Bukkit.getServer().getMaxPlayers() <= Bukkit.getServer().getOnlinePlayers().size()
                    && SurvivalGames.getInstance().getState().ordinal() < SurvivalGames.SGState.START_COUNTDOWN.ordinal()) {
            if (event.getPlayer().hasPermission("badlion.donatorplus")) {
                Player lastPlayerJoined = this.getPlayerWithoutPermission("badlion.donator");

                if (lastPlayerJoined != null) {
                    lastPlayerJoined.kickPlayer("You have been removed to make room for a Donator+ to join. Donate to secure a slot.");
                    event.setResult(PlayerLoginEvent.Result.ALLOWED);
                } else {
                    lastPlayerJoined = this.getPlayerWithoutPermission("badlion.donatorplus");
                    if (lastPlayerJoined != null) {
                        lastPlayerJoined.kickPlayer("You have been removed to make room for a Donator+ to join. Become a Donator+ to secure a slot.");
                        event.setResult(PlayerLoginEvent.Result.ALLOWED);
                    } else {
                        lastPlayerJoined = this.getPlayerWithoutPermission("badlion.lion");
                        if (lastPlayerJoined != null) {
                            lastPlayerJoined.kickPlayer("You have been removed to make room for a Lion to join. Become a Lion to secure a slot.");
                            event.setResult(PlayerLoginEvent.Result.ALLOWED);
                        } else {
                            event.setKickMessage("No lesser ranks found to remove. Unable to join, server full.");
                        }
                    }
                }
            } else if (event.getPlayer().hasPermission("badlion.donator") || SurvivalGames.isInWhitelist(event.getPlayer().getName().toLowerCase())) {
                Player lastPlayerJoined = this.getPlayerWithoutPermission("badlion.donator");

                if (lastPlayerJoined != null) {
	                lastPlayerJoined.kickPlayer("You have been removed to make room for a Donator to join. Donate to secure a slot.");
                    event.setResult(PlayerLoginEvent.Result.ALLOWED);
                } else {
	                event.setKickMessage("No lesser ranks found to remove. Unable to join, server full.");
                }
            }
	    }
    }

    private Player getPlayerWithoutPermission(String permission) {
	    Set<SGPlayer> sgPlayers = SGPlayerManager.getPlayersByState(SGPlayer.State.ALIVE);

	    List<Player> kickables = new ArrayList<>();

	    for (SGPlayer sgPlayer : sgPlayers) {
			Player player = SurvivalGames.getInstance().getServer().getPlayer(sgPlayer.getUuid());
			if (permission.equals("badlion.donator")) {
				if (!player.hasPermission(permission) && !SurvivalGames.isInWhitelist(player.getName().toLowerCase()) ) {
					kickables.add(player);
				}
			} else if (!player.hasPermission(permission)){
				kickables.add(player);
			}
	    }

	    if (!kickables.isEmpty()) {
		    return kickables.get(kickables.size() - 1);
	    }

	    return null;
    }

    /*@EventHandler
    public void onPlayerJoinReduceKB(PlayerJoinEvent event) {
        event.getPlayer().setKnockbackReduction(0.20F);
    }*/

    @EventHandler
    public void onSpectatorTakeDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(((Player) event.getEntity()).getUniqueId());
            if (sgPlayer.getState() == SGPlayer.State.SPECTATOR) {
                String[] locValues = SurvivalGames.getInstance().getGame().getGWorld().getYml().getString("deathmatch_spectator_location").split(" ");
                Location spectatorLocation = new Location(Bukkit.getWorld(SurvivalGames.getInstance().getGame().getGWorld().getInternalName()), Double.parseDouble(locValues[0]), Double.parseDouble(locValues[1]), Double.parseDouble(locValues[2]), Float.parseFloat(locValues[3]), Float.parseFloat(locValues[4]));

                event.getEntity().teleport(spectatorLocation);
            }
        }
    }

	@EventHandler
	public void onPlayerExpEvent(PlayerExpChangeEvent event) {
		event.setAmount(0);
	}

	@EventHandler
	public void onPlayerFishEvent(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
		    event.setCancelled(true);
		    event.setExpToDrop(0);
        }
	}

}
