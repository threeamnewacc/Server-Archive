package com.massivecraft.factions.manager;

import club.minemen.core.util.finalutil.CC;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.type.SubclaimOwner;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.material.Sign;

public class SubclaimManager {

	private static final BlockFace[] signFaces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

	public static boolean anySubclaimsAround(Block block) {
		for (BlockFace face : signFaces) {
			Block relative = block.getRelative(face);
			if (isSubclaimChest(relative)) {
				return true;
			}
			if (relative.getType().equals(Material.WALL_SIGN)) {
				org.bukkit.block.Sign sign = (org.bukkit.block.Sign) block.getState();
				if (isSubclaimSign(sign)) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean canUseSubclaim(SubclaimOwner subclaimOwner, Player player) {
		FactionPlayer factionPlayer = FPlayers.getInstance().get(player);
		if (subclaimOwner.getOwner().equals(player.getUniqueId()) || subclaimOwner.getAddedMembers().contains(player.getUniqueId())) {
			Faction faction = factionPlayer.getFaction();
			if (faction != null) {
				if (faction.getUUID().equals(subclaimOwner.getFactionId())) {
					return true;
				}
			}
		} else {
			Faction faction = factionPlayer.getFaction();
			if (faction != null) {
				if (faction.getUUID().equals(subclaimOwner.getFactionId())) {
					if (faction.isRaidable()) {
						return true;
					}
					// Officers can use any chest
					if (factionPlayer.getRole().isAtLeast(Role.MODERATOR)) {
						return true;
					}
				}
			}
		}
		return false;
	}


	public static boolean canUseSubclaim(Block subclaim, Player player) {
		SubclaimOwner subclaimOwner = getSubclaim(subclaim);
		FactionPlayer factionPlayer = FPlayers.getInstance().get(player);
		if (subclaimOwner.getOwner().equals(player.getUniqueId()) || subclaimOwner.getAddedMembers().contains(player.getUniqueId())) {
			Faction faction = factionPlayer.getFaction();
			if (faction != null) {
				if (faction.getUUID().equals(subclaimOwner.getFactionId())) {
					return true;
				}
			}
		} else {
			Faction faction = factionPlayer.getFaction();
			if (faction != null) {
				if (faction.getUUID().equals(subclaimOwner.getFactionId())) {
					if (!Board.getFactionAt(new FLocation(subclaim.getLocation())).equals(faction)) {
						return true;
					}
					if (faction.isRaidable()) {
						return true;
					}
					// Officers can use any chest
					if (factionPlayer.getRole().isAtLeast(Role.MODERATOR)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean isSubclaimChest(Block block) {
		if (block.getType() != Material.CHEST && block.getType() != Material.TRAPPED_CHEST) {
			return false;
		}
		Faction faction = Board.getFactionAt(new FLocation(block.getLocation()));

		Chest chest = (Chest) block.getState();
		if (chest.getInventory() instanceof DoubleChestInventory) {
			DoubleChestInventory dub = (DoubleChestInventory) chest.getInventory();
			if (faction.getSubclaims().containsKey(((Chest) dub.getLeftSide().getHolder()).getBlock().getLocation())) {
				return true;
			}
			if (faction.getSubclaims().containsKey(((Chest) dub.getRightSide().getHolder()).getBlock().getLocation())) {
				return true;
			}
		} else {
			if (faction.getSubclaims().containsKey(block.getLocation())) {
				return true;
			}
		}
		return false;
	}

	public static SubclaimOwner getSubclaim(Block block) {
		if (block.getType() != Material.CHEST && block.getType() != Material.TRAPPED_CHEST) {
			return null;
		}
		Faction faction = Board.getFactionAt(new FLocation(block.getLocation()));

		Chest chest = (Chest) block.getState();
		if (chest.getInventory() instanceof DoubleChestInventory) {
			DoubleChestInventory dub = (DoubleChestInventory) chest.getInventory();
			if (faction.getSubclaims().containsKey(((Chest) dub.getLeftSide().getHolder()).getBlock().getLocation())) {
				return faction.getSubclaims().get(((Chest) dub.getLeftSide().getHolder()).getBlock().getLocation());
			}
			if (faction.getSubclaims().containsKey(((Chest) dub.getRightSide().getHolder()).getBlock().getLocation())) {
				return faction.getSubclaims().get(((Chest) dub.getRightSide().getHolder()).getBlock().getLocation());
			}
		} else {
			if (faction.getSubclaims().containsKey(block.getLocation())) {
				return faction.getSubclaims().get(block.getLocation());
			}
		}
		return null;
	}

	public static boolean isSubclaimSign(org.bukkit.block.Sign sign) {
		return sign.getLine(1).equals(CC.B_RED + "Subclaim");
	}

	public void onPlaceSubclaimSign(SignChangeEvent event) {
		Sign sign = new Sign(event.getBlock().getType(), event.getBlock().getData());
		if (!sign.isWallSign()) {
			clearLines(event);
			event.getPlayer().sendFormattedMessage("{0}You must place subclaim signs on chests. Sneak and right click the a chest with a sign in your hand.", ChatColor.RED);
			return;
		}
		Block chest = event.getBlock().getRelative(sign.getAttachedFace());
		if (chest.getType() != Material.CHEST && chest.getType() != Material.TRAPPED_CHEST) {
			clearLines(event);
			event.getPlayer().sendFormattedMessage("{0}You must place subclaim signs on chests. Sneak and right click a chest with a sign in your hand.", ChatColor.RED);
			return;
		}
		FactionPlayer fplayer = FPlayers.getInstance().get(event.getPlayer());
		if (fplayer == null || fplayer.getFaction().isNone()) {
			clearLines(event);
			event.getPlayer().sendFormattedMessage("{0}You must be in a faction to create a subclaim chest.", ChatColor.RED);
			return;
		}
		if (Board.getFactionAt(new FLocation(event.getBlock().getLocation())) != fplayer.getFaction()) {
			clearLines(event);
			event.getPlayer().sendFormattedMessage("{0}You may only create subclaims in your land.", ChatColor.RED);
			return;
		}
		if (getSubclaim(chest) != null) {
			clearLines(event);
			event.getPlayer().sendFormattedMessage("{0}This chest is already a subclaim.", ChatColor.RED);
			return;
		}
		SubclaimOwner subclaimOwner = new SubclaimOwner();
		subclaimOwner.setOwner(event.getPlayer().getUniqueId());
		subclaimOwner.setFactionId(fplayer.getFaction().getUUID());
		fplayer.getFaction().getSubclaims().put(chest.getLocation(), subclaimOwner);
		event.setLine(0, "");
		event.setLine(1, CC.B_RED + "Subclaim");
		event.setLine(2, "");
		event.setLine(3, "");
	}

	private void clearLines(SignChangeEvent event) {
		for (int i = 0; i < 4; i++) {
			event.setLine(i, "");
		}
	}
}
