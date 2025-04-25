package us.zonix.core.punishment.helpers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;
import us.zonix.core.profile.Profile;
import us.zonix.core.shared.api.callback.Callback;
import us.zonix.core.util.UUIDType;

import java.util.StringJoiner;
import java.util.UUID;

public class AltsHelper {

    private CommandSender sender;

    private UUID uuid;
    private String name;

    public AltsHelper(CommandSender sender, String name) {
        this.sender = sender;
        this.name = name;

        this.getPlayerInformation((useless) -> {
            if (this.uuid == null || this.name == null) {
                sender.sendMessage(ChatColor.RED + "Failed to find that player.");
            }
            else {
                this.attempt();
            }
        });
    }

    private void getPlayerInformation(Callback callback) {
        Player player = Bukkit.getPlayer(this.name);

        if (player != null) {
            this.uuid = player.getUniqueId();
            this.name = player.getName();

            callback.callback(null);
        }
        else {
            this.sender.sendMessage(ChatColor.GRAY + "(Resolving player information...)");

            Profile.getPlayerInformation(this.name, this.sender, (retrieved) -> {
                if (retrieved != null) {
                    uuid = UUIDType.fromString(retrieved.getAsJsonObject().get("uuid").getAsString());
                }

                callback.callback(null);
            });
        }
    }

    private void attempt() {
        new BukkitRunnable() {
            public void run() {
                Profile profile = Profile.getByUuid(uuid);
                profile.loadProfileAlts();

                if (profile.getAlts().size() == 0) {
                    sender.sendMessage(ChatColor.RED + "That player doesn't have any alts.");
                    return;
                }

                StringJoiner alts = new StringJoiner(", ");

                for (UUID uuid : profile.getAlts()) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

                    if (offlinePlayer != null && offlinePlayer.getName() != null) {
                        alts.add(offlinePlayer.getName());
                    }
                }

                sender.sendMessage(ChatColor.RED + name + "'s Alts: " + ChatColor.WHITE + alts.toString());
            }
        }.runTaskAsynchronously(CorePlugin.getInstance());
    }

}
