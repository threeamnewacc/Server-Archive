package me.devkevin.landcore.inventory.menu.impl;

import com.google.common.collect.Maps;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.inventory.menu.Menu;
import me.devkevin.landcore.inventory.menu.action.Action;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.utils.item.ItemBuilder;
import me.devkevin.landcore.utils.message.CC;
import me.devkevin.landcore.utils.timer.Timer;
import org.bukkit.Material;

import java.util.Map;

public class ReportMenu extends Menu {
    private final LandCore plugin;

    public ReportMenu(LandCore plugin) {
        super(1, "Select a Report Reason");
        this.plugin = plugin;
    }

    private Action getAction(String reason) {
        return player -> {
            CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
            Timer cooldownTimer = profile.getReportCooldownTimer();

            if (cooldownTimer.isActive()) {
                player.sendMessage(CC.RED + "You can't report a player for another " + cooldownTimer.formattedExpiration() + ".");
                player.closeInventory();
                return;
            }

            String targetName = profile.getReportingPlayerName();

            Map<String, Object> reportInformation = Maps.newHashMap();

            reportInformation.put("server", plugin.getServerName());
            reportInformation.put("reporter", player.getName());
            reportInformation.put("reported", targetName);
            reportInformation.put("reason", reason);

            plugin.getStaffManager().messageStaff("");
            plugin.getRedisMessenger().send("report", reportInformation);
            plugin.getStaffManager().messageStaff("");

            player.sendMessage(CC.GREEN + "Report sent for " + targetName + CC.GREEN + ": " + CC.R + reason);

            player.closeInventory();
        };
    }

    @Override
    public void setup() {
    }

    @Override
    public void update() {
        setActionableItem(1, new ItemBuilder(Material.DIAMOND_SWORD).name(CC.PRIMARY + "Combat Cheats").build(), getAction("Combat Cheats"));
        setActionableItem(3, new ItemBuilder(Material.DIAMOND_BOOTS).name(CC.PRIMARY + "Movement Cheats").build(), getAction("Movement Cheats"));
        setActionableItem(5, new ItemBuilder(Material.PAPER).name(CC.PRIMARY + "Chat Violation").build(), getAction("Chat Violation"));
        setActionableItem(7, new ItemBuilder(Material.NETHER_STAR).name(CC.PRIMARY + "Assistance").build(), getAction("I need assistance related to this player"));
    }
}
