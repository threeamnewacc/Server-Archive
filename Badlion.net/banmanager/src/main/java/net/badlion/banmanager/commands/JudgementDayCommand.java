package net.badlion.banmanager.commands;

import net.badlion.banmanager.BanManager;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.io.IOUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class JudgementDayCommand implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        BanManager.isAllowedMultipleConsoleBans = true;
        BanManager.useLineSeparators = false;

        new BukkitRunnable() {
            public void run() {
                File file = new File(BanManager.plugin.getDataFolder(), "judgement_day.txt");
                FileInputStream in = null;
                try {
                    in = new FileInputStream(file);
                    final List<String> lines = IOUtils.readLines(in);
                    new BukkitRunnable() {
                        int counter = 0;
                        public void run() {
                            if (counter == lines.size()) {
                                BanManager.isAllowedMultipleConsoleBans = false;
                                this.cancel();
                                sender.sendMessage(ChatColor.GREEN + "Done");
                                return;
                            }

                            String banCommand = lines.get(counter++);
                            BanManager.plugin.getServer().dispatchCommand(BanManager.plugin.getServer().getConsoleSender(), banCommand);
                        }
                    }.runTaskTimer(BanManager.plugin, 7, 7);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.closeQuietly(in);
                }
            }
        }.runTaskAsynchronously(BanManager.plugin);

        sender.sendMessage(ChatColor.GREEN + "Started");
        return true;
    }

}
