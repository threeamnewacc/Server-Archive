package me.devkevin.landcore.faction.commands;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/03/2023 @ 2:11
 * FactionHelpCommand / me.devkevin.landcore.faction.commands / LandCore
 */
public class FactionHelpCommand extends PlayerCommand {

    public FactionHelpCommand(LandCore plugin) {
        super("faction");
        setAliases(Arrays.asList("f", "f help", "faction help"));
    }

    @Override
    public void execute(Player player, String[] args) {
        player.sendMessage(new String[] {
                CC.GRAY + CC.BOARD_SEPARATOR,
                CC.GOLD + CC.B + "Factions Help " + CC.GRAY + "-" + CC.GRAY + " Information on how to use faction commands",
                CC.GRAY + CC.BOARD_SEPARATOR,
                CC.GOLD + "General Commands:",
                CC.YELLOW + "/faction create " + CC.GRAY + "- Create a new faction",
                CC.YELLOW + "/faction leave " + CC.GRAY + "- Leave your current faction",
                CC.YELLOW + "/faction accept [faction|player] " + CC.GRAY + "- Accept clan invitation",
                CC.YELLOW + "/faction info [faction|player] " + CC.GRAY + "- View a clan's information",
                "",
                CC.GOLD + "Leader Commands:",
                CC.YELLOW + "/faction disband " + CC.GRAY + "- Disband your faction",
                CC.YELLOW + "/faction description <text> " + CC.GRAY + "- Set your faction's description",
                CC.YELLOW + "/faction password <password> " + CC.GRAY + "- Sets faction password",
                CC.YELLOW + "/faction promote <player> " + CC.GRAY + "- Promote a player",
                CC.YELLOW + "/faction demote <player> " + CC.GRAY + "- Demote a player",
                "",
                CC.GOLD + "Captain Commands:",
                CC.YELLOW + "/faction invite <player> " + CC.GRAY + "- Invite a player to join your faction",
                CC.YELLOW + "/faction kick <player> " + CC.GRAY + "- Kick a player from your faction",
                "",
                CC.GOLD + "Other Help:",
                CC.YELLOW + "To use " + CC.PINK + "faction chat" + CC.YELLOW + " /factionchat or /fc",
                CC.YELLOW + "Factions are limited to " + CC.PINK + "12 members" + CC.YELLOW + ".",
                CC.GRAY + CC.BOARD_SEPARATOR
        });
    }
}
