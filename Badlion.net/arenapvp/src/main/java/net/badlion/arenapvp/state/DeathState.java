package net.badlion.arenapvp.state;

import net.badlion.arenapvp.TeamStateMachine;
import net.badlion.gberry.Gberry;
import net.badlion.statemachine.GState;
import net.badlion.statemachine.StateMachine;
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

public class DeathState extends GState<Player> implements Listener {

	public DeathState() {
		super("spectator", "they are dead.", TeamStateMachine.getInstance());
	}

	@Override
	public void before(Player player){
		super.before(player);
		Gberry.log("STATE", "DEATH before: " + player.getName());
	}

	@Override
	public void after(Player player){
		super.after(player);
		Gberry.log("STATE", "DEATH after: " + player.getName());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		for (Player spec : this.elements()) {
			if (event.getPlayer() == spec) {
				continue;
			}
			event.getPlayer().hidePlayer(spec);
			Gberry.log("VISIBILITY", "DEATHSTATE: " + event.getPlayer().getName() + " hides " + spec.getName());
		}
	}

	@EventHandler
	public void onDeadExtinguishFireEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (this.contains(player)) {
			if (event.getClickedBlock() != null && event.getClickedBlock().getRelative(BlockFace.UP).getType().equals(Material.FIRE)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onDeadHorseInventoryClickEvent(InventoryClickEvent event) {
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
}
