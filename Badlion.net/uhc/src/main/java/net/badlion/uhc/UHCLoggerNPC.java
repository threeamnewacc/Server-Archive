package net.badlion.uhc;

import net.badlion.combattag.LoggerNPC;
import net.badlion.gberry.Gberry;
import net.badlion.gpermissions.GPermissions;
import net.badlion.ministats.MiniStats;
import net.badlion.ministats.PlayerData;
import net.badlion.uhc.commands.handlers.BanCommandHandler;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.util.GoldenHeadUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UHCLoggerNPC extends LoggerNPC {

    public UHCLoggerNPC(Player player) {
        super(player);
    }

    public void remove(REMOVE_REASON reason) {
        super.remove(reason);

        // Don't do anything else, they didn't die
        if (reason == REMOVE_REASON.REJOIN) {
            return;
        }

        UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(this.getUUID());
        uhcPlayer.setArmor(this.getArmor());
        uhcPlayer.setInventory(this.getInventory());
        uhcPlayer.setDeathLocation(this.getEntity().getLocation());

        // Fake player death, update their state
        BadlionUHC.getInstance().updateDeathState(this.getUUID(), GPermissions.plugin.userHasPermission(this.getUUID().toString(), "badlion.donatorplus"));

        // Give killer a kill
        Player killer = this.getEntity().getKiller();
        if (killer != null) {
            UHCPlayer k = UHCPlayerManager.getUHCPlayer(killer.getUniqueId());
            k.addKill();

	        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + this.getEntity().getCustomName() + ChatColor.RED + " (CombatLogger) was slain by "
			        + ChatColor.YELLOW + killer.getDisguisedName());

            PlayerData playerData = MiniStats.getInstance().getPlayerDataListener().getPlayerData(killer.getUniqueId());
            if (playerData != null) { // Always track this regardless (they might have attacked another time or something b4 they died)
                playerData.setKills(playerData.getKills());
                playerData.addPlayerKill(this.getPlayer());

                playerData = MiniStats.getInstance().getPlayerDataListener().getPlayerData(this.getUUID());
                if (playerData != null && playerData.isTrackData()) {
                    playerData.setDeaths(playerData.getDeaths() + 1);
                }
            }
        } else {
	        Gberry.broadcastMessageNoBalance(ChatColor.YELLOW + this.getEntity().getCustomName() + ChatColor.RED + " (CombatLogger) has died");

            PlayerData playerData = MiniStats.getInstance().getPlayerDataListener().getPlayerData(this.getUUID());
            if (playerData != null && playerData.isTrackData()) {
                playerData.setDeaths(playerData.getDeaths() + 1);
            }
        }

        // Handle golden heads
        if ((boolean) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.GOLDENHEADS.name()).getValue()) {
            GoldenHeadUtils.makeHeadStakeForPlayer(this.getEntity());
        }

        BadlionUHC.getInstance().checkForWinners();
        BanCommandHandler.deathLocations.put(this.getUUID(), this.getEntity().getLocation());
    }

}
