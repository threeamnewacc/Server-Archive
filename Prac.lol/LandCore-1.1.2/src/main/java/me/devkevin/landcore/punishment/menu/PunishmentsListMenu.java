package me.devkevin.landcore.punishment.menu;

import lombok.AllArgsConstructor;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.punishment.Punishment;
import me.devkevin.landcore.punishment.PunishmentType;
import me.devkevin.landcore.punishment.procedure.PunishmentProcedure;
import me.devkevin.landcore.punishment.procedure.PunishmentProcedureStage;
import me.devkevin.landcore.punishment.procedure.PunishmentProcedureType;
import me.devkevin.landcore.utils.item.ItemBuilder;
import me.devkevin.landcore.utils.menu.Button;
import me.devkevin.landcore.utils.menu.pagination.PaginatedMenu;
import me.devkevin.landcore.utils.message.CC;
import me.devkevin.landcore.utils.time.TimeUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 09/02/2023 @ 3:53
 * PunishmentsListMenu / me.devkevin.landcore.punishment.menu / LandCore
 */
@AllArgsConstructor
public class PunishmentsListMenu extends PaginatedMenu {

    private CoreProfile profile;
    private PunishmentType punishmentType;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&6" + punishmentType.getTypeData().getReadable() + " &7- &f" + profile.getGrant().getRank().getColor() + profile.getName();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (Punishment punishment : profile.getPunishments()) {
            if (punishment.getType() == punishmentType) {
                buttons.put(buttons.size(), new PunishmentInfoButton(punishment));
            }
        }

        return buttons;
    }

    @AllArgsConstructor
    private class PunishmentInfoButton extends Button {

        private Punishment punishment;

        @Override
        public ItemStack getButtonItem(Player player) {
            String addedBy = "Console";

            if (punishment.getAddedBy() != null) {
                try {
                    CoreProfile addedByProfile = LandCore.getInstance().getProfileManager().getProfile(punishment.getAddedBy());
                    addedBy = addedByProfile.getName();
                } catch (Exception e) {
                    addedBy = "Could not fetch...";
                }
            }

            List<String> lore = new ArrayList<>();

            lore.add(CC.BOARD_SEPARATOR);
            lore.add("&eAdded by: &c" + addedBy);
            lore.add("&eAdded for: &c" + punishment.getAddedReason());

            if (punishment.isActive() && !punishment.isPermanent() && punishment.getDuration() != -1) {
                lore.add("&eDuration: &c" + punishment.getTimeRemaining());
            }

            if (punishment.isPardoned()) {
                String removedBy = "Console";

                if (punishment.getPardonedBy() != null) {
                    try {
                        CoreProfile removedByProfile = LandCore.getInstance().getProfileManager().getProfile(punishment.getPardonedBy());
                        removedBy = removedByProfile.getName();
                    } catch (Exception e) {
                        removedBy = "Could not fetch...";
                    }
                }

                lore.add(CC.BOARD_SEPARATOR);
                lore.add("&ePardoned at: &c" + TimeUtil.dateToString(new Date(punishment.getPardonedAt())));
                lore.add("&ePardoned by: &c" + removedBy);
                lore.add("&ePardoned for: &c" + punishment.getPardonedReason());
            }

            lore.add(CC.BOARD_SEPARATOR);

            if (!punishment.isPardoned() && punishment.getType().isCanBePardoned()) {
                lore.add("&eRight click to pardon this punishment");
                lore.add(CC.BOARD_SEPARATOR);
            }

            return new ItemBuilder(Material.PAPER)
                    .name("&6" + TimeUtil.dateToString(new Date(punishment.getAddedAt())))
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            if (clickType == ClickType.RIGHT && !punishment.isPardoned() && punishment.getType().isCanBePardoned()) {
                PunishmentProcedure procedure = new PunishmentProcedure(player, profile, PunishmentProcedureType.PARDON, PunishmentProcedureStage.REQUIRE_TEXT);
                procedure.setPunishment(punishment);

                player.sendMessage(CC.GREEN + "Type a reason for pardoning this punishment in chat...");
                player.closeInventory();
            }
        }
    }
}
