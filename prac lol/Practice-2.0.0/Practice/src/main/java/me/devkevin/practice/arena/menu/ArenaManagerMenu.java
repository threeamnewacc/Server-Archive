package me.devkevin.practice.arena.menu;

import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.menu.buttons.ArenaButton;
import me.devkevin.practice.util.ItemBuilder;
import me.devkevin.practice.util.menu.Button;
import me.devkevin.practice.util.menu.pagination.PageButton;
import me.devkevin.practice.util.menu.pagination.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 31/12/2021 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class ArenaManagerMenu extends PaginatedMenu {

    private final Practice plugin = Practice.getInstance();

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Arena Management";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        this.plugin.getArenaManager().getArenas().forEach((s, arena) -> buttons.put(buttons.size(), new ArenaButton(arena)));

        return buttons;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(0, new PageButton(-1, this));
        buttons.put(8, new PageButton(1, this));

        bottomTopButtons(false, buttons, new ItemBuilder(Material.STAINED_GLASS_PANE).name(" ").durability(15).build());

        return buttons;
    }

    @Override
    public int getSize() {
        return 9 * 5;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 9 * 3;
    }
}

