package us.zonix.hcfactions.factions.commands;

import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.util.command.CommandArgs;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.profile.Profile;

/**
 * Copyright 2016 Alexander Maxwell
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Alexander Maxwell
 */
public class FactionDepositCommand extends FactionCommand {


    @Command(name = "f.deposit", aliases = {"faction.deposit", "factions.deposit", "f.d", "faction.d", "factions.d"}, inFactionOnly = true)
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(langConfig.getString("TOO_FEW_ARGS.DEPOSIT"));
            return;
        }

        Profile profile = Profile.getByPlayer(player);
        int amount;

        if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("a")) {
            amount = (int) Math.floor(profile.getBalance());
        } else {
            if (!(NumberUtils.isNumber(args[0]))) {
                player.sendMessage(langConfig.getString("ERROR.INVALID_NUMBER").replace("%STRING%", args[0]));
                return;
            }

            amount = (int) Math.floor(Double.valueOf(args[0]));

           if (amount > profile.getBalance()) {
                player.sendMessage(langConfig.getString("ERROR.NOT_ENOUGH_MONEY"));
                return;
           }
        }

        if (amount <= 0) {
            player.sendMessage(langConfig.getString("ERROR.INVALID_DEPOSIT_AMOUNT"));
            return;
        }

        profile.setBalance(profile.getBalance() - amount);

        PlayerFaction playerFaction = profile.getFaction();
        playerFaction.setBalance(playerFaction.getBalance() + amount);
        playerFaction.sendMessage(langConfig.getString("ANNOUNCEMENTS.FACTION.PLAYER_DEPOSIT_MONEY").replace("%PLAYER%", player.getName()).replace("%AMOUNT%", amount + ""));
    }
}
