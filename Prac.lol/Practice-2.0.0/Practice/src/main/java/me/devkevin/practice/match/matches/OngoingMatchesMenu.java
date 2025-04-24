package me.devkevin.practice.match.matches;

import club.inverted.chatcolor.CC;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.matches.buttons.OngoingMatchButton;
import me.devkevin.practice.match.matches.buttons.RefreshButton;
import me.devkevin.practice.util.menu.Button;
import me.devkevin.practice.util.menu.pagination.PageButton;
import me.devkevin.practice.util.menu.pagination.PaginatedMenu;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static me.devkevin.practice.util.PlayerUtil.PLACEHOLDER_ITEM;

/**
 * Copyright 14/04/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class OngoingMatchesMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return CC.translate("&8Current matches &7(" + this.plugin.getMatchManager().getMatches().values().size() + ")");
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new ConcurrentHashMap<>();

        int slot = 0;

        for (Map.Entry<UUID, Match> entry : this.plugin.getMatchManager().getMatches().entrySet()) {
            Match match = entry.getValue();
            if (!match.isParty() || !match.isPartyMatch() || !match.isFFA()) {
                buttons.put(slot, new OngoingMatchButton(match));
                slot++;
            }
        }

        return buttons;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new ConcurrentHashMap<>();

        buttons.put(0, new PageButton(-1, this));

        buttons.put(4, new RefreshButton(this));
        buttons.put(8, new PageButton(1, this));

        bottomTopButtons(false, buttons, PLACEHOLDER_ITEM);

        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * (3 + 1);
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 9 * 3;
    }


}
