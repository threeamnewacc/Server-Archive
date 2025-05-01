package net.badlion.uhc.listeners.gamemodes;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.events.GameStartEvent;
import net.badlion.uhc.events.GiveStarterItemsEvent;
import net.badlion.uhc.events.SpecialTeamsEvent;
import net.badlion.uhc.events.UHCTeleportPlayerLocationEvent;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.managers.UHCTeamManager;
import net.badlion.uhc.util.ScatterUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class OpsVsWorldGameMode implements GameMode {

	public static List<UUID> worldPlayers = new ArrayList<>();
	public static List<UUID> opPlayers = new ArrayList<>();

	private Location spawnA = new Location(BadlionUHC.getInstance().getUHCWorld(), -100, 0, -100);
	private Location spawnB = new Location(BadlionUHC.getInstance().getUHCWorld(), 100, 255, 100);

	private Location tower1A = new Location(BadlionUHC.getInstance().getUHCWorld(), -496, 62, -560);
	private Location tower1B = new Location(BadlionUHC.getInstance().getUHCWorld(), -527, 133, -590);

	private Location tower2A = new Location(BadlionUHC.getInstance().getUHCWorld(), 503, 65, 493);
	private Location tower2B = new Location(BadlionUHC.getInstance().getUHCWorld(), 536, 136, 525);

	private Location tower3A = new Location(BadlionUHC.getInstance().getUHCWorld(), -488, 66, 517);
	private Location tower3B = new Location(BadlionUHC.getInstance().getUHCWorld(), -458, 137, 487);

	private Location tower4A = new Location(BadlionUHC.getInstance().getUHCWorld(), 564, 69, -450);
	private Location tower4B = new Location(BadlionUHC.getInstance().getUHCWorld(), 532, 140, -420);

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.BEDROCK);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "OPs Vs World");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- Lapis Lazuli doesn't drop healing potions for Ore Frenzy");
        lore.add(ChatColor.AQUA + "- Ender Dragons don't destroy the four towers on the  map");
        lore.add(ChatColor.AQUA + "- Beds act as 2.5 second TNT bombs in the main world,");
        lore.add(ChatColor.AQUA + "   breaking the bed before it explodes stops the bomb");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "Badlion";
    }

	public OpsVsWorldGameMode() {
		BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.TEAMSIZE.name()).setValue(10);
	}

	public static String getPrefix(UHCPlayer uhcPlayer) {
		// Check if they are in OPs
		if (opPlayers.contains(uhcPlayer.getUUID())) {
			// They are in OPs team
			return ChatColor.DARK_RED + "[OP]";
		} else if (worldPlayers.contains(uhcPlayer.getUUID())) {
			// They aren't in OPs, but they are in world
			return ChatColor.GREEN + "[World]";
		}
		return "";
	}


	@EventHandler
	public void onTeleportPlayer(UHCTeleportPlayerLocationEvent event) {
		Player player = event.getPlayer();
		if (!opPlayers.contains(player.getUniqueId()) && !worldPlayers.contains(player.getUniqueId())) {
			// In case some shit happened, and they don't have a team yet! Give them one -.-
			sortTeam(player);
		}

		Location location = null;

		if (opPlayers.contains(player.getUniqueId())) {
			// If they are in OPs just get a randy spawn in 100x100 lol
			location = ScatterUtils.randomSquareScatterFromPoints(1, 100, 100, -100, -100, 5).get(0);
		} else {
			// Else, just get a randy location outside 100x100
			int tries = 0;
			while (location == null || location.getX() <= 100 || location.getZ() <= 100) {
				// Catch it at 200 tries, it shouldn't be that hard to find a non-100x100
				if (++tries >= 200) {
					location = null;
					break;
				}

				// Just get a random location lol fuck pre-done scatterpoints
				// MFW - there are 1k people all trying this method lol RIP SERVER TODO: Fix this shit when u arent lazy
				ArrayList<Location> scatterPoints;
				scatterPoints = ScatterUtils.randomSquareScatter(1);
				location = scatterPoints.get(0);
			}
		}

		event.setLocation(location);
	}

	private static void sortTeam(Player player) {
		if (player.hasPermission("badlion.staff") || player.hasPermission("badlion.retired")
				|| player.hasPermission("badlion.famous") || player.hasPermission("badlion.famousplus")) {
			opPlayers.add(player.getUniqueId());
		} else {
			worldPlayers.add(player.getUniqueId());
		}
	}

	@EventHandler
	public void onTeamsCreation(SpecialTeamsEvent event) {
		event.setOverriden(true);

		// We want to remove all teams, and override it ourselves..
		for (UHCTeam uhcTeam : UHCTeamManager.getAllUHCTeams()) {
			UHCTeamManager.removeUHCTeam(uhcTeam);
		}

		// First assign them an OvW team
		for (UHCPlayer uhcPlayer : UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.PLAYER)) {
			Player player = uhcPlayer.getPlayer();
			sortTeam(player);
		}

		// Assign new teams
		for (UHCPlayer uhcPlayer : UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.PLAYER)) {
			UUID uuid = uhcPlayer.getUUID();
			UHCTeam uhcTeam = getUHCTeam(uuid);
			// uhcTeam will only be null if there are no more teams for them to join
			// So many a new one.
			if (uhcTeam == null) {
				UHCPlayerManager.getUHCPlayer(uuid).setTeam(new UHCTeam(uuid));
				continue;
			}
			UHCPlayerManager.getUHCPlayer(uuid).setTeam(uhcTeam);

			// Check for people who don't have a team
			// Should never be the case (because we set it above) but yolo fail-safe!
			if (uhcPlayer.getTeam() == null) {
				uhcPlayer.getPlayer().kickPlayer(ChatColor.RED + "There has been an error, please contact a developer with this error code: #3EB");
			}
		}
	}

	private static UHCTeam getUHCTeam(UUID uuid) {
		Player player = BadlionUHC.getInstance().getServer().getPlayer(uuid);

		if (!opPlayers.contains(player.getUniqueId()) && !worldPlayers.contains(player.getUniqueId())) {
			player.kickPlayer("An error occurred, please send this error code to a developer - #3EC");
		}

		for (UHCTeam uhcTeam : UHCTeamManager.getAllUHCTeams()) {
			// Team is full
			if (uhcTeam.getSize() >= (int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.TEAMSIZE.name()).getValue()) {
				continue;
			}

			// They are both in the same team, and the team isn't full, so add them!
			if ((worldPlayers.contains(player.getUniqueId()) && worldPlayers.contains(uhcTeam.getLeader()))
					|| (opPlayers.contains(player.getUniqueId()) && opPlayers.contains(uhcTeam.getLeader()))) {
				return uhcTeam;
			}
		}

		// Can't find a team
		return null;
	}

	@EventHandler
	public void onGiveStarterItems(GiveStarterItemsEvent event) {
		Player player = event.getPlayer();

		// Check if they are on OP team
		if (!opPlayers.contains(player.getUniqueId())) {
			return;
		}

		// Clear inventory first
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);

		// Give them items
		player.getInventory().addItem(enchantItem(new ItemStack(Material.DIAMOND_PICKAXE), Enchantment.DIG_SPEED));
		player.getInventory().addItem(enchantItem(new ItemStack(Material.STONE_SWORD), Enchantment.DAMAGE_ALL, 5));
		player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 2));
		player.getInventory().addItem(enchantItem(new ItemStack(Material.BOW), Enchantment.ARROW_DAMAGE));
		player.getInventory().addItem(enchantItem(new ItemStack(Material.FISHING_ROD), Enchantment.DURABILITY));
		player.getInventory().addItem(new ItemStack(Material.WATER_BUCKET));

		// Armour
		player.getInventory().setArmorContents(new ItemStack[] {
				enchantItem(new ItemStack(Material.IRON_BOOTS), Enchantment.PROTECTION_ENVIRONMENTAL, 1),
				enchantItem(new ItemStack(Material.IRON_LEGGINGS), Enchantment.PROTECTION_ENVIRONMENTAL, 1),
				enchantItem(new ItemStack(Material.IRON_CHESTPLATE), Enchantment.PROTECTION_ENVIRONMENTAL, 1),
				enchantItem(new ItemStack(Material.IRON_HELMET), Enchantment.PROTECTION_ENVIRONMENTAL, 1)
		});

		// Give them speed/strength
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 0));
		player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 0));
	}

	private static ItemStack enchantItem(ItemStack itemStack, Enchantment enchantment) {
		return enchantItem(itemStack, enchantment, 1);
	}

	private static ItemStack enchantItem(ItemStack itemStack, Enchantment enchantment, int level) {
		itemStack.addEnchantment(enchantment, level);
		return itemStack;
	}

	@EventHandler
	public void onGameStart(GameStartEvent event) {
		// Go through all teams, find the OP teams
		for (UHCTeam uhcTeam : UHCTeamManager.getAllUHCTeams()) {
			// Check if their leader is in OPs
			if (opPlayers.contains(uhcTeam.getLeader())) {
				// If they are, means they are in OPs team
				// Give them the OP kit
				for (UUID uuid : uhcTeam.getUuids()) {
					Player player = BadlionUHC.getInstance().getServer().getPlayer(uuid);

					// Alert them of their success
					player.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "You are on the OPs team!");
				}
			} else if (worldPlayers.contains(uhcTeam.getLeader())) {
				// Iterate through them, alert them of their team
				for (UUID uuid : uhcTeam.getUuids()) {
					BadlionUHC.getInstance().getServer().getPlayer(uuid).sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "You are on the World team!");
				}
			}
		}
	}

	@EventHandler
	public void onBlockBurnEvent(BlockBurnEvent event) {
		if (Gberry.isLocationInBetween(this.spawnA, this.spawnB, event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockIgniteEvent(BlockIgniteEvent event) {
		if (Gberry.isLocationInBetween(this.spawnA, this.spawnB, event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlaceEvent(final BlockPlaceEvent event) {
		if (event.getBlock().getType() == Material.FIRE) {
			if (Gberry.isLocationInBetween(this.spawnA, this.spawnB, event.getBlock().getLocation())) {
				event.getPlayer().sendMessage(ChatColor.RED + "You cannot light a fire in 200x200 in OPs vs World!");
				event.setCancelled(true);
			}
		} else if (event.getBlock().getType() == Material.BED_BLOCK && event.getBlock().getWorld().getWorldType() == WorldType.NORMAL) {
			new BukkitRunnable() {

				private int counter = 0;

				@Override
				public void run() {
					this.counter++;

					// Is the block still a bed?
					if (event.getBlock().getLocation().getBlock().getType() != Material.BED_BLOCK) {
						this.cancel();
						return;
					}

					// Explode after 2.5s
					if (this.counter == 5) {
						// Play sound
						event.getBlock().getWorld().playSound(event.getBlock().getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1F, 1F);

						// Remove block so it doesn't explode and let people pick it up
						event.getBlock().setType(Material.AIR);

						// Create explosion
						event.getBlock().getWorld().createExplosion(event.getBlock().getLocation(), 4.0F, true);

						this.cancel();
					} else {
						// Play sound
						event.getBlock().getWorld().playSound(event.getBlock().getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1F, 0.5F);
					}
				}
			}.runTaskTimer(BadlionUHC.getInstance(), 10L, 10L);
		}
	}

	@EventHandler
	public void onEntityExplodeEvent(EntityExplodeEvent event) {
		// Did an enderdragon explode?
		if (event.getEntity() instanceof EnderDragon || event.getEntity() instanceof EnderDragonPart) {
			Iterator<Block> iterator = event.blockList().iterator();
			while (iterator.hasNext()) {
				Block block = iterator.next();
				Location location = block.getLocation();

				// Remove block if it's in the tower region
				if (Gberry.isLocationInBetween(this.tower1A, this.tower1B, location)
						|| Gberry.isLocationInBetween(this.tower2A, this.tower2B, location)
						|| Gberry.isLocationInBetween(this.tower3A, this.tower3B, location)
						|| Gberry.isLocationInBetween(this.tower4A, this.tower4B, location)) {
					iterator.remove();
				}
			}
		}
	}

    @Override
    public void unregister() {
	    BlockBreakEvent.getHandlerList().unregister(this);
	    EntityExplodeEvent.getHandlerList().unregister(this);
	    BlockPlaceEvent.getHandlerList().unregister(this);
	    GameStartEvent.getHandlerList().unregister(this);
	    BlockBurnEvent.getHandlerList().unregister(this);
	    BlockIgniteEvent.getHandlerList().unregister(this);
		UHCTeleportPlayerLocationEvent.getHandlerList().unregister(this);
		GiveStarterItemsEvent.getHandlerList().unregister(this);
		SpecialTeamsEvent.getHandlerList().unregister(this);
    }

}
