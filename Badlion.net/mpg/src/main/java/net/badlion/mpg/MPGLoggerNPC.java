package net.badlion.mpg;

import net.badlion.combattag.LoggerNPC;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public class MPGLoggerNPC extends LoggerNPC {

	private MPGPlayer mpgPlayer;

	private String skinTexture;
	private String skinSignature;

	public MPGLoggerNPC(UUID uuid, MPGPlayer mpgPlayer, Location location, String namePrefix) {
		super(uuid, namePrefix + mpgPlayer.getUsername(), location);

		this.mpgPlayer = mpgPlayer;
	}

	public MPGLoggerNPC(Player player, MPGPlayer mpgPlayer, String namePrefix) {
		super(player, namePrefix);

		this.mpgPlayer = mpgPlayer;

		// Cache skin texture and signature if need it for dead laying entities
		if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.DEAD_ENTITY_ON_DEATH)) {
			Object playerProfile = TinyProtocolReferences.getPlayerProfile.invoke(player);

			Object properties = TinyProtocolReferences.gameProfilePropertyMap.get(playerProfile);
			Collection<Object> propertyCollection = (Collection<Object>) TinyProtocolReferences.propertyMapGet.invoke(properties, "textures");

			Object property = propertyCollection.iterator().next();

			this.skinTexture = TinyProtocolReferences.propertyValue.get(property);
			this.skinSignature = TinyProtocolReferences.propertySignature.get(property);
		}
	}

    public void remove(REMOVE_REASON reason) {
	    // Always delete the reference to the disconnect timer task
	    this.mpgPlayer.cancelDisconnectTimerTask();

        // Don't do anything else, they didn't die
        if (reason == REMOVE_REASON.REJOIN) {
	        super.remove(reason);
            return;
        }

	    // Did this combat logger timeout?
	    if (reason == REMOVE_REASON.TIMEOUT) {
		    if (MPG.getInstance().getBooleanOption(MPG.ConfigFlag.KILL_COMBAT_LOGGER_ON_TIMEOUT)) {
			    // Broadcast kill message
			    Player killer = this.getEntity().getKiller();
			    if (killer != null) {
				    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + this.getEntity().getCustomName() + ChatColor.RED + " (CombatLogger) was slain by "
						    + ChatColor.YELLOW + killer.getDisguisedName());
			    } else {
				    Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + this.getEntity().getCustomName() + ChatColor.RED + " (CombatLogger) has died");
			    }

			    // Update player's state
			    this.mpgPlayer.setState(MPGPlayer.PlayerState.DEAD);
		    }
	    } else {
		    // Update player's state since the combat logger died
		    this.mpgPlayer.setState(MPGPlayer.PlayerState.DEAD);
	    }

	    // Remove cache and handle everything else after we process the death (we need the references to the logger npc)
	    super.remove(reason);
    }

	public String getSkinTexture() {
		return this.skinTexture;
	}

	public String getSkinSignature() {
		return this.skinSignature;
	}

}
