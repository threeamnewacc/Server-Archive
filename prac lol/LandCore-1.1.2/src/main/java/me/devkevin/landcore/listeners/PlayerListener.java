package me.devkevin.landcore.listeners;

import com.google.common.collect.Maps;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.event.player.PlayerRankChangeEvent;
import me.devkevin.landcore.faction.profile.FactionProfile;
import me.devkevin.landcore.nametag.impl.InternalNametag;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.grant.Grant;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.punishment.Punishment;
import me.devkevin.landcore.server.ServerSettings;
import me.devkevin.landcore.utils.message.CC;
import me.devkevin.landcore.utils.message.Messages;
import me.devkevin.landcore.utils.time.TimeUtil;
import me.devkevin.landcore.utils.timer.Timer;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_8_R3.PlayerList;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.permissions.PermissionAttachment;
import xyz.haoshoku.nick.api.NickAPI;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
    private static final String[] DISALLOWED_PERMISSIONS = {
            "bukkit.command.version", "bukkit.command.plugins", "bukkit.command.help", "bukkit.command.tps",
            "minecraft.command.tell", "minecraft.command.me", "minecraft.command.help"
    };
    private final LandCore plugin;

    private void handleBan(AsyncPlayerPreLoginEvent event, Punishment punishment) {
        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
        event.setKickMessage(punishment.getKickMessage());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (plugin.getPlayerManager().isNameOnline(event.getName()) || plugin.getPlayerManager().getOnlineByIp(event.getAddress()) > 3) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CC.RED + "You're already online!");
        } else if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            CoreProfile profile = plugin.getProfileManager().createProfile(event.getName(), event.getUniqueId(), event.getAddress().getHostAddress());
            ServerSettings serverSettings = plugin.getServerSettings();

            if (serverSettings.getServerWhitelistMode().isProfileIneligible(profile)) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, serverSettings.getWhitelistMessage());
                return;
            }

            if (profile.getActiveBan() != null) {
                handleBan(event, profile.getActiveBan());
            }

            if (profile.getCurrentAddress() == null) {
                profile.setCurrentAddress(event.getAddress().getHostAddress());
            }

            if (!profile.getIpAddresses().contains(event.getAddress().getHostAddress())) {
                profile.getIpAddresses().add(event.getAddress().getHostAddress());
            }

            if (!profile.getCurrentAddress().equals(event.getAddress().getHostAddress())) {
                List<CoreProfile> alts = CoreProfile.getByIpAddress(event.getAddress().getHostAddress());

                for (CoreProfile alt : alts) {
                    if (alt.getActiveBan() != null) {
                        handleBan(event, alt.getActiveBan());
                        return;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        if (profile == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Messages.DATA_LOAD_FAIL);
            return;
        } else if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            plugin.getProfileManager().removeProfile(player.getUniqueId());
            return;
        }

        PermissionAttachment attachment = player.addAttachment(plugin);

        if (!profile.hasRank(Rank.ADMIN)) {
            for (String permission : DISALLOWED_PERMISSIONS) {
                attachment.setPermission(permission, false);
            }
        }

        profile.getRank().apply(player);

        if (profile.hasStaff()) {
            plugin.getStaffManager().addCachedStaff(profile);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();

        plugin.getPlayerManager().addPlayer(player);

        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        plugin.getStaffManager().hideVanishedStaffFromPlayer(player);

        if (profile.hasStaff()) {
            Map<String, Object> message = Maps.newHashMap();
            message.put("server", plugin.getServerName());
            message.put("sender", profile.getRank().getColor() + player.getName());

            plugin.getRedisMessenger().send("staff-join", message);
        }
    }

    private void onDisconnect(Player player) {
        plugin.getPlayerManager().removePlayer(player);

        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        // in case disconnect is somehow called twice
        if (profile == null) {
            return;
        }

        if (profile.isFrozen()) {
            Map<String, Object> message = Maps.newHashMap();

            message.put("server", plugin.getServerName());
            message.put("sender", profile.getRank().getColor() + player.getName());

            plugin.getRedisMessenger().send("frozen-disconnect", message);
        }

        if (profile.hasStaff()) {
            plugin.getStaffManager().removeCachedStaff(profile);

            Map<String, Object> message = Maps.newHashMap();
            message.put("server", plugin.getServerName());
            message.put("sender", profile.getRank().getColor() + player.getName());

            plugin.getRedisMessenger().send("staff-left", message);
        }

        profile.save(true);
        plugin.getProfileManager().removeProfile(player.getUniqueId());

        Bukkit.getScheduler().runTask(this.plugin, () -> {
            PlayerList playerList = ((CraftServer) Bukkit.getServer()).getHandle();
            playerList.disconnect(((CraftPlayer) player).getHandle());
        });
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        event.setLeaveMessage(null);

        onDisconnect(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        onDisconnect(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        String msg = event.getMessage();

        Punishment punishment = profile.getActiveMute();

        if (!profile.hasStaff()) {
            if (plugin.getServerSettings().isGlobalChatMuted()) {
                event.setCancelled(true);
                player.sendMessage(CC.RED + "Global chat is currently muted.");
                return;
            } else if (punishment != null) {
                event.setCancelled(true);

                player.sendMessage(CC.RED + "You are currently muted.");
                player.sendMessage(CC.RED + "Reason: " + CC.YELLOW + punishment.getAddedReason());
                player.sendMessage(CC.RED + "Expires: " + CC.YELLOW + punishment.getTimeRemaining());
                return;
            } else if (plugin.getServerSettings().getSlowChatTime() != -1) {
                long lastChatTime = profile.getLastChatTime();
                int slowChatTime = plugin.getServerSettings().getSlowChatTime();
                long sum = lastChatTime + (slowChatTime * 1000L);

                if (lastChatTime != 0 && sum > System.currentTimeMillis()) {
                    event.setCancelled(true);
                    String diff = TimeUtil.formatTimeMillis(sum - System.currentTimeMillis());
                    player.sendMessage(CC.RED + "Slow chat is currently enabled. You can talk again in " + diff + ".");
                    return;
                }
            }

            Timer timer = profile.getChatCooldownTimer();

            if (timer.isActive()) {
                event.setCancelled(true);
                player.sendMessage(CC.RED + "You can't chat for another " + timer.formattedExpiration() + ".");
                return;
            }

        } else if (profile.isInStaffChat()) {
            event.setCancelled(true);

            Map<String, Object> message = Maps.newHashMap();

            message.put("server", plugin.getServerName());
            message.put("format", profile.getRank().getColor() + player.getName());
            message.put("message", event.getMessage());
            message.put("sender", event.getPlayer().getName());

            plugin.getRedisMessenger().send("staff-chat", message);
            return;
        }

        if (plugin.getFilter().isFiltered(msg)) {
            if (profile.hasStaff()) {
                player.sendMessage(CC.RED + "That would have been filtered.");
            } else {
                event.setCancelled(true);

                String formattedMessage = profile.getChatFormat() + CC.R + ": " + msg;

                plugin.getStaffManager().messageStaff(CC.RED + "(Filtered) " + formattedMessage);
                player.sendMessage(formattedMessage);
                return;
            }
        }

        Iterator<Player> recipients = event.getRecipients().iterator();

        while (recipients.hasNext()) {
            Player recipient = recipients.next();
            CoreProfile recipientProfile = plugin.getProfileManager().getProfile(recipient.getUniqueId());

            if (recipientProfile == null) {
                continue;
            }

            if (recipientProfile.hasPlayerIgnored(player.getUniqueId())
                    || (!recipientProfile.isGlobalChatEnabled() && (!profile.hasStaff() || recipientProfile.hasStaff()))) {
                recipients.remove();
            } else if (recipient != player) {
                String[] words = msg.split(" ");
                boolean found = false;

                StringBuilder newMessage = new StringBuilder();

                for (String word : words) {
                    if (recipient.getName().equalsIgnoreCase(word) && !found) {
                        newMessage.append(CC.PINK).append(CC.I).append(word).append(CC.R).append(" ");
                        found = true;
                    } else {
                        newMessage.append(word).append(" ");
                    }
                }

                if (!found) {
                    continue;
                }

                if (recipientProfile.isPlayingSounds()) {
                    recipient.playSound(recipient.getLocation(), Sound.LEVEL_UP, 1.0F, 2.0F);
                }

                String mentionMessage = profile.getChatFormat() + CC.R + ": " + newMessage;

                recipient.sendMessage(mentionMessage);
                recipient.sendMessage(player.getDisplayName() + CC.PRIMARY + " mentioned you!");

                recipients.remove();
            }
        }

        if (NickAPI.isNicked(player)) {
            event.setFormat(String.format(profile.getDisguiseRank().getRawFormat()) + profile.getDisguiseRank().getColor() + player.getName() + CC.R + ": %2$s");
        } else {
            event.setFormat(profile.getChatFormat() + CC.R + ": %2$s");
        }

        profile.updateLastChatTime();
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        if (profile.hasStaff()) {
            return;
        }

        Timer timer = profile.getCommandCooldownTimer();

        if (timer.isActive()) {
            event.setCancelled(true);
            player.sendMessage(CC.RED + "You can't use commands for another " + timer.formattedExpiration() + ".");
        }
    }

    @EventHandler
    public void onRankChange(PlayerRankChangeEvent event) {
        Player player = event.getPlayer();
        CoreProfile profile = event.getProfile();
        Grant newRank = event.getNewRank();

        profile.getGrant().setRank(newRank.getRank());

        if (profile.hasStaff()) {
            if (!plugin.getStaffManager().isInStaffCache(profile)) {
                plugin.getStaffManager().addCachedStaff(profile);
            }
        } else if (plugin.getStaffManager().isInStaffCache(profile)) {
            plugin.getStaffManager().removeCachedStaff(profile);
        }

        newRank.getRank().apply(player);

        InternalNametag.reloadPlayer(player);
        InternalNametag.reloadOthersFor(player);
    }

    @EventHandler
    public void onFreezeDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player player = (Player) e.getDamager();
            CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
            if (e.getEntity() instanceof Player && plugin.getProfileManager().getProfile(e.getEntity().getUniqueId()).isFrozen() || profile.isFrozen())
                e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFreezeCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        if (profile.isFrozen()) {
            player.sendMessage(CC.RED + "You cannot run commands while you are frozen.");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFreezeInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        if (profile.isFrozen()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFreezeSprint(PlayerToggleSprintEvent e) {
        Player player = e.getPlayer();
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        if (profile.isFrozen()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());
        if (profile.isFrozen()) {
            e.setCancelled(true);
        }
    }

    /*@EventHandler
    public void onVelocity(PlayerVelocityEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId().toString().equalsIgnoreCase(LandCore.verzideUUID)) {
            event.setVelocity(event.getVelocity().multiply(0.92).setY(event.getVelocity().getY()));
        }
    }*/
}
