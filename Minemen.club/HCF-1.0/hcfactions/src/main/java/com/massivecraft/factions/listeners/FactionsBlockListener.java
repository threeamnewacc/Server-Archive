package com.massivecraft.factions.listeners;

import club.minemen.core.util.finalutil.CC;
import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.manager.SubclaimManager;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.type.SubclaimOwner;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.material.Sign;

import java.util.Map;

public class FactionsBlockListener implements Listener {

	public static boolean playerCanBuildDestroyBlock(Player player, Location location, String action, boolean justCheck) {
		String name = player.getName();
		if (Conf.playersWhoBypassAllProtection.contains(name)) {
			return true;
		}

		FactionPlayer me = FPlayers.getInstance().get(player);
		if (me.isAdminBypassing()) {
			return true;
		}

		FLocation loc = new FLocation(location);
		Faction otherFaction = Board.getFactionAt(loc);

		if (otherFaction.isNone()) {

			if (!Conf.wildernessDenyBuild || Conf.worldsNoWildernessProtection.contains(location.getWorld().getName())) {
				return true; // This is not faction territory. Use whatever you like here.
			}
			if (!justCheck) {
				me.getPlayer().sendFormattedMessage("{0}You can''t {3} in {2}{0}.", ChatColor.RED, CC.DARK_RED, Factions.getInstance()
						.getNone().getTag(me), action);
			}

			return false;
		} else if (otherFaction.isSafeZone()) {

			if (!Conf.safeZoneDenyBuild || Permission.MANAGE_SAFE_ZONE.has(player)) {
				return true;
			}

			if (!justCheck) {
				me.getPlayer().sendFormattedMessage("{0}You can''t {3} in {2}{0}.", ChatColor.RED, CC.DARK_RED, Factions.getInstance()
						.getSafeZone().getTag(me), action);
			}

			return false;
		} else if (otherFaction.isWarZone()) {

			if (!Conf.warZoneDenyBuild || Permission.MANAGE_WAR_ZONE.has(player)) {
				return true;
			}

			if (!justCheck) {
				me.getPlayer().sendFormattedMessage("{0}You can''t {3} in {2}{0}.", ChatColor.RED, CC.DARK_RED, Factions.getInstance()
						.getWarZone().getTag(me), action);
			}

			return false;
		} else if (otherFaction.isSystem()) {
			if (!justCheck) {
				me.getPlayer().sendFormattedMessage("{0}You can''t {3} in {2}{0}.", ChatColor.RED, CC.DARK_RED, otherFaction.getTag(me), action);
			}

			return false;
		}

		if (otherFaction.isRaidable()) {
			return true;
		}

		Faction myFaction = me.getFaction();
		Relation rel = myFaction.getRelationTo(otherFaction);
		boolean online = otherFaction.hasPlayersOnline();
		boolean pain = !justCheck && rel.confPainBuild(online);
		boolean deny = rel.confDenyBuild(online);

		// hurt the player for building/destroying in other territory?
		if (pain) {
			player.damage(Conf.actionDeniedPainAmount);

			if (!deny) {
				me.getPlayer().sendFormattedMessage("{0}It''s painful to try to {3} in the territory of {2}{0}.", ChatColor.RED, CC.DARK_RED, otherFaction.getTag(myFaction), action);
			}
		}

		// cancel building/destroying in other territory?
		if (deny) {
			if (!justCheck) {
				me.getPlayer().sendFormattedMessage("{0}You can''t {3} in {2}{0}.", ChatColor.RED, CC.DARK_RED, otherFaction.getTag(myFaction), action);
			}

			return false;
		}

		// Also cancel and/or cause pain if player doesn't have ownership rights for this claim
		if (Conf.ownedAreasEnabled && (Conf.ownedAreaDenyBuild || Conf.ownedAreaPainBuild) && !otherFaction.playerHasOwnershipRights(me, loc)) {
			if (!pain && Conf.ownedAreaPainBuild && !justCheck) {
				player.damage(Conf.actionDeniedPainAmount);

				if (!Conf.ownedAreaDenyBuild) {
					me.getPlayer().sendFormattedMessage("{0}It''s painful to try to {3} in in this territory, it''s owned by {2}{0}.", ChatColor.RED, CC.DARK_RED, otherFaction.getOwnerListString(loc), action);
				}
			}
			if (Conf.ownedAreaDenyBuild) {
				if (!justCheck) {
					me.getPlayer().sendFormattedMessage("{0}You can''t {3} in in this territory, it''s owned by {2}{0}.", ChatColor.RED, CC.DARK_RED, otherFaction.getOwnerListString(loc), action);
				}

				return false;
			}
		}

		Block block = location.getBlock();

		// officer+ protections
		if (me.getRole().value < Role.MODERATOR.value) {

			// officer chest
			if (FactionsPlayerListener.isOfficerChest(block)) {
				if (!justCheck) {
					me.getPlayer().sendFormattedMessage("{0}You must be an officer or leader to break officer chests.", ChatColor.RED);
				}
				return false;
			}

			// officer chest sign
			if (block.getType() == Material.WALL_SIGN) {
				if (FactionsPlayerListener.isOfficerSign((org.bukkit.block.Sign) block.getState())) {
					if (!justCheck) {
						me.getPlayer().sendFormattedMessage("{0}You must be an officer or leader to break officer chests.", ChatColor.RED);
					}
					return false;
				}
			}

			if (block.getType() == Material.BEACON) {
				if (!justCheck) {
					me.getPlayer().sendFormattedMessage("{0}You must be an officer or leader to break beacons in faction land.", ChatColor.RED);
				}
				return false;
			}

			if (block.getType() == Material.MOB_SPAWNER) {
				if (!justCheck) {
					me.getPlayer().sendFormattedMessage("{0}You must be an officer or leader to break mob spawners in faction land.", ChatColor.RED);
				}
				return false;
			}
		}
		return true;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (!event.canBuild()) {
			return;
		}

		// special case for flint&steel, which should only be prevented by DenyUsage list
		if (event.getBlockPlaced().getType() == Material.FIRE) {
			return;
		}

		if (!playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), "build", false)) {
			FactionsPlayerListener.handleExploitInteractionSpam(event.getPlayer());
			event.setCancelled(true);
		}

		if (FactionsPlayerListener.liquidsToReset.containsKey(event.getBlockPlaced().getLocation())) {
			Location liquidLoc = event.getBlockPlaced().getLocation();
			HCFactions.getInstance().getServer().getScheduler().cancelTask(FactionsPlayerListener.liquidsToReset.get(liquidLoc));
			FactionsPlayerListener.liquidsToReset.remove(liquidLoc);
		}

		FactionPlayer factionPlayer = FPlayers.getInstance().get(event.getPlayer());
		FLocation fLocation = new FLocation(event.getBlockPlaced().getLocation());

        /* Stop placing in warzone
        if (Board.isRealWarzone(fLocation)) {
            if (!factionPlayer.isAdminBypassing()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You can not build in warzone.");
                return;
            }
        }
        */
		if (event.getBlockPlaced().getType().equals(Material.CHEST) || event.getBlockPlaced().getType().equals(Material.TRAPPED_CHEST)) {
			Faction factionAt = Board.getFactionAt(new FLocation(event.getBlockPlaced().getLocation()));
			if (factionAt.isNone()) {
				if (SubclaimManager.anySubclaimsAround(event.getBlockPlaced())) {
					event.setCancelled(true);
					event.getPlayer().sendFormattedMessage("{0}You can''t place a chest next to a subclaim in {1}{0}.", ChatColor.RED, Factions.getInstance()
							.getNone().getTag(factionPlayer));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (!playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), "destroy", false)) {
			if (event.getBlock().getType().isSolid()) {
				FactionsPlayerListener.handleExploitInteractionSpam(event.getPlayer());
			}
			event.setCancelled(true);
		}

		Player player = event.getPlayer();
		Block block = event.getBlock();

		FactionPlayer factionPlayer = FPlayers.getInstance().get(player);
		FLocation fLocation = new FLocation(block.getLocation());

        /* Stop breaking in warzone
        if (Board.isRealWarzone(fLocation)) {
            if (!factionPlayer.isAdminBypassing()) {
                if (!Conf.allowBreakingInWarzoneMaterials.contains(block.getType())) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You can not build in warzone.");
                    return;
                }
            }
        }

        */
		// subclaim protection

		// Subclaim chest
		if (SubclaimManager.isSubclaimChest(block)) {
			SubclaimOwner subclaimOwner = SubclaimManager.getSubclaim(block);
			if (subclaimOwner != null) {
				Faction faction = Board.getFactionAt(new FLocation(block.getLocation()));
				if (faction == null || faction.isNone()) {
					return;
				}
				// Remove the subclaim if the land no longer belongs to the original faction
				if (!faction.getUUID().equals(subclaimOwner.getFactionId())) {
					for (Map.Entry<Location, SubclaimOwner> entry : faction.getSubclaims().entrySet()) {
						if (entry.getValue().equals(subclaimOwner)) {
							faction.getSubclaims().remove(entry.getKey());
							return;
						}
					}
					return;
				}
				if (!SubclaimManager.canUseSubclaim(subclaimOwner, player)) {
					player.sendFormattedMessage("{0]You can not break this subclaim.", ChatColor.RED);
					event.setCancelled(true);
					return;
				} else {
					Faction fPlayerFaction = factionPlayer.getFaction();
					if (fPlayerFaction != null && fPlayerFaction.getUUID().equals(subclaimOwner.getFactionId())) {
						for (Map.Entry<Location, SubclaimOwner> entry : faction.getSubclaims().entrySet()) {
							if (entry.getValue().equals(subclaimOwner)) {
								faction.getSubclaims().remove(entry.getKey());
								player.sendFormattedMessage("{0}You have removed this subclaim.", ChatColor.GREEN);
								return;
							}
						}
					}
				}
			}
			return;
		}

		// Subclaim chest sign
		if (block.getType() == Material.WALL_SIGN) {
			Sign sign = new Sign(block.getType(), block.getData());
			if (SubclaimManager.isSubclaimSign((org.bukkit.block.Sign) block.getState())) {
				Block chest = block.getRelative(sign.getAttachedFace());
				SubclaimOwner subclaimOwner = SubclaimManager.getSubclaim(chest);
				if (subclaimOwner != null) {
					Faction faction = Board.getFactionAt(new FLocation(block.getLocation()));
					if (faction == null || faction.isNone()) {
						return;
					}
					// Remove the subclaim if the land no longer belongs to the original faction
					if (!faction.getUUID().equals(subclaimOwner.getFactionId())) {
						for (Map.Entry<Location, SubclaimOwner> entry : faction.getSubclaims().entrySet()) {
							if (entry.getValue().equals(subclaimOwner)) {
								faction.getSubclaims().remove(entry.getKey());
								return;
							}
						}
						return;
					}
					if (!SubclaimManager.canUseSubclaim(subclaimOwner, player)) {
						player.sendFormattedMessage("{0}You can not break this subclaim.", ChatColor.RED);
						event.setCancelled(true);
						return;
					} else {
						Faction fPlayerFaction = factionPlayer.getFaction();
						if (fPlayerFaction != null && fPlayerFaction.getUUID().equals(subclaimOwner.getFactionId())) {
							for (Map.Entry<Location, SubclaimOwner> entry : faction.getSubclaims().entrySet()) {
								if (entry.getValue().equals(subclaimOwner)) {
									faction.getSubclaims().remove(entry.getKey());
									player.sendFormattedMessage("{0}You have removed this subclaim.", ChatColor.GREEN);
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockDamage(BlockDamageEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (event.getInstaBreak() && !playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), "destroy", false)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (!Conf.pistonProtectionThroughDenyBuild) {
			return;
		}

		Faction pistonFaction = Board.getFactionAt(new FLocation(event.getBlock()));

		// target end-of-the-line empty (air) block which is being pushed into, including if piston itself would extend into air
		Block targetBlock = event.getBlock().getRelative(event.getDirection(), event.getLength() + 1);

		// if potentially pushing into air/water/lava in another territory, we need to check it out
		if ((targetBlock.isEmpty() || targetBlock.isLiquid()) && !canPistonMoveBlock(pistonFaction, targetBlock.getLocation())) {
			event.setCancelled(true);
			return;
		}

		/*
		 * note that I originally was testing the territory of each affected block, but since I found that pistons can only push up to 12 blocks and the width of any territory is 16 blocks, it should be safe (and much more lightweight) to test only the final target block as done above
		 */
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		// if not a sticky piston, retraction should be fine
		if (event.isCancelled() || !event.isSticky() || !Conf.pistonProtectionThroughDenyBuild) {
			return;
		}

		Location targetLoc = event.getRetractLocation();

		// if potentially retracted block is just air/water/lava, no worries
		if (targetLoc.getBlock().isEmpty() || targetLoc.getBlock().isLiquid()) {
			return;
		}

		Faction pistonFaction = Board.getFactionAt(new FLocation(event.getBlock()));

		if (!canPistonMoveBlock(pistonFaction, targetLoc)) {
			event.setCancelled(true);
			return;
		}
	}

	// stop block from burning
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBurn(BlockBurnEvent event) {
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockSpread(BlockSpreadEvent event) {
		if (event.getBlock().getType() == Material.FIRE) {
			event.setCancelled(true);
		}
	}

	private boolean canPistonMoveBlock(Faction pistonFaction, Location target) {

		Faction otherFaction = Board.getFactionAt(new FLocation(target));

		if (pistonFaction == otherFaction) {
			return true;
		}

		if (otherFaction.isNone()) {
			if (!Conf.wildernessDenyBuild || Conf.worldsNoWildernessProtection.contains(target.getWorld().getName())) {
				return true;
			}

			return false;
		} else if (otherFaction.isSafeZone()) {
			if (!Conf.safeZoneDenyBuild) {
				return true;
			}

			return false;
		} else if (otherFaction.isWarZone()) {
			if (!Conf.warZoneDenyBuild) {
				return true;
			}

			return false;
		}

		Relation rel = pistonFaction.getRelationTo(otherFaction);

		if (rel.confDenyBuild(otherFaction.hasPlayersOnline())) {
			return false;
		}

		return true;
	}

	@EventHandler(ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
		if (event.getLine(0).equalsIgnoreCase("[officer]")) {
			onPlaceOfficerSign(event);
			return;
		}
		if (event.getLine(0).equalsIgnoreCase("[subclaim]")) {
			HCFactions.getInstance().subclaimManager.onPlaceSubclaimSign(event);
			return;
		}
	}

	private void onPlaceOfficerSign(SignChangeEvent event) {
		Sign sign = new Sign(event.getBlock().getType(), event.getBlock().getData());
		if (!sign.isWallSign()) {
			clearLines(event);
			event.getPlayer().sendFormattedMessage("{0}You must place officer signs on chests. Sneak and right click the a chest with a sign in your hand.", ChatColor.RED);
			return;
		}
		Block chest = event.getBlock().getRelative(sign.getAttachedFace());
		if (chest.getType() != Material.CHEST && chest.getType() != Material.TRAPPED_CHEST) {
			clearLines(event);
			event.getPlayer().sendFormattedMessage("{0}You must place officer signs on chests. Sneak and right click a chest with a sign in your hand.", ChatColor.RED);
			return;
		}
		if (SubclaimManager.isSubclaimChest(chest)) {
			clearLines(event);
			event.getPlayer().sendFormattedMessage("{0}That chest is already a subclaim, use another chest.", ChatColor.RED);
			return;
		}
		FactionPlayer fplayer = FPlayers.getInstance().get(event.getPlayer());
		if (fplayer == null || fplayer.getFaction().isNone()) {
			clearLines(event);
			event.getPlayer().sendFormattedMessage("{0}You must be in a faction to protect chests.", ChatColor.RED);
			return;
		}
		if (fplayer.getRole().value < Role.MODERATOR.value) {
			clearLines(event);
			event.getPlayer().sendFormattedMessage("{0}You must be officer or higher to protect chests.", ChatColor.RED);
			return;
		}
		if (Board.getFactionAt(new FLocation(event.getBlock().getLocation())) != fplayer.getFaction()) {
			clearLines(event);
			event.getPlayer().sendFormattedMessage("{0}You may only protect chests in your land.", ChatColor.RED);
			return;
		}
		event.setLine(0, "");
		event.setLine(1, CC.B_GREEN + "Officer");
		event.setLine(2, "");
		event.setLine(3, "");
	}

	private void clearLines(SignChangeEvent event) {
		for (int i = 0; i < 4; i++) {
			event.setLine(i, "");
		}
	}

	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent event) {
		// officer chests
		InventoryHolder holder = event.getSource().getHolder();
		if (holder instanceof Chest) {
			if (FactionsPlayerListener.hasOfficerSignAttached(((Chest) holder).getBlock())) {
				event.setCancelled(true);
				return;
			}
			if (SubclaimManager.isSubclaimChest(((Chest) holder).getBlock())) {
				event.setCancelled(true);
			}
		} else if (holder instanceof DoubleChest) {
			DoubleChest dub = (DoubleChest) holder;
			if (FactionsPlayerListener.hasOfficerSignAttached(((Chest) dub.getLeftSide()).getBlock())) {
				event.setCancelled(true);
				return;
			} else if (FactionsPlayerListener.hasOfficerSignAttached(((Chest) dub.getRightSide()).getBlock())) {
				event.setCancelled(true);
				return;
			}

			if (SubclaimManager.isSubclaimChest(((Chest) dub.getRightSide()).getBlock())) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onLiquidFlow(BlockFromToEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Location fromLoc = event.getBlock().getLocation();
		Location toLoc = event.getToBlock().getLocation();
		if (FactionsPlayerListener.liquidsToReset.containsKey(fromLoc) ||
				FactionsPlayerListener.liquidsToReset.containsKey(toLoc)) {
			event.setCancelled(true);
		}
	}
}
