package net.badlion.uhc.listeners.gamemodes;

import net.badlion.gberry.Gberry;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.events.GameStartEvent;
import net.badlion.uhc.events.UHCTeleportPlayerLocationEvent;
import net.badlion.uhc.listeners.MiniStatsListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class OPGameMode implements GameMode {

	public OPGameMode() {
		BadlionUHC.getInstance().getConfigurator().getBooleanOption(BadlionUHC.CONFIG_OPTIONS.STATS.name()).setValue("false");
	}

	@EventHandler(ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		if (event.getBlock().getType().toString().contains("_ORE")) {
			// They mined an ore, make it triple
			event.setCancelled(true);

			// Stat Tracking (do before we set to AIR)
			if ((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.STATS.name()).getValue()) {
				MiniStatsListener.handleBlockBreak(event);
			}

			// Item to drop
			Material material;
			byte data = (byte) 0;
			switch (event.getBlock().getType()) {
				case IRON_ORE:
					material = Material.IRON_INGOT;
					break;
				case COAL_ORE:
					material = Material.COAL;
					break;
				case DIAMOND_ORE:
					material = Material.DIAMOND;
					break;
				case GOLD_ORE:
					material = Material.GOLD_INGOT;
					break;
				case REDSTONE_ORE:
					material = Material.REDSTONE;
					break;
				case GLOWING_REDSTONE_ORE:
					material = Material.REDSTONE;
					break;
				case LAPIS_ORE:
					material = Material.INK_SACK;
					data = (byte) 4;
					break;
				case QUARTZ_ORE:
					material = Material.QUARTZ;
					break;
				case EMERALD_ORE:
					material = Material.EMERALD;
					break;
				default:
					return;
			}

			// Give them the triple ores
			event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(material, 3, data));

			// Set the block to air
			event.getBlock().setType(Material.AIR);
		}
	}

	// Handle superhero effects on start
	@EventHandler
	public void onGameStart(GameStartEvent event) {
		BadlionUHC.getInstance().getConfigurator().getBooleanOption(BadlionUHC.CONFIG_OPTIONS.STATS.name()).setValue("false");
	}

	@EventHandler
	public void onUHCTeleportPlayerLocationEvent(UHCTeleportPlayerLocationEvent event) {
		Player player = event.getPlayer();

		int random = Gberry.generateRandomInt(0, 5);

		switch (random) {
			case 0:
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
				player.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "You have been given the Speedy ability!");
				break;
			case 1:
				player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 3));
				player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 9));
				player.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "You have been given the Survivor ability!");
				break;
			case 2:
				player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
				player.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "You have been given the Resistor ability!");
				break;
			case 3:
				player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, Integer.MAX_VALUE, 19));
				player.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "You have been given the Healer ability!");
				break;
			case 4:
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
				player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0));
				player.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "You have been given the Stealth ability!");
				break;
			case 5:
				player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0));
				player.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "You have been given the Rage ability!");
				break;
			default:
				BadlionUHC.getInstance().getServer().dispatchCommand(BadlionUHC.getInstance().getServer().getConsoleSender(),
						"uhcmc " + player.getName() + " has not been given their ability due to an error.");
				break;
		}
	}

    public ItemStack getExplanationItem() {
	    ItemStack item = new ItemStack(Material.POTION);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "OP UHC");

        List<String> lore = new ArrayList<>();

	    lore.add(ChatColor.AQUA + "- Triple ores");
	    lore.add(ChatColor.AQUA + "- Random abilities");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "Badlion";
    }

    @Override
    public void unregister() {
	    GameStartEvent.getHandlerList().unregister(this);
	    BlockBreakEvent.getHandlerList().unregister(this);
		GameStartEvent.getHandlerList().unregister(this);
		UHCTeleportPlayerLocationEvent.getHandlerList().unregister(this);
    }

}
