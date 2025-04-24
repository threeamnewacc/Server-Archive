package me.devkevin.practice.arena.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.arena.menu.buttons.ArenaGenerateButton;
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
public class ArenaGenerationMenu extends Menu {

    private final Arena arena;

    @Getter
    private final int[] clonableAmounts = {1, 2, 3, 4, 5, 10, 15};

    @Override
    public String getTitle(Player player) {
        return "Arena Copies Generation";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int curr : clonableAmounts) {
            buttons.put(1 + buttons.size(), new ArenaGenerateButton(arena, curr));
        }

        return buttons;
    }
}

