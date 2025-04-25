package cc.patrone.practice.command;

import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.player.PracticeProfile;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;

@AllArgsConstructor
public class ToggleScoreboardCommand {

    private PracticePlugin plugin;

    @Command(name = "togglescoreboard", aliases = {"tsb", "togglesidebar"}, inGameOnly = true)
    public void toggleScoreboard(CommandArgs args) {
        PracticeProfile practiceProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(args.getPlayer());
        practiceProfile.setScoreboardToggled(!practiceProfile.isScoreboardToggled());
        args.getSender().sendMessage(practiceProfile.isScoreboardToggled() ? ChatColor.RED + "You have disabled your scoreboard." : ChatColor.GREEN + "You have enabled your scoreboard.");
        return;
    }

}
