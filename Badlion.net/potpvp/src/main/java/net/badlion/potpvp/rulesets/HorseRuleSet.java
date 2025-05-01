package net.badlion.potpvp.rulesets;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.arenas.Arena;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.ladders.MatchLadder;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.matchmaking.QueueService;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HorseRuleSet extends KitRuleSet {

	private Set<UUID> quittingPlayers = new HashSet<>();

    public HorseRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.GOLD_BARDING),  ArenaManager.ArenaType.HORSE,  false, false);

	    // Enable in duels
	    this.enabledInDuels = true;

        if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_7) {
            Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.OneVsOneRanked, true, true));
            Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.TwoVsTwoRanked, true, true));
        }

        // Create default armor kit
        this.defaultArmorKit[3] = new ItemStack(Material.IRON_HELMET);
        this.defaultArmorKit[2] = new ItemStack(Material.IRON_CHESTPLATE);
        this.defaultArmorKit[1] = new ItemStack(Material.IRON_LEGGINGS);
        this.defaultArmorKit[0] = new ItemStack(Material.IRON_BOOTS);
        this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_FALL, 4);

        // Create default inventory kit
        this.defaultInventoryKit[0] = new ItemStack(Material.IRON_SWORD);
        this.defaultInventoryKit[1] = new ItemStack(Material.BOW);
        this.defaultInventoryKit[2] = new ItemStack(Material.COOKED_BEEF, 64);
        this.defaultInventoryKit[3] = ItemStackUtil.GOLDEN_APPLE;
        this.defaultInventoryKit[4] = ItemStackUtil.GOLDEN_APPLE;
        this.defaultInventoryKit[7] = new ItemStack(Material.ARROW, 64);
        this.defaultInventoryKit[8] = new ItemStack(Material.ARROW, 64);

        // Initialize info signs
        this.info2Sign[0] = "================";
        this.info2Sign[1] = "§5Horse";
        this.info2Sign[2] = "";
        this.info2Sign[3] = "================";

        this.info4Sign[0] = "§dIron Armor";
        this.info4Sign[1] = "Feather";
        this.info4Sign[2] = "Falling IV";
        this.info4Sign[3] = "";

        this.info5Sign[0] = "§dIron Sword";
        this.info5Sign[1] = "No Enchants";
        this.info5Sign[2] = "";
        this.info5Sign[3] = "";

        this.info6Sign[0] = "§dBow";
        this.info6Sign[1] = "No Enchants";
        this.info6Sign[2] = "128 Arrows";
        this.info6Sign[3] = "";

        this.info8Sign[0] = "§dFood";
        this.info8Sign[1] = "64 Steak";
        this.info8Sign[2] = "2 Golden Apples";
        this.info8Sign[3] = "";
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

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Group group = PotPvP.getInstance().getPlayerGroup(player);

		if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
			this.quittingPlayers.add(player.getUniqueId());
		}
	}

	@EventHandler
	public void onVehicleExitEvent(VehicleExitEvent event) {
		if (event.getExited() instanceof Player) {
			Player player = (Player) event.getExited();

			// Are they online?
			if (!this.quittingPlayers.remove(player.getUniqueId())) {
				Group group = PotPvP.getInstance().getPlayerGroup(player);

				if (GameState.groupIsInMatchmakingAndUsingRuleSet(group, this)) {
					if (!GameState.getGroupGame(group).isOver()) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

    public void teleport(Player player, Location location, Arena arena) {
        HorseRuleSet.createHorseAndAttach(player, location, arena);
    }

    public static Horse createHorse(Player player, Location location, Arena arena) {
        Horse horse = (Horse) player.getWorld().spawnEntity(location, EntityType.HORSE);
        horse.setAdult();
        horse.setTamed(true);
        horse.setAgeLock(true);
        horse.setVariant(Horse.Variant.HORSE);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.getInventory().setArmor(new ItemStack(Material.DIAMOND_BARDING));
        horse.setStyle(Horse.Style.values()[Gberry.generateRandomInt(0, Horse.Style.values().length - 1)]);
        horse.setColor(Horse.Color.values()[Gberry.generateRandomInt(0, Horse.Color.values().length - 1)]);
        horse.setJumpStrength(0.8D);
        horse.setMaxHealth(40D);
        horse.setHealth(horse.getMaxHealth());
        horse.setSpeed(0.2125D);
        horse.setOwner(player);

        arena.getLivingEntities().add(horse);

        return horse;
    }

    public static void createHorseAndAttach(final Player player, Location location, Arena arena) {
        final Horse horse = HorseRuleSet.createHorse(player, location, arena);

	    player.setFallDistance(0);

        new BukkitRunnable() {
            public void run() {
                if (Gberry.isPlayerOnline(player)) {
                    horse.setPassenger(player);
                }
            }
        }.runTaskLater(PotPvP.getInstance(), 1L);
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

}
