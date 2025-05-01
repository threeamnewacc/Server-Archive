package net.badlion.gfactions.listeners;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Rel;
import net.badlion.gfactions.GFactions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGainBeaconEffectEvent;
import org.bukkit.potion.PotionEffectType;

public class BeaconListener implements Listener {

	private GFactions plugin;

	public BeaconListener(GFactions plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBeaconEffect(PlayerGainBeaconEffectEvent event) {
		Player player = event.getPlayer();

		FPlayer fplayer = FPlayers.i.get(player);
		Faction faction = fplayer.getFaction();

		// Get the faction they are standing near
		Faction beaconLocationFaction = Board.getFactionAt(event.getBeaconLocation());
		Rel relation = beaconLocationFaction.getRelationTo(faction);
		if (relation.isLessThan(Rel.ALLY)) {
			event.setCancelled(true);
		}

		// What if it's jump boost? WE DONT ALLOW THAT SHIT
		if (event.getEffect().getType().equals(PotionEffectType.JUMP)) {
			event.setCancelled(true);
		}
	}

}
