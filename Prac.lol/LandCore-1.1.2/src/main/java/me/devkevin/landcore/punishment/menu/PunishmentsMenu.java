package me.devkevin.landcore.punishment.menu;

import lombok.AllArgsConstructor;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.punishment.PunishmentType;
import me.devkevin.landcore.utils.item.ItemBuilder;
import me.devkevin.landcore.utils.menu.Button;
import me.devkevin.landcore.utils.menu.Menu;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 09/02/2023 @ 3:52
 * PunishmentsMenu / me.devkevin.landcore.punishment.menu / LandCore
 */
@AllArgsConstructor
public class PunishmentsMenu extends Menu {

    private CoreProfile profile;

    @Override
    public String getTitle(Player player) {
        return "&6Punishments of " + profile.getGrant().getRank().getColor() + profile.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(9, new SelectPunishmentTypeButton(profile, PunishmentType.BLACKLIST));
        buttons.put(11, new SelectPunishmentTypeButton(profile, PunishmentType.BAN));
        buttons.put(13, new SelectPunishmentTypeButton(profile, PunishmentType.MUTE));
        buttons.put(15, new SelectPunishmentTypeButton(profile, PunishmentType.WARN));
        buttons.put(17, new SelectPunishmentTypeButton(profile, PunishmentType.KICK));

        return buttons;
    }

    @Override
    public int getSize() {
        return 27;
    }

    @AllArgsConstructor
    private static class SelectPunishmentTypeButton extends Button {

        private CoreProfile profile;
        private PunishmentType punishmentType;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.WOOL)
                    .name(punishmentType.getTypeData().getColor() + CC.B + punishmentType.getTypeData().getReadable())
                    .lore(CC.GRAY + profile.getPunishmentCountByType(punishmentType) + " " + (punishmentType.getTypeData().getReadable().toLowerCase()) + " on record")
                    .durability(punishmentType.getTypeData().getDurability())
                    .build();
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarButton) {
            new PunishmentsListMenu(profile, punishmentType).openMenu(player);
        }
    }

}

