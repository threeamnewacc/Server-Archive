package cc.patrone.practice.command;

import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.player.PlayerState;
import cc.patrone.practice.player.PracticeProfile;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;

@AllArgsConstructor
public class LeaderboardsCommand {

    private PracticePlugin plugin;

    @Command(name = "leaderboards", aliases = "lbs", inGameOnly = true)
    public void leaderboards(CommandArgs args) {
        PracticeProfile practiceProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(args.getPlayer());
        if (practiceProfile.getPlayerState() != PlayerState.LOBBY) {
            args.getSender().sendMessage(ChatColor.RED + "You must in the lobby to do this.");
            return;
        }
        args.getPlayer().openInventory(this.plugin.getManagerHandler().getInventoryManager().getLeaderboardsInventory());
        return;
    }

}
