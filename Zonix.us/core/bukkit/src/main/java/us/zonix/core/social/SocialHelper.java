package us.zonix.core.social;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.zonix.core.CorePlugin;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;

public class SocialHelper {

    private static CorePlugin main = CorePlugin.getInstance();

    public void sendMessage(Player from, Profile fromProfile, Player to, Profile toProfile, String message) {

        toProfile.setLastMessaged(from.getUniqueId());
        fromProfile.setLastMessaged(to.getUniqueId());

        from.sendMessage(main.getConfigFile()
                .getString("messages.player_send")
                .replace("%TO%", (toProfile != null ? toProfile.getRank().getColor() : "") + to.getName())
                .replace("%MESSAGE%", ChatColor.stripColor(message)));

        to.sendMessage(main.getConfigFile()
                .getString("messages.player_receive")
                .replace("%FROM%", (fromProfile != null ? fromProfile.getRank().getColor() : "") + from.getName())
                .replace("%MESSAGE%", ChatColor.stripColor(message)));

        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = Profile.getByUuid(player.getUniqueId());

            if (profile != null && profile.getRank().isAboveOrEqual(Rank.TRIAL_MOD) && profile.getOptions().isSocialSpy()) {
                player.sendMessage(main.getConfigFile()
                        .getString("messages.social_spy")
                        .replace("%FROM%", (fromProfile != null ? fromProfile.getRank().getColor() : "") + from.getName())
                        .replace("%TO%", (toProfile != null ? toProfile.getRank().getColor() : "") + to.getName())
                        .replace("%MESSAGE%", ChatColor.stripColor(message)));
            }
        }
    }
}
