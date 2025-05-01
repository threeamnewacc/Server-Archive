package net.badlion.gfactions.listeners;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.*;
import com.massivecraft.factions.struct.Rel;
import net.badlion.gfactions.managers.FactionManager;
import net.badlion.gfactions.GFactions;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FactionListener implements Listener {

	private GFactions plugin;

    private Map<String, Long> spamCreate = new HashMap<String, Long>();
    private Map<String, Long> spamRename = new HashMap<String, Long>();

	public FactionListener(GFactions plugin) {
		this.plugin = plugin;
	}

    @EventHandler
    public void factionCreate(FactionCreateEvent event) {
        // Stop spam
        Long lastRenameTime = this.spamCreate.get(event.getFPlayer().getPlayer().getUniqueId().toString());
        Player player = event.getFPlayer().getPlayer();
        if (player != null && !player.hasPermission("GFactions.admin") && lastRenameTime != null && lastRenameTime + (3600 * 1000) > System.currentTimeMillis()) {
            event.setCancelled(true);
            event.getFPlayer().getPlayer().sendMessage(ChatColor.RED + "You can only create factions once every hour!");
            return;
        }

        this.spamCreate.put(event.getFPlayer().getPlayer().getUniqueId().toString(), System.currentTimeMillis());
    }

    @EventHandler
    public void onFactionClaimLand(LandClaimEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        Chunk chunk = event.getLocation().getWorld().getChunkAt((int)event.getLocation().getX(), (int)event.getLocation().getZ());
        if (chunk != null) {
            if (!this.isWilderness(chunk)) {
                event.getPlayer().sendMessage(ChatColor.RED + "Cannot claim, some of the land belongs to the server.");
                event.setCancelled(true);
            }
        }
    }

    private boolean isWilderness(Chunk chunk) {
		/*  (+ z)
		 *  ^       ^>
		 *  ^     ^>
		 *  ^   ^>
		 *  ^ ^>
		 *  P > > > > > (- x)
		 *
		 * P = Location we generate
		 * Arrows represent the way the schematic is pasted
		 */

        // Check borders of 16x16 area
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                // We don't care about the middle
                if (i != 0 && i != 15 && j != 0 && j != 15)  {
                    continue;
                }

                // TODO: HARDCODE SOME Y LEVEL
                ProtectedRegion region = this.plugin.getgGuardPlugin().getProtectedRegion(chunk.getBlock(i, 70, j).getLocation(),
                                                                                                 this.plugin.getgGuardPlugin().getProtectedRegions());

                // Return false if region isn't null
                if (region != null) {
                    Bukkit.getLogger().info(region.getRegionName());
                    return false;
                }
            }
        }

        return true;
    }

	@EventHandler
	public void onPlayerJoinFaction(final FPlayerJoinEvent event) {
		if (event.getReason() == FPlayerJoinEvent.PlayerJoinReason.CREATE) {
    		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
				@Override
				public void run() {
					FactionManager.insertOrUpdateNewFaction(event.getFaction());
					FactionManager.insertOrUpdatePlayerIntoFaction(event.getFaction(), event.getFPlayer());
					FactionManager.insertIntoFactionHistory(event.getFaction(), event.getReason().name(), event.getFPlayer().getPlayer().getUniqueId().toString(), new Timestamp(new Date().getTime()));
				}
			});
		} else if (event.getReason() == FPlayerJoinEvent.PlayerJoinReason.LEADER) {
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                @Override
                public void run() {
                    FactionManager.insertOrUpdateNewFaction(event.getFaction());
                    FactionManager.insertOrUpdatePlayerIntoFaction(event.getFaction(), event.getFPlayer());
                }
            });
        } else {
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                @Override
                public void run() {
                    FactionManager.insertOrUpdatePlayerIntoFaction(event.getFaction(), event.getFPlayer());
                    FactionManager.insertIntoFactionHistory(event.getFaction(), event.getReason().name(), event.getFPlayer().getPlayer().getUniqueId().toString(), new Timestamp(new java.util.Date().getTime()));
                }
            });
        }
	}

	@EventHandler
	public void onPlayerLeaveFaction(final FPlayerLeaveEvent event) {
		if (event.getReason() == FPlayerLeaveEvent.PlayerLeaveReason.KICKED || event.getReason() == FPlayerLeaveEvent.PlayerLeaveReason.LEAVE) {
			this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
				@Override
				public void run() {
					FactionManager.insertIntoFactionHistory(event.getFaction(), event.getReason().name(), event.getFPlayer().getId(), new Timestamp(new java.util.Date().getTime()));
					FactionManager.deletePlayerFromFaction(event.getFaction(), event.getFPlayer());
				}
			});
		}
	}

	@EventHandler
	public void onPlayerRoleChangeInFaction(final FactionRoleChangeEvent event) {
		final String type;
		if (event.getOldRole() == Rel.RECRUIT && event.getNewRole() == Rel.MEMBER) {
			type = "member_promote";
		} else if (event.getOldRole() == Rel.MEMBER && event.getNewRole() == Rel.OFFICER) {
			type = "officer_promote";
		} else if (event.getOldRole() == Rel.OFFICER && event.getNewRole() == Rel.LEADER) {
			type = "leader_promote";
		} else if (event.getOldRole() == Rel.LEADER && event.getNewRole() == Rel.OFFICER) {
			type = "leader_demote";
		} else if (event.getOldRole() == Rel.OFFICER && event.getNewRole() == Rel.MEMBER) {
			type = "officer_demote";
		} else if (event.getOldRole() == Rel.MEMBER && event.getNewRole() == Rel.RECRUIT) {
			type = "member_demote";
		} else {
			type = "leader_promote";
		}

		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
			@Override
			public void run() {
				FactionManager.insertOrUpdatePlayerIntoFaction(event.getFaction(), event.getFPlayer());
				FactionManager.insertIntoFactionHistory(event.getFaction(), type, event.getFPlayer().getId(), new Timestamp(new Date().getTime()));
			}
		});
	}

	@EventHandler
	public void onFactionDisband(FactionDisbandEvent event) {
		final Faction faction = event.getFaction();
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
			@Override
			public void run() {
                // Don't delete this stuff anymore
				//FactionManager.deleteFaction(faction);
				//FactionManager.deleteFactionHistory(faction);
				//FactionManager.deleteFactionPlayers(faction);
				//FactionManager.deleteFactionRelation(faction);
				//FactionManager.deleteFactionRelationHistory(faction);
			}
		});
	}

	@EventHandler
	public void onFactionRename(final FactionRenameEvent event) {
		// Stop spam
		Long lastRenameTime = this.spamRename.get(event.getFPlayer().getPlayer().getUniqueId().toString());
        Player player = event.getFPlayer().getPlayer();
		if (player != null && !player.hasPermission("GFactions.admin") && lastRenameTime != null && lastRenameTime + (3600 * 1000) > System.currentTimeMillis()) {
			event.setCancelled(true);
            event.getFPlayer().getPlayer().sendMessage(ChatColor.RED + "You can only rename your faction once every hour!");
			return;
		}

		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
			@Override
			public void run() {
				FactionManager.insertOrUpdateNewFaction(event.getFaction());
			}
		});

		this.spamRename.put(event.getFPlayer().getPlayer().getUniqueId().toString(), System.currentTimeMillis());
	}

	@EventHandler
	public void onFactionRelationChangeEvent(final FactionRelationEvent event) {
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
			@Override
			public void run() {
				FactionManager.getAndDeleteCurrentRelationship(event.getFaction(), event.getTargetFaction());
				FactionManager.insertOrUpdateCurrentRelationship(event.getFaction(), event.getTargetFaction());
			}
		});
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {   // TODO: REMOVE THIS AFTER TESTING IS DONE
		this.spamCreate.remove(event.getPlayer().getUniqueId().toString());
	}

    @EventHandler
    public void onFactionPlayerDeath(PlayerDeathEvent event) {
        final Faction deathFaction = FPlayers.i.get(event.getEntity()).getFaction();
        final Faction killFaction;

        if (event.getEntity().getKiller() != null) {
            killFaction = FPlayers.i.get(event.getEntity().getKiller()).getFaction();
        } else {
            killFaction = null;
        }

        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                if (!deathFaction.getId().equals("0")) {
                    FactionManager.addKillDeathToFaction("deaths", deathFaction);
                }

                if (killFaction != null && !killFaction.getId().equals("0")) {
                    FactionManager.addKillDeathToFaction("kills", killFaction);
                }
            }
        });
    }

}
