package net.badlion.uhc.practice;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.inventories.CosmeticsInventory;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.PetManager;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.practice.kits.BuildKit;
import net.badlion.uhc.practice.kits.Kit;
import net.badlion.uhc.practice.kits.RegularKit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PracticeManager {

	public static PracticeArena getArena() {
		return PracticeManager.arena;
	}

	private static PracticeArena arena;

	public static void initialize() {
		Kit.kits.add(new RegularKit());
		Kit.kits.add(new BuildKit());

		PracticeManager.arena = new PracticeArena(Kit.getKit("uhc"));
	}

	public static void addPlayer(UHCPlayer uhcPlayer) {
		if (BadlionUHC.getInstance().isPractice()) {
			Player player = BadlionUHC.getInstance().getServer().getPlayer(uhcPlayer.getUUID());
			player.setAllowFlight(false);
			player.setGameMode(GameMode.SURVIVAL);
			player.setHealth(player.getMaxHealth());

			player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);

			CosmeticsManager.disableCosmetic(player.getUniqueId(), Cosmetics.CosmeticType.GADGET);
			CosmeticsManager.disableCosmetic(player.getUniqueId(), Cosmetics.CosmeticType.MORPH);
			CosmeticsManager.disableCosmetic(player.getUniqueId(), Cosmetics.CosmeticType.PARTICLE);
			//CosmeticsManager.disableCosmetic(player.getUniqueId(), Cosmetics.CosmeticType.PET);

			// TODO: TEMP FIX, REFER TO MC4-C2165C5
			PetManager.despawnPet(player, CosmeticsManager.getCosmeticsSettings(player.getUniqueId()));

            // Remove wither walk speed
            player.setWalkSpeed(0.2f);

			// Teleport the player after we disable cosmetics
			PracticeManager.arena.addPlayer(player);
		}
	}

	// Don't check for isPractice as we might remove them afterwards...
	public static void removePlayer(UHCPlayer uhcPlayer, boolean teleportSpawn) {
		Player player = uhcPlayer.getPlayer();

		// Fail-safe in case a player gets removed from practice in-game
		// TODO: Move this to use the BadlionUHC Method so the event gets fired off
		if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.PRE_START) {
			// Make them nice again
			player.setLevel(0);
			player.setHealth(player.getMaxHealth());
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 127, true));

			if (player.getOpenInventory() != null && player.getOpenInventory().getType() == InventoryType.CRAFTING) {
				player.getOpenInventory().getTopInventory().clear();
			}

			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			// Hotbar item for cosmetics
			player.getInventory().setItem(8, CosmeticsInventory.getOpenCosmeticInventoryItem());

			CosmeticsManager.enableCosmetic(player.getUniqueId(), Cosmetics.CosmeticType.GADGET);
			CosmeticsManager.enableCosmetic(player.getUniqueId(), Cosmetics.CosmeticType.MORPH);
			CosmeticsManager.enableCosmetic(player.getUniqueId(), Cosmetics.CosmeticType.PARTICLE);
			CosmeticsManager.enableCosmetic(player.getUniqueId(), Cosmetics.CosmeticType.PET);
		}

		// Remove the player from Practice
		PracticeManager.arena.removePlayer(player, teleportSpawn);
	}

	public static boolean isInPractice(UHCPlayer uhcPlayer) {
		return PracticeManager.arena.isInArena(uhcPlayer.getPlayer());
	}

	public static void endPractice() {
		// Remove everyone from Practice
		Set<UUID> playersCopy = new HashSet<>();
		playersCopy.addAll(PracticeManager.arena.getPlayers());
		for (UUID uuid : playersCopy) {
			PracticeManager.removePlayer(UHCPlayerManager.getUHCPlayer(uuid), true);
		}
	}

}
