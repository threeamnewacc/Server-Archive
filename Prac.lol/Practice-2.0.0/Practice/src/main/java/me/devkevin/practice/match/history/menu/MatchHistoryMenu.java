package me.devkevin.practice.match.history.menu;

import lombok.AllArgsConstructor;
import club.inverted.chatcolor.CC;
import me.devkevin.practice.match.history.MatchLocatedData;
import me.devkevin.practice.match.history.menu.button.MatchHistoryButton;
import me.devkevin.practice.util.menu.Button;
import me.devkevin.practice.util.menu.pagination.PaginatedMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Copyright 02/01/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@AllArgsConstructor
public class MatchHistoryMenu extends PaginatedMenu {

    private final UUID uuid;
    private final List<MatchLocatedData> matchLocatedData;

    @Override
    public String getTitle(Player player) {
        return CC.GREEN + Bukkit.getOfflinePlayer(uuid).getName() + "'s Matches | " + getPage() + "/" + getPages(player);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "null";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> map = new HashMap<>();

        Button button = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, "");
        for (int i = 1; i < 8; i++) {
            map.put(i, button);
        }

        return map;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttonMap = new HashMap<>();

        this.matchLocatedData.stream().sorted(Comparator.comparing(MatchLocatedData::getDate).reversed()).forEach(matchData -> {
            buttonMap.put(buttonMap.size(), new MatchHistoryButton(matchData));
        });

        return buttonMap;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 18;
    }
}
