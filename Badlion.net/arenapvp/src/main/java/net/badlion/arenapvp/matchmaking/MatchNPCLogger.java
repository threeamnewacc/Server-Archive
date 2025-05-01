package net.badlion.arenapvp.matchmaking;

import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.Team;
import net.badlion.arenapvp.manager.MatchManager;
import net.badlion.combattag.LoggerNPC;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.logging.Level;

public class MatchNPCLogger extends LoggerNPC {

	private static final int INSTANT_DEATH_TIME = 45; // seconds

	private BukkitTask despawnTask;


	private Location logoutLocation;
	private ItemStack[] inventory;
	private ItemStack[] armor;
	private double health;
	private int hunger;
	private float saturation;
	private Collection<PotionEffect> potionEffects;
	private boolean onHorse;
	private ItemStack horseArmor;
	private double horseHealth;
	private double horseMaxHealth;
	private double horseSpeed;
	private double horseJump;

	public MatchNPCLogger(Player player) {
		super(player);
		this.logoutLocation = player.getLocation();
		this.inventory = player.getInventory().getContents();
		this.armor = player.getInventory().getArmorContents();
		this.health = player.getHealth();
		this.hunger = player.getFoodLevel();
		this.saturation = player.getSaturation();
		this.potionEffects = player.getActivePotionEffects();

		if (player.getVehicle() != null) {
			if (player.getVehicle() instanceof Horse) {
				Horse horse = (Horse) player.getVehicle();
				this.onHorse = true;
				this.horseArmor = horse.getInventory().getArmor();
				this.horseHealth = horse.getHealth();
				this.horseMaxHealth = horse.getMaxHealth();
				this.horseSpeed = horse.getSpeed();
				this.horseJump = horse.getJumpStrength();
			}
		}

		Bukkit.getLogger().log(Level.INFO, "Match NPC Logger: " + player.getName());
	}

	public void restorePlayer(Player player, Match match) {
		Gberry.safeTeleport(player, this.logoutLocation);
		player.getInventory().setContents(this.inventory);
		player.getInventory().setArmorContents(this.armor);
		player.setHealth(this.health);
		player.setFoodLevel(this.hunger);
		player.setSaturation(this.saturation);
		for (PotionEffect potionEffect : potionEffects) {
			player.addPotionEffect(potionEffect);
		}

		// Restore the horse they logged with and make them ride it.
		if (onHorse) {
			Horse horse = (Horse) player.getWorld().spawnEntity(this.logoutLocation, EntityType.HORSE);
			horse.setAdult();
			horse.setTamed(true);
			horse.setAgeLock(true);
			horse.setVariant(Horse.Variant.HORSE);
			horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
			horse.getInventory().setArmor(this.horseArmor);
			horse.setStyle(Horse.Style.values()[Gberry.generateRandomInt(0, Horse.Style.values().length - 1)]);
			horse.setColor(Horse.Color.values()[Gberry.generateRandomInt(0, Horse.Color.values().length - 1)]);
			horse.setJumpStrength(this.horseJump);
			horse.setMaxHealth(this.horseMaxHealth);
			horse.setHealth(this.horseHealth);
			horse.setSpeed(this.horseSpeed);
			horse.setOwner(player);

			new BukkitRunnable() {
				public void run() {
					if (Gberry.isPlayerOnline(player)) {
						horse.setPassenger(player);
					}
				}
			}.runTaskLater(ArenaPvP.getInstance(), 1L);
			match.getArena().getLivingEntities().add(horse);
		}
	}

	@Override
	public void remove(REMOVE_REASON reason) {
		super.remove(reason);


		Team team = MatchManager.getCombatLoggedPlayers().remove(this.getUUID());

		despawnTask.cancel();
		Bukkit.getLogger().log(Level.INFO, "Combat Logger: " + getPlayer().getName() + " remove: " + reason.toString());
		// Don't do anything else, they didn't die
		if (reason == REMOVE_REASON.REJOIN) {
			return;
		}
		Match match = MatchManager.getActiveMatches().get(team);

		if (match != null) {
			team.handlePlayerDeath(getUUID());
			match.storeLoggerStats(getUUID(), getArmor(), getInventory());
			Player killer = this.getEntity().getKiller();
			// Send death message
			if (killer != null) {
				Gberry.log("MATCH2", this.getEntity().getCustomName() + " logger killed by " + killer.getDisguisedName());
				match.broadcastMessage(ChatColor.RED + getEntity().getCustomName() + " (CombatLogger)" + ChatColor.YELLOW + " was slain by " + ChatColor.RED + killer.getDisguisedName());
			} else {
				match.broadcastMessage(ChatColor.RED + getEntity().getCustomName() + " (CombatLogger)" + ChatColor.YELLOW + " has died");
			}
			if (match instanceof RedRoverBattle) {
				RedRoverBattle redRoverBattle = (RedRoverBattle) match;
				redRoverBattle.handleLoggerDeath(getUUID());
			}
			match.checkWin();
		}

		getPlayer().getInventory().clear();
		getPlayer().getInventory().setArmorContents(null);
		// set the craftplayer health to 0, which has the side effect of saving the player as dead in their data file
		// without actually killing the entity
		// TODO: we may or may not need this, only testing will tell, I am thinking its better without because then when they rejoin to another match we don't have to manually respawn them
		TinyProtocolReferences.craftPlayerHealth.set(getPlayer(), 0);
		getPlayer().saveData();

		Player killer = this.getEntity().getKiller();
	}

	public void resetDespawnTimer() {
		if (despawnTask != null) {
			despawnTask.cancel();
		}
		despawnTask = Bukkit.getScheduler().runTaskLater(ArenaPvP.getInstance(), () -> remove(REMOVE_REASON.DEATH), INSTANT_DEATH_TIME * 20);
	}
}
