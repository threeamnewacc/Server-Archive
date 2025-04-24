// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command;

import org.bukkit.plugin.Plugin;
import club.mineman.core.util.finalutil.CC;
import club.mineman.core.util.finalutil.PlayerUtil;
import club.mineman.core.rank.Rank;
import org.bukkit.command.CommandSender;
import club.mineman.core.task.ShutdownTask;
import club.mineman.core.CorePlugin;
import org.bukkit.command.Command;

public class ShutdownCommand extends Command
{
    private final CorePlugin plugin;
    private ShutdownTask shutdownTask;
    
    public ShutdownCommand(final CorePlugin plugin) {
        super("shutdown");
        this.plugin = plugin;
        this.setDescription("Schedule the server to be shutdown.");
    }
    
    public boolean execute(final CommandSender sender, final String s, final String[] args) {
        if (!PlayerUtil.testPermission(sender, Rank.OWNER)) {
            return true;
        }
        if (args.length != 1) {
            sender.sendMessage(CC.RED + "Please use /shutdown <seconds | time | cancel>");
            return true;
        }
        if (args[0].equalsIgnoreCase("time")) {
            if (this.shutdownTask == null) {
                sender.sendMessage(CC.RED + "The server is not scheduled to shut down.");
            }
            else {
                sender.sendMessage(CC.GREEN + "The server will shutdown in " + this.shutdownTask.getSecondsUntilShutdown() + " seconds.");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("cancel")) {
            if (this.shutdownTask == null) {
                sender.sendMessage(CC.RED + "The server is not scheduled to shut down.");
            }
            else {
                this.shutdownTask.cancel();
                this.shutdownTask = null;
                sender.sendMessage(CC.RED + "The server shutdown has been canceled.");
            }
        }
        else {
            int seconds;
            try {
                seconds = Integer.parseInt(args[0]);
            }
            catch (final NumberFormatException e) {
                sender.sendMessage(CC.RED + "You must input a valid number!");
                return true;
            }
            if (seconds <= 0) {
                sender.sendMessage(CC.RED + "You must input a number greater than 0!");
                return true;
            }
            if (this.shutdownTask == null) {
                (this.shutdownTask = new ShutdownTask(this.plugin, seconds)).runTaskTimer((Plugin)this.plugin, 20L, 20L);
            }
            else {
                this.shutdownTask.setSecondsUntilShutdown(seconds);
            }
            sender.sendMessage(CC.GREEN + "The server will shutdown in " + seconds + " seconds.");
        }
        return true;
    }
}
