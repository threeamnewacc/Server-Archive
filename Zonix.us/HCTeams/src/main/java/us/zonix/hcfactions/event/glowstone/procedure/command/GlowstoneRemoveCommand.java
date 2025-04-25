package us.zonix.hcfactions.event.glowstone.procedure.command;

import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.util.PluginCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import us.zonix.hcfactions.event.EventManager;
import us.zonix.hcfactions.event.Event;
import us.zonix.hcfactions.event.glowstone.GlowstoneEvent;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;

public class GlowstoneRemoveCommand extends PluginCommand {

    @Command(name = "glowstone.remove", aliases = {"glowstone.delete", "glowstoneremove", "removeglowstone"}, permission = Rank.DEVELOPER)
    public void onCommand(CommandArgs command) {

        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/glowstone remove <zone>");
            return;
        }

        Event event = EventManager.getInstance().getByName(args[0]);

        if (event == null || (!(event instanceof GlowstoneEvent))) {
            sender.sendMessage(ChatColor.RED + "Please specify a valid Glowstone Mountain.");
            return;
        }


        GlowstoneEvent glowstoneEvent = (GlowstoneEvent) event;
        sender.sendMessage(ChatColor.YELLOW + "(" + glowstoneEvent.getName() + ") Glowstone Mountain has been removed.");
        glowstoneEvent.remove();

    }
}
