package net.badlion.arenapvp.state;

import net.badlion.arenapvp.TeamStateMachine;
import net.badlion.arenapvp.manager.SidebarManager;
import net.badlion.arenapvp.manager.SpectateManager;
import net.badlion.arenapvp.matchmaking.Match;
import net.badlion.gberry.Gberry;
import net.badlion.statemachine.GState;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SpectatorState extends GState<Player> implements Listener {

	private static Map<Player, Match> spectatingMatchMap = new HashMap<>();

	private static Set<Player> coloredArmorEnabled = new HashSet<>();

	public SpectatorState() {
		super("spectator", "they are in spectator mode.", TeamStateMachine.getInstance());
	}

	@Override
	public void before(Player player) {
		super.before(player);
		Gberry.log("STATE", "SPEC before: " + player.getName());
	}

	@Override
	public void after(Player player) {
		super.after(player);

		if (TeamStateMachine.spectatorState.getSpectatorMatch(player) != null) {
			SidebarManager.removeSidebar(player);
		}
		TeamStateMachine.spectatorState.removeSpectatorMatch(player);
		SpectatorState.coloredArmorEnabled.remove(player);

		Gberry.log("STATE", "SPEC after: " + player.getName());
	}


	public void cleanupMatchSpectators(Match match) {
		Iterator<Map.Entry<Player, Match>> iterator = spectatingMatchMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Player, Match> entry = iterator.next();
			if (entry.getValue().equals(match)) {
				SpectateManager.removeSpectatingMatch(entry.getKey(), entry.getValue());
				iterator.remove();
			}
		}
	}

	public void broadcastToSpectators(Match match, String message) {
		Iterator<Map.Entry<Player, Match>> iterator = spectatingMatchMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Player, Match> entry = iterator.next();
			if (entry.getValue().equals(match)) {
				entry.getKey().sendMessage(message);
			}
		}
	}

	public Match getSpectatorMatch(Player player) {
		return SpectatorState.spectatingMatchMap.get(player);
	}


	public void setSpectatorMatch(Player player, Match match) {
		// Debug code
		if (SpectatorState.spectatingMatchMap.containsKey(player)) {
			try {
				throw new Exception("Unexpected player already located in map " + player);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		SpectatorState.spectatingMatchMap.put(player, match);
	}

	public Match removeSpectatorMatch(Player player) {
		return SpectatorState.spectatingMatchMap.remove(player);
	}

	public void setColoredArmorEnabled(Player player) {
		SpectatorState.coloredArmorEnabled.add(player);
		if(getSpectatorMatch(player) != null){
			Match match = getSpectatorMatch(player);
			match.addLeatherColoredHelmets(player);
		}
	}

	public void setColoredArmorDisabled(Player player) {
		SpectatorState.coloredArmorEnabled.remove(player);
		if(getSpectatorMatch(player) != null){
			Match match = getSpectatorMatch(player);
			match.removeLeatherColoredHelmets(player);
		}
	}

	public boolean isColorArmorEnabled(Player player) {
		return SpectatorState.coloredArmorEnabled.contains(player);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		for (Player spec : this.elements()) {
			if (event.getPlayer() == spec) {
				continue;
			}
			event.getPlayer().hidePlayer(spec);
			Gberry.log("VISIBILITY", "SpecState: " + event.getPlayer().getName() + " hides " + spec.getName());
		}
	}

	@EventHandler
	public void onSpectatorExtinguishFireEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (this.contains(player)) {
			if (event.getClickedBlock() != null && event.getClickedBlock().getRelative(BlockFace.UP).getType().equals(Material.FIRE)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onSpectatorHorseInventoryClickEvent(InventoryClickEvent event) {
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
