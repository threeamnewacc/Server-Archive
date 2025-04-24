package me.devkevin.practice.arena.menu;

import lombok.AllArgsConstructor;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.arena.menu.buttons.ArenaCopyButton;
import me.devkevin.practice.arena.standalone.StandaloneArena;
import me.devkevin.practice.util.menu.Button;
import me.devkevin.practice.util.menu.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 31/12/2021 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@AllArgsConstructor
public class ArenaCopyMenu extends Menu {

    private final Arena arena;

    @Override
    public String getTitle(Player player) {
        return "Arena Copies";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int i = 0;
        for (StandaloneArena arenaCopy : Practice.getInstance().getArenaManager().getArena(arena.getName()).getStandaloneArenas()) {
            buttons.put(buttons.size(), new ArenaCopyButton(i, arena, arenaCopy));
            i++;
        }

        return buttons;
    }
}
