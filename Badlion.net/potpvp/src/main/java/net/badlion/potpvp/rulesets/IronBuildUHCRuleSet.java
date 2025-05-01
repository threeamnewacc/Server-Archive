package net.badlion.potpvp.rulesets;

import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.Game;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.arenas.BuildUHCArena;
import net.badlion.potpvp.bukkitevents.KitLoadEvent;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.ladders.MatchLadder;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.matchmaking.QueueService;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class IronBuildUHCRuleSet extends KitRuleSet {

	private List<Material> whitelistedBlocks = new ArrayList<>();
	//private static Map<UUID, Deque<Location>> blockLocations = new HashMap<>();

    public IronBuildUHCRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.WATER_BUCKET), ArenaManager.ArenaType.BUILD_UHC, false, false);

	    this.is1_9Compatible = true;

	    // Enable in duels
	    this.enabledInDuels = true;

	    Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.OneVsOneRanked, true, true));
	    Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.TwoVsTwoRanked, true, true));
	    Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.ThreeVsThreeRanked, true, true));

	    // Create default armor kit
	    this.defaultArmorKit[3] = new ItemStack(Material.IRON_HELMET);
	    this.defaultArmorKit[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

	    this.defaultArmorKit[2] = new ItemStack(Material.IRON_CHESTPLATE);
	    this.defaultArmorKit[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

	    this.defaultArmorKit[1] = new ItemStack(Material.IRON_LEGGINGS);
	    this.defaultArmorKit[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

	    this.defaultArmorKit[0] = new ItemStack(Material.DIAMOND_BOOTS);
	    this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_PROJECTILE, 3);

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.DIAMOND_SWORD);
	    this.defaultInventoryKit[0].addEnchantment(Enchantment.DAMAGE_ALL, 1);

	    this.defaultInventoryKit[1] = new ItemStack(Material.FISHING_ROD);

	    this.defaultInventoryKit[2] = new ItemStack(Material.BOW);
	    this.defaultInventoryKit[2].addEnchantment(Enchantment.ARROW_DAMAGE, 1);

	    this.defaultInventoryKit[3] = new ItemStack(Material.COOKED_BEEF, 64);
	    this.defaultInventoryKit[4] = new ItemStack(Material.GOLDEN_APPLE, 4);
	    this.defaultInventoryKit[5] = ItemStackUtil.createGoldenHead(2);
	    this.defaultInventoryKit[6] = new ItemStack(Material.DIAMOND_PICKAXE);
	    this.defaultInventoryKit[7] = new ItemStack(Material.WATER_BUCKET);
	    this.defaultInventoryKit[8] = new ItemStack(Material.COBBLESTONE, 64);
	    this.defaultInventoryKit[9] = new ItemStack(Material.ARROW, 20);
	    this.defaultInventoryKit[10] = new ItemStack(Material.COBBLESTONE, 64);
	    this.defaultInventoryKit[11] = new ItemStack(Material.WATER_BUCKET);

	    if (PotPvP.getInstance().getServer().getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
		    this.defaultExtraItems[0] = new ItemStack(Material.SHIELD);

		    // TODO: MOJANG BUG
		    //this.defaultInventoryKit[9] = new ItemStack(Material.SPECTRAL_ARROW, 20);
	    }

	    // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "§5Iron Build UHC";
	    this.info2Sign[2] = "";
        this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dIron Armor";
	    this.info4Sign[1] = "Prot 2 H/C/L";
	    this.info4Sign[2] = "Proj Prot 3";
	    this.info4Sign[3] = "Boots";

	    this.info5Sign[0] = "§Diamond Sword";
	    this.info5Sign[1] = "Sharpness I";
	    this.info5Sign[2] = "";
	    this.info5Sign[3] = "";

	    this.info6Sign[0] = "§dBow";
	    this.info6Sign[1] = "Power I";
	    this.info6Sign[2] = "20 Arrows";
	    this.info6Sign[3] = "2 Water Buckets";

		this.info7Sign[0] = "§dOther";
		this.info7Sign[1] = "D Pickaxe";
		this.info7Sign[2] = "";
		this.info7Sign[3] = "Fishing Rod";

	    this.info8Sign[0] = "§dFood";
	    this.info8Sign[1] = "64 Steak";
	    this.info8Sign[2] = "4 Gold Apples";
	    this.info8Sign[3] = "2 Gold Heads";

		this.info9Sign[0] = "§dBlocks";
		this.info9Sign[1] = "128 Cobble";
		this.info9Sign[2] = "";
		this.info9Sign[3] = "";

	    this.whitelistedBlocks.add(Material.LOG);
	    this.whitelistedBlocks.add(Material.LOG_2);
	    this.whitelistedBlocks.add(Material.WOOD);
	    this.whitelistedBlocks.add(Material.LEAVES);
	    this.whitelistedBlocks.add(Material.LEAVES_2);
	    this.whitelistedBlocks.add(Material.WATER);
	    this.whitelistedBlocks.add(Material.STATIONARY_WATER);
	    this.whitelistedBlocks.add(Material.LAVA);
	    this.whitelistedBlocks.add(Material.STATIONARY_LAVA);
	    this.whitelistedBlocks.add(Material.LONG_GRASS);
	    this.whitelistedBlocks.add(Material.YELLOW_FLOWER);
	    this.whitelistedBlocks.add(Material.COBBLESTONE);
	    this.whitelistedBlocks.add(Material.CACTUS);
	    this.whitelistedBlocks.add(Material.SUGAR_CANE_BLOCK);
	    this.whitelistedBlocks.add(Material.OBSIDIAN);
	    this.whitelistedBlocks.add(Material.SNOW);
    }

	@Override
	public void sendMessages(Player player) {
		player.sendMessage(ChatColor.DARK_RED + "WARNING: " + ChatColor.DARK_AQUA + "If you sky base camp you will " +
							   "receive a punishment (ArenaPvP Rule 6).");
	}

	@EventHandler
	public void onKitLoadEvent(KitLoadEvent event) {
		if (this == event.getKitRuleSet()) {
			for (ItemStack itemStack : event.getPlayer().getInventory().getContents()) {
				if (itemStack == null) continue;

				if (itemStack.getType() == Material.FISHING_ROD) {
					itemStack.setDurability((short) 0);
				}
			}
		}
	}

	@EventHandler(priority= EventPriority.LAST, ignoreCancelled=true)
	public void onArrowHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
			Player damaged = (Player) event.getEntity();
			Group group = PotPvP.getInstance().getPlayerGroup(damaged);

			if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
				if (((Arrow) event.getDamager()).getShooter() instanceof Player) {
					Player damager = (Player) ((Arrow) event.getDamager()).getShooter();

					if (damaged != damager && damaged.getHealth() - event.getFinalDamage() > 0) {
						damager.sendMessage(ChatColor.GOLD + damaged.getName() + ChatColor.DARK_AQUA + " is now at " + ChatColor.GOLD +
								Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2D + ChatColor.DARK_RED + " ♥");
					}
				}
			}
		}
	}

    @EventHandler
    public void onHealthRegenEvent(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            Group group = PotPvP.getInstance().getPlayerGroup(player);

            if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
                if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
                    event.setCancelled(true);
                }
            }
        }
    }

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		Group group = PotPvP.getInstance().getPlayerGroup(player);
		Block block = event.getBlockClicked().getRelative(event.getBlockFace());

		// Don't let them take liquids that were already in the map
		if (!ArenaManager.containsLiquidBlock(block)) {
			event.setCancelled(true);

			event.getPlayer().sendMessage(ChatColor.RED + "You can't take that!");
		} else if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
			if (!this.canBreakBlock(block)) return;

			event.setCancelled(false);
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerUseBucketEvent(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			if (player.getItemInHand() != null) {
				if (player.getItemInHand().getType() == Material.BUCKET) {
					Group group = PotPvP.getInstance().getPlayerGroup(player);

					if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
						if (!this.canBreakBlock(event.getClickedBlock())) return;

						event.setUseInteractedBlock(Event.Result.ALLOW);
						event.setCancelled(false);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Group group = PotPvP.getInstance().getPlayerGroup(player);

		if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
			if (!this.canBreakBlock(event.getBlock())) return;

			// Cancel if block is not whitelisted or if it's the map's obsidian
			if (!this.whitelistedBlocks.contains(event.getBlock().getType())
					|| (event.getBlock().getType() == Material.OBSIDIAN
					&& !GameState.getGroupGame(group).getArena().containsBlockPlaced(event.getBlock()))
					&& !GameState.getGroupGame(group).getArena().containsBlockRemoved(event.getBlock())) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + "You're not allowed to break this block!");
			} else {
				event.setCancelled(false);
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Group group = PotPvP.getInstance().getPlayerGroup(player);

		if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
			if (!this.canBreakBlock(event.getBlock())) return;

			event.setCancelled(false);

			Game game = GameState.getGroupGame(group);
			if (game.getArena() instanceof BuildUHCArena) {
				int max = ((BuildUHCArena) game.getArena()).getMaxBlockYLevel(event.getBlock().getLocation());
				if (event.getBlock().getY() > max) {
					event.setCancelled(true);

					// Prevent block glitching
					if (player.getLocation().getY() > max + 2) {
						Location gotchaBitch = player.getLocation();
						gotchaBitch.setY(max + 1);

						// Temporary fix for broken area scans
						if (gotchaBitch.getBlock().getRelative(0, 1, 0).isEmpty()) {
							player.teleport(gotchaBitch);
						}
						player.sendMessage(ChatColor.RED + "Block glitching is not allowed!");
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LAST)
	public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		Group group = PotPvP.getInstance().getPlayerGroup(player);

		if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
			// Check whichever block they click on for this
			if (!this.canBreakBlock(event.getBlockClicked())) return;

			event.setCancelled(false);

			Game game = GameState.getGroupGame(group);
			if (game.getArena() instanceof BuildUHCArena) {
				Block block = event.getBlockClicked().getRelative(event.getBlockFace());
				int max = ((BuildUHCArena) game.getArena()).getMaxBlockYLevel(block.getLocation());
				if (block.getY() > max) {
					event.setCancelled(true);

					// Prevent water bucket and lava bucket glitching
					if (event.getBucket() == Material.WATER_BUCKET || event.getBucket() == Material.LAVA_BUCKET) {
						if (player.getLocation().getY() > max + 2) {
							Location gotchaBitch = player.getLocation();
							gotchaBitch.setY(max + 1);

							// Temporary fix for broken area scans
							if (gotchaBitch.getBlock().getRelative(0, 1, 0).isEmpty()) {
								player.teleport(gotchaBitch);
							}
							player.sendMessage(ChatColor.RED + "Water and lava glitching is not allowed!");
						}
					}
				}
			}
		}
	}

}
