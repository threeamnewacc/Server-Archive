package me.devkevin.landcore.commands.impl.staff;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.BaseCommand;
import me.devkevin.landcore.event.server.ServerShutdownCancelEvent;
import me.devkevin.landcore.event.server.ServerShutdownScheduleEvent;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.task.ShutdownTask;
import me.devkevin.landcore.utils.NumberUtil;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.command.CommandSender;

public class ShutdownCommand extends BaseCommand {
    private final LandCore plugin;

    public ShutdownCommand(LandCore plugin) {
        super("shutdown", Rank.ADMIN);
        this.plugin = plugin;
        setUsage(CC.RED + "Usage: /shutdown <seconds|cancel>");
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(usageMessage);
            return;
        }

        String arg = args[0];

        if (arg.equals("cancel")) {
            ShutdownTask task = plugin.getServerSettings().getShutdownTask();

            if (task == null) {
                sender.sendMessage(CC.RED + "There is no shutdown in progress.");
            } else {
                plugin.getServer().getPluginManager().callEvent(new ServerShutdownCancelEvent());

                task.cancel();
                plugin.getServerSettings().setShutdownTask(null);
                plugin.getServer().broadcastMessage(CC.GREEN + "The shutdown in progress has been cancelled by " + sender.getName() + ".");
            }
            return;
        }

        Integer seconds = NumberUtil.getInteger(arg);

        if (seconds == null) {
            sender.sendMessage(usageMessage);
        } else {
            if (seconds >= 5 && seconds <= 300) {
                plugin.getServer().getPluginManager().callEvent(new ServerShutdownScheduleEvent());

                ShutdownTask task = new ShutdownTask(seconds);

                plugin.getServerSettings().setShutdownTask(task);
                task.runTaskTimer(plugin, 0L, 20L);
            } else {
                sender.sendMessage(CC.RED + "Please enter a time between 5 and 300 seconds.");
            }
        }
    }
}
