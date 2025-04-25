package us.zonix.core.punishment.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;
import us.zonix.core.api.callback.AbstractBukkitCallback;
import us.zonix.core.api.request.PunishmentRequest;
import us.zonix.core.profile.Profile;
import us.zonix.core.punishment.Punishment;
import us.zonix.core.punishment.PunishmentType;
import us.zonix.core.shared.api.callback.Callback;
import us.zonix.core.util.UUIDType;

import java.util.UUID;

public class PunishmentHelper {

    private static CorePlugin main = CorePlugin.getInstance();

    private CommandSender sender;
    private UUID senderUuid;
    private String senderName;

    private UUID uuid;
    private String name;

    private PunishmentType type;
    private String reason;
    private Long duration;
    private boolean silent;
    private boolean undo;

    public PunishmentHelper(CommandSender sender, String name, PunishmentType type, String reason, Long duration, boolean silent, boolean undo) {
        this.type = type;
        this.sender = sender;
        this.name = name;
        this.reason = reason;
        this.duration = duration;
        this.silent = silent;
        this.undo = undo;

        if (sender instanceof Player) {
            Player player = (Player) sender;

            this.senderUuid = player.getUniqueId();
            this.senderName = player.getName();
        }
        else {
            this.senderName = sender.getName();
        }

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

                if (type == PunishmentType.TEMPBAN || type == PunishmentType.BAN) {
                    if (undo && !profile.isBanned()) {
                        sender.sendMessage(ChatColor.RED + name + " is not banned!");
                        return;
                    }
                    else if (!undo && profile.isBanned()){
                        sender.sendMessage(ChatColor.RED + name + " is banned!");
                        return;
                    }
                }
                else if (type == PunishmentType.BLACKLIST) {
                    if (undo && !profile.isBlacklisted()) {
                        sender.sendMessage(ChatColor.RED + name + " is not blacklisted!");
                        return;
                    }
                    else if (!undo && profile.isBlacklisted()) {
                        sender.sendMessage(ChatColor.RED + name + " is blacklisted!");
                        return;
                    }
                }
                else {
                    if (undo && !profile.isMuted()) {
                        sender.sendMessage(ChatColor.RED + name + " is not muted!");
                        return;
                    }
                    else if (!undo && profile.isMuted()) {
                        sender.sendMessage(ChatColor.RED + name + " is muted!");
                        return;
                    }
                }

                Punishment punishment;

                if (undo) {
                    if (type == PunishmentType.TEMPBAN || type == PunishmentType.BAN) {
                        punishment = profile.getBannedPunishment();
                    }
                    else if (type == PunishmentType.BLACKLIST) {
                        punishment = profile.getBlacklistedPunishment();
                    }
                    else {
                        punishment = profile.getMutedPunishment();
                    }

                    if (punishment == null) {
                        sender.sendMessage(ChatColor.RED + "Could not find existing punishment...");
                        return;
                    }

                    punishment.setRemovedAt(System.currentTimeMillis());
                    punishment.setRemovedBy(senderUuid);
                    punishment.setRemovedReason(reason);

                    main.getRequestProcessor().sendRequestAsync(new PunishmentRequest.RemoveRequest(punishment.toJson()));
                    main.getRedisManager().writePunishment(punishment, name, senderName, silent);
                }
                else {
                    JsonObject object = new JsonObject();

                    if(type != null) {
                        object.addProperty("type", type.name());
                    }

                    object.addProperty("uuid", uuid.toString());
                    object.addProperty("added_by", senderUuid == null ? null : senderUuid.toString());
                    object.addProperty("reason", reason);
                    object.addProperty("duration", duration);

                    main.getRequestProcessor().sendRequestAsync(new PunishmentRequest.InsertRequest(object), new AbstractBukkitCallback() {
                        @Override
                        public void callback(JsonElement response) {
                            if (response == null || response.isJsonNull() || response.isJsonPrimitive()) {
                                sender.sendMessage(ChatColor.RED + "Failed to contact the database...");
                                return;
                            }

                            main.getRedisManager().writePunishment(Punishment.fromJson(response.getAsJsonObject()), name, senderName, silent);
                        }

                        @Override
                        public void onError(String message) {
                            super.onError(message);
                            this.callback(null);
                        }
                    });
                }
            }
        }.runTaskAsynchronously(CorePlugin.getInstance());
    }

}
