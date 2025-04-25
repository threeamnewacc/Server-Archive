package us.zonix.hcfactions.event.koth.command;

import us.zonix.core.rank.Rank;
import us.zonix.hcfactions.event.EventManager;
import us.zonix.hcfactions.event.koth.KothEvent;
import us.zonix.hcfactions.util.DateUtil;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.event.Event;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class KothStartCommand extends PluginCommand {

    private static final long DEFAULT_DURATION = 900000;

    @Command(name = "koth.start", permission = Rank.MANAGER)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/koth start <koth> [<duration>]");
            return;
        }

        Event event = EventManager.getInstance().getByName(args[0]);

        if (event == null || (!(event instanceof KothEvent))) {
            sender.sendMessage(ChatColor.RED + "Please specify a valid KoTH.");
            return;
        }

        long capTime = DEFAULT_DURATION;

        if (args.length > 1) {
            try {
                capTime = System.currentTimeMillis() - DateUtil.parseDateDiff(args[1], false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        KothEvent koth = (KothEvent) event;

        if (koth.isActive()) {
            sender.sendMessage(ChatColor.RED + "KoTH " + koth.getName() + " is already active!");
            return;
        }

        koth.start(capTime);


    }
}
