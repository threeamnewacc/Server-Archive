package net.kohi.vaultbattle.type;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public enum TeamColor {

    RED, BLUE, YELLOW, GREEN;

    private TeamColor() {
    }

    public ChatColor toChatColor() {
        switch (this) {
            case RED:
                return ChatColor.RED;
            case BLUE:
                return ChatColor.BLUE;
            case YELLOW:
                return ChatColor.YELLOW;
            case GREEN:
                return ChatColor.GREEN;
        }
        return null;
    }


    public Color toColor() {
        switch (this) {
            case RED:
                return Color.RED;
            case BLUE:
                return Color.BLUE;
            case YELLOW:
                return Color.YELLOW;
            case GREEN:
                return Color.GREEN;
        }
        return null;
    }

}
