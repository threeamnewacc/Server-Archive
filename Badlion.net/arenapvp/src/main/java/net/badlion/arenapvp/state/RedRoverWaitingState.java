package net.badlion.arenapvp.state;

import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.TeamStateMachine;
import net.badlion.arenapvp.helper.SpectatorHelper;
import net.badlion.gberry.Gberry;
import net.badlion.statemachine.GState;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.HorseInventory;

public class RedRoverWaitingState extends GState<Player> implements Listener {

	public RedRoverWaitingState() {
		super("redrover", "they are in a red rover match.", TeamStateMachine.getInstance());
	}

	@Override
	public void before(Player player) {
		player.sendFormattedMessage("{0}You are now in a red rover", ChatColor.GREEN);
		Gberry.log("STATE", "RRWaiting before: " + player.getName());

		setPlayerWaiting(player);
	}

	@Override
	public void after(Player player) {
		Gberry.log("STATE", "RRWaiting after: " + player.getName());

		for (Player p2 : ArenaPvP.getInstance().getServer().getOnlinePlayers()) {
			if (p2 != player) {
				p2.showPlayer(player);
				Gberry.log("VISIBILITY", "RRWaiting: " + p2.getName() + " shows " + player.getName());
			}
		}
	}


	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		for (Player spec : this.elements()) {
			if (event.getPlayer() == spec) {
				continue;
			}
			event.getPlayer().hidePlayer(spec);
			Gberry.log("VISIBILITY", "RRWaiting State: " + event.getPlayer().getName() + " hides " + spec.getName());
		}
	}

	@EventHandler
	public void onRRWaitingExtinguishFireEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (this.contains(player)) {
			if (event.getClickedBlock() != null && event.getClickedBlock().getRelative(BlockFace.UP).getType().equals(Material.FIRE)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onRRWaitingHorseInventoryClickEvent(InventoryClickEvent event) {
		Player player = ((Player) event.getWhoClicked());

		if (player.isOp()) {
			return;
		}

		if (player.getGameMode() == GameMode.CREATIVE && !player.isOp()) {
			event.setCancelled(true);
		} else if (event.getClickedInventory() instanceof HorseInventory) {
			event.setCancelled(true);
		}
	}


	public void setPlayerWaiting(Player player) {
		SpectatorHelper.setGameModeCreative(player);

		player.getInventory().setItem(0, SpectatorHelper.getCompassItem());

		player.updateInventory();

		for (Player p2 : ArenaPvP.getInstance().getServer().getOnlinePlayers()) {
			if (p2 != player) {
				p2.hidePlayer(player);
				Gberry.log("VISIBILITY", "RRWaiting: " + p2.getName() + " hides " + player.getName());
				if (this.contains(p2)) {
					player.hidePlayer(p2);
					Gberry.log("VISIBILITY", "RRWaiting: " + player.getName() + " hides " + p2.getName());
				}
			}
		}
	}
}
