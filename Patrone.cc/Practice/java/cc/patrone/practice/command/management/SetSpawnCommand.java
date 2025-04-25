package cc.patrone.practice.command.management;

import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import cc.patrone.practice.PracticePlugin;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;

@AllArgsConstructor
public class SetSpawnCommand {

    private PracticePlugin plugin;

    @Command(name = "setspawn", permission = "practice.command.setspawn", inGameOnly = true)
    public void setSpawn(CommandArgs args) {
        this.plugin.getManagerHandler().getSettingsManager().setSpawn(args.getPlayer().getLocation());
        args.getSender().sendMessage(ChatColor.GREEN + "You have set the spawn.");
        return;
    }
}
