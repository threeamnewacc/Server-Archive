package me.devkevin.practice.util;

import com.google.common.base.Strings;
import org.bukkit.ChatColor;

/**
 * Copyright 12/01/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class ProgressBar {

    public static String getBar(int current, int total) {
        return ProgressBar.getProgressBar(current, total, 40, '\u258e', ChatColor.GREEN, ChatColor.GRAY);
    }

    public static String getProgressBar(int current, int max, int totalBars, char symbol, ChatColor completedColor, ChatColor notCompletedColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return Strings.repeat("" + completedColor + symbol, progressBars)
                + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
    }
}
