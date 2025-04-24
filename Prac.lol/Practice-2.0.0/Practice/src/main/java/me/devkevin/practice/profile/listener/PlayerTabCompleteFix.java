package me.devkevin.practice.profile.listener;

import me.devkevin.practice.Practice;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.util.StringUtil;

import java.util.List;

/**
 * Copyright 19/02/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class PlayerTabCompleteFix implements Listener {

    private final Practice plugin = Practice.getInstance();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChatTabComplete(PlayerChatTabCompleteEvent event) {
        List<String> completions = (List<String>) event.getTabCompletions();
        completions.clear();
        String token = event.getLastToken();
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (StringUtil.startsWithIgnoreCase(player.getName(), token)) {
                completions.add(player.getName());
            }
        }
    }
}
