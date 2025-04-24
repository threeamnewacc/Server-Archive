package com.massivecraft.factions.listeners;

import club.minemen.core.util.finalutil.CC;
import club.minemen.hcfactions.HCFactions;
import club.minemen.hcfactions.request.DataRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.manager.SubclaimManager;
import com.massivecraft.factions.menu.player.SubclaimAuthMenu;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.type.HomeTask;
import com.massivecraft.factions.type.StuckTask;
import com.massivecraft.factions.type.SubclaimOwner;
import com.massivecraft.factions.zcore.util.TextUtil;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.NumberConversions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class FactionsPlayerListener implements Listener {

	private static final BlockFace[] signFaces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
	private static final BlockFace[] blockFaces = { BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.DOWN, BlockFace.UP };
	public static Map<Location, Integer> liquidsToReset = new HashMap<>();
	// for handling people who repeatedly spam attempts to open a door (or similar) in another faction's territory
	private static Map<String, InteractAttemptSpam> interactSpammers = new HashMap<String, InteractAttemptSpam>();
	private final Random random;
	public HCFactions plugin;

	public FactionsPlayerListener(HCFactions plugin) {
		this.plugin = plugin;
		this.random = new Random();
	}

	public static void handleExploitInteractionSpam(Player player) {
		if (Conf.handleExploitInteractionSpam) {
			String name = player.getName();
			InteractAttemptSpam attempt = interactSpammers.get(name);
			if (attempt == null) {
				attempt = new InteractAttemptSpam();
				interactSpammers.put(name, attempt);
			}
			int count = attempt.increment();
			if (count >= 5) {
				FactionPlayer me = FPlayers.getInstance().get(player);
				me.msg("<b>Ouch, that is starting to hurt. You should give it a rest.");
				player.damage(NumberConversions.floor((double) count / 4));
			}
		}
	}

	public static boolean playerCanUseItemHere(Player player, Location location, Material material, boolean justCheck) {
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

		if (!Conf.territoryDenyUseageMaterials.contains(material)) {
			return true; // Item isn't one we're preventing for online factions.
		}

		if (otherFaction.isNone()) {
			if (!Conf.wildernessDenyUseage || Conf.worldsNoWildernessProtection.contains(location.getWorld().getName())) {
				return true; // This is not faction territory. Use whatever you like here.
			}
			if (!justCheck) {
				me.getPlayer().sendFormattedMessage("{0}You can''t use {1}{3}{0} in {2}{0}.", ChatColor.RED, CC.DARK_RED, Factions.getInstance()
						.getNone().getTag(me), TextUtil.getMaterialName(material));
			}

			return false;
		} else if (otherFaction.isSafeZone()) {
			if (!Conf.safeZoneDenyUseage || Permission.MANAGE_SAFE_ZONE.has(player)) {
				return true;
			}

			if (!justCheck) {
				me.getPlayer().sendFormattedMessage("{0}You can''t use {1}{3}{0} in {2}{0}.", ChatColor.RED, CC.DARK_RED, Factions.getInstance()
						.getSafeZone().getTag(me), TextUtil.getMaterialName(material));
			}

			return false;
		} else if (otherFaction.isWarZone()) {
			if (!Conf.warZoneDenyUseage || Permission.MANAGE_WAR_ZONE.has(player)) {
				return true;
			}

			if (!justCheck) {
				me.getPlayer().sendFormattedMessage("{0}You can''t use {1}{3}{0} in {2}{0}.", ChatColor.RED, CC.DARK_RED, Factions.getInstance()
						.getWarZone().getTag(me), TextUtil.getMaterialName(material));
			}

			return false;
		} else if (otherFaction.isSystem()) {
			if (!justCheck) {
				me.getPlayer().sendFormattedMessage("{0}You can''t use {1}{3}{0} in {2}{0}.", ChatColor.RED, CC.DARK_RED, otherFaction.getTag(me), TextUtil.getMaterialName(material));
			}

			return false;
		}

		if (otherFaction.isRaidable()) {
			return true;
		}

		Faction myFaction = me.getFaction();
		Relation rel = myFaction.getRelationTo(otherFaction);

		// Cancel if we are not in our own territory
		if (rel.confDenyUseage()) {
			if (!justCheck) {
				me.msg("<b>You can't use <h>%s<b> in the territory of <h>%s<b>.", TextUtil.getMaterialName(material), otherFaction.getTag(myFaction));
			}

			return false;
		}

		// Also cancel if player doesn't have ownership rights for this claim
		if (Conf.ownedAreasEnabled && Conf.ownedAreaDenyUseage && !otherFaction.playerHasOwnershipRights(me, loc)) {
			if (!justCheck) {
				me.msg("<b>You can't use <h>%s<b> in this territory, it is owned by: %s<b>.", TextUtil.getMaterialName(material), otherFaction.getOwnerListString(loc));
			}

			return false;
		}

		return true;
	}

	public static boolean canPlayerUseBlock(Player player, Block block, boolean justCheck, Action action) {
		String name = player.getName();
		if (Conf.playersWhoBypassAllProtection.contains(name)) {
			return true;
		}

		FactionPlayer me = FPlayers.getInstance().get(player);
		if (me.isAdminBypassing()) {
			return true;
		}

		Material material = block.getType();
		FLocation loc = new FLocation(block);
		Faction otherFaction = Board.getFactionAt(loc);

		// treat trampling crops as build
		if (action == Action.PHYSICAL && material == Material.SOIL) {
			return FactionsBlockListener.playerCanBuildDestroyBlock(player, block.getLocation(), "", true);
		}

		// no door/chest/whatever protection in wilderness, war zones, or safe zones
		if (!otherFaction.isNormal()) {
			return true;
		}

		if (otherFaction.isRaidable()) {
			return true;
		}

		if (action == Action.PHYSICAL) {
			if (!Conf.territoryProtectedMaterialsPhysical.contains(material)) {
				return true;
			}
		} else {
			if (!Conf.territoryProtectedMaterials.contains(material)) {
				return true;
			}
		}

		Faction myFaction = me.getFaction();
		Relation rel = myFaction.getRelationTo(otherFaction);

		// You may use any block unless it is another faction's territory...
		if (rel.isNeutral() || (rel.isAlly() && Conf.territoryAllyProtectMaterials)) {
			if (!justCheck) {
				me.msg("<b>You can't %s <h>%s<b> in the territory of <h>%s<b>.", (material == Material.SOIL ? "trample" : "use"), TextUtil.getMaterialName(material), otherFaction.getTag(myFaction));
			}

			return false;
		}

		// Also cancel if player doesn't have ownership rights for this claim
		if (Conf.ownedAreasEnabled && Conf.ownedAreaProtectMaterials && !otherFaction.playerHasOwnershipRights(me, loc)) {
			if (!justCheck) {
				me.msg("<b>You can't use <h>%s<b> in this territory, it is owned by: %s<b>.", TextUtil.getMaterialName(material), otherFaction.getOwnerListString(loc));
			}

			return false;
		}

		// officer chests
		if (me.getRole().value < Role.MODERATOR.value && isOfficerChest(block)) {
			me.msg("<b>You must be officer or leader to open that.");
			return false;
		}

		if (SubclaimManager.isSubclaimChest(block) && !SubclaimManager.canUseSubclaim(block, player)) {
			me.msg("<b>You can not use that subclaim.");
			return false;
		}

		return true;
	}

	public static boolean preventCommand(String fullCmd, Player player) {
		if ((Conf.territoryNeutralDenyCommands.isEmpty() && Conf.permanentFactionMemberDenyCommands.isEmpty())) {
			return false;
		}

		fullCmd = fullCmd.toLowerCase();

		FactionPlayer me = FPlayers.getInstance().get(player);

		String shortCmd; // command without the slash at the beginning
		if (fullCmd.startsWith("/")) {
			shortCmd = fullCmd.substring(1);
		} else {
			shortCmd = fullCmd;
			fullCmd = "/" + fullCmd;
		}

		if (me.hasFaction() && !me.isAdminBypassing() && !Conf.permanentFactionMemberDenyCommands.isEmpty() && me.getFaction().isPermanent() && isCommandInList(fullCmd, shortCmd, Conf.permanentFactionMemberDenyCommands.iterator())) {
			me.msg("<b>You can't use the command \"" + fullCmd + "\" because you are in a permanent faction.");
			return true;
		}

		if (!me.isInOthersTerritory()) {
			return false;
		}

		Relation rel = me.getRelationToLocation();
		if (rel.isAtLeast(Relation.ALLY)) {
			return false;
		}

		if (rel.isNeutral() && !Conf.territoryNeutralDenyCommands.isEmpty() && !me.isAdminBypassing() && isCommandInList(fullCmd, shortCmd, Conf.territoryNeutralDenyCommands.iterator())) {
			me.msg("<b>You can't use the command \"" + fullCmd + "\" in neutral territory.");
			return true;
		}

		return false;
	}

	private static boolean isCommandInList(String fullCmd, String shortCmd, Iterator<String> iter) {
		String cmdCheck;
		while (iter.hasNext()) {
			cmdCheck = iter.next();
			if (cmdCheck == null) {
				iter.remove();
				continue;
			}

			cmdCheck = cmdCheck.toLowerCase();
			if (fullCmd.startsWith(cmdCheck) || shortCmd.startsWith(cmdCheck)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isOfficerChest(Block block) {
		if (block.getType() != Material.CHEST && block.getType() != Material.TRAPPED_CHEST) {
			return false;
		}
		Chest chest = (Chest) block.getState();
		if (chest.getInventory() instanceof DoubleChestInventory) {
			DoubleChestInventory dub = (DoubleChestInventory) chest.getInventory();
			return hasOfficerSignAttached(((Chest) dub.getLeftSide().getHolder()).getBlock()) ||
			       hasOfficerSignAttached(((Chest) dub.getRightSide().getHolder()).getBlock());
		} else {
			return hasOfficerSignAttached(block);
		}
	}

	public static boolean isOfficerSign(Sign sign) {
		return sign.getLine(1).equals(CC.B_GREEN + "Officer");
	}

	public static boolean hasOfficerSignAttached(Block block) {
		for (BlockFace face : signFaces) {
			Block other = block.getRelative(face);
			if (other.getType() == Material.WALL_SIGN) {
				org.bukkit.material.Sign signMaterial = new org.bukkit.material.Sign(other.getType(), other.getData());
				if (signMaterial.getAttachedFace() == face.getOppositeFace()) {
					Sign sign = (Sign) other.getState();
					if (isOfficerSign(sign)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean checkTempLiquidPlacement(Location location, Player player, Material bucket) {
		if (location.getBlock().getType().equals(Material.AIR)) {
			FactionPlayer me = FPlayers.getInstance().get(player);
			FLocation loc = new FLocation(location);
			Faction otherFaction = Board.getFactionAt(loc);
			Relation rel = me.getFaction().getRelationTo(otherFaction);
			Block block = location.getBlock();

			for (BlockFace face : blockFaces) {
				Block relBlock = block.getRelative(face);
				if (bucket.equals(Material.LAVA_BUCKET)) {
					if (relBlock.getType().equals(Material.WATER) || relBlock.getType().equals(Material.STATIONARY_WATER)) {
						return false;
					}
				} else if (bucket.equals(Material.WATER_BUCKET)) {
					if (relBlock.getType().equals(Material.LAVA) || relBlock.getType().equals(Material.STATIONARY_LAVA)) {
						return false;
					}
				}
			}
			if (rel.confDenyUseage()) {
				return true;
			}
			if (Conf.ownedAreasEnabled && Conf.ownedAreaDenyUseage && !otherFaction.playerHasOwnershipRights(me, loc)) {
				return true;
			}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		// Make sure that all online players do have a fplayer.
		final FactionPlayer me = FPlayers.getInstance().get(event.getPlayer());

		// Update the lastLoginTime for this fplayer
		me.setLastLoginTime(System.currentTimeMillis());

		// Store player's current FLocation and notify them where they are
		me.setLastStoodAt(new FLocation(event.getPlayer().getLocation()));

		Faction myFaction = me.getFaction();
		if (myFaction != null && myFaction.isNormal()) {
			for (Player member : me.getFaction().getOnlinePlayers()) {
				FPlayers.getInstance().get(member).msg("<gold>Member online: <green>%s", me.getName());
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					me.sendInfoMessage();
				}
			}.runTaskLater(this.plugin, 5);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		final FactionPlayer me = FPlayers.getInstance().get(event.getPlayer());

		HomeTask task = this.plugin.getHomeTasks().get(event.getPlayer());
		if (task != null) {
			task.cancel();
		}
		StuckTask stuckTask = this.plugin.getStuckTasks().get(event.getPlayer());
		if (stuckTask != null) {
			stuckTask.cancel();
		}

		// and update their last login time to point to when the logged off, for auto-remove routine
		me.setLastLoginTime(System.currentTimeMillis());

		Faction myFaction = me.getFaction();
		if (myFaction != null && myFaction.isNormal()) {
			myFaction.memberLoggedOff();
			for (Player member : me.getFaction().getOnlinePlayers()) {
				FPlayers.getInstance().get(member).msg("<gold>Member offline: <red>%s", me.getName());
			}
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				if (!event.getPlayer().isOnline()) {
					me.setPlayer(null);
				}
			}
		}.runTask(this.plugin);

		// not stored yet
		JsonArray kills = new JsonArray();
		JsonArray deaths = new JsonArray();

		// ore counts
		JsonObject ores = new JsonObject();
		ores.addProperty("diamond", event.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE));
		ores.addProperty("emerald", event.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.EMERALD_ORE));
		ores.addProperty("gold", event.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.GOLD_ORE));
		ores.addProperty("iron", event.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.IRON_ORE));
		ores.addProperty("coal", event.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.COAL_ORE));
		ores.addProperty("redstone", event.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.REDSTONE_ORE));
		ores.addProperty("lapis", event.getPlayer().getStatistic(Statistic.MINE_BLOCK, Material.LAPIS_ORE));

		// send save request
		new DataRequest.SaveRequest(event.getPlayer().getUniqueId(), kills, deaths, ores, myFaction != null ? myFaction.getUUID() : null);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			HomeTask task = this.plugin.getHomeTasks().get(player);
			if (task != null) {
				task.cancel();
				player.sendFormattedMessage("{0}Teleporting home cancelled because you took damage.", ChatColor.RED);
			}
			StuckTask stuckTask = this.plugin.getStuckTasks().get(player);
			if (stuckTask != null) {
				stuckTask.cancel();
				player.sendFormattedMessage("{0}Teleporting out cancelled because you took damage.", ChatColor.RED);
			}
		}
		if (event.getDamager() instanceof Player) {
			Player damager = (Player) event.getDamager();
			HomeTask task = this.plugin.getHomeTasks().get(damager);
			if (task != null) {
				task.cancel();
				damager.sendFormattedMessage("{0}Teleporting home cancelled because you attacked.", ChatColor.RED);
			}
			StuckTask stuckTask = this.plugin.getStuckTasks().get(damager);
			if (stuckTask != null) {
				stuckTask.cancel();
				damager.sendFormattedMessage("{0}Teleporting out cancelled because you attacked.", ChatColor.RED);
			}
		}
	}

	@EventHandler
	public void onPlayerEnderPortal(PlayerPortalEvent event) {
		if (event.getCause().equals(TeleportCause.END_PORTAL)) {
			FactionPlayer me = FPlayers.getInstance().get(event.getPlayer());
			if (event.getFrom().getWorld().getEnvironment().equals(World.Environment.THE_END)) {
				return;
			}
			if (HCFactions.getInstance().isEndEnabled()) {
				event.setCancelled(true);
				me.sendMessage(ChatColor.RED + "The end is not enabled right now.");
				return;
			}
			if (Conf.denyPvpTaggedEndPortalEnter) {
				if (me.isPvpTagged()) {
					event.setCancelled(true);
					long now = System.currentTimeMillis();
					if (now - me.getLastPvpTagEndPortalMessage() > 5000) {
						me.setLastPvpTagEndPortalMessage(System.currentTimeMillis());
						me.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "You can not enter an end portal while pvp tagged.");
					}
					return;
				}
			}
			if (me.hasPvpProtection()) {
				event.setCancelled(true);
				long now = System.currentTimeMillis();
				if (now - me.getLastPvpTagEndPortalMessage() > 5000) {
					me.setLastPvpTagEndPortalMessage(System.currentTimeMillis());
					me.sendMessage(ChatColor.RED + ChatColor.BOLD.toString() + "You can not enter an end portal with pvp protection.");
				}
			}
		}
	}

	@EventHandler
	public void onPearlLaunch(ProjectileLaunchEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			if (event.getEntity() instanceof EnderPearl) {
				Player player = (Player) event.getEntity().getShooter();
				if (HCFactions.getInstance().getHomeTasks().containsKey(player)) {
					HCFactions.getInstance().getHomeTasks().get(player).cancel();
					player.sendFormattedMessage("{0}Teleporting home cancelled because you used a enderpearl.", ChatColor.RED);
				}
				if (HCFactions.getInstance().getStuckTasks().containsKey(player)) {
					HCFactions.getInstance().getStuckTasks().get(player).cancel();
					player.sendFormattedMessage("{0}Teleporting out cancelled because you used a enderpearl.", ChatColor.RED);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Block block = event.getClickedBlock();
		Player player = event.getPlayer();

		if (block == null) {
			return; // clicked in air, apparently
		}
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (!canPlayerUseBlock(player, block, false, event.getAction())) {
				event.setCancelled(true);
				handleExploitInteractionSpam(player);
				return;
			} else {
				if (player.isSneaking()) {
					if (block.getType() == Material.WALL_SIGN) {
						if (SubclaimManager.isSubclaimSign((Sign) block.getState())) {
							org.bukkit.material.Sign signMaterial = new org.bukkit.material.Sign(block.getType(), block.getData());
							BlockFace behind = signMaterial.getAttachedFace();
							if (behind != null) {
								SubclaimOwner subclaimOwner = SubclaimManager.getSubclaim(block.getRelative(behind));
								if (subclaimOwner != null) {
									if (subclaimOwner.canEdit(FPlayers.getInstance().get(player))) {
										if (SubclaimManager.canUseSubclaim(block.getRelative(behind), player)) {
											new SubclaimAuthMenu(HCFactions.getInstance(), SubclaimManager
													.getSubclaim(block.getRelative(behind)), FPlayers.getInstance().get(player))
													.open(player);
										} else {
											player.sendFormattedMessage("{0}You don''t have permission to edit this subclaim.", CC.RED);
										}
									} else {
										player.sendFormattedMessage("{0}You don''t have permission to edit this subclaim.", CC.RED);
									}
								}
							}
						}
					}
				}
			}
			if (!playerCanUseItemHere(player, block.getLocation(), event.getMaterial(), false)) {
				event.setCancelled(true);
				return;
			}
		} else if (event.getAction() == Action.PHYSICAL) {
			if (!canPlayerUseBlock(player, block, true, event.getAction())) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		FactionPlayer me = FPlayers.getInstance().get(event.getPlayer());

		Location home = me.getFaction().getHome();
		if (Conf.homesEnabled && Conf.homesTeleportToOnDeath && home != null) {
			event.setRespawnLocation(home);
		}

		me.clearPvpTag();
	}

	// For some reason onPlayerInteract() sometimes misses bucket events depending on distance (something like 2-3 blocks away isn't detected),
	// but these separate bucket events below always fire without fail
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Block block = event.getBlockClicked();
		Player player = event.getPlayer();
		Faction here = Board.getFactionAt(new FLocation(block));

		if (event.getBucket().equals(Material.WATER_BUCKET) || event.getBucket().equals(Material.LAVA_BUCKET)) {
			Location liquidLoc = block.getRelative(event.getBlockFace()).getLocation();
			Faction fac = Board.getFactionAt(new FLocation(liquidLoc));
			if (fac.isNormal() && checkTempLiquidPlacement(liquidLoc, event.getPlayer(), event.getBucket())) {
				if (liquidsToReset.containsKey(liquidLoc)) {
					HCFactions.getInstance().getServer().getScheduler().cancelTask(liquidsToReset.get(liquidLoc));
					liquidsToReset.remove(liquidLoc);
				}
				scheduleReplaceLiquid(liquidLoc);
				event.getPlayer().sendMessage(ChatColor.BLUE + "Your liquid will disappear after 5 seconds.");
				return;
			}
		}

		if (!playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false)) {
			event.setCancelled(true);
			return;
		}

		// log liquid placement near other factions >= y64
		if (block.getY() > 63 && (here.isNone() || here.isWarZone())) {
			int x = block.getX() >> 4;
			int z = block.getZ() >> 4;
			int radius = 1;
			outer:
			for (int dx = -radius; dx <= radius; dx++) {
				for (int dz = -radius; dz <= radius; dz++) {
					Faction f = Board.getFactionAt(new FLocation(block.getWorld().getName(), x + dx, z + dz));
					if (!f.isNone() && !f.isWarZone()) {
						HCFactions.getInstance().getLogger().info("[PlaceLiquid] " + player.getName() + " places " + event.getBucket() + " at " + String.format("%s(%d,%d,%d)", block.getWorld().getName(), block.getX(), block.getY(), block.getZ()));
						break outer;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if (block.getType().equals(Material.STATIONARY_LAVA) || block.getType().equals(Material.STATIONARY_WATER)) {
			Location liquidLoc = block.getLocation();
			if (liquidsToReset.containsKey(liquidLoc)) {
				HCFactions.getInstance().getServer().getScheduler().cancelTask(liquidsToReset.get(liquidLoc));
				liquidsToReset.remove(liquidLoc);
				return;
			}
		}

		if (!playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false)) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.isCancelled()) {
			return;
		}

		FactionPlayer badGuy = FPlayers.getInstance().get(event.getPlayer());
		if (badGuy == null) {
			return;
		}
	}

	private Location getNearbyWilderness(Player player) {
		FLocation fLocation = Board.getClosestNonClaimed(new FLocation(player.getLocation()));
		if (fLocation == null) {
			return null;
		}
		Chunk chunk = fLocation.getWorld().getChunkAt((int) fLocation.getX(), (int) fLocation.getZ());
		int groundLvl = 70;
		Location closest = null;
		for (int i = 0; i < 16; i++) {
			Location chunkLoc = new Location(chunk.getWorld(), (chunk.getX() << 4) + random.nextInt(16), 0, (chunk.getZ() << 4) + random.nextInt(16));
			Location highestChunkLoc = chunk.getWorld().getHighestBlockAt(chunkLoc).getLocation();
			double distance = Math.abs(highestChunkLoc.getY() - groundLvl);
			if (highestChunkLoc.clone().add(0, -1, 0).getBlock().getType().isSolid()) {
				if (closest != null) {
					if (Math.abs(closest.getY() - groundLvl) > distance) {
						closest = highestChunkLoc;
					}
				} else {
					closest = highestChunkLoc;
				}
			}
		}
		if (closest != null) {
			return closest.add(0.5, 0, 0.5);
		}
		return null;
	}

	private void scheduleReplaceLiquid(Location loc) {
		BukkitTask task = this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
			if (liquidsToReset.containsKey(loc)) {
				Material mat = loc.getBlock().getType();
				// just in case
				if (mat.equals(Material.STATIONARY_LAVA) ||
				    mat.equals(Material.STATIONARY_WATER) ||
				    mat.equals(Material.WATER) ||
				    mat.equals(Material.LAVA)) {
					loc.getBlock().setType(Material.AIR);
				}
			}
		}, 100L);
		liquidsToReset.put(loc, task.getTaskId());
	}

	@EventHandler
	public void onExpChangeEvent(PlayerExpChangeEvent event) {
		event.setAmount(Math.round(event.getAmount() * (float) Conf.expMultiplier));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerTeleportNormal(PlayerTeleportEvent event) {
		this.onPlayerMoveNormal(event);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerMoveNormal(PlayerMoveEvent event) {
		if (event.getFrom().getBlockX() >> 4 == event.getTo().getBlockX() >> 4 && event.getFrom().getBlockZ() >> 4 == event.getTo().getBlockZ() >> 4 && event.getFrom().getWorld() == event.getTo().getWorld()) {
			return;
		}
		FactionPlayer me = FPlayers.getInstance().get(event.getPlayer());
		FLocation from = new FLocation(event.getFrom());
		FLocation to = new FLocation(event.getTo());
		Faction factionFrom = Board.getFactionAt(from);
		Faction factionTo = Board.getFactionAt(to);

		if (!factionFrom.isSafeZone() && factionTo.isSafeZone() && me.denySafeZoneEntry() && !me.isAdminBypassing()) {
			if (event instanceof PlayerTeleportEvent) {
				event.setCancelled(true);
			} else {
				Location loc = event.getFrom();
				loc.setX(loc.getBlockX() + 0.5);
				loc.setY(loc.getBlockY());
				loc.setZ(loc.getBlockZ() + 0.5);
				event.setTo(loc);
				me.safezoneWalls.update(me, true);
			}
			event.getPlayer().sendFormattedMessage("{0}You can not enter {1}{0} while PvP tagged. Please wait {2} seconds.", ChatColor.RED, Factions.getInstance()
					.getSafeZone().getTag(me), me.getPvpTagRemaining() / 1000);
		}

		if (!factionFrom.denyPvpProtEntry() && factionTo.denyPvpProtEntry() && me.denyPvpProtEntry() && !me.isAdminBypassing()) {
			if (event instanceof PlayerTeleportEvent) {
				event.setCancelled(true);
			} else {
				Location loc = event.getFrom();
				loc.setX(loc.getBlockX() + 0.5);
				loc.setY(loc.getBlockY());
				loc.setZ(loc.getBlockZ() + 0.5);
				event.setTo(loc);
				me.pvpProtWalls.update(me, true);
			}
			event.getPlayer().sendFormattedMessage("{0}You can not enter {1}''s land while PvP protected. Please use ''/pvp enable'' if you wish to enable PvP.", ChatColor.RED, factionTo.getTag());
		}

		if (!factionFrom.isEvent() && factionTo.isEvent() && me.denyEventZoneEntry() && !me.isAdminBypassing()) {
			if (event instanceof PlayerTeleportEvent) {
				event.setCancelled(true);
			} else {
				Location loc = event.getFrom();
				loc.setX(loc.getBlockX() + 0.5);
				loc.setY(loc.getBlockY());
				loc.setZ(loc.getBlockZ() + 0.5);
				event.setTo(loc);
				me.eventZoneWalls.update(me, true);
			}
			if (me.getFactionId().equals("0")) {
				event.getPlayer().sendFormattedMessage("{0}You must be in a faction to enter event zones.", ChatColor.RED);
			} else {
				long timeInFaction = System.currentTimeMillis() - me.getJoinedFactionTime();
				if (timeInFaction < Conf.joinFactionEventCooldown * 60 * 1000) {
					String required = DurationFormatUtils.formatDurationWords(Conf.joinFactionEventCooldown * 60 * 1000, true, true);
					String current = DurationFormatUtils.formatDurationWords(timeInFaction, true, true);
					event.getPlayer().sendFormattedMessage("{0}You must be in a faction for {1} in order to enter event zones.", ChatColor.RED, required);
					event.getPlayer().sendFormattedMessage("{0}You have been in {1} for {2}.", ChatColor.RED, me.getFaction().getTag(), current);
				} else {
					event.getPlayer().sendFormattedMessage("{0}You can not enter event zones.", ChatColor.RED);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		this.onPlayerMove(event);

		if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && Conf.pvpTagEnderpearl > 0 && !Conf.worldsNoPvpTag.contains(event.getTo().getWorld().getName())) {
			FactionPlayer fplayer = FPlayers.getInstance().get(event.getPlayer());
			if (!fplayer.hasPvpProtection()) {
				fplayer.pvpTag(Conf.pvpTagEnderpearl);
			}

		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ() && event.getFrom().getWorld() == event.getTo().getWorld()) {
			return;
		}

		Player player = event.getPlayer();
		FactionPlayer me = FPlayers.getInstance().get(player);

		me.updateWalls();
		me.safezoneWalls.update(me, false);
		me.pvpProtWalls.update(me, false);
		me.eventZoneWalls.update(me, false);

		// quick check to make sure player is moving between chunks; good performance boost
		if (event.getFrom().getBlockX() >> 4 == event.getTo().getBlockX() >> 4 && event.getFrom().getBlockZ() >> 4 == event.getTo().getBlockZ() >> 4 && event.getFrom().getWorld() == event.getTo().getWorld()) {
			return;
		}

		// Did we change coord?
		FLocation from = me.getLastStoodAt();
		FLocation to = new FLocation(event.getTo());

		if (from.equals(to)) {
			return;
		}

		// Did we change "host"(faction)?
		Faction factionFrom = Board.getFactionAt(from);
		Faction factionTo = Board.getFactionAt(to);

		//TP or remove pvp prot if someone gets into a claim with pvp protection.
		if (factionTo.isSystem() || factionTo.isNormal()) {
			if (me.denyPvpProtEntry()) {
				player.sendFormattedMessage("{0}You can''t be in a claimed area with pvp protection.", ChatColor.RED);
				Location nearbyWilderness = getNearbyWilderness(player);
				if (nearbyWilderness == null) {
					me.removePvpProtection();
				} else {
					player.teleport(nearbyWilderness);
				}
			}
		}

		boolean changedFaction = (factionFrom != factionTo);

		me.setLastStoodAt(to);

		if (me.isMapAutoUpdating()) {
			me.sendMessage(Board.getMap(me.getFaction(), to, player.getLocation().getYaw()));

		} else {
			Faction myFaction = me.getFaction();
			String ownersTo = myFaction.getOwnerListString(to);

			if (changedFaction) {
				me.sendAreaChangeMessage(factionFrom, factionTo);
				if (Conf.ownedAreasEnabled && Conf.ownedMessageOnBorder && myFaction == factionTo && !ownersTo.isEmpty()) {
					me.sendMessage(Conf.ownedLandMessage + ownersTo);
				}
			} else if (Conf.ownedAreasEnabled && Conf.ownedMessageInsideTerritory && factionFrom == factionTo && myFaction == factionTo) {
				String ownersFrom = myFaction.getOwnerListString(from);
				if (Conf.ownedMessageByChunk || !ownersFrom.equals(ownersTo)) {
					if (!ownersTo.isEmpty()) {
						me.sendMessage(Conf.ownedLandMessage + ownersTo);
					} else if (!Conf.publicLandMessage.isEmpty()) {
						me.sendMessage(Conf.publicLandMessage);
					}
				}
			}
		}

		if (me.getAutoClaimFor() != null) {
			me.attemptClaim(me.getAutoClaimFor(), event.getTo(), true);
		} else if (me.isAutoSafeClaimEnabled()) {
			if (!Permission.MANAGE_SAFE_ZONE.has(player)) {
				me.setIsAutoSafeClaimEnabled(false);
			} else {
				if (!Board.getFactionAt(to).isSafeZone()) {
					Board.setFactionAt(Factions.getInstance().getSafeZone(), to, 0);
					me.msg("<instance>This land is now a safe zone.");
				}
			}
		} else if (me.isAutoWarClaimEnabled()) {
			if (!Permission.MANAGE_WAR_ZONE.has(player)) {
				me.setIsAutoWarClaimEnabled(false);
			} else {
				if (!Board.getFactionAt(to).isWarZone()) {
					Board.setFactionAt(Factions.getInstance().getWarZone(), to, 0);
					me.msg("<instance>This land is now a war zone.");
				}
			}
		}
	}

	private static class InteractAttemptSpam {

		private int attempts = 0;
		private long lastAttempt = System.currentTimeMillis();

		// returns the current attempt count
		public int increment() {
			long Now = System.currentTimeMillis();
			if (Now > lastAttempt + 3000) {
				attempts = 1;
			} else {
				attempts++;
			}
			lastAttempt = Now;
			return attempts;
		}
	}
}
