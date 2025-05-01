package net.badlion.uhc.listeners.gamemodes;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.events.ObjectivesCommandEvent;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LongshotsGameMode implements GameMode {

	private static final Map<Integer, Double> teamShotDistance = new HashMap<>();
	private static final Set<Integer> hasPrize = new HashSet<>();

	@EventHandler
	public void onShot(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Arrow && ((Arrow) event.getDamager()).getShooter() instanceof Player) {
			Player whoShot = (Player) ((Arrow) event.getDamager()).getShooter();
			UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(whoShot.getUniqueId());

			int uhcTeamID = uhcPlayer.getTeam().getTeamNumber();

			if (!teamShotDistance.containsKey(uhcTeamID)) {
				teamShotDistance.put(uhcPlayer.getTeam().getTeamNumber(), 0.0D);
			}

			teamShotDistance.put(uhcTeamID,
					teamShotDistance.get(uhcTeamID)
							+ (whoShot.getLocation().distance(event.getEntity().getLocation())));

			if (teamShotDistance.get(uhcTeamID) >= 1000 && !hasPrize.contains(uhcTeamID)) {
				// They completed the objective! GJ M80 NOW GIV ME PRIZ
				hasPrize.add(uhcTeamID);

				Gberry.broadcastMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Congratulations to team #" + uhcTeamID + " for completed the Longshots objective!");

				for (UUID uuid : uhcPlayer.getTeam().getUuids()) {
					Player player = BadlionUHC.getInstance().getServer().getPlayer(uuid);

					player.sendMessage(ChatColor.GOLD + "Congratulations on completing the Longshots objective! Here is your prize:");
					player.sendMessage(ChatColor.YELLOW + "- Golden hoe of healing");

					if (!RiskyRetrievalGameMode.addItem(player.getInventory(), ItemStackUtil.createItem(Material.GOLD_HOE, ChatColor.RED + "Instant Heal"))) {
						player.getWorld().dropItem(player.getLocation(), ItemStackUtil.createItem(Material.GOLD_HOE, ChatColor.RED + "Instant Heal"));
					}
				}
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getItem() != null && event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()
					&& event.getItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Instant Heal")
					&& (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			event.setUseItemInHand(Event.Result.DENY);
			event.setCancelled(true);
			event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
			event.getPlayer().updateInventory();
			event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
		}
	}

	@EventHandler
	public void onObjectivesCommand(ObjectivesCommandEvent event) {
		event.setSentMessages(true);

		Player player = event.getPlayer();
		UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
		player.sendMessage(ChatColor.GOLD + "Complete this UHC objective by doing the following:");
		double shotDistance = LongshotsGameMode.getTeamShotDistance(uhcPlayer);
		player.sendMessage(ChatColor.YELLOW + "Have a total shot distance of 1000 " + "(" + (shotDistance >= 1000 ? ChatColor.GREEN : ChatColor.RED) + Math.round(shotDistance) + ChatColor.YELLOW + ")");
	}

	public static double getTeamShotDistance(UHCPlayer uhcPlayer) {
		if (!teamShotDistance.containsKey(uhcPlayer.getTeam().getTeamNumber())) {
			teamShotDistance.put(uhcPlayer.getTeam().getTeamNumber(), 0.0D);
		}

		return teamShotDistance.get(uhcPlayer.getTeam().getTeamNumber());
	}

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Longshots");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- Get a total of 1000 block distance with a bow to complete the objective");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "???";
    }

    @Override
    public void unregister() {
	    EntityDamageByEntityEvent.getHandlerList().unregister(this);
		PlayerInteractEvent.getHandlerList().unregister(this);
		ObjectivesCommandEvent.getHandlerList().unregister(this);
    }

}
