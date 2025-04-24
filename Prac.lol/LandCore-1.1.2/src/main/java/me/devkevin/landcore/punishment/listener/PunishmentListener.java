package me.devkevin.landcore.punishment.listener;

import me.devkevin.landcore.punishment.procedure.PunishmentProcedure;
import me.devkevin.landcore.punishment.procedure.PunishmentProcedureStage;
import me.devkevin.landcore.punishment.procedure.PunishmentProcedureType;
import me.devkevin.landcore.utils.menu.TypeCallback;
import me.devkevin.landcore.utils.menu.menus.ConfirmMenu;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 09/02/2023 @ 3:57
 * PunishmentListener / me.devkevin.landcore.punishment.listener / LandCore
 */
public class PunishmentListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        if (!event.getPlayer().hasPermission("zoot.staff.grant")) {
            return;
        }

        PunishmentProcedure procedure = PunishmentProcedure.getByPlayer(event.getPlayer());

        if (procedure != null && procedure.getStage() == PunishmentProcedureStage.REQUIRE_TEXT) {
            event.setCancelled(true);

            if (event.getMessage().equalsIgnoreCase("cancel")) {
                PunishmentProcedure.getProcedures().remove(procedure);
                event.getPlayer().sendMessage(CC.RED + "You have cancelled the punishment procedure.");
                return;
            }

            if (procedure.getType() == PunishmentProcedureType.PARDON) {
                new ConfirmMenu(CC.YELLOW + "Pardon this punishment?", (TypeCallback<Boolean>) data -> {
                    if (data) {
                        procedure.getPunishment().setPardonedBy(event.getPlayer().getUniqueId());
                        procedure.getPunishment().setPardonedAt(System.currentTimeMillis());
                        procedure.getPunishment().setPardonedReason(event.getMessage());
                        procedure.getPunishment().setPardoned(true);
                        procedure.finish();

                        event.getPlayer().sendMessage(CC.GREEN + "The punishment has been pardoned.");
                    } else {
                        event.getPlayer().sendMessage(CC.RED + "You did not confirm to pardon the punishment.");
                    }
                }, true) {
                    @Override
                    public void onClose(Player player) {
                        if (!isClosedByMenu()) {
                            event.getPlayer().sendMessage(CC.RED + "You did not confirm to pardon the punishment.");
                        }
                    }
                }.openMenu(event.getPlayer());
            }
        }
    }
}
