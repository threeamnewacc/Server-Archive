package me.devkevin.landcore.task;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.utils.message.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import xyz.refinedev.spigot.plugin;

public class BroadcastTask implements Runnable {
    private final LandCore plugin;

    private static final String[] MESSAGES = {
            CC.YELLOW + "Butterfly/double clicking is discouraged and may get you banned.",
            CC.YELLOW + "Check out our website to view statistics, leaderboards, the forum, and more: " + CC.GRAY + ChatColor.UNDERLINE + "http://prac.lol",
            CC.YELLOW + "Want a free rank? Vote for us on NameMC!\n" + CC.GRAY + ChatColor.UNDERLINE  + "https://namemc.com/server/prac.lol",
            CC.YELLOW + "Follow us on Twitter for giveaways, updates, and more!\n" + CC.GRAY + ChatColor.UNDERLINE  + "http://twitter.prac.lol",
            CC.YELLOW + "Join our Discord server!\n" + CC.GRAY + ChatColor.UNDERLINE  + "http://invite.gg/prac",
            CC.YELLOW + "Get cool perks and help support us by getting a rank on the store!\n" + CC.GRAY + ChatColor.UNDERLINE  + "https://store.prac.lol",
            CC.YELLOW + "Staff applications are open! Apply here: " + CC.GRAY + ChatColor.UNDERLINE  + "http://apply.prac.lol"
    };

    private int currentIndex = -1;
    private static final String LINE_SEPARATOR = "\n";

    public BroadcastTask(LandCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (++currentIndex >= MESSAGES.length) {
            currentIndex = 0;
        }

        String message = MESSAGES[currentIndex];

        String broadcastMessage = LINE_SEPARATOR + message + LINE_SEPARATOR;
        plugin.getServer().broadcastMessage(broadcastMessage);
    }
}
