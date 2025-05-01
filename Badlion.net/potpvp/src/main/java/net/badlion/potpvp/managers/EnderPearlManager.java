package net.badlion.potpvp.managers;

import net.badlion.potpvp.Party;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EnderPearlManager {

    private static Map<Player, EnderPearl> playerToPearl = new HashMap<>();

    public static void put(Player player, EnderPearl enderPearl) {
        EnderPearlManager.playerToPearl.put(player, enderPearl);
    }

    public static void remove(Collection<Player> players) {
        for (Player player : players) {
            EnderPearlManager.remove(player);
        }
    }

    public static void remove(Party party) {
        for (Player player : party.getPlayers()) {
            EnderPearlManager.remove(player);
        }
    }

    public static void remove(Player player) {
        EnderPearl enderPearl = EnderPearlManager.playerToPearl.remove(player);
        if (enderPearl != null) {
            enderPearl.remove();
        }
    }

}
