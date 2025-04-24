// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.manager;

import java.beans.ConstructorProperties;
import club.mineman.core.api.request.RequestCallback;
import club.mineman.core.api.APIMessage;
import club.mineman.core.mineman.Mineman;
import club.mineman.core.util.finalutil.StringUtil;
import org.json.simple.JSONObject;
import club.mineman.core.api.request.AbstractCallback;
import club.mineman.core.api.impl.PunishmentRequest;
import club.mineman.core.util.finalutil.TimeUtil;
import java.sql.Timestamp;
import club.mineman.core.command.punish.PunishCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import club.mineman.core.util.finalutil.PlayerUtil;
import club.mineman.core.util.finalutil.CC;
import club.mineman.core.CorePlugin;

public class PunishmentManager
{
    private final CorePlugin plugin;
    
    private void broadcastPunishment(final String type, final String reason, String punished, final String punisher, final boolean global) {
        final Player player = this.plugin.getServer().getPlayer(punished);
        if (player != null) {
            punished = player.getName();
        }
        if (global) {
            this.plugin.getServer().broadcastMessage(CC.RED + punished + " was " + type + " by " + punisher + " for " + reason + ".");
        }
        else {
            PlayerUtil.messageStaff(CC.RED + "STAFF ONLY: " + punished + " was " + type + " by " + punisher + " for " + reason + ".");
        }
    }
    
    public void punish(final CommandSender punisher, final PunishCommand.PunishType type, final String target, final String reason, final String ip, final Timestamp expiry, final boolean silent, final boolean temporary) {
        final int id = (punisher instanceof Player) ? this.plugin.getPlayerManager().getPlayer(((Player)punisher).getUniqueId()).getId() : -1;
        String finalType;
        if (type.getName().toLowerCase().startsWith("un")) {
            finalType = type.getName();
        }
        else if (temporary) {
            finalType = "temp-" + type.getName();
        }
        else {
            finalType = "perm-" + type.getName();
        }
        if (type == PunishCommand.PunishType.KICK && this.plugin.getServer().getPlayer(target) == null) {
            punisher.sendMessage(CC.RED + "Player not found.");
            return;
        }
        final Timestamp finalExpiry = expiry;
        final String finalExpiryTime = (finalExpiry == null) ? "" : TimeUtil.millisToRoundedTime(Math.abs(System.currentTimeMillis() - finalExpiry.getTime()));
        this.plugin.getRequestManager().sendRequest(new PunishmentRequest(finalExpiry, ip, reason, target, finalType, id), new AbstractCallback("Error executing punishment (" + type.getName() + ") for " + target + ".") {
            @Override
            public void callback(final JSONObject data) {
                final String s;
                final String response = s = (String)data.get((Object)"response");
                switch (s) {
                    case "player-never-joined": {
                        punisher.sendMessage(CC.RED + "That player has never joined!");
                        break;
                    }
                    case "invalid-player": {
                        punisher.sendMessage(CC.RED + "That player isn't online!");
                        break;
                    }
                    case "not-muted": {
                        punisher.sendMessage(CC.RED + "That player isn't muted!");
                        break;
                    }
                    case "not-banned": {
                        punisher.sendMessage(CC.RED + "That player isn't banned!");
                        break;
                    }
                    case "success": {
                        final Player targetPlayer = PunishmentManager.this.plugin.getServer().getPlayer(target);
                        String broadcast = null;
                        final Mineman mineman = (targetPlayer != null) ? PunishmentManager.this.plugin.getPlayerManager().getPlayer(targetPlayer.getUniqueId()) : null;
                        switch (type) {
                            case BLACKLIST: {
                                broadcast = "blacklisted";
                                if (targetPlayer != null) {
                                    targetPlayer.kickPlayer(StringUtil.BLACKLIST);
                                    break;
                                }
                                break;
                            }
                            case UNBLACKLIST: {
                                broadcast = "un-blacklisted";
                                break;
                            }
                            case IPBAN: {
                                broadcast = "ip-banned";
                                if (targetPlayer != null) {
                                    targetPlayer.kickPlayer(StringUtil.IP_BAN);
                                    break;
                                }
                                break;
                            }
                            case BAN: {
                                broadcast = (temporary ? ("temporarily banned for " + finalExpiryTime) : "permanently banned");
                                if (targetPlayer != null) {
                                    targetPlayer.kickPlayer(temporary ? String.format(StringUtil.TEMPORARY_BAN, finalExpiryTime) : StringUtil.PERMANENT_BAN);
                                    break;
                                }
                                break;
                            }
                            case KICK: {
                                broadcast = "kicked";
                                if (targetPlayer != null) {
                                    targetPlayer.kickPlayer(CC.RED + "You were kicked: " + reason);
                                    break;
                                }
                                break;
                            }
                            case UNBAN: {
                                broadcast = "unbanned";
                                break;
                            }
                            case UNMUTE: {
                                broadcast = "unmuted";
                                if (mineman != null) {
                                    mineman.setMuted(false);
                                    mineman.setMuteTime(new Timestamp(0L));
                                    break;
                                }
                                break;
                            }
                            case MUTE: {
                                broadcast = (temporary ? ("temporarily muted for " + finalExpiryTime) : "permanently muted");
                                if (mineman != null) {
                                    mineman.setMuted(true);
                                    mineman.setMuteTime(finalExpiry);
                                    break;
                                }
                                break;
                            }
                        }
                        if (!type.getName().startsWith("un")) {
                            PunishmentManager.this.broadcastPunishment(broadcast, reason, target, punisher.getName(), silent);
                            break;
                        }
                        punisher.sendMessage(CC.GREEN + target + " was " + broadcast + " for " + reason + ".");
                        break;
                    }
                    default: {
                        System.out.println("Punish error :V " + data.toJSONString());
                        punisher.sendMessage(CC.RED + "An error has occurred. Please notify an administrator.");
                        break;
                    }
                }
            }
        });
    }
    
    @ConstructorProperties({ "plugin" })
    public PunishmentManager(final CorePlugin plugin) {
        this.plugin = plugin;
    }
}
