package me.devkevin.practice.hcf.menu;

import club.inverted.chatcolor.CC;
import me.devkevin.practice.hcf.menu.button.ClassSelectionButton;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.util.menu.Button;
import me.devkevin.practice.util.menu.pagination.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 25/02/2023 @ 1:14
 * ClassSelectionMenu / me.devkevin.practice.hcf.menu / Practice
 */
public class ClassSelectionMenu extends PaginatedMenu {

    @Override
    public void setUpdateAfterClick(boolean updateAfterClick) {
        super.setUpdateAfterClick(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return CC.translate("&8HCF Team");
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party != null) {
            party.getMembers().forEach(member -> buttonMap.put(buttonMap.size(), new ClassSelectionButton(member)));
        }

        return buttonMap;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> map = new HashMap<>();
        Button button = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7, " ");

        for (int i = 1; i < 8; i++) {
            map.put(i, button);
        }

        return map;
    }
}

