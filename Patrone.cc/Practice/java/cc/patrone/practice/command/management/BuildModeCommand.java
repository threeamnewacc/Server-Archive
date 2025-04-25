package cc.patrone.practice.command.management;

import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import cc.patrone.practice.PracticePlugin;
import cc.patrone.practice.player.PracticeProfile;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;

@AllArgsConstructor
public class BuildModeCommand {

    private PracticePlugin plugin;

    @Command(name = "buildmode", permission = "practice.command.buildmode", inGameOnly = true)
    public void buildMode(CommandArgs args) {
        PracticeProfile practiceProfile = this.plugin.getManagerHandler().getProfileManager().getProfile(args.getPlayer());
        practiceProfile.setBuildMode(!practiceProfile.isBuildMode());
        args.getSender().sendMessage(practiceProfile.isBuildMode() ? ChatColor.GREEN + "Build mode enabled." : ChatColor.RED + "Build mode disabled.");
        return;
    }

}
