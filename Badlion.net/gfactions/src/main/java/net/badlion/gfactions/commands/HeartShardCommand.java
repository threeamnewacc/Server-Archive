package net.badlion.gfactions.commands;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.badlion.gberry.Gberry;
import net.badlion.gfactions.managers.DeathBanManager;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class HeartShardCommand implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "/heartshard [name|faction] [name here] [#]");
            return true;
        }

        int numOfShards = 0;
        try {
            numOfShards = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid # provided.");
            return true;
        }

        if (numOfShards < 1) {
            sender.sendMessage(ChatColor.RED + "Must give at least 1 shard");
            return true;
        }

        final int finalNumOfShards = numOfShards;
        BukkitUtil.runTaskAsync(new Runnable() {
            @Override
            public void run() {
                final UUID uuid = Gberry.getOfflineUUID(args[1]);
                if (uuid == null) {
                    sender.sendMessage(ChatColor.RED + "No user found.");
                    return;
                }

                BukkitUtil.runTask(new Runnable() {
                    @Override
                    public void run() {
                        if (args[0].equalsIgnoreCase("name")) {
                            DeathBanManager.addHeartShards(uuid, finalNumOfShards);
                        } else {
                            FPlayer fPlayer = FPlayers.i.get(uuid.toString());
                            Faction faction = fPlayer.getFaction();

                            // No wilderness
                            if (!faction.getId().equals("0")) {
                                for (FPlayer fp : faction.getFPlayers()) {
                                    DeathBanManager.addHeartShards(UUID.fromString(fp.getId()), finalNumOfShards);
                                }
                            }
                        }

                        sender.sendMessage(ChatColor.GREEN + "Heart Shards added.");
                    }
                });
            }
        });

        return true;
    }

}
