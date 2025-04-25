package cc.patrone.practice.command.management;

import cc.patrone.core.command.Command;
import cc.patrone.core.command.CommandArgs;
import cc.patrone.practice.PracticePlugin;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;

@AllArgsConstructor
public class SetEditorCommand {

    private PracticePlugin plugin;

    @Command(name = "seteditor", permission = "practice.command.seteditor", inGameOnly = true)
    public void setEditor(CommandArgs args) {
        this.plugin.getManagerHandler().getSettingsManager().setEditor(args.getPlayer().getLocation());
        args.getSender().sendMessage(ChatColor.GREEN + "You have set the editor.");
        return;
    }
}
