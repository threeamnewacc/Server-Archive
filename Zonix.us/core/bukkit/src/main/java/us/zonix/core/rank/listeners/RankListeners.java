package us.zonix.core.rank.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import us.zonix.core.CorePlugin;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;

public class RankListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (profile == null) {
            return;
        }

        if (CorePlugin.getInstance().getRedisManager().isChatSilenced() && !profile.getRank().isAboveOrEqual(Rank.TRIAL_MOD)) {
            player.sendMessage(ChatColor.RED + "Chat is currently muted.");
            event.setCancelled(true);
            return;
        }

        if (!profile.getRank().isAboveOrEqual(Rank.SILVER)) {
            long slowChat = CorePlugin.getInstance().getRedisManager().getChatSlowDownTime();

            if (System.currentTimeMillis() < profile.getChatCooldown()) {
                player.sendMessage(slowChat > 0L ? ChatColor.RED + "Chat is currently slowed down." : "Please wait, before typing again.");
                event.setCancelled(true);
                return;
            } else {
                profile.setChatCooldown(System.currentTimeMillis() + (slowChat > 0L ? slowChat : 3000L));
            }
        }

        event.setFormat("%1$s: %2$s");
        player.setDisplayName(player.getName());
        Rank rank = profile.getRank();

        String prefix = rank.isAboveOrEqual(Rank.SILVER) && !rank.isAboveOrEqual(Rank.BUILDER) && profile.getSymbol() != null ? profile.getSymbol().getPrefix() : rank == Rank.DEFAULT && profile.isBoughtSymbols() && profile.getSymbol() != null ? profile.getSymbol().getPrefix() + rank.getPrefix() : rank.getPrefix();
        String userTag = rank.isAboveOrEqual(Rank.BUILDER) ? prefix + rank.getColor() + rank.getName() + rank.getSuffix() + rank.getColor() + player.getName() + ChatColor.WHITE : prefix + rank.getColor() + rank.getSuffix() + rank.getColor() + player.getName() + ChatColor.WHITE;

        if (!player.getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', userTag))) {
            player.setDisplayName(ChatColor.translateAlternateColorCodes('&', userTag));
        }
    }

}
