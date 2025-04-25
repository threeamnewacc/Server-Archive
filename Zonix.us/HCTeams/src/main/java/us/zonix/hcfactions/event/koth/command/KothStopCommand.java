package us.zonix.hcfactions.event.koth.command;

import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.event.EventManager;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.hcfactions.event.Event;
import us.zonix.hcfactions.event.koth.KothEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import us.zonix.hcfactions.event.EventManager;
import us.zonix.hcfactions.util.command.CommandArgs;

public class KothStopCommand extends PluginCommand {

    private static final long DEFAULT_DURATION = 900000;

    @Command(name = "koth.stop", permission = Rank.MANAGER)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/koth stop <koth>");
            return;
        }

        Event event = EventManager.getInstance().getByName(args[0]);

        if (event == null || (!(event instanceof KothEvent))) {
            sender.sendMessage(ChatColor.RED + "Please specify a valid KoTH.");
            return;
        }

        KothEvent koth = (KothEvent) event;

        if (!(koth.isActive())) {
            sender.sendMessage(ChatColor.RED + "KoTH " + koth.getName() + " isn't active!");
            return;
        }

        koth.stop(true);
    }
}
