package us.zonix.core.profile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import us.zonix.core.CorePlugin;
import us.zonix.core.api.request.PlayerRequest;
import us.zonix.core.board.Board;
import us.zonix.core.punishment.Punishment;
import us.zonix.core.punishment.PunishmentType;
import us.zonix.core.rank.Rank;
import us.zonix.core.server.ServerType;
import us.zonix.core.util.ItemUtil;
import us.zonix.core.util.auth.TimeBasedOneTimePasswordUtil;
import us.zonix.core.util.inventory.InventoryUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ProfileListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAysncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {

        if (!CorePlugin.getInstance().isSetupMode()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "Server is setting up!");
            return;
        }

        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            return;
        }

        Profile profile = new Profile(event.getUniqueId());

        Bukkit.getScheduler().runTaskLaterAsynchronously(CorePlugin.getInstance(), () -> {

            profile.setLastLogin(System.currentTimeMillis());
            profile.setChatCooldown(0L);
            profile.setChatEnabled(true);

            if (profile.getRank() == Rank.MEDIA_OWNER) {
                profile.setAuthenticated(true);
            } else if (profile.getTwoFactorAuthentication() != null && !profile.isAuthenticated() && profile.getRank().isAboveOrEqual(Rank.TRIAL_MOD)) {
                profile.setAuthenticated(false);
            } else if (profile.getTwoFactorAuthentication() != null && !profile.getIp().equalsIgnoreCase(event.getAddress().getHostAddress()) && profile.getRank().isAboveOrEqual(Rank.TRIAL_MOD)) {
                profile.setAuthenticated(false);
            } else {
                profile.setAuthenticated(true);
            }

            if (profile.isAuthenticated()) {
                profile.setIp(event.getAddress().getHostAddress());
            }

            if (profile.getName() == null) {
                profile.setName(event.getName());
                profile.save();
            }

            Punishment ban = profile.getBannedPunishment();

            if (ban != null && CorePlugin.getInstance().getServerType() != ServerType.HUB) {
                event.setResult(PlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage(ban.getType().getMessage());
                Profile.getProfiles().remove(profile.getUuid());
                return;
            }

            if (profile.isBlacklisted()) {
                event.setResult(PlayerPreLoginEvent.Result.KICK_OTHER);
                event.setKickMessage(PunishmentType.BLACKLIST.getMessage());
                Profile.getProfiles().remove(profile.getUuid());
                return;
            }

            if (profile.getAlts().size() == 0) {
                profile.loadProfileAlts();
            }

            for (UUID uuid : profile.getAlts()) {
                if (!uuid.equals(event.getUniqueId())) {
                    Profile altProfile = Profile.getByUuid(uuid);
                    Punishment bannedAlt = altProfile.getBannedPunishment();

                    if (bannedAlt != null && CorePlugin.getInstance().getServerType() != ServerType.HUB) {
                        event.setResult(PlayerPreLoginEvent.Result.KICK_OTHER);
                        event.setKickMessage(bannedAlt.getType().getMessage());
                        Profile.getProfiles().remove(altProfile.getUuid());
                        return;
                    }

                    if (altProfile.isBlacklisted()) {
                        event.setResult(PlayerPreLoginEvent.Result.KICK_OTHER);
                        event.setKickMessage(PunishmentType.BLACKLIST.getMessage());
                        Profile.getProfiles().remove(altProfile.getUuid());
                        return;
                    }
                }
            }
        }, 20L);

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        Profile profile = Profile.getByUuidIfAvailable(event.getPlayer().getUniqueId());

        if (event.getResult() == PlayerLoginEvent.Result.KICK_FULL && profile != null && profile.getRank().isAboveOrEqual(Rank.SILVER)) {
            event.allow();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinEvent(PlayerJoinEvent event) throws IOException {
        Player player = event.getPlayer();

        Profile profile = Profile.getByUuidIfAvailable(event.getPlayer().getUniqueId());

        if (profile != null && profile.getTwoFactorAuthentication() == null && profile.getRank().isAboveOrEqual(Rank.TRIAL_MOD) && (profile.getRank() != Rank.MEDIA_OWNER)) {

            String token = TimeBasedOneTimePasswordUtil.generateBase32Secret();
            String url = TimeBasedOneTimePasswordUtil.qrImageUrl(player.getName() + "@zonix.us", token);

            InventoryUI inventoryUI = new InventoryUI("Authentication", 1);

            ItemStack item = ItemUtil.createItem(Material.PAPER, ChatColor.RED.toString() + ChatColor.BOLD + "Authentication Information");
            ItemUtil.reloreItem(item, ChatColor.DARK_RED + "Download Authentication App - Google Auth, 1Password, Authy", ChatColor.RED + "Use Online App - https://gauth.apps.gbraad.nl/", ChatColor.RED + "QR code (Scan): " + url, ChatColor.RED + "or input the code in app: " + token, ChatColor.RED + "Finally, use /auth <token>", "", ChatColor.WHITE.toString() + ChatColor.BOLD + "CLICK TO SHOW IN CHAT");
            inventoryUI.setItem(4, new InventoryUI.AbstractClickableItem(item) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    player.sendMessage("§8§m----------------------------------------------------");
                    player.sendMessage(ChatColor.RED + "Download Authentication App - Google Auth, 1Password, Authy");
                    player.sendMessage(ChatColor.RED + "Use Online App - https://gauth.apps.gbraad.nl/");
                    player.sendMessage(ChatColor.RED + "QR code (Scan): " + url);
                    player.sendMessage(ChatColor.RED + "or input the code in app: " + token);
                    player.sendMessage(ChatColor.RED + "Finally, use /auth <token>");
                    player.sendMessage("§8§m----------------------------------------------------");
                    player.closeInventory();
                }
            });

            // Send the message just in case the retards close the UI.
            player.sendMessage("§8§m----------------------------------------------------");
            player.sendMessage(ChatColor.RED + "Download Authentication App - Google Auth, 1Password, Authy");
            player.sendMessage(ChatColor.RED + "Use Online App - https://gauth.apps.gbraad.nl/");
            player.sendMessage(ChatColor.RED + "QR code (Scan): " + url);
            player.sendMessage(ChatColor.RED + "or input the code in app: " + token);
            player.sendMessage(ChatColor.RED + "Finally, use /auth <token>");
            player.sendMessage("§8§m----------------------------------------------------");

            profile.setTwoFactorAuthentication(token);
            profile.setAuthenticated(false);
            PlayerRequest.UpdateAuthenticationRequest request = new PlayerRequest.UpdateAuthenticationRequest(player.getUniqueId(), profile.getTwoFactorAuthentication(), profile.isAuthenticated());
            CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(request);
            Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(CorePlugin.getInstance(), () -> player.openInventory(inventoryUI.getCurrentPage()), 10L);
        }

        if (profile != null && profile.getTwoFactorAuthentication() != null && !profile.isAuthenticated() && profile.getRank().isAboveOrEqual(Rank.TRIAL_MOD)) {
            player.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "AUTHENTICATE YOURSELF!");
            player.sendMessage(ChatColor.GRAY + "Usage: /auth <token>");
        }

        if (CorePlugin.getInstance().getBoardManager() != null) {
            CorePlugin.getInstance().getBoardManager().getPlayerBoards().put(player.getUniqueId(), new Board(player, CorePlugin.getInstance().getBoardManager().getAdapter()));
        }

        CorePlugin.getInstance().getServer().getScheduler().runTaskLater(CorePlugin.getInstance(), () -> {

            if (profile != null && profile.getRank() != null) {
                Profile.updateTabList(player, profile.getRank());
            }

            if(profile != null && profile.isBlacklisted()) {
                player.kickPlayer(PunishmentType.BLACKLIST.getMessage());
            }
            
        }, 20L);

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuidIfAvailable(event.getPlayer().getUniqueId());

        if (profile != null) {
            CorePlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(CorePlugin.getInstance(), profile::save);
            Profile.getProfiles().remove(profile.getUuid());
        }

        if (CorePlugin.getInstance().getBoardManager() != null) {
            CorePlugin.getInstance().getBoardManager().getPlayerBoards().remove(player.getUniqueId());
        }

        if (CorePlugin.getInstance().getTabListManager() != null) {
            CorePlugin.getInstance().getTabListManager().removePlayer(player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCommandEvent(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());

        if (event.getMessage().toLowerCase().startsWith("/auth")) {
            return;
        }

        if (profile.getTwoFactorAuthentication() != null && !profile.isAuthenticated() && profile.getRank().isAboveOrEqual(Rank.TRIAL_MOD) && (profile.getRank() != Rank.MEDIA_OWNER)) {
            player.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "AUTHENTICATE YOURSELF!");
            player.sendMessage(ChatColor.GRAY + "Usage: /auth <token>");
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());


        if (profile.getTwoFactorAuthentication() != null && !profile.isAuthenticated() && profile.getRank().isAboveOrEqual(Rank.TRIAL_MOD) && (profile.getRank() != Rank.MEDIA_OWNER)) {
            event.setCancelled(true);
            return;
        }

        Punishment punishment = profile.getMutedPunishment();

        if (punishment != null) {
            event.setCancelled(true);
            player.sendMessage(PunishmentType.MUTE.getMessage().replace("%DURATION%", punishment.getTimeLeft()));
        }

        if (CorePlugin.getInstance().getRedisManager().getStaffChat().contains(player.getUniqueId())) {
            event.setCancelled(true);
            CorePlugin.getInstance().getRedisManager().writeStaffChat(player.getName(), profile.getRank(), ChatColor.stripColor(event.getMessage()));
        }

        List<Player> recipientList = new ArrayList<>(event.getRecipients());

        for (Player recipient : recipientList) {
            Profile profileRecipient = Profile.getByUuid(recipient.getUniqueId());

            if (profileRecipient != null) {
                if (!profileRecipient.isChatEnabled() || profileRecipient.getIgnored().contains(profile.getUuid())) {
                    event.getRecipients().remove(recipient);
                }
            }
        }
    }

}
