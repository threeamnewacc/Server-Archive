package net.badlion.uhcmeetup.listeners;

import net.badlion.arenacommon.kits.Kit;
import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.kits.KitType;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.combattag.CombatTagPlugin;
import net.badlion.combattag.LoggerNPC;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.bukkitevents.MPGCreatePlayerEvent;
import net.badlion.mpg.bukkitevents.MPGGameStateChangeEvent;
import net.badlion.mpg.bukkitevents.MPGServerStateChangeEvent;
import net.badlion.mpg.bukkitevents.MapManagerInitializeEvent;
import net.badlion.mpg.managers.MPGMapManager;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.uhcmeetup.UHCMeetupGame;
import net.badlion.uhcmeetup.UHCMeetupPlayer;
import net.badlion.uhcmeetup.UHCMeetupWorld;
import net.badlion.worldrotator.GWorld;
import net.badlion.worldrotator.WorldRotator;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MPGListener implements Listener {

    @EventHandler
    public void onMapManagerInitialize(MapManagerInitializeEvent event) {
        for (GWorld world : WorldRotator.getInstance().getWorlds()) {
            event.getWorlds().add(new UHCMeetupWorld(world));
        }
    }

	@EventHandler
	public void onMPGServerStateChangeEvent(MPGServerStateChangeEvent event) {
		if (event.getNewState() == MPG.ServerState.LOBBY) {
			new UHCMeetupGame(((UHCMeetupWorld) MPGMapManager.getRandomWorld()));
		}
	}

	@EventHandler
	public void onMPGGameStateChangeEvent(MPGGameStateChangeEvent event) {
		// Did the game start?
		if (event.getMPGGame().getGameState() == MPGGame.GameState.GAME) {
			// Load default kits for players who haven't selected a kit yet
			for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER)) {
				Player player = mpgPlayer.getPlayer();

				// Do they still have the kit selection items?
				if (player.getInventory().first(Material.ENCHANTED_BOOK) != -1) {
					// Load their first saved kit or default kit
					KitType kitType = new KitType(player.getUniqueId().toString(), KitRuleSet.buildUHCRuleSet.getName());
					Map<KitType, List<Kit>> kitTypeListMap = KitCommon.inventories.get(player.getUniqueId());

					if (kitTypeListMap != null) {
						List<Kit> kits = kitTypeListMap.get(kitType);
						if (kits != null) {
							// Load the first custom kit we can find for the player, if they don't have a custom kit load the default kit
							for (Kit kit : kits) {
								KitCommon.loadKit(player, KitRuleSet.buildUHCRuleSet, kit.getId());
								break;
							}
						} else {
							KitCommon.loadDefaultKit(player, KitRuleSet.buildUHCRuleSet, true);
						}
					} else {
						KitCommon.loadDefaultKit(player, KitRuleSet.buildUHCRuleSet, true);
					}

					player.getInventory().setHeldItemSlot(0);
				}
			}

			// Load default kits for combat loggers
			for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.DC)) {
				LoggerNPC loggerNPC = CombatTagPlugin.getInstance().getLogger(mpgPlayer.getUniqueId());
				LivingEntity livingEntity = loggerNPC.getEntity();

				// Do they still have the kit selection items?
				boolean hasKit = true;
				for (ItemStack itemStack : loggerNPC.getInventory()) {
					if (itemStack != null && itemStack.getType() == Material.ENCHANTED_BOOK) {
						hasKit = false;
						break;
					}
				}

				// Does this logger NPC already have a kit loaded?
				if (hasKit) continue;

				// Load their first saved kit or default kit
				KitType kitType = new KitType(mpgPlayer.getUniqueId().toString(), KitRuleSet.buildUHCRuleSet.getName());
				Map<KitType, List<Kit>> kitTypeListMap = KitCommon.inventories.get(mpgPlayer.getUniqueId());

				if (kitTypeListMap != null) {
					List<Kit> kits = kitTypeListMap.get(kitType);
					if (kits != null) {
						// Load the first custom kit we can find for the player, if they don't have a custom kit load the default kit
						for (Kit kit : kits) {
							this.loadKitLivingEntity(loggerNPC, KitRuleSet.buildUHCRuleSet.getName(), kit.getId());
							break;
						}
					} else {
						this.loadDefaultKitLivingEntity(loggerNPC, KitRuleSet.buildUHCRuleSet);
					}
				} else {
					this.loadDefaultKitLivingEntity(loggerNPC, KitRuleSet.buildUHCRuleSet);
				}
			}
		}
	}

	@EventHandler
	public void onMPGCreatePlayerEvent(MPGCreatePlayerEvent event) {
		UHCMeetupPlayer uhcMeetupPlayer = new UHCMeetupPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getDisguisedName());
		event.setMpgPlayer(uhcMeetupPlayer);
	}

	private void loadDefaultKitLivingEntity(LoggerNPC loggerNPC, KitRuleSet kitRuleSet) {
		loggerNPC.setArmor(kitRuleSet.getDefaultArmorKit());
		loggerNPC.setInventory(kitRuleSet.getDefaultInventoryKit());

		for (PotionEffect potionEffect : kitRuleSet.getPotionEffects()) {
			loggerNPC.getEntity().addPotionEffect(potionEffect);
		}
	}

	private void loadKitLivingEntity(final LoggerNPC loggerNPC, final String kitRulesetName, final int kitId) {
		KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(kitRulesetName);
		Set<PotionEffect> potionEffects = kitRuleSet.getPotionEffects();
		Map<KitType, List<Kit>> inventories = KitCommon.inventories.get(loggerNPC.getUUID());
		KitType kitType = new KitType(loggerNPC.getUUID().toString(), kitRulesetName);
		List<Kit> kits = inventories.get(kitType);

		Kit kit = null;
		if (kits != null) {
			for (Kit k : kits) {
				if (k.getId() == kitId) {
					kit = k;
				}
			}
		}
		if (kit != null) {
			loggerNPC.setArmor(kitRuleSet.getDefaultArmorKit());
			loggerNPC.setInventory(kitRuleSet.getDefaultInventoryKit());

			for (PotionEffect potionEffect : potionEffects) {
				loggerNPC.getEntity().addPotionEffect(potionEffect);
			}
		} else {
			this.loadDefaultKitLivingEntity(loggerNPC, kitRuleSet);
		}
	}

}
