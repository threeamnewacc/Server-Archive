package me.devkevin.landcore.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 09/02/2023 @ 3:32
 * PunishmentType / me.devkevin.landcore.punishment / LandCore
 */
@Getter
@AllArgsConstructor
public enum PunishmentType {

    BLACKLIST("blacklisted", "unblacklisted", true, true, new PunishmentTypeData("Blacklists", ChatColor.DARK_RED, 14)),
    BAN("banned", "unbanned", true, true, new PunishmentTypeData("Bans", ChatColor.GOLD, 1)),
    MUTE("muted", "unmuted", false, true, new PunishmentTypeData("Mutes", ChatColor.YELLOW, 4)),
    WARN("warned", null, false, false, new PunishmentTypeData("Warnings", ChatColor.GREEN, 13)),
    KICK("kicked", null, false, false, new PunishmentTypeData("Kicks", ChatColor.GRAY, 7));

    private String context;
    private String undoContext;
    private boolean ban;
    private boolean canBePardoned;
    private PunishmentTypeData typeData;


    @Getter
    @AllArgsConstructor
    public static class PunishmentTypeData {
        private String readable;
        private ChatColor color;
        private int durability;
    }
}
