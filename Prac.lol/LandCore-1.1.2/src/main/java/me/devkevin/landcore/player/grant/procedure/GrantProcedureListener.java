package me.devkevin.landcore.player.grant.procedure;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.grant.menus.GrantConfirmMenu;
import me.devkevin.landcore.utils.message.CC;
import me.devkevin.landcore.utils.time.TimeFormatUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 20/01/2023 @ 12:09
 * GrantProcedureListener / land.pvp.core.player.grant.procedure / LandCore
 */
@RequiredArgsConstructor
public class GrantProcedureListener implements Listener {
    private final LandCore plugin;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        GrantProcedure grantProcedure = GrantProcedure.getByPlayer(player.getName());

        if (grantProcedure != null) {
            if (grantProcedure.getStage() == GrantProcedureStage.DURATION) {
                event.setCancelled(true);
                if (event.getMessage().equalsIgnoreCase("cancel")) {
                    GrantProcedure.getProcedures().remove(grantProcedure);
                    player.sendMessage(CC.RED + "You have cancelled grant procedure.");
                    return;
                }
                if (event.getMessage().equalsIgnoreCase("perm")) {
                    grantProcedure.setDuration(-1L);
                    grantProcedure.setStage(GrantProcedureStage.REASON);
                    player.sendMessage(CC.SECONDARY + "You have entered " + CC.PRIMARY + "perm" + CC.SECONDARY + " as your duration. Please enter your grant " + CC.PRIMARY + "reason" + CC.SECONDARY + ".");
                    return;
                }
                grantProcedure.setDuration(System.currentTimeMillis() + TimeFormatUtils.parseTime(event.getMessage()));
                grantProcedure.setStage(GrantProcedureStage.REASON);
                player.sendMessage(CC.SECONDARY + "You have entered " + CC.PRIMARY + (System.currentTimeMillis() + TimeFormatUtils.parseTime(event.getMessage()) + CC.SECONDARY + " as your duration. Please enter your grant " + CC.PRIMARY + "reason" + CC.SECONDARY + "."));
            } else if (grantProcedure.getStage() == GrantProcedureStage.REASON) {
                event.setCancelled(true);

                if (event.getMessage().equalsIgnoreCase("cancel")) {
                    GrantProcedure.getProcedures().remove(grantProcedure);
                    player.sendMessage(CC.RED + "You have cancelled grant procedure.");
                    return;
                }
                grantProcedure.setStage(GrantProcedureStage.CONFIRMATION);
                grantProcedure.setReason(event.getMessage());
                player.sendMessage(CC.SECONDARY + "You have entered " + CC.PRIMARY + event.getMessage() + CC.SECONDARY + " as a reason.");
                new GrantConfirmMenu(grantProcedure).openMenu(player);
            }
        }
    }
}
