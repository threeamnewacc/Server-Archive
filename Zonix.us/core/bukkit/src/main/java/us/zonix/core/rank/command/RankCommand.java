package us.zonix.core.rank.command;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;
import us.zonix.core.api.callback.AbstractBukkitCallback;
import us.zonix.core.api.request.PlayerRequest;
import us.zonix.core.profile.Profile;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.Clickable;
import us.zonix.core.util.UUIDType;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

import java.util.UUID;

public class RankCommand extends BaseCommand {

    @Command(name = "rank", rank = Rank.MANAGER)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length < 2) {
            Clickable clickable = new Clickable(ChatColor.RED + "Usage: /rank [target] [rank]", ChatColor.YELLOW + "Give a player a rank.", "");

            if (sender instanceof Player) {
                clickable.sendToPlayer((Player) sender);
            }

            return;
        }

        Rank rank;

        try {
            if (args[1].equalsIgnoreCase("ADMIN")) {
                rank = Rank.ADMINISTRATOR;
            } else if (args[1].equalsIgnoreCase("MOD")) {
                rank = Rank.MODERATOR;
            } else if (args[1].equalsIgnoreCase("TMOD") || args[1].equalsIgnoreCase("TRIALMOD")) {
                rank = Rank.TRIAL_MOD;
            } else if (args[1].equalsIgnoreCase("SRMOD")) {
                rank = Rank.SENIOR_MODERATOR;
            } else if (args[1].equalsIgnoreCase("DEV")) {
                rank = Rank.DEVELOPER;
            } else if (args[1].equalsIgnoreCase("YOUTUBE") || args[1].equalsIgnoreCase("YOUTUBER") || args[1].equalsIgnoreCase("YT")) {
                rank = Rank.MEDIA;
            } else if (args[1].equalsIgnoreCase("MEDIAOWNER")) {
                rank = Rank.MEDIA_OWNER;
            } else if (args[1].equalsIgnoreCase("PLATADMIN")) {
                rank = Rank.PLATFORM_ADMINISTRATOR;
            } else if (args[1].equalsIgnoreCase("EGIRL") || args[1].equalsIgnoreCase("GURL")) {
                rank = Rank.E_GIRL;
            } else {
                rank = Rank.valueOf(args[1].toUpperCase());
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Could not parse that rank.");
            return;
        }

        if (sender instanceof Player && Profile.getByUuid(((Player) sender).getUniqueId()).getRank() == Rank.MANAGER) {
            if (rank.isAboveOrEqual(Rank.MANAGER) || rank == Rank.MEDIA_OWNER) {
                sender.sendMessage(ChatColor.RED + "You are not allowed to set up ranks above Admin.");
                return;
            }
        }

        new BukkitRunnable() {
            public void run() {
                Profile.getPlayerInformation(args[0], sender, new AbstractBukkitCallback() {
                    @Override
                    public void callback(JsonElement object) {
                        if (object == null) {
                            sender.sendMessage(ChatColor.RED + "Failed to find that player.");
                        } else {
                            JsonObject o = object.getAsJsonObject();

                            UUID uuid = UUIDType.fromString(o.get("uuid").getAsString());
                            String name = o.get("name").getAsString();

                            main.getRequestProcessor().sendRequestAsync(new PlayerRequest.UpdateRankRequest(uuid, rank));
                            main.getRedisManager().writeRank(uuid, rank);

                            sender.sendMessage(ChatColor.GREEN + "Updated " + name + "'s rank to " + rank.getName() + ".");
                        }
                    }

                    @Override
                    public void onError(String message) {
                        this.callback(null);
                    }
                });
            }
        }.runTaskAsynchronously(CorePlugin.getInstance());
    }

}
